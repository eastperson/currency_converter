package com.wirebarley.front.controller;

import com.wirebarley.core.model.TestView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyController {



    @GetMapping("")
    public String currency(Model model) {
        return "currency";
    }
}
