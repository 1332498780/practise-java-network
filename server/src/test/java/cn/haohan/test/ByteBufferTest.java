package cn.haohan.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.ByteBuffer;

@Slf4j
public class ByteBufferTest {

    @Test
    public void test(){

        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put(new byte[]{65,66,67});
        log.info("position:{},limit:{}",byteBuffer.position(),byteBuffer.limit());

        byteBuffer.flip();
        byte aByte = byteBuffer.get();
        log.info("read a byte:{}",(char)aByte);

        byteBuffer.put((byte) 90);
        log.info("position:{},limit:{}",byteBuffer.position(),byteBuffer.limit());
//        byte[] res = new byte[byteBuffer.limit()];
//        byteBuffer.get(res,0,2);
        log.info("byte:{}",byteBuffer.get(1));
    }
}
