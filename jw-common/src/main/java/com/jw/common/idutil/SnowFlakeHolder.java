package com.jw.common.idutil;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class SnowFlakeHolder {
    private static long workerId = 0;
    private static long datacenterId = 1;
    private static Snowflake snowflake = IdUtil.createSnowflake(workerId,datacenterId);

    private SnowFlakeHolder(){}

    public static  String generateId(){
       return String.valueOf(snowflake.nextId());
    }
}
