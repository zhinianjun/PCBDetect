package com.example.pcbclient.mq;

import com.alibaba.fastjson.JSONObject;
import com.example.pcbclient.MonitorController;
import com.example.pcbclient.entity.PcbDetectResult;
import lombok.SneakyThrows;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class DetectResultSubThread extends  Thread{
    /**
     * zeroMQ上下文
     */
    private ZMQ.Context context;
    /**
     * zeroMQ套接字
     */
    private ZMQ.Socket socket;
    /**
     * zeroMQ地址
     */
    private String zmqUrl = "tcp://127.0.0.1:7777";
    private MonitorController monitorController;
    public DetectResultSubThread(MonitorController monitorController) {
        this.monitorController = monitorController;
    }

    /**
     * 是否退出标志
     */
    public Boolean isExit = false;
    @SneakyThrows //
    @Override
    public void run() {
        // 创建一个I/O线程的上下文
        context = ZMQ.context(1);
        // 创建一个sub类型的socket，用于接收服务端发送检测的数据
        socket = context.socket(SocketType.SUB);
        // 订阅所有消息
        socket.subscribe("");
        // 与response端建立连接
        socket.connect(zmqUrl);
        while (true) {
            // 如果退出标志为true，则退出循环
            if (isExit) {
                break;
            }
            // 接收response发送回来的数据
            String result = socket.recvStr(ZMQ.NOBLOCK); // 接收response发送回来的数据
            if (result == null) {
                System.out.println("no data");
                sleep(1000);
                continue;
            }
            JSONObject detectionResult = JSONObject.parseObject(result);
            JSONObject data = detectionResult.getJSONObject("data");
            PcbDetectResult pcbDetectResult = data.getJSONObject("json").toJavaObject(PcbDetectResult.class);
            String imagePath = data.getString("image");
            this.monitorController.processDetectResult(imagePath,pcbDetectResult);
        }
        socket.disconnect(zmqUrl);
        socket.close();
    }
}
