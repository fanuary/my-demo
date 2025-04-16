package com.demo.springbootconsumer.config;

import com.demo.springbootinterface.constant.DubboConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;

import java.util.UUID;

/**
 * @Author tfan
 * @Description
 * @Date 2025/4/16 18:29
 **/


@Slf4j
@Activate(group = {CommonConstants.CONSUMER})
public class ConsumerFilter implements Filter {

    private static final String TRACE_ID_KEY = "TRACE_ID";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String traceId = UUID.randomUUID().toString();
        // RpcContext实现上下文通信，这里将traceId设置到RpcContext的附加信息中，传递给provider
        RpcContext.getClientAttachment().setAttachment(TRACE_ID_KEY, traceId);
        MDC.put(TRACE_ID_KEY, traceId);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        log.info("[Consumer] 正在调用远程方法：{}", invocation.getMethodName());

        Result result;
        try {
            result = invoker.invoke(invocation); // 发起远程调用
        } catch (Exception e) {
            log.error("[Consumer] 远程方法调用失败：{}", e.getMessage(), e);
            throw e;
        } finally {
            stopWatch.stop();
            log.info("[Consumer] 远程方法耗时：{} ms", stopWatch.getTotalTimeMillis());
            MDC.remove(TRACE_ID_KEY);
        }

        return result;
    }
}
