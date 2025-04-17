package com.smt.ai.llm.controller;

import com.smt.ai.llm.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/llm")
@RequiredArgsConstructor
@Slf4j
public class LlmController {

    private final LlmService llmService;

    @GetMapping("/ask")
    public List<String> askCommandQuery(@RequestParam String prompt){
        return llmService.askCommandQuery(prompt);
    }


    @PostMapping("/ask")
    public String askSummaryPEJson(@RequestBody String prompt){
        List<String> answerEngList = llmService.askSummaryPEJson(prompt);
        log.info(answerEngList.get(0));
        return llmService.translateEngToKor(answerEngList.get(0));
    }
}
