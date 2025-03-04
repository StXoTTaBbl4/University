package com.xoeqvdp.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificates")
public class CertificatesController {
    @GetMapping("/ping")
    public String pong(){
        return "pong";
    }
}
