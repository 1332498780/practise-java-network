package cn.com.haohan.common;

import cn.com.haohan.common.enumation.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * Created by SinjinSong on 2017/5/23.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageHeader {
    private String sender;
    private String receiver;
    private MessageType type;
    private Long timestamp;
}
