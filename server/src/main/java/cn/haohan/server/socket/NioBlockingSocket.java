package cn.haohan.server.socket;

import cn.haohan.server.util.HttpUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class NioBlockingSocket {

    public void startRequest(String host,int port) throws IOException {
        SocketChannel socketChannel = null;
        StringBuilder sb = new StringBuilder();
        try {
            socketChannel = SocketChannel.open();
            Socket socket = socketChannel.socket();
            SocketAddress address = new InetSocketAddress(host,port);

            socketChannel.configureBlocking(true);
            socketChannel.connect(address);
            String requestContent = HttpUtil.compositeRequest(host);
            PrintWriter writer = wrapOutputStream(socket);
            writer.write(requestContent);
            writer.flush();
            //如果不写这个，while循环后和while循环里的内容不会执行。因为，服务端依然在等待客户端发送内容，没有到达服务端输出流的结尾
            socket.shutdownOutput();

            BufferedReader bufferedReader = wrapIntputStream(socket);
            String msg = null;
            int count = 0;
            while ((msg = bufferedReader.readLine()) != null) {
                sb.append(msg);
            }
            System.out.println(sb.toString());
        }finally {
            if(socketChannel != null){
                socketChannel.close();
            }
        }
    }

    private PrintWriter wrapOutputStream(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream);
    }
    private BufferedReader wrapIntputStream(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    public static void main(String[] args) throws IOException {
        NioBlockingSocket nioBlockingSocket = new NioBlockingSocket();
        nioBlockingSocket.startRequest("www.baidu.com",80);
    }

}
