package org.xiaofan.zhou.vo.factory;

import jdk.internal.dynalink.linker.LinkerServices;
import org.xiaofan.zhou.util.RandomUtil;
import org.xiaofan.zhou.vo.Task;

import java.util.ArrayList;
import java.util.List;
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
}
