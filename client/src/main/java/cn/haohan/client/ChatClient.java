package cn.haohan.client;

import cn.com.haohan.common.Message;
import cn.com.haohan.common.MessageHeader;
import cn.com.haohan.common.enumation.MessageType;
import cn.com.haohan.common.util.ProtoStuffUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

@Slf4j
public class ChatClient implements Runnable {

    private static  Selector selector;
    private SocketChannel socketChannel;
    private Charset charset = Charset.forName("UTF-8");
    private String username = "张三";
    private Thread writeThread;


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
                    }else if(selectionKey.isReadable()){
                        handleRead();
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
                socketChannel.register(selector,SelectionKey.OP_READ);

                //开启写线程
                startToWrite();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleWrite(){
        Callable<String> callable = new Callable() {
            @Override
            public Object call() throws Exception {
                Scanner scanner = new Scanner(System.in);
                String inputStr = scanner.next();
                return inputStr;
            }
        };
        FutureTask<String> futureTask = new FutureTask<String>(callable);
        if(this.writeThread==null){
            this.writeThread = new Thread(futureTask);
            this.writeThread.setName("writeThread");
        }
        log.info("{}'s name is {}",this.writeThread.getName(),this.writeThread.getState());
        if(this.writeThread.getState() == Thread.State.NEW || this.writeThread.getState() == Thread.State.TERMINATED){
            this.writeThread.start();
        }

        if(futureTask.isDone()){
            MessageHeader header = null;
            String inputStr = "";
            try {
                inputStr = futureTask.get();
                header = handleInput(inputStr);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(header!=null){
                try {
                    Message message = new Message(header ,inputStr.substring(inputStr.indexOf(":")+1).getBytes("UTF-8"));
                    ByteBuffer byteBuffer = ByteBuffer.wrap(ProtoStuffUtil.serialize(message));
                    socketChannel.write(byteBuffer);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startToWrite(){
        if(writeThread == null){
            writeThread = new Thread(){
                @Override
                public void run() {
                    while (true) {
                        Scanner scanner = new Scanner(System.in);
                        String inputStr = scanner.next();

                        MessageHeader header = handleInput(inputStr);
                        if(header!=null){
                            String msg = inputStr.indexOf(":") >= 0 ? inputStr.substring(inputStr.indexOf(":")+1) : "";
                            try {
                                Message message = new Message(header ,msg.getBytes("UTF-8"));
                                ByteBuffer byteBuffer = ByteBuffer.wrap(ProtoStuffUtil.serialize(message));
                                socketChannel.write(byteBuffer);

                                //后序处理
                                processSomething(header);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            writeThread.setName("writeThread");
        }
        writeThread.start();
    }

    private void processSomething(MessageHeader header){
        switch (header.getType()){
            case LOGOUT:
                //关闭写线程
                this.writeThread.interrupt();
                //关闭socketchannel
                try {
                    this.socketChannel.close();
                    selector.close();
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }

    private void handleRead(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        try {
            int readCount = socketChannel.read(byteBuffer);
            byteBuffer.flip();
            if(readCount>0){
                byte[] bytes = new byte[readCount];
                byteBuffer.get(bytes,0,readCount);
                Message message = ProtoStuffUtil.deserialize(bytes,Message.class);
                MessageHeader header = message.getHeader();
                log.info("接收到[ {} ]发送的消息：{}",header.getReceiver(),new String(message.getBody(),"UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MessageHeader handleInput(String inputStr){
        MessageHeader header = null;
        if(!inputStr.trim().equals("")){
            if(inputStr.startsWith("@@")){
                //群聊
            }else if(inputStr.startsWith("@")){
                //单聊
                String receiver = inputStr.substring(inputStr.indexOf("@")+1,inputStr.indexOf(":"));
                if(receiver.length() > 0){
                    header = MessageHeader.builder()
                            .sender(this.username)
                            .receiver(receiver)
                            .timestamp(System.currentTimeMillis())
                            .type(MessageType.NORMAL)
                            .build();
                }
            }else if(inputStr.toLowerCase().equals("bye")){
                //登出
                header = MessageHeader.builder()
                        .sender(this.username)
                        .timestamp(System.currentTimeMillis())
                        .type(MessageType.LOGOUT)
                        .build();
            }
        }
        return header;
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
