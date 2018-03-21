package org.xiaofan.zhou.vo.factory;

import com.alibaba.fastjson.JSONObject;
import org.xiaofan.zhou.util.PropertyReaderUtil;
import org.xiaofan.zhou.util.RandomUtil;
import org.xiaofan.zhou.vo.AGV;
import org.xiaofan.zhou.vo.AShore;
import org.xiaofan.zhou.vo.Task;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 任务工厂
 */
public class TaskFactory {

    private static TaskFactory taskFactory = new TaskFactory();

    private TaskFactory(){}

    public static TaskFactory getTaskFactory(){
        return taskFactory;
    }

    private Task createTask(int id){
        return new Task(id);
    }

    /**所有任务**/
    public static List<Task> tasks = new ArrayList<>();

    public static List<Integer> randoms = new ArrayList<>();
    /**
     * 获取一组1-3之间的随机数
     * @param num 随机数的个数
     * @return
     */
    private static List<Integer> getRandom(int num){
        IntStream.range(0,num).forEach(p->randoms.add(RandomUtil.randomNumber(1,3)));
        return randoms;
    }

    /**
     * 批量创建任务
     * @param count
     * @return
     */
    public List<Task> batchCreateTask(int count){
        getRandom(count);
        IntStream.range(1,count+1).forEach(p->{
            Task task = createTask(p);
            task.setState(Task.NOT_COMPLETE);
            task.setDegree(randoms.get(p-1));
            tasks.add(task);
        });
        return tasks;
    }

    /**
     * @desc 获取未完成任务
     * @author maokeluo
     * @methodName getNotCompleteTask
     * @create 18-3-15
     * @return java.util.List<org.xiaofan.zhou.vo.Task>
     */
    public Task accessTask(AGV agv){
        List<Task> collect = new ArrayList<>();
        synchronized (TaskFactory.class) {
            System.out.println(Thread.currentThread().getName() + "拿到资源");
            collect = tasks.stream()
                    .filter(p -> Task.NOT_COMPLETE == p.getState())
                    .collect(Collectors.toList());

            List<AShore> shores = AShoreFactory.shores;
            JSONObject distanceOfBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
            //以任务的id为key，加权值为value
            Map<Integer, Double> weightedMap = new HashMap<>();
            Task task = null;
            if (collect.size() > 0) {
                for (AShore ashore : shores) {
                    String distanceKey = ashore.getId() + String.valueOf(agv.getLocation().getId());
                    Double distance = Double.valueOf(distanceOfBridge.get(distanceKey).toString());
                    for (Task task1 : collect) {
                        double weighted = 0.4 * distance + 0.6 * task1.getDegree();
                        weightedMap.put(task1.getId(), weighted);
                    }
                }
                Map.Entry<Integer, Double> entry = weightedMap.entrySet().stream()
                        .min(Comparator.comparing(Map.Entry::getValue))
                        .get();
                System.out.printf("%d号AGV接取到任务%d \n", agv.getId(), entry.getKey());
                task = getTaskById(entry.getKey());
                task.setState(Task.BE_ACCESS);
                agv.setCurrentTask(task);
            }
            return task;
        }
    }

    /**
     * 根据id获取任务
     * @param id
     * @return
     */
    public static Task getTaskById(int id){
        return tasks.get(id-1);
    }
}
