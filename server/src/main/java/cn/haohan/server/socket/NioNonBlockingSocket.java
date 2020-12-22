package cn.haohan.server.socket;

import cn.haohan.server.util.HttpUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioNonBlockingSocket {

    private static Selector selector;
    private Charset charset = Charset.forName("UTF-8");
    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRequest(String host,int port) throws IOException {

        SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress(host,port);
            socketChannel.configureBlocking(false);
            socketChannel.connect(socketAddress);

            socketChannel.register(selector,
                    SelectionKey.OP_CONNECT
                    |SelectionKey.OP_READ
                    |SelectionKey.OP_WRITE
            );

            while(selector.select(500)>0){
                Set<SelectionKey> selectionKeys =  selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if(selectionKey.isConnectable()){
                        connected(selectionKey);
                    }else if(selectionKey.isWritable()){
                        write(selectionKey);
                    }else if(selectionKey.isReadable()){
                        read(selectionKey);
                    }
                }
            }
        }finally {
            if(socketChannel!=null)
                socketChannel.close();
        }

    }



    private void connected(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if(socketChannel.finishConnect()){
            InetSocketAddress address = (InetSocketAddress)socketChannel.socket().getRemoteSocketAddress();
            System.out.println(String.format("访问地址：%s:%d",address.getHostName(),address.getPort()));
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        InetSocketAddress address = (InetSocketAddress)socketChannel.socket().getRemoteSocketAddress();
        String host = address.getHostName();
        String request = HttpUtil.compositeRequest(host);

        socketChannel.write(charset.encode(request));
        key.interestOps(SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        String receivedData = charset.decode(byteBuffer).toString();
        if("".equals(receivedData)){
            key.cancel();
            socketChannel.close();
            return;
        }
        System.out.println(receivedData);

//        while(socketChannel.read(byteBuffer)!=-1){
//            byteBuffer.flip();
//            String receivedData = charset.decode(byteBuffer).toString();
//            System.out.println(receivedData);
//            byteBuffer.flip();
//            System.out.println("1");
//        }

        key.interestOps(SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        NioNonBlockingSocket nioNonBlockingSocket = new NioNonBlockingSocket();
        nioNonBlockingSocket.startRequest("www.baidu.com",80);
    }

}
