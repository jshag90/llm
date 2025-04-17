package com.smt.ai.llm.controller;

import com.smt.ai.llm.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/llm")
@RequiredArgsConstructor
@Slf4j
public class LlmController {

    private final LlmService llmService;

    @GetMapping("/ask")
    public List<String> askCommandQuery(@RequestParam String prompt) {
        try {
            return llmService.askCommandQuery(prompt);
        } catch (Exception e) {
            return List.of("죄송합니다. 해당 요청은 처리할 수 없습니다.");
        }
    }


    @PostMapping("/ask")
    public String askSummaryPEJson(@RequestBody String prompt) {
        List<String> answerEngList = llmService.askSummaryPEJson(prompt);
        log.info(answerEngList.get(0));
        return llmService.translateEngToKor(answerEngList.get(0));
    }
}
