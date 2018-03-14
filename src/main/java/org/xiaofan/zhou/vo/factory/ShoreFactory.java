package org.xiaofan.zhou.vo.factory;

import org.xiaofan.zhou.vo.Shore;

/**
 * 岸桥生产工厂
 */
public class ShoreFactory {

    //单例
    private static ShoreFactory shoreFactory = new ShoreFactory();
    //私有化构造方法
    private ShoreFactory(){}

    //定义一个对外开放的方法，以获取该工厂实例
    public static ShoreFactory createShoreFactory(){
        return shoreFactory;
    }

    /**
     * 创建岸桥实体
     * @param id
     * @return
     */
    public Shore createShore(int id){
        return new Shore(id);
    }
}
