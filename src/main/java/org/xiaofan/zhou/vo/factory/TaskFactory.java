package org.xiaofan.zhou.vo.factory;

import org.xiaofan.zhou.util.RandomUtil;
import org.xiaofan.zhou.vo.Task;

import java.util.ArrayList;
import java.util.List;
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
    /**未完成任务**/
    public static List<Task> notCompleteTask = new ArrayList<>();

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
     * @desc 获取未完成renw
     * @author maokeluo
     * @methodName getNotCompleteTask       
     * @create 18-3-15
     * @return java.util.List<org.xiaofan.zhou.vo.Task>
     */
    public List<Task> getNotCompleteTask(){
        return tasks.stream()
                .filter(p->Task.NOT_COMPLETE.equals(p.getDegree()))
                .collect(Collectors.toList());
    }

}
