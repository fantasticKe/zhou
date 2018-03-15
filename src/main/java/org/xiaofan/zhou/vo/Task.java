package org.xiaofan.zhou.vo;

import lombok.Data;

/**
 * @author maokeluo
 * @desc 任务类
 * @create 18-3-13
 */
@Data
public class Task {

    public static final int NOT_COMPLETE = 0;
    public static final int COMPLETED = 1;
    /** id **/
    private int id;

    /** 完成时间 **/
    private long completedTime;

    /** 紧急程度 **/
    private int degree;

    /**任务状态 **/
    private int state;

    private Task(){}

    public Task(int id){
        this.id = id;
    }

}
enum degree{
    FRIST("最紧急",1), SECOND("较为紧急",2),THRID("一般",3);

    private String level;

    private int value;

    degree(String degreeLevel, int i) {
        this.level = degreeLevel;
        this.value = i;
    }
}
