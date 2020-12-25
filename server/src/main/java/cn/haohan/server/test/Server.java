package cn.haohan.server.test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static final int SERVER_PORK = 9999;

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORK);
        System.out.println("正在监听 " + SERVER_PORK + " 端口！");
        Socket socket;
        BufferedReader bufferedReader;
        while (true) {
            socket = serverSocket.accept();
            System.out.println("一客户端连接。");
//            Thread writerThread = new Thread(new MyServerWriter(socket));
//            writerThread.start();

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            try {
                String msg;//客户端发过来的信息
                while ((msg = bufferedReader.readLine()) != null) {
                    System.out.println("##客户端：" + msg);
                }
            } catch (IOException e) {
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e1) {
                    System.out.println("关闭socket出现错误");
                }
            }
            System.out.println("提示：当前客户端已经断开连接，服务器正等待下一个客户端的连接。");
        }
    }
}

class MyServerWriter implements Runnable {
    private Socket socket = null;
    private PrintWriter printWriter;
    private Scanner scanner;

    public MyServerWriter(Socket socket) throws IOException {
        this.socket = socket;
        scanner = new Scanner(System.in);
        printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"), true);
    }

    @Override
    public void run() {
        String msg;//要发送的信息
        try {
            while ((msg = scanner.nextLine()) != null) {
                if (msg.equals("88")) {
                    if (!socket.isClosed()) {
                        System.out.println("服务器手动与客户端断开");
                        socket.close();
                    }
                    break;
                }
                printWriter.println(msg);
            }
        } catch (IOException io) {
            System.out.println("关闭socket出现问题");
        } catch (Exception e) {
            System.out.println("异常关闭客户端（可能是直接关闭退出窗口）");
        }
    }
}
