package org.xiaofan.zhou.vo;

import lombok.Data;

/**
 * @author maokeluo
 * @desc AVG
 * @create 18-3-13
 */
@Data
public class AVG {

    /**id**/
    private int id;

    /**满电空载行程**/
    private int lightTravel = 200;

    /**满电负载行程**/
    private int loadStroke = 150;

    /**当前电量**/
    private double currentPower;

    /**空载速度**/
    private int noLoadSpeed = 25;

    /**负载速度**/
    private int loadSpeed = 20;

    /**空载耗电速度**/
    private double noLoadConsumptionSpeed = 12.5;

    /**负载耗电速度**/
    private double loadConsumptionSpeed = 13.3;

    /**充电速度**/
    private double chargingSpeed = 0.67;

    /**允许工作的最低电量**/
    private double workPower = 0.1;

    /**允许充电断开的最低电量**/
    private double disconnectPower = 0.6;

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

}
