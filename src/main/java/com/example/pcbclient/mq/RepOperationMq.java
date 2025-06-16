package com.example.pcbclient.mq;

import com.alibaba.fastjson.JSONObject;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 * zeromq与服务端rep通信的类
 */
public class RepOperationMq {
    /**
     * zeromq上下文
     */
    static ZMQ.Context context;
    /**
     * zeromq socket
     */
    static ZMQ.Socket socket;
    /**
     * 服务端地址
     */
    static String portUrlStr = "tcp://127.0.0.1:7788";
    /**
     * 发送命令
     * @param data
     * @return
     */
    public static JSONObject sendObjCommand(Object data){
        try {
            // 发送数据, 将对象转换为json字符串
            socket.send(JSONObject.toJSONString(data));
            // 接收数据
            byte[] response = socket.recv();
            // 如果接收到的数据为空，则返回null
            if (response == null)
                return null;
            // 将接收到的数据转换为json对象
            String respStr = new String(response);
            JSONObject object = JSONObject.parseObject(respStr);
            return object;
        }
        catch(ZMQException e){
            // 如果出现异常，则关闭socket，重新创建socket
            socket.close();
            createSocket();
            return null;
        }
    }
    /**
     * 创建一个新的ZMQ socket连接。
     * 该方法初始化一个I/O线程的上下文，并创建一个request类型的socket。
     */
    public static void createSocket(){
        // 创建一个I/O线程的上下文
        context = ZMQ.context(1);
        // 创建一个request类型的socket，这里可以将其简单的理解为客户端，用于向response端发送数据
        socket = context.socket(SocketType.REQ);
        // 设置接收超时时间
        socket.setReceiveTimeOut(60000);
        // 设置发送超时时间
        socket.setSendTimeOut(60000);
        // 设置重连间隔
        socket.setReconnectIVL(1000);
        // 设置最大重连间隔
        socket.setReconnectIVLMax(4000);
        // 设置linger，linger 时间是指当套接字关闭时，尚未发送的消息在套接字缓冲区中保留的时间。
        // 当套接字关闭时，如果还有未发送的消息，setLinger 决定了这些消息的处理方式。
        // 如果 linger 时间大于 0，套接字会等待指定的时间（以毫秒为单位），尝试将未发送的消息发送出去。
        // 如果 linger 时间为 0，套接字会立即关闭，丢弃所有未发送的消息。
        socket.setLinger(0);
        // 与response端建立连接;
        socket.connect(portUrlStr);
    }

    /**
     * 关闭socket
     */
    public static void stop(){
        // 断开连接
        socket.disconnect(portUrlStr);
        // 关闭socket
        socket.close();
    }
}
