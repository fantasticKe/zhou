package org.xiaofan.zhou;

import org.xiaofan.zhou.util.RandomUtil;
import org.xiaofan.zhou.vo.AShore;
import org.xiaofan.zhou.vo.CBridge;
import org.xiaofan.zhou.vo.Task;
import org.xiaofan.zhou.vo.factory.AShoreFactory;
import org.xiaofan.zhou.vo.factory.CBridgeFactory;
import org.xiaofan.zhou.vo.factory.TaskFactory;

import java.util.List;

/**
 * @author maokeluo
 * @desc
 * @create 18-3-13
 */
public class Application {

    public static void main(String[] args) {
        //利用工厂创建场桥
        CBridgeFactory cBridgeFactory = CBridgeFactory.getCBridgeFactory();
        List<CBridge> cBridges = cBridgeFactory.batchCreateBridge(6);
        cBridges.forEach(System.out::println);

        //利用工厂创建岸桥
        AShoreFactory aShoreFactory = AShoreFactory.createShoreFactory();
        List<AShore> aShores = aShoreFactory.batchCreateShore(2);
        aShores.forEach(System.out::println);

        //利用工厂创建任务
        TaskFactory taskFactory = TaskFactory.getTaskFactory();
        List<Task> tasks = taskFactory.batchCreateTask(1000);
        tasks.forEach(System.out::println);
    }
}
