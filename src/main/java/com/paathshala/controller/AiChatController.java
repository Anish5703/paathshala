package com.paathshala.controller;

import com.paathshala.service.AiChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/ai/chat")
public class AiChatController {

    private final AiChatService aiChatService;
    private final Logger log;

    public AiChatController(AiChatService aiChatService)
    {
        this.aiChatService = aiChatService;
        this.log = LoggerFactory.getLogger(AiChatController.class);
    }

    @GetMapping("/{message}")
    public ResponseEntity<String> getChatResponse(@PathVariable(name="message")String message)
    {
        log.info("Endpoint hit : {}","/api/ai/chat/"+message);
        String decodedMessage = URLDecoder.decode(message, StandardCharsets.UTF_8);
        String response = aiChatService.getAiResponse(decodedMessage);
        return ResponseEntity.ok(response);
    }


}
