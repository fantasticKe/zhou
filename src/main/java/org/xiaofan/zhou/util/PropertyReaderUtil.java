package org.xiaofan.zhou.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author maokeluo
 * @desc
 * @create 18-3-13
 */
public class PropertyReaderUtil {

    public static Map<String,String> map = new HashMap<>();

   /**
    * @desc 加载指定配置文件
    * @author maokeluo
    * @methodName readPropertyFile
    * @param  fileName
    * @create 18-3-13
    * @return java.util.Map<java.lang.String,java.lang.String>
    */
    public Map<String, String> readPropertyFile(String fileName) {
        Properties pro = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            pro.load(in);
            Iterator<String> iterator = pro.stringPropertyNames().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = pro.getProperty(key);
                map.put(key, value);
            }
            in.close();
        } catch (IOException e) {
            System.out.printf("加载配置文件出错%s",e.getMessage());
        }
        return map;
    }
}
