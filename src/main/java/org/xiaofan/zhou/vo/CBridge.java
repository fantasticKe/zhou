package org.xiaofan.zhou.vo;

import lombok.Data;
import sun.font.StrikeCache;

import java.io.UnsupportedEncodingException;

/**
 * @author maokeluo
 * @desc 厂桥
 * @create 18-3-13
 */
@Data
public class CBridge extends Bridge{

    /**id**/
    private int id;

    /**到充电站的距离**/
    private double distanceOfStation;

    private CBridge(){}

    public CBridge(int id){
        this.id = id;
    }
}
