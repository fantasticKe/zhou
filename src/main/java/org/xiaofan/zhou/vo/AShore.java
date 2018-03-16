package org.xiaofan.zhou.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author maokeluo
 * @desc 岸桥
 * @create 18-3-13
 */
@Data
public class AShore extends Bridge{

    /**id**/
    private int id;

    /**到场桥的距离 以岸桥和厂桥的id组合为key**/
    private Map<String,Integer> distanceOfCBridge;

    /**到厂桥的任务 以岸桥和厂桥的id组合为key,任务集合为value**/
    private Map<String, List<Task>> tasks;

    /**到充电站的距离**/
    private int distanceOfStation;

    private AShore(){}

    public AShore(int id){
        this.id = id;
    }

}
