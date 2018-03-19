package org.xiaofan.zhou;

import org.xiaofan.zhou.vo.*;
import org.xiaofan.zhou.vo.factory.AGVFactory;
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

        //利用工厂创建任务
        TaskFactory taskFactory = TaskFactory.getTaskFactory();
        List<Task> tasks = taskFactory.batchCreateTask(1000);
        tasks.forEach(System.out::println);

        //利用工厂创建场桥
        CBridgeFactory cBridgeFactory = CBridgeFactory.getCBridgeFactory();
        List<CBridge> cBridges = cBridgeFactory.batchCreateBridge(6);
        cBridges.forEach(System.out::println);

        //利用工厂创建岸桥
        AShoreFactory aShoreFactory = AShoreFactory.getShoreFactory();
        List<AShore> aShores = aShoreFactory.batchCreateShore(2);
        aShores.forEach(System.out::println);

        //初始化充电站
        ChargeStation.newInstance();

        //利用工厂创建AGV
        AGVFactory agvFactory = AGVFactory.getAgvFactory();
        List<AGV> agvs = agvFactory.batchCreateAGV(8);
        //agvs.forEach(System.out::println);
        while (TaskFactory.getNotCompleteTask().size() > 0){
            agvs.forEach(p->{
                p.accessTask();
                p.run();
            });
        }
        long time = 0;
        for (Task task : TaskFactory.tasks) {
            time += task.getCompletedTime();
        }
        System.out.println("完成任务总共花费时间(h):"+time);

    }
}
