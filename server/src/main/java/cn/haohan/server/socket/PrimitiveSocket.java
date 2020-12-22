package cn.haohan.server.socket;

import cn.haohan.server.util.HttpUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrimitiveSocket {

    public void startRequest(String host,int port) throws IOException {
        Socket socket = null;
        StringBuilder sb = new StringBuilder();
        try {
            socket = new Socket();
            SocketAddress address = new InetSocketAddress(host,port);
            socket.connect(address);
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
            if(socket != null){
                socket.close();
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


    private void threadPoolHandle(){
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        String[] hosts = new String[]{"www.baidu.com","www.taobao.com","www.sogou.com"};
        for(final String host:hosts){
            Thread t = new Thread(){
                @Override
                public void run(){
                    try {
                        startRequest(host,80);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            executorService.submit(t);

            executorService.shutdown();
        }
    }

    private void multiThreadHandle(){
        String[] hosts = new String[]{"www.sogou.com"};
        for(final String host:hosts){
            Thread t = new Thread(){
                @Override
                public void run(){
                    try {
                        startRequest(host,80);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
    }

    private void primitiveHandle() throws IOException {
        startRequest("www.baidu.com",80);
    }

    public static void main(String[] args) throws IOException {
        PrimitiveSocket ps = new PrimitiveSocket();
//        ps.primitiveHandle();
//        ps.multiThreadHandle();
        ps.threadPoolHandle();
    }

}
