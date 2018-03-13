package org.xiaofan.zhou.vo;

import lombok.Data;

/**
 * @author maokeluo
 * @desc 厂桥
 * @create 18-3-13
 */
@Data
public class Bridge {

    /**id**/
    private int id;

    /**到岸桥的距离**/
    private int distanceOfShore;

    /**到充电站的距离**/
    private int distanceOfStation;
}
