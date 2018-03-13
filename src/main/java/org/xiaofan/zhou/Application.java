package org.xiaofan.zhou;

import org.xiaofan.zhou.util.PropertyReaderUtil;

import java.util.Map;

/**
 * @author maokeluo
 * @desc
 * @create 18-3-13
 */
public class Application {

    public static void main(String[] args) {
        PropertyReaderUtil propertyReaderUtil = new PropertyReaderUtil();
        Map<String, String> map = propertyReaderUtil.readPropertyFile("property.properties");
        System.out.println(map);
    }
}
