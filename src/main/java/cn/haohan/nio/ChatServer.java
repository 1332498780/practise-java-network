package cn.haohan.nio;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

@Slf4j
public class ChatServer {

    private static Selector selector;

    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start(int port){
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            SocketAddress address = new InetSocketAddress(9000);
            serverSocketChannel.bind(address);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT | SelectionKey.OP_READ);

            for(Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();iterator.hasNext();){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    handleAccepted(key);
                }else if(key.isReadable()){

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
        SocketChannel channel = (SocketChannel) key.channel();
        InetSocketAddress inetSocketAddress = (InetSocketAddress)channel.getRemoteAddress();
        if(channel.isConnected()){
            log.info("client [{}:{}] connects succeed!",inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        }else{
            log.info("client [{}:{}] connects failed!",inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        while(channel.read(byteBuffer)!=-1){
            byteBuffer.flip();
            String res = Charset.forName("UTF-8").decode(byteBuffer).toString();
            System.out.println(res);
        }
    }

}
