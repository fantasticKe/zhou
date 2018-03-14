package org.xiaofan.zhou.vo;

import lombok.Data;

import java.util.List;

/**
 * @author maokeluo
 * @desc 岸桥
 * @create 18-3-13
 */
@Data
public class Shore {

    /**id**/
    private int id;

    /**到场桥的距离**/
    private int distanceOfBridge;

    /**到厂桥的任务**/
    private List<Task> tasks;

    /**到充电站的距离**/
    private int distanceOfStation;

    private Shore(){}

    public Shore(int id){
        this.id = id;
    }

}
