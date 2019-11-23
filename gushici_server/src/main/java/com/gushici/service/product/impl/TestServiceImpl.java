package com.gushici.service.product.impl;

import com.gushici.service.product.TestService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import sun.awt.SunHints;
import sun.misc.Lock;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@EnableAsync
public class TestServiceImpl implements TestService {


    @Override
    @Async
    public Future<String> getName() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("哈哈哈");


        int i = 10;
        for (int j = 0; j < 10; j++) {
            i++;
        }
        new AtomicInteger(1);

        return new AsyncResult<>(String.valueOf(i));
    }


    public static void main(String[] args) {
        testttt testttt = new testttt();
        testttt.testa();
    }

    public static Lock lock = new Lock();


    private static class testttt{
        public  void testa(){

            try {
                lock.lock();
                System.out.println("进入A");
                testb();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        }

        public  void testb(){
            //Lock lock = new Lock();
            try {
                lock.lock();
                System.out.println("进入b");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        }
    }


}
