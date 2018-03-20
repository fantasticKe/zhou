package org.xiaofan.zhou.vo.factory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.xiaofan.zhou.util.PropertyReaderUtil;
import org.xiaofan.zhou.vo.AGV;
import org.xiaofan.zhou.vo.Bridge;
import org.xiaofan.zhou.vo.CBridge;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

/**
 * AGV创建工厂
 */
public class AGVFactory {

    private static AGVFactory agvFactory = new AGVFactory();

    private AGVFactory(){}

    static JSONObject jsonObject;

    public static AGVFactory getAgvFactory(){
        jsonObject = PropertyReaderUtil.readYml().getJSONObject("agv");
        return agvFactory;
    }

    private static volatile List<AGV> waitQueue = new LinkedList<>();
    //入队时间
    public static ConcurrentHashMap<Integer,Long> addQueueMap = new ConcurrentHashMap<>();
    //出队时间
    public static ConcurrentHashMap<Integer,Long> popQueueMap = new ConcurrentHashMap<>();


    /**
     * @desc 根据配置文件创建AGV
     * @author maokeluo
     * @methodName createAgv
     * @param  id
     * @create 18-3-15
     * @return org.xiaofan.zhou.vo.AGV
     */
    private AGV createAgv(int id){
        int lightTravel = Integer.valueOf(jsonObject.get("lightTravel").toString());
        int loadStroke = Integer.valueOf(jsonObject.get("loadStroke").toString());
        int noLoadSpeed = Integer.valueOf(jsonObject.get("noLoadSpeed").toString());
        int loadSpeed = Integer.valueOf(jsonObject.get("loadSpeed").toString());
        double noLoadConsumptionSpeed = Double.valueOf(jsonObject.get("noLoadConsumptionSpeed").toString());
        double loadConsumptionSpeed = Double.valueOf(jsonObject.get("loadConsumptionSpeed").toString());
        double chargingSpeed = Double.valueOf(jsonObject.get("chargingSpeed").toString());
        int workPower = Integer.valueOf(jsonObject.get("workPower").toString());
        int disconnectPower = Integer.valueOf(jsonObject.get("disconnectPower").toString());
        CBridge location = CBridgeFactory.list.get(0);

        return new AGV(id,lightTravel,loadStroke,noLoadSpeed,loadSpeed,noLoadConsumptionSpeed,loadConsumptionSpeed,
                chargingSpeed,workPower,disconnectPower,location,AGV.WORK);
    }

    public static List<AGV> agvList = new ArrayList<>();

    /**
     * 批量创建AGV
     * @param count
     * @return
     */
    public List<AGV> batchCreateAGV(int count){
        JSONArray power = jsonObject.getJSONArray("power");
        IntStream.range(1,count+1).forEach(p->{
            AGV agv = createAgv(p);
            agv.setCurrentPower(Double.valueOf(power.get(p-1).toString()));

            agvList.add(agv);
        });
        return agvList;
    }

    /**
     * @desc 充电等待队列
     * @author maokeluo
     * @methodName waitQueue
     * @create 18-3-18
     * @return java.util.List<org.xiaofan.zhou.vo.AGV>
     */
    public static List<AGV> waitQueue(){
        return waitQueue;
    }

    /**
     * @desc 入队
     * @author maokeluo
     * @methodName addWaitQueue
     * @param  agv
     * @create 18-3-18
     * @return void
     */
    public static synchronized void addWaitQueue(AGV agv){
        waitQueue.add(agv);
        addQueueMap.put(agv.getId(),System.currentTimeMillis());
    }

    /**
     * @desc 出队
     * @author maokeluo
     * @methodName popWaitQueue
     * @param  agv
     * @create 18-3-18
     * @return void
     */
    public static synchronized void popWaitQueue(AGV agv){
        waitQueue.remove(agv);
        popQueueMap.put(agv.getId(),System.currentTimeMillis());
    }
}
