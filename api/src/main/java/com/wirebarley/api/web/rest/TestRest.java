package com.wirebarley.api.web.rest;

import com.wirebarley.core.model.TestView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestRest {

    @GetMapping("/1")
    public ResponseEntity<?> test(){
        TestView testView = new TestView();
        testView.setUrl("url");
        testView.setName("name");
        return ResponseEntity.ok(testView);
    }

}
