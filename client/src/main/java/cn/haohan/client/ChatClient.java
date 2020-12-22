package cn.haohan.client;

import cn.com.haohan.common.Message;
import cn.com.haohan.common.MessageHeader;
import cn.com.haohan.common.enumation.MessageType;
import cn.com.haohan.common.util.ProtoStuffUtil;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

@Slf4j
public class ChatClient implements Runnable {

    private static  Selector selector;
    private SocketChannel socketChannel;
    private Charset charset = Charset.forName("UTF-8");

    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start(){
        try {
            while (selector.select() > 0) {
                for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isConnectable()) {
                        handleConnected();
                    }else if(selectionKey.isWritable()){

                    }
                }
            }
        }catch (IOException exception){
            exception.printStackTrace();
        }finally {
            try {
                socketChannel.close();
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleConnected(){
      log.info("client connected");
        try {
            if(socketChannel.finishConnect()){
                login();
                socketChannel.register(selector,SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleWrite(){
        Scanner scanner = new Scanner(System.in);
        String inputStr = "";
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        do {
            inputStr = scanner.next();

        }while (!inputStr.equals("bye"));
    }

    private void initNetWork(String host,int port){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host,port));

            socketChannel.register(selector,SelectionKey.OP_CONNECT);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login(){
        String username = "张三";
        String password = "123";
        Message message = new Message(
                MessageHeader.builder()
                        .type(MessageType.LOGIN)
                        .sender(username)
                        .timestamp(System.currentTimeMillis())
                        .build(), password.getBytes(charset));
        ByteBuffer byteBuffer = ByteBuffer.wrap(ProtoStuffUtil.serialize(message));
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        ChatClient chatClient = new ChatClient();
        new Thread(chatClient).start();
    }

    @Override
    public void run() {
        initNetWork("localhost",9999);
    }
}
