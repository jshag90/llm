package com.smt.ai.llm.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TranslateVO {
    List<String> alternatives;
    String translatedText;
}
