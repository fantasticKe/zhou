package org.xiaofan.zhou;

import org.xiaofan.zhou.vo.*;
import org.xiaofan.zhou.vo.factory.AGVFactory;
import org.xiaofan.zhou.vo.factory.AShoreFactory;
import org.xiaofan.zhou.vo.factory.CBridgeFactory;
import org.xiaofan.zhou.vo.factory.TaskFactory;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
        //tasks.forEach(System.out::println);

        //利用工厂创建场桥
        CBridgeFactory cBridgeFactory = CBridgeFactory.getCBridgeFactory();
        cBridgeFactory.batchCreateBridge(6);

        //利用工厂创建岸桥
        AShoreFactory aShoreFactory = AShoreFactory.getShoreFactory();
        aShoreFactory.batchCreateShore(2);

        //初始化充电站
        ChargeStation.newInstance();

        //利用工厂创建AGV
        AGVFactory agvFactory = AGVFactory.getAgvFactory();
        List<AGV> agvs = agvFactory.batchCreateAGV(8);
        //agvs.forEach(System.out::println);

        AtomicBoolean run = new AtomicBoolean(true);

        //AGV开始执行任务
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable task = ()->{
            while (run.get()){
                for (int i = 0; i < AGVFactory.NUM; i++) {
                    if (agvs.get(i).getState() == AGV.WORK){
                        taskFactory.accessTask(agvs.get(i));
                    }
                }
            }
        };
        executorService.submit(task);
        /*ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task1 = ()->{
            while (run.get()){
                if (agvs.get(0).getState() == AGV.WORK){
                    taskFactory.accessTask(agvs.get(0));
                }
            }
        };
        Runnable task2 = ()->{
            while (run.get()){
                if (agvs.get(1).getState() == AGV.WORK){
                    taskFactory.accessTask(agvs.get(1));
                }
            }
        };
        Runnable task3 = ()->{
            while (run.get()){
                if (agvs.get(2).getState() == AGV.WORK){
                    taskFactory.accessTask(agvs.get(2));
                }
            }
        };
        Runnable task4 = ()->{
            while (run.get()){
                if (agvs.get(3).getState() == AGV.WORK){
                    taskFactory.accessTask(agvs.get(3));
                }
            }
        };
        Runnable task5 = ()->{
            while (run.get()){
                if (agvs.get(4).getState() == AGV.WORK){
                    taskFactory.accessTask(agvs.get(4));
                }
            }
        };
        Runnable task6 = ()->{
            while (run.get()){
                if (agvs.get(5).getState() == AGV.WORK){
                    taskFactory.accessTask(agvs.get(5));
                }
            }
        };
        Runnable task7 = ()->{
            while (run.get()){
                if (agvs.get(6).getState() == AGV.WORK){
                    taskFactory.accessTask(agvs.get(6));
                }
            }
        };
        Runnable task8 = ()->{
            while (run.get()){
                if (agvs.get(7).getState() == AGV.WORK){
                    taskFactory.accessTask(agvs.get(7));
                }
            }
        };
        executorService.submit(task1);
        executorService.submit(task2);
        executorService.submit(task3);
        executorService.submit(task4);
        executorService.submit(task5);
        executorService.submit(task6);
        executorService.submit(task7);
        executorService.submit(task8);*/
        while (true){
            long count = tasks.stream().filter(p -> p.getState() == Task.COMPLETED).count();
            if (count == 1000){
                run.compareAndSet(true,false);
                System.out.println("stop:"+run.get());
                closeThread(executorService);
                break;
            }
        }
        List<Double> times = new ArrayList<>();
        for (int i = 0; i < AGVFactory.NUM; i++) {
            double time = agvs.get(i).getChargeTime() + agvs.get(i).getWaitTime() + agvs.get(i).getWorkTime();
            times.add(time);
            System.out.printf("%d号AGV完成任务数：%d\n",agvs.get(i).getId(),agvs.get(i).getCompletedTask());
            System.out.printf("%d号AGV行走距离(km)：%f\n",agvs.get(i).getId(),agvs.get(i).getDistance());
            System.out.printf("%d号AGV等待时间(h)：%f\n",agvs.get(i).getId(),agvs.get(i).getWaitTime());
            System.out.printf("%d号AGV充电时间(h)：%f\n",agvs.get(i).getId(),agvs.get(i).getChargeTime());
            System.out.printf("%d号AGV从开始任务到最后一个任务的时间(h)：%f\n",agvs.get(i).getId(), time);
            System.out.printf("%d号小车当前电量%f\n",agvs.get(i).getId(),agvs.get(i).getCurrentPower());
        }
        OptionalDouble max = times.stream().mapToDouble(p -> p.doubleValue()).max();
        System.out.println("AGV完成所有任务用时(h)："+ max.getAsDouble());
    }

    /**
     * 关闭线程
     * @param executorService
     */
    public static void closeThread(ExecutorService executorService){
        try {
            //System.out.println("尝试关闭ExecutorService");
            executorService.shutdown();
            //指定一段时间温和关闭
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.out.println("任务中断...");
        }
        finally {
            if (!executorService.isTerminated()) {
                System.out.println("结束未完成的任务...");
            }
            executorService.shutdownNow();
            //System.out.println("ExecutorService被停止...");
        }
    }
}
