package org.xiaofan.zhou.vo;

import lombok.Data;

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
    private double workPower;

    /**允许充电断开的最低电量**/
    private double disconnectPower;

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
    private Task nowTask;

    /**下一个任务**/
    private Task nextTask;

    /**当前位置**/
    private Bridge location;

    private AGV(){}
}
