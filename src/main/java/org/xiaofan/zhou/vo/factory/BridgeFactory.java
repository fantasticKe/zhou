package org.xiaofan.zhou.vo.factory;

import org.xiaofan.zhou.vo.Bridge;

/**
 * 场桥生产工厂
 */
public class BridgeFactory {

    private static BridgeFactory bridgeFactory = new BridgeFactory();

    private BridgeFactory(){}

    public static BridgeFactory getBridgeFactory(){
        return bridgeFactory;
    }

    public Bridge createBridge(int id){
        return new Bridge(id);
    }
}
