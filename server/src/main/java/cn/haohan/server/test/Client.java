package cn.haohan.server.test;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 9999);
        if(!socket.isConnected()){
           return;
        }
        System.out.println("连接服务器成功！如果想断开连接请输入88（回车）");
        new Thread(new MyClientWriter(socket)).start();

//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
//        try {
//            String msg;//服务器发过来的信息
//            while ((msg = bufferedReader.readLine()) != null) {
//                System.out.println("##服务器：" + msg);
//            }
//        } catch (IOException e) {
//            System.out.println("警告：断开连接！");
//            try {
//                if (!socket.isClosed()) {
//                    socket.close();
//                }
//            } catch (IOException e1) {
//                System.out.println("读取线程：关闭socket出现错误");
//            }
//        }
//        System.exit(1);
    }
}

class MyClientWriter implements Runnable{
    private Socket socket = null;
    private PrintWriter printWriter;
    private Scanner scanner;

    public MyClientWriter(Socket socket) throws IOException {
        this.socket = socket;
        scanner = new Scanner(System.in);
        printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"), true);
    }

    @Override
    public void run() {
        String msg;//要发送的信息
        try {
            while ((msg = scanner.nextLine()) != null) {
//                System.out.println("isClosed="+socket.isClosed());
                if(socket.isClosed()) {
                    break;
                } else {
                    if(msg.equals("88")) {
                        break;
                    }
                }
                printWriter.println(msg);
            }
        } catch (Exception e) {
//            System.out.println("异常关闭客户端（可能是直接关闭退出窗口）");
        }
        System.out.println("写线程准备死亡");
    }
}
