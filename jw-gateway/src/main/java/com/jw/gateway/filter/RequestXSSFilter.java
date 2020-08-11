package com.jw.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description:  防止XSS攻击处理
 * @date: 2020/8/11
 * @author: jinwei
 */
@Slf4j
public class RequestXSSFilter implements GlobalFilter, Ordered {
    private static int REQUEST_PARAM_COUNT = 5000;
    private static int REQUEST_PARAM_JSONCOUNT = 10000;
    //白名单
    private static final ArrayList<String> whiteList = new ArrayList<>();

    static {
        //添加可能会出现“< 参数的请求”
        whiteList.add("ukey/update");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String[] requestURI = request.getURI().getPath().split("\\?");
        String url = requestURI[0].endsWith("/") ? requestURI[0].substring(0, requestURI.length) : requestURI[0];
        if (whiteList.contains(url)) {
            chain.filter(exchange);
        }

        //拦截该url并进行xss过滤
        String contentType = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if (contentType != null) {
            String type = MediaType.valueOf(contentType).getSubtype();
            if ((request instanceof ServerWebExchange) && MediaType.APPLICATION_JSON.getSubtype().equals(type)) {
                String body = resolveBodyFromRequest(request);
                if (body.matches(".*<[A-Za-z]+.*") || body.length() > REQUEST_PARAM_JSONCOUNT) {
                    restructuReresponse(response);
                }
            }
        }
        //处理get 请求传参数
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        queryParams.keySet().stream().map(queryParams::get).forEach(strs -> {
            for (int i = 0; i < strs.size(); i++) {
                try {
                    if (strs.get(i).matches(".*<[A-Za-z]+.*") || strs.size() > REQUEST_PARAM_COUNT) {
                        restructuReresponse(response);
                    }
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        });
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -21;
    }

    /**
     * 重构响应数据
     * @param response
     * @return
     */
    private Mono<Void> restructuReresponse(ServerHttpResponse response) {
        JSONObject message = new JSONObject();
        message.put("status", -1);
        message.put("data", "非法请求，已经被拦截");
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }
}
