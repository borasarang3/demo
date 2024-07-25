package com.example.demo.controller;

import com.example.demo.dto.ImgDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Log4j2
public class BoardController {

    @GetMapping("/register")
    public void uploadEx(){

    }

    @PostMapping("/register")
    public void uploadEx(List<String> imgDTOS){

        for (String a : imgDTOS) {
            log.info(a);
//            a.setbno = boarddto.getbno();
        }

    }

}
