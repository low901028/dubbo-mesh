package com.alibaba.dubbo.performance.demo.consumer;

import okhttp3.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class HelloController {

    private OkHttpClient client;

    @PostConstruct
    public void init() {

        // 使用100个连接，默认是5个。
        // okhttp使用http 1.1，默认打开keep-alive
        ConnectionPool pool = new ConnectionPool(100, 5L, TimeUnit.MINUTES);

        client = new OkHttpClient.Builder()
                .connectionPool(pool)
                .connectTimeout(60, TimeUnit.SECONDS)       //设置连接超时
                .readTimeout(60, TimeUnit.SECONDS)          //不考虑超时
                .writeTimeout(60, TimeUnit.SECONDS)          //不考虑超时
                .retryOnConnectionFailure(true)
                .build();
    }

    Random r = new Random(1);

    @RequestMapping(value = "/invoke")
    public ResponseEntity invoke() throws Exception {
        String str = RandomStringUtils.random(r.nextInt(1024), true, true);

        String url = "http://127.0.0.1:20000";

        RequestBody formBody = new FormBody.Builder()
                .add("interface","com.alibaba.dubbo.performance.demo.provider.IHelloService")
                .add("method","hash")
                .add("parameterTypesString","Ljava/lang/String;")
                .add("parameter",str)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String result = response.body().string();

            // 检查返回值是否正确,如果不正确返回500。有以下原因可能导致返回值不对:
            // 1. agent解析dubbo返回数据不对
            // 2. agent没有把request和dubbo的response对应起来
            if (!String.valueOf(str.hashCode()).equals(result)){
                return new ResponseEntity("ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity("OK", HttpStatus.OK);
        }
    }
}
