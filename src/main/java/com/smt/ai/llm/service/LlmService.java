package com.smt.ai.llm.service;

import com.smt.ai.llm.vo.TranslateVO;
import com.smt.ai.llm.util.PromptTemplateUtil;
import com.smt.ai.llm.util.jdbcTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmService {
    private static final OllamaOptions options = OllamaOptions.builder().temperature(0.3).build();
    private static final String translateServerUrl = "http://localhost:5000/translate";
    private final OllamaChatModel chatModel;
    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;

    public List<String> askCommandQuery(String condition) {
        String prompt = PromptTemplateUtil.getSchemaPrompt(jdbcTemplate, condition);
        log.info("prompt : {}", prompt);
        Prompt promptObj = new Prompt(List.of(new UserMessage(prompt)), options);

        String responsePrompt = chatModel.call(promptObj)
                .getResult()
                .getOutput()
                .getText();

        log.info("responsePrompt {}", responsePrompt);
        responsePrompt = responsePrompt.replace("```", "");
        responsePrompt = responsePrompt.replace("sql", "");
        String substring = responsePrompt.trim().substring(0, 6);
        if (substring.equalsIgnoreCase("update")) {
            int updateIndex = jdbcTemplate.update(responsePrompt);
            return List.of(String.valueOf(updateIndex));
        }

        if(substring.equalsIgnoreCase("select")){
            List<String> queryResultList = jdbcTemplateUtil.executeQueryAndReturnStrings(jdbcTemplate, responsePrompt);
            if(queryResultList.isEmpty()){
                return Arrays.asList("해당 요청은 처리할 수 없습니다.");
            }
            return queryResultList;
        }

        return Arrays.asList("해당 요청은 처리할 수 없습니다.");

    }

    public List<String> askSummaryPEJson(String prompt) {
        UserMessage userMessage = new UserMessage(PromptTemplateUtil.getSummaryPEJsonPrompt(prompt));
        Prompt promptObj = new Prompt(List.of(userMessage), options);
        log.info("promptObj : {}", promptObj);

        String responsePrompt = chatModel.call(promptObj).getResult().getOutput().getText();
        return Collections.singletonList(responsePrompt);
    }

    public String translateEngToKor(String engTextString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestTranslateParam = PromptTemplateUtil.getRequestTranslateParam(engTextString);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestTranslateParam, headers);

        ResponseEntity<TranslateVO> response = restTemplate.postForEntity(translateServerUrl, request, TranslateVO.class);
        return Objects.requireNonNull(response.getBody()).getTranslatedText();
    }

    public String translateKorToEng(String korTextString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestTranslateParam = PromptTemplateUtil.getRequestTranslateParamToEng(korTextString);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestTranslateParam, headers);

        ResponseEntity<TranslateVO> response = restTemplate.postForEntity(translateServerUrl, request, TranslateVO.class);
        return Objects.requireNonNull(response.getBody()).getTranslatedText();
    }

}
