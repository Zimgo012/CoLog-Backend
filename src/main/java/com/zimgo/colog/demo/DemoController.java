package com.zimgo.colog.demo;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class DemoController {

    private final DemoState demoState;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public DemoController(DemoState demoState, SimpMessagingTemplate simpMessagingTemplate){
        this.demoState = demoState;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/demo")
    public void demo(byte[] yjs){
        demoState.setYjsDemoUpdate(yjs);

        simpMessagingTemplate.convertAndSend("/demo", yjs);
    }
}
