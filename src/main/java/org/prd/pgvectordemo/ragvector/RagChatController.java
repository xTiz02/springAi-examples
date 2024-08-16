package org.prd.pgvectordemo.ragvector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagChatController {

    @Autowired
    private RagChatService ragChatService;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return ragChatService.chat(message);
    }
}
