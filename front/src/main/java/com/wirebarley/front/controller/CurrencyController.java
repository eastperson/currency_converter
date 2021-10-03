package com.wirebarley.front.controller;

import com.wirebarley.core.model.TestView;
import com.wirebarley.core.properties.CommonProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CommonProperties commonProperties;

    @GetMapping("")
    public String currency(Model model) {
        model.addAttribute("host",commonProperties.getHost());
        return "currency";
    }
}
