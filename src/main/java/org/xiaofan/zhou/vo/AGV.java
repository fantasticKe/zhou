package org.xiaofan.zhou.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.xiaofan.zhou.util.PropertyReaderUtil;
import org.xiaofan.zhou.vo.factory.AGVFactory;
import org.xiaofan.zhou.vo.factory.AShoreFactory;
import org.xiaofan.zhou.vo.factory.TaskFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author maokeluo
 * @desc AGV
 * @create 18-3-13
 */
@Data
public class AGV {

    /**小车状态 0 工作 1 等待充电 2 充电**/
    public static final int WORK = 0;
    public static final int WAIT = 1;
    public static final int CHARGE = 2;

    /**id**/
    private int id;

    /**满电空载行程**/
    private int lightTravel;

    /**满电负载行程**/
    private int loadStroke;

    /**当前电量**/
    private double currentPower;

    /**空载速度**/
    private int noLoadSpeed;

    /**负载速度**/
    private int loadSpeed;

    /**空载耗电速度**/
    private double noLoadConsumptionSpeed;

    /**负载耗电速度**/
    private double loadConsumptionSpeed;

    /**充电速度**/
    private double chargingSpeed;

    /**允许工作的最低电量**/
    private int workPower;

    /**允许充电断开的最低电量**/
    private int disconnectPower;

    /**完成任务数**/
    private int completedTask;

    /**工作时间**/
    private long workTime;

    /**充电时间**/
    private long chargeTime;

    /**等待时间**/
    private long waitTime;

    /**行驶距离**/
    private double distance;

    /**当前任务**/
    private Task currentTask;

    /**下一个任务**/
    private Task nextTask;

    /**当前位置**/
    private CBridge location;

    /**小车状态**/
    private int state;

    private AGV(){}

    public AGV(int id){
        this.id = id;
    }

    public AGV(int id, int lightTravel, int loadStroke, int noLoadSpeed, int loadSpeed, double noLoadConsumptionSpeed,
               double loadConsumptionSpeed, double chargingSpeed, int workPower, int disconnectPower, CBridge location,
               int state) {
        this.id = id;
        this.lightTravel = lightTravel;
        this.loadStroke = loadStroke;
        this.noLoadSpeed = noLoadSpeed;
        this.loadSpeed = loadSpeed;
        this.noLoadConsumptionSpeed = noLoadConsumptionSpeed;
        this.loadConsumptionSpeed = loadConsumptionSpeed;
        this.chargingSpeed = chargingSpeed;
        this.workPower = workPower;
        this.disconnectPower = disconnectPower;
        this.location = location;
        this.state = state;
    }

    /**
     * 获取当前任务
     * @return
     */
    public Task accessTask(){
        List<Task> notCompleteTask = TaskFactory.getNotCompleteTask();
        List<AShore> shores = AShoreFactory.shores;
        JSONObject distanceOfBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
        //以任务的id为key，加权值为value
        Map<Integer,Double> weightedMap = new HashMap<>();
        Task task = null;
        if (notCompleteTask.size() > 0){
            shores.forEach(p->{
                String distanceKey = p.getId()+String.valueOf(location.getId());
                Double distance = Double.valueOf(distanceOfBridge.get(distanceKey).toString());
                notCompleteTask.forEach(q->{
                    double weighted = 0.4 * distance + 0.6 * q.getDegree();
                    weightedMap.put(q.getId(),weighted);
                });
            });


            Map.Entry<Integer, Double> entry = weightedMap.entrySet().stream()
                    .min(Comparator.comparing(Map.Entry::getValue))
                    .get();
            System.out.printf("%d号AGV接取到任务%d \n",id,entry.getKey());
            task = TaskFactory.getTaskById(entry.getKey());
            task.setState(Task.BE_ACCESS);
            setCurrentTask(task);
        }
        return task;
    }



    /**
     * AGV开始运行
     */
    public void run(){
        System.out.printf("%d号AGV开始执行任务：%d \n",id,currentTask.getId());
        AShore aShoreByTask = AShoreFactory.getAShoreByTask(currentTask);
        /*//空载行程
        JSONObject distanceOfBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
        String noLoaddistanceKey = location.getId() + String.valueOf(aShoreByTask.getId());
        double noLoadDistance = Double.valueOf(distanceOfBridge.get(noLoaddistanceKey).toString());
        //空载电量消耗
        double noLoadPower = noLoadDistance / noLoadSpeed * noLoadConsumptionSpeed;

        //负载行程
        String loaddistanceKey = aShoreByTask.getId() + String.valueOf(location.getId());
        double loadDistance = Double.valueOf(distanceOfBridge.get(loaddistanceKey).toString());
        //负载电量消耗
        double loadPower = loadDistance / loadSpeed * loadConsumptionSpeed;
        System.out.println("任务"+currentTask.getId()+",空载距离:"+noLoadDistance+",负载距离:"
                +loadDistance + ",空载耗电:"+noLoadPower+",负载耗电:"+loadPower);*/
        Map<String, Double> map = consumptionOfRunTask(aShoreByTask);
        Double noLoadPower = map.get("noLoadPower");
        Double loadPower = map.get("loadPower");
        Double loadDistance = map.get("loadDistance");
        Double noLoadDistance = map.get("noLoadDistance");
        //当前电量
        currentPower = currentPower - noLoadPower - loadPower;
        //行程改变
        distance = loadDistance + noLoadDistance;
        //任务完成
        Task task = TaskFactory.getTaskById(currentTask.getId());
        task.setState(Task.COMPLETED);
        task.setCompletedTime(loadDistance / loadSpeed);
        completedTask++;
        System.out.println("完成任务:"+currentTask.getId()+",消耗时间："+ (loadDistance / loadSpeed));

        if (currentPower < 10){
            System.out.printf("%d AGV前往充电,当前电量: %f \n",id,currentPower);
            charge();
        }
        Task accessTask = accessTask();
        if (accessTask != null){
            //判断完成任务后小车剩余电量是否足够返回充电站充电
            AShore aShore = AShoreFactory.getAShoreByTask(accessTask);
            Map<String, Double> runTask = consumptionOfRunTask(aShore);
            Double nextLoadPower = runTask.get("loadPower");
            Double nextNoLoadPower = runTask.get("noLoadPower");
            double soc = currentPower - nextLoadPower - nextNoLoadPower;
            if (soc < 10){//若执行完下一个任务后电量小于10,则放弃任务,前往充电
                accessTask.setState(Task.NOT_COMPLETE);
                charge();
            }
            System.out.printf("%d AGV 当前电量: %f \n",id,currentPower);
        }
    }

    /**
     * @desc 充电
     * @author maokeluo
     * @methodName charge
     * @create 18-3-18
     * @return void
     */
    public void charge(){

        long startTime = System.currentTimeMillis();
        //充电
        if (AGVFactory.waitQueue().size() == 0 ){//没有等待AGV
            state = CHARGE;
            //waitTime += (System.currentTimeMillis() - startTime) / (1000 * 60 * 60);
            currentPower = 60;
            chargeTime = (long) (chargeTime + (60 - currentPower) / chargingSpeed);
            state = WORK;
            ChargeStation.newInstance();
            /*while (true){
                //等待车辆的数量
                int waitSize = AGVFactory.waitQueue().size();
                System.out.printf("%d AGV当前电量: %f \n",id,currentPower);
                if ((waitSize > 0 && currentPower >= 60) || currentPower == 100) {
                    state = WORK;
                    ChargeStation.newInstance();
                    break;
                }
                long time = (System.currentTimeMillis() - startTime) / (1000 * 60 *60);
                chargeTime += time;
                currentPower = 1.5 * time;
            }*/
        }else if ((AGVFactory.waitQueue().size() > 0) && AGVFactory.waitQueue().get(0) == this){//第一个等待AGV为当前AGV
            state = CHARGE;
            waitTime += (System.currentTimeMillis() - startTime) / (1000 * 60 * 60);
            while (true){
                //等待车辆的数量
                int waitSize = AGVFactory.waitQueue().size();
                if ((waitSize > 0 && currentPower >= 60) || currentPower == 100) {
                    long time = (System.currentTimeMillis() - startTime) / (1000 * 60 *60);
                    chargeTime += time;
                    currentPower = 1.5 * time;
                    state = WORK;
                    ChargeStation.newInstance();
                    AGVFactory.waitQueue().remove(this);
                    break;
                }
            }
        }else {//等待
            state = WAIT;
            AGVFactory.addWaitQueue(this);
        }
    }

    /**
     * @desc 执行任务AGV改变量
     * @author maokeluo
     * @methodName consumptionOfRunTask
     * @param  aShore
     * @create 18-3-19
     * @return java.util.Map<java.lang.String,java.lang.Double>
     */
    public Map<String,Double> consumptionOfRunTask(AShore aShore){
        Map<String,Double> map = new HashMap<>();
        //空载行程
        JSONObject distanceOfBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
        String noLoaddistanceKey = location.getId() + String.valueOf(aShore.getId());
        double noLoadDistance = Double.valueOf(distanceOfBridge.get(noLoaddistanceKey).toString());
        //空载电量消耗
        double noLoadPower = noLoadDistance / noLoadSpeed * noLoadConsumptionSpeed;

        //负载行程
        String loaddistanceKey = aShore.getId() + String.valueOf(location.getId());
        double loadDistance = Double.valueOf(distanceOfBridge.get(loaddistanceKey).toString());
        //负载电量消耗
        double loadPower = loadDistance / loadSpeed * loadConsumptionSpeed;
        System.out.println("执行任务"+currentTask.getId()+"需,空载距离:"+noLoadDistance+",负载距离:"
                +loadDistance + ",空载耗电:"+noLoadPower+",负载耗电:"+loadPower);

        map.put("noLoadDistance",noLoadDistance);
        map.put("noLoadPower",noLoadPower);
        map.put("loadDistance",loadDistance);
        map.put("loadPower",loadPower);
        return map;
    }
}
