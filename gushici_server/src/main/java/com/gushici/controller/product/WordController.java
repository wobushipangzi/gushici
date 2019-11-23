package com.gushici.controller.product;


import com.gushici.service.daodejing.ApplyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@CrossOrigin
public class WordController {

    private static Logger logger = LoggerFactory.getLogger(WordController.class);

    @Autowired
    private ApplyUserService applyUserService;

    @RequestMapping("/picture/toText")
    public String index(){
        return "index";
    }

}
