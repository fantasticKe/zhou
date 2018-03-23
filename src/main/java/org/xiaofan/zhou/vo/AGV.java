package org.xiaofan.zhou.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.xiaofan.zhou.util.PropertyReaderUtil;
import org.xiaofan.zhou.util.RandomUtil;
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
    private double workTime;

    /**充电时间**/
    private double chargeTime;

    /**等待时间**/
    private double waitTime;

    /**当前等待时间**/
    private double currentWaitTime;

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
     * AGV开始运行
     */
    public void run(){
        if (currentTask.getState() == Task.BE_ACCESS){
            System.out.printf("%d号AGV开始执行任务：%d \n",id,currentTask.getId());
            Map<String, AShore> aShoreByTask = AShoreFactory.getAShoreByTask(currentTask);
            Map<String, Double> map = TaskFactory.consumptionOfRunTask(this,aShoreByTask);
            Double noLoadPower = map.get("noLoadPower");
            Double loadPower = map.get("loadPower");
            Double loadDistance = map.get("loadDistance");
            Double noLoadDistance = map.get("noLoadDistance");
            //当前电量
            currentPower = currentPower - noLoadPower - loadPower;
            //行程改变
            distance = distance + loadDistance + noLoadDistance;
            //工作时间改变
            workTime = workTime + loadDistance / loadSpeed + noLoadDistance / noLoadSpeed;
            //任务完成
            Task task = TaskFactory.getTaskById(currentTask.getId());
            task.setState(Task.COMPLETED);
            currentTask.setState(Task.COMPLETED);

            task.setCompletedTime(loadDistance / loadSpeed);
            completedTask++;
            double time = (loadDistance / loadSpeed ) + (noLoadDistance / noLoadSpeed);
            System.out.println("完成任务:"+currentTask.getId()+",消耗时间："+ time + ",当前电量："+currentPower);
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

        //充电路途上的消耗
        JSONArray distanceOfCBridge2Station = PropertyReaderUtil.readYml().getJSONArray("distanceOfCBridge2Station");
        double distance2Station = Double.valueOf(distanceOfCBridge2Station.get(location.getId()-1).toString());
        distance += distance2Station;
        workTime += distance2Station / noLoadSpeed;

        //到达充电站
        AGVFactory.addWaitQueue(this);
        while (true){
            //等待
            int waitIndex = AGVFactory.waitQueueSize() -1;
            System.out.println("当前等待车辆数："+waitIndex);
            if (waitIndex > 0){
                System.out.println(id+"号AGV，等待。。。。");
                AGV preAgv = AGVFactory.waitQueue().get(waitIndex);
                currentWaitTime = (long) (preAgv.currentWaitTime + (100 - preAgv.currentPower) / chargingSpeed);
                state = WAIT;
            }else {
                //充电
                System.out.println(id+"号AGV，充电。。。");
                chargeTime = (chargeTime + (100 - currentPower) / chargingSpeed);
                currentPower = 100;
                state = WORK;
                AGVFactory.popWaitQueue(this);
                System.out.println(id+"号小车充完电，等待车辆数："+AGVFactory.waitQueue().size());
                break;
            }
            waitTime += currentWaitTime;
        }
    }

}
