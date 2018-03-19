package org.xiaofan.zhou.vo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 充电站
 */
@Data
public class ChargeStation {

    /**未工作**/
    public static final AtomicInteger NO_WORK  = new AtomicInteger(0);

    /**工作**/
    public static final AtomicInteger WORK = new AtomicInteger(1);

    /**充电站状态**/
    private AtomicInteger state;

    private static ChargeStation chargeStation = new ChargeStation();

    private ChargeStation(){}

    /**
     * @desc 以单例创建充电站
     * @author maokeluo
     * @methodName getChargeStation
     * @create 18-3-18
     * @return org.xiaofan.zhou.vo.ChargeStation
     */
    public static ChargeStation newInstance(){
        chargeStation.setState(NO_WORK);
        return chargeStation;
    }

    /**
     * @desc 获取充电站当前状态
     * @author maokeluo
     * @methodName getStateOfChargeStation
     * @create 18-3-18
     * @return int
     */
    public static int getStateOfChargeStation(){
        return chargeStation.state.get();
    }


}
