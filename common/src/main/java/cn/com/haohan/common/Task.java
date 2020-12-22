package cn.com.haohan.common;

import cn.com.haohan.common.enumation.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.channels.SocketChannel;

/**
 * Created by SinjinSong on 2017/5/23.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private SocketChannel receiver;
    private TaskType type;
    private String desc;
    private Message message;
}
