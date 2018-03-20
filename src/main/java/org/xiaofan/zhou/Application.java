package org.xiaofan.zhou;

import org.xiaofan.zhou.vo.*;
import org.xiaofan.zhou.vo.factory.AGVFactory;
import org.xiaofan.zhou.vo.factory.AShoreFactory;
import org.xiaofan.zhou.vo.factory.CBridgeFactory;
import org.xiaofan.zhou.vo.factory.TaskFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
        Runnable task1 = ()->{
            AGV agv = agvs.get(0);
            agv.accessTask();
            agv.run();
        };

        Runnable task2 = ()->{
            AGV agv = agvs.get(1);
            agv.accessTask();
            agv.run();
        };

        Runnable task3 = ()->{
            AGV agv = agvs.get(2);
            agv.accessTask();
            agv.run();
        };

        Runnable task4 = ()->{
            AGV agv = agvs.get(3);
            agv.accessTask();
            agv.run();
        };

        Runnable task5 = ()->{
            AGV agv = agvs.get(4);
            agv.accessTask();
            agv.run();
        };

        Runnable task6 = ()->{
            AGV agv = agvs.get(5);
            agv.accessTask();
            agv.run();
        };

        Runnable task7 = ()->{
            AGV agv = agvs.get(6);
            agv.accessTask();
            agv.run();
        };

        Runnable task8 = ()->{
            AGV agv = agvs.get(7);
            agv.accessTask();
            agv.run();
        };

        while (TaskFactory.getNotCompleteTask().size() > 0){
            /*agvs.forEach(p->{
                p.accessTask();
                p.run();
            });*/
        }
        double time = 0;
        for (Task task : TaskFactory.tasks) {
            time = time + task.getCompletedTime();
        }
        System.out.println("完成任务总共花费时间(h):"+time);

    }
}
