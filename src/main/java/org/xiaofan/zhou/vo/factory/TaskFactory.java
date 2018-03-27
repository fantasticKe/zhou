package org.xiaofan.zhou.vo.factory;

import com.alibaba.fastjson.JSONArray;
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

    public static List<Double> randoms = new ArrayList<>();
    /**
     * 获取一组1-3之间的随机数
     * @param num 随机数的个数
     * @return
     */
    /*private static List<Integer> getRandom(int num){
        IntStream.range(0,num).forEach(p->randoms.add(RandomUtil.randomNumber(1,3)));
        return randoms;
    }

    public static void main(String[] args) {
        List<Integer> random = getRandom(1000);
        for (int i = 0; i < random.size(); i++) {
            System.out.println(random.get(i));
        }
    }*/

    /**
     * 批量创建任务
     * @param count
     * @return
     */
    public List<Task> batchCreateTask(int count){
        //getRandom(count);
        List<Object> random = PropertyReaderUtil.readYml().getJSONArray("randoms").subList(0, 1000);
        random.forEach(p-> randoms.add(Double.valueOf(p.toString())));
        IntStream.range(1,count+1).forEach(p->{
            Task task = createTask(p);
            task.setState(Task.NOT_COMPLETE);
            task.setDegree(TaskFactory.randoms.get(p-1));
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
        synchronized (TaskFactory.class) {
            List<AShore> shores = AShoreFactory.shores;
            JSONObject distanceOfBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
            //以任务的id为key，加权值为value
            Map<String, Double> weightedMap = new HashMap<>();
            for (AShore ashore : shores) {
                String distanceKey = ashore.getId() + String.valueOf(agv.getLocation().getId());
                Double distance = Double.valueOf(distanceOfBridge.get(distanceKey).toString());
                List<Task> taskList = new ArrayList<>();
                ashore.getTasks().entrySet()
                        .forEach(p->taskList.addAll(p.getValue().stream()
                                .filter(q->q.getState() == Task.NOT_COMPLETE)
                                .collect(Collectors.toList())));
                for (Task task1 : taskList) {
                    double weighted = 1.3 * distance + 0.7 * task1.getDegree();
                    weightedMap.put(String.valueOf(ashore.getId())+":"+task1.getId(), weighted);
                    //System.out.printf("%d 号AGV执行任务 %d 的权重为:%f  ",agv.getId(),task1.getId(),weighted);
                }

            }
            Map.Entry<String, Double> entry = weightedMap.entrySet().stream()
                    .min(Comparator.comparing(Map.Entry::getValue))
                    .get();
            System.out.printf("任务id:%s, 任务权重: %f \n",entry.getKey().split(":")[1],entry.getValue());
            int taskId = Integer.parseInt(entry.getKey().split(":")[1]);
            System.out.printf("%d号AGV接取到任务%d \n", agv.getId(), taskId);
            Task task = getTaskById(taskId);
            Map<String, AShore> aShoreByTask = AShoreFactory.getAShoreByTask(task);
            Map<String, Double> map = consumptionOfRunTask(agv,aShoreByTask);

            Double noLoadPower = map.get("noLoadPower");
            Double loadPower = map.get("loadPower");
            double soc = agv.getCurrentPower() - noLoadPower - loadPower;
            if (soc >= 10){
                task.setState(Task.BE_ACCESS);
                agv.setCurrentTask(task);
                agv.setState(AGV.WORK);
                agv.run();
            }else {
                task.setState(Task.NOT_COMPLETE);
                agv.setState(AGV.CHARGE);
                agv.charge();
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

    /**
     * @desc 执行任务AGV改变量
     * @author maokeluo
     * @methodName consumptionOfRunTask
     * @param  agv aShoreMap
     * @create 18-3-19
     * @return java.util.Map<java.lang.String,java.lang.Double>
     */
    public static Map<String,Double> consumptionOfRunTask(AGV agv, Map<String,AShore> aShoreMap){
        JSONObject distanceOfBridge = PropertyReaderUtil.readYml().getJSONObject("distanceOfBridge");
        Map<String,Double> map = new HashMap<>();
        AShore aShore = null;
        for (Map.Entry<String, AShore> aShoreEntry : aShoreMap.entrySet()){
            aShore = aShoreEntry.getValue();
        }

        //空载行程
        String noLoaddistanceKey = String.valueOf(aShore.getId()) + agv.getLocation().getId();
        double noLoadDistance = Double.valueOf(distanceOfBridge.get(noLoaddistanceKey).toString());
        //空载电量消耗
        double noLoadPower = noLoadDistance / agv.getNoLoadSpeed() * agv.getNoLoadConsumptionSpeed();
        //负载行程
        String loaddistanceKey = aShoreMap.keySet().toArray()[0].toString();
        double loadDistance = Double.valueOf(distanceOfBridge.get(loaddistanceKey).toString());
        //负载电量消耗
        double loadPower = loadDistance / agv.getLoadSpeed() * agv.getLoadConsumptionSpeed();

        map.put("noLoadDistance",noLoadDistance);
        map.put("noLoadPower",noLoadPower);
        map.put("loadDistance",loadDistance);
        map.put("loadPower",loadPower);

        return map;
    }
}
