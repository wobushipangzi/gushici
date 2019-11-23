package com.gushici.controller.user;

import com.gushici.service.product.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping(value = "/hahaha")
    public String test() throws ExecutionException, InterruptedException {
        Future<String> name = testService.getName();


        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.submit(new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        }));

        System.out.println("liujie");
        String s = name.get();

        System.out.println(s);




        return "";
    }

}
