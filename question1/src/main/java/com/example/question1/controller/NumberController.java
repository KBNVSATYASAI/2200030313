package com.example.question1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.question1.model.baluResponse;
import com.example.question1.service.NumberService;

@RestController
@RequestMapping("/numbers")
public class NumberController {

    @Autowired
    private NumberService numberService;

    @GetMapping
    public baluResponse getresponsenum(@RequestParam("type") String type) {
        return (baluResponse) numberService.processrequest(type);
    }
}
