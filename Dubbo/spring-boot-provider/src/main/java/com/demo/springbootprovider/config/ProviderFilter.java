package com.demo.springbootprovider.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;

/**
 * @Author tfan
 * @Description
 * @Date 2025/4/16 18:03
 **/

@Slf4j
@Activate(group = {CommonConstants.PROVIDER}) // 告诉Dubbo这个Filter什么时候启用,这里是Provider端的Filter
public class ProviderFilter implements Filter {

    private static final String TRACE_ID_KEY = "TRACE_ID"; // 日志追踪用的唯一ID键

    /**
     * 服务端处理请求之前的拦截器
     * 每个远程调用进入Filter时，都会执行这个方法
     *
     * @param invoker
     * @param invocation
     * @return
     * @throws RpcException
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // RpcContext: Dubbo的线程上下文，支持在消费者与提供者之间传递参数
        String traceId = RpcContext.getServerAttachment().getAttachment(TRACE_ID_KEY);
        log.info("[Provider] 接收到远程调用，traceId：{}", traceId);
        // MDC 全称是 Mapped Diagnostic Context，它是日志框架（如 SLF4J、Logback、Log4j）提供的一种机制，用于为每个线程保存上下文信息，比如用户 ID、请求 ID、链路追踪 ID（traceId）等
        MDC.put(TRACE_ID_KEY, traceId);
        StopWatch stopWatch = new StopWatch(); //Spring提供的计时器工具，方便记录执行耗时
        stopWatch.start();
        log.info("[Provider] 接收到远程调用，方法：{}", invocation.getMethodName());
        Result result;
        try {
            result = invoker.invoke(invocation); // 真正执行服务方法
        } catch (Exception e) {
            log.error("[Provider] 方法调用异常：{}", e.getMessage(), e);
            throw e; //异常需要向上传递，让框架能够返回异常结果给调用方
        } finally {
            stopWatch.stop();
            log.info("[Provider] 方法调用耗时：{} ms", stopWatch.getTotalTimeMillis());
            MDC.remove(TRACE_ID_KEY);
        }

        return result;
    }
}
