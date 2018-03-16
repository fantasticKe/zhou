package org.xiaofan.zhou.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.xiaofan.zhou.util.PropertyReaderUtil;
import org.xiaofan.zhou.vo.factory.AShoreFactory;
import org.xiaofan.zhou.vo.factory.TaskFactory;

import java.util.*;

/**
 * @author maokeluo
 * @desc AGV
 * @create 18-3-13
 */
@Data
public class AGV {

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

    private AGV(){}

    public AGV(int id){
        this.id = id;
    }

    public AGV(int id, int lightTravel, int loadStroke, int noLoadSpeed, int loadSpeed, double noLoadConsumptionSpeed,
               double loadConsumptionSpeed, double chargingSpeed, int workPower, int disconnectPower, CBridge location) {
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
    }

    /**
     * 获取当前任务
     * @return
     */
    public void accessTask(){
        List<Task> notCompleteTask = TaskFactory.getNotCompleteTask();
        List<AShore> shores = AShoreFactory.shores;
        JSONObject distanceOfBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
        //以任务的id为key，加权值为value
        Map<Integer,Double> weightedMap = new HashMap<>();
        List<Double> weighteds = new ArrayList<>();
        shores.forEach(p->{
            String distanceKey = p.getId()+String.valueOf(location.getId());
            Integer distance = Integer.valueOf(distanceOfBridge.get(distanceKey).toString());
            notCompleteTask.forEach(q->{
                double weighted = 0.4 * distance + 0.6 * q.getDegree();
                weightedMap.put(q.getId(),weighted);
                weighteds.add(weighted);
            });
        });

        Map.Entry<Integer, Double> entry = weightedMap.entrySet().stream()
                .min(Comparator.comparing(Map.Entry::getValue))
                .get();

        System.out.printf("%d号AGV接取到任务：%d \n",id,entry.getKey());
        Task task = TaskFactory.getTaskById(entry.getKey());
        task.setState(Task.BE_ACCESS);
        setCurrentTask(task);
    }

    /**
     * AGV开始运行
     */
    public void run(){
        System.out.printf("%d号AGV开始执行任务：%d \n",id,currentTask.getId());
        //空载行程
        JSONObject distanceOfBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
        String noLoaddistanceKey = location.getId() + String.valueOf(currentTask.getId());
        Integer distance = Integer.valueOf(distanceOfBridge.get(noLoaddistanceKey).toString());
        String loaddistanceKey = currentTask.getId() + String.valueOf(location.getId());

        //行程改变

    }
}
