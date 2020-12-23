package cn.haohan.test;

public class InteruptTest {

    public static void main(String[] args){

        InteruptTest interuptTest = new InteruptTest();
        interuptTest.run();
        System.out.println("end-------");


//        thread.interrupt();
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private void run(){
        Thread thread1 = new Thread(){
            @Override
            public void run(){
//                for(int i=0;i<1000000;i++){
//                    System.out.println(Thread.currentThread().getName()+i);
//                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.setName("1-1");
        thread1.start();

        Thread thread2 = new Thread(){
            @Override
            public void run(){
//                for(int i=200000;i>0;i--){
//                    System.out.println(Thread.currentThread().getName()+i);
//                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread2.setName("2-1");
        thread2.start();
    }


}
