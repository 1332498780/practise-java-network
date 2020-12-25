package cn.haohan.server.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PrimitiveServerSocket {

    private boolean isRunable = true;
    private Map<String,Socket> sockets = new HashMap<>();

    public void startServer(){

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(9999));
            while (isRunable){
                final Socket socket = serverSocket.accept();
                log.info("client [{}] connected",((InetSocketAddress)socket.getRemoteSocketAddress()).getHostString());
//                sockets.put(((InetSocketAddress)socket.getRemoteSocketAddress()).getHostName(),socket);
//                handleRead(socket);
                try{
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
//                    byte[] bytes = new byte[256];
//                    int readCount = -1;
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while((line = bufferedReader.readLine())!= null){
//                        builder.append(new String(bytes,0,readCount,"UTF-8"));
//                        builder.append(line);
                        log.info("client send a msg: {}",line);
                    }
                    String hostName = ((InetSocketAddress)socket.getRemoteSocketAddress()).getHostName();
                    log.info("client:[{}] send:{}",hostName,builder.toString());
                    handleWrite(socket,builder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRead(final Socket socket){
        Thread inputThread = new Thread(){
            @Override
            public void run(){
                try{
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
//                    byte[] bytes = new byte[256];
//                    int readCount = -1;
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while((line = bufferedReader.readLine())!= null){
//                        builder.append(new String(bytes,0,readCount,"UTF-8"));
                        builder.append(line);
                    }
                    String hostName = ((InetSocketAddress)socket.getRemoteSocketAddress()).getHostName();
                    log.info("client:[{}] send:{}",hostName,builder.toString());
                    handleWrite(socket,builder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        inputThread.start();
    }

    private void handleWrite(Socket socket,String msg){
        try{
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        PrimitiveServerSocket main = new PrimitiveServerSocket();
        main.startServer();
    }

}
