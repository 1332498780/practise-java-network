package cn.haohan.server;

import cn.com.haohan.common.Message;
import cn.com.haohan.common.util.ProtoStuffUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

@Slf4j
public class ChatServer implements Runnable{

    private static Selector selector;
    private int port = 9999;
    private ServerSocketChannel serverSocketChannel;

    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start(int port){
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            SocketAddress address = new InetSocketAddress(port);
            serverSocketChannel.socket().bind(address);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while(selector.select() > 0){
                for(Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();iterator.hasNext();){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(key.isAcceptable()){
                        handleAccepted(key);
                    }else if(key.isReadable()){
                        handleRead(key);
                    }
                }
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }finally {
            if(serverSocketChannel != null){
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void handleAccepted(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();

        InetSocketAddress inetSocketAddress = (InetSocketAddress)channel.getRemoteAddress();
        if(channel.isConnected()){
            log.info("client [{}:{}] connects succeed!",inetSocketAddress.getHostName(),inetSocketAddress.getPort());
            channel.configureBlocking(false);
            channel.register(selector,SelectionKey.OP_READ);
        }else{
            log.info("client [{}:{}] connects failed!",inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        final int readCount = channel.read(byteBuffer);
        if(readCount > 0){
            byteBuffer.flip();
            byte[] array = new byte[readCount];
            byteBuffer.get(array,0,readCount);
            log.info("{}",array);
            Message message = ProtoStuffUtil.deserialize(array, Message.class);
            switch (message.getHeader().getType()){
                case LOGIN:
                    log.info("user [{}] login success!",message.getHeader().getSender());
                    break;
                case NORMAL:
                    break;
                default:
                    log.error("no appropriate type");
            }
        }
    }

    public static void main(String[] args){
        ChatServer server = new ChatServer();
        new Thread(server).start();
    }

    @Override
    public void run() {
        log.info("开始监听 [{}]",this.port);
       start(this.port);
    }
}