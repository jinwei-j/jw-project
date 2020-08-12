package com.jw.common.idutil;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * @Description:生产全局唯一Id
 * @Author: jinwei
 * @Date:  2020/8/11
 * @version: 1.0.0
 */
@Slf4j
public class IdGenerateUtils implements IdentifierGenerator {

    public synchronized String snowflakeId(){
        return SnowFlakeHolder.generateId();
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return snowflakeId();
    }

    public static void main(String[] args) {
//        ExecutorService threadPool = Executors.newFixedThreadPool(5);
//        for (int i = 0; i <= 10; i++){
//            threadPool.submit(() -> {
//                System.out.println(SnowFlakeHolder.generateId()+" : ");
//            });
//        }
//        threadPool.shutdown();

    }

}
