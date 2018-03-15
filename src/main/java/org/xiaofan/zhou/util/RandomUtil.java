package org.xiaofan.zhou.util;

import java.util.Random;

/**
 * 随机数工具类
 */
public class RandomUtil {

    /**
     * @desc 指定范围内生成随机数
     * @author maokeluo
     * @methodName generateRandomNumbersByRang
     * @param  min 最小值, max 最大值
     * @create 18-03-15
     * @return int
     */
    public static int randomNumber(int min, int max){
        int minNum = min;
        int maxNum = max;
        if (min > max){
            maxNum = min;
            minNum = max;
        }
        Random random = new Random();
        int randomNumber = random.nextInt(maxNum)%(maxNum-minNum+1) + minNum;
        return randomNumber;
    }
}
