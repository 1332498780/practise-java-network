package cn.haohan.test;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class WaitUseTest {
    public static void main(String[] args){

        List<Thread> threads = new ArrayList<>();
        final MyQueue myQueue = new MyQueue();

        //consume
        for(int i=0;i<5;i++){
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        Integer res = myQueue.getElement();
                        log.info("{} consume a element [{}] ",Thread.currentThread().getName(),res);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.setName("男{"+(i+1)+"}号");
            threads.add(thread);
            thread.start();
        }

        //produce
        for(int i=0;i<5;i++){
            Thread thread = new Thread(){
                @Override
                public void run(){
                    myQueue.setElement();
                    log.info("{} produce a element",Thread.currentThread().getName());
                }
            };
            thread.setName("女{"+(i+1)+"}号");
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(1000);
            log.info("end");
            for(Thread th:threads){
                th.interrupt();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static class MyQueue{
        Queue<Integer> queue = new ArrayDeque();
        Random random = new Random();
        public synchronized Integer getElement() throws InterruptedException {
            while(queue.isEmpty()){
                this.wait();
            }
            return queue.remove();
        }

        public synchronized void setElement(){
            queue.add(random.nextInt(10));
            this.notifyAll();
        }
    }
}
