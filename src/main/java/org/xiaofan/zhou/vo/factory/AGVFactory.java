package org.xiaofan.zhou.vo.factory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.xiaofan.zhou.util.PropertyReaderUtil;
import org.xiaofan.zhou.vo.AGV;
import org.xiaofan.zhou.vo.Bridge;
import org.xiaofan.zhou.vo.CBridge;

import java.util.ArrayList;
import java.util.List;
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
                chargingSpeed,workPower,disconnectPower,location);
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

}
