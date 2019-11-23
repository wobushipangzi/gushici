package com.gushici.service.product;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

@Service
public interface TestService {

    Future<String> getName();
}
