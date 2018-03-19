package org.xiaofan.zhou.vo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author maokeluo
 * @desc 任务类
 * @create 18-3-13
 */
@Data
public class Task {

    /**未完成**/
    public static final AtomicInteger NOT_COMPLETE = new AtomicInteger(0);
    /**完成**/
    public static final AtomicInteger COMPLETED = new AtomicInteger(1);
    /**被接取**/
    public static final AtomicInteger BE_ACCESS = new AtomicInteger(2);

    /** id **/
    private int id;

    /** 完成时间 **/
    private double completedTime;

    /** 紧急程度 **/
    private int degree;

    /**任务状态 **/
    private AtomicInteger state;

    private Task(){}

    public Task(int id){
        this.id = id;
    }

}

