package org.springframework.http.server.reactive;

import org.springframework.core.io.buffer.DataBufferFactory;
import reactor.netty.http.server.HttpServerResponse;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-11-26 16:12:16
 */
public class PubicReactorServerHttpResponse extends ReactorServerHttpResponse {

    public PubicReactorServerHttpResponse(HttpServerResponse response, DataBufferFactory bufferFactory) {
        super(response, bufferFactory);
    }

}
