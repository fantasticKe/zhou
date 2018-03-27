package org.xiaofan.zhou.vo.factory;

import com.alibaba.fastjson.JSONArray;
import org.xiaofan.zhou.util.PropertyReaderUtil;
import org.xiaofan.zhou.vo.CBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 场桥生产工厂
 */
public class CBridgeFactory {

    private static CBridgeFactory CBridgeFactory = new CBridgeFactory();

    private CBridgeFactory(){}

    public static CBridgeFactory getCBridgeFactory(){
        return CBridgeFactory;
    }

    private CBridge createBridge(int id){
        return new CBridge(id);
    }

    public static List<CBridge> list = new ArrayList<>();

    /**
     * 批量创建场桥
     * @param count 创建场桥的数量
     * @return
     */
    public List<CBridge> batchCreateBridge(int count){
        JSONArray jsonArray = PropertyReaderUtil.readYml().getJSONArray("distanceOfCBridge2Station");
        IntStream.range(1,count+1).forEach(p->{
            CBridge bridge = createBridge(p);
            bridge.setDistanceOfStation(Double.valueOf(jsonArray.get(p-1).toString()));
            list.add(bridge);
        });
        return list;
    }

    /**
     * @desc 通过id获取厂桥
     * @author maokeluo
     * @methodName getById
     * @param  id 厂桥id
     * @create 18-3-26
     * @return org.xiaofan.zhou.vo.CBridge
     */
    public synchronized static CBridge getById(int id){
        return list.stream()
                .filter(p->p.getId() == id)
                .collect(Collectors.toList())
                .get(0);
    }
}
