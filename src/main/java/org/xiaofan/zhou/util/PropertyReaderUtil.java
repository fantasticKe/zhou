package org.xiaofan.zhou.util;

import com.alibaba.fastjson.JSONObject;
import org.ho.yaml.Yaml;

import java.io.*;
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
    private static JSONObject jsonObject = new JSONObject();

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

    /**
     * 读取yml配置文件
     * @return
     */
    public static JSONObject readYml(){
        try {
            File path = new File(System.getProperty("user.dir")+"/src/main/resources");
            File file = new File(path,"property.yml");
            InputStream inputStream = new FileInputStream(file);
            jsonObject = Yaml.loadType(inputStream,JSONObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static void main(String[] args) {
        JSONObject jsonObject = PropertyReaderUtil.readYml();
        System.out.println(jsonObject.getJSONObject("distanceOfBridge").get("11"));
    }

}
