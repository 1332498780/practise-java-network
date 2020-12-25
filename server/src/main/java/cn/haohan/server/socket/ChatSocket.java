package cn.haohan.server.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class ChatSocket {

    private Thread writeListern;
    private boolean writeListernRunable = true;
    private boolean isRunable = true;
    private Socket socket;
    private PrintWriter printWriter;

    private void startRun(){
        Socket socket = new Socket();
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost",9999);
            socket.connect(inetSocketAddress);
            if(socket.isConnected()){
                this.socket = socket;
//                while(isRunable){
//                    handleRead();
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRead(){
        if(socket.isInputShutdown() || socket.isClosed()){
            isRunable = false;
            return;
        }
        try(InputStream inputStream = socket.getInputStream();) {
            byte[] bytes = new byte[256];
            int readCount = -1;
            StringBuilder builder = new StringBuilder();
            while((readCount = inputStream.read(bytes)) != -1){
                builder.append(new String(bytes,0,readCount,"UTF-8"));
            }
            String hostName = ((InetSocketAddress)socket.getRemoteSocketAddress()).getHostName();
            log.info("client:[{}] send:{}",hostName,builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleWrite(String msg){
        if(socket != null && socket.isConnected()){
            try{
                if(printWriter == null){
                    OutputStream outputStream = socket.getOutputStream();
                    printWriter = new PrintWriter(new OutputStreamWriter(outputStream,"UTF-8"),true);
                }
//                byte[] bytes = msg.getBytes("UTF-8");
//                outputStream.write(bytes);
                printWriter.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startToWrite(){
        Thread thread = new Thread(){
            @Override
            public void run(){
                Scanner scanner = new Scanner(System.in);
                String inputMsg = null;
                while((inputMsg = scanner.nextLine()) != null){
                    handleWrite(inputMsg);
                }
            }
        };
        thread.setName("write-thread");
        this.writeListern = thread;
        thread.start();
    }

    public static void main(String[] args){

        final ChatSocket chatSocket = new ChatSocket();
        chatSocket.startToWrite();
        chatSocket.startRun();
    }


}
