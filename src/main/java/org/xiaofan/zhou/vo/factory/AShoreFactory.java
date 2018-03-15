package org.xiaofan.zhou.vo.factory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.xiaofan.zhou.util.PropertyReaderUtil;
import org.xiaofan.zhou.vo.AShore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 岸桥生产工厂
 */
public class AShoreFactory {

    private static List<AShore> shores = new ArrayList<>();

    //单例
    private static AShoreFactory AShoreFactory = new AShoreFactory();
    //私有化构造方法
    private AShoreFactory(){}

    //定义一个对外开放的方法，以获取该工厂实例
    public static AShoreFactory createShoreFactory(){
        return AShoreFactory;
    }

    /**
     * 创建岸桥实体
     * @param id
     * @return
     */
    private AShore createShore(int id){
        return new AShore(id);
    }

    /**
     * 批量创建岸桥
     * @param count
     * @return
     */
    public List<AShore> batchCreateShore(int count){
        //岸桥到充电站的距离
        JSONArray distances2Station = PropertyReaderUtil.readYml().getJSONArray("distanceOfAShore2Station");
        //岸桥和厂桥的距离
        JSONObject distance2CBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
        //岸桥到场桥的任务
        JSONObject task = PropertyReaderUtil.readYml().getJSONObject("task");

        IntStream.range(1,count+1).forEach((int p) ->{
            AShore shore = createShore(p);
            shore.setDistanceOfStation(Integer.valueOf(distances2Station.get(p-1).toString()));
            Map<String,Integer> tasks = new HashMap<>();
            IntStream.range(1,6).forEach(q->{
                String key = p+String.valueOf(q);
                tasks.put(key, Integer.valueOf(task.get(key).toString()));
            });
            shore.setTasks(tasks);
            Map<String,Integer> distanceOfCBridge = new HashMap<>();
            IntStream.range(1,7).forEach(k->{
                String key = p+String.valueOf(k);
                distanceOfCBridge.put(key,Integer.valueOf(distance2CBridge.get(key).toString()));
            });
            shore.setDistanceOfCBridge(distanceOfCBridge);

            shores.add(shore);
        });
        return shores;
    }
}
