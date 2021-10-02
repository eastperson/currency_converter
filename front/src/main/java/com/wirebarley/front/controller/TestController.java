package com.wirebarley.front.controller;

import com.wirebarley.core.model.TestView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/home")
    public String test(Model model) {
        TestView testView = new TestView();
        testView.setName("name");
        testView.setUrl("url");
        model.addAttribute(testView);
        return "index";
    }
}
