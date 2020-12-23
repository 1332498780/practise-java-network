package cn.haohan.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ThreadStateTest {

    @Test
    public void testThreadState(){

        Thread thread = new Thread(){
            @Override
            public void run(){
                log.info("this is {} and my state is {}",Thread.currentThread().getName(),Thread.currentThread().getState());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("last this is {} and my state is {}",Thread.currentThread().getName(),Thread.currentThread().getState());
            }
        };

        if(thread.getState() == Thread.State.NEW){
            thread.start();
        }
        log.info("this is {} and my state is {}",Thread.currentThread().getName(),Thread.currentThread().getState());

        try {
            thread.join();
            Object obj = new Object();
            obj.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("end this is {} and my state is {}",thread.getName(),thread.getState());
        log.info("last this is {} and my state is {}",Thread.currentThread().getName(),Thread.currentThread().getState());
    }
}
