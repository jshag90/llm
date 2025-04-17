package com.smt.ai.llm.util;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class PromptTemplateUtil {

    public static String getSummaryPEJsonPrompt(String peJson) {
        return peJson +
                "The following JSON data is data that the machine learning server suspects to be malware. " +
                "The data is extracted from the exe's PE format. " +
                "briefly summarize the JSON data above. " +
                "And Please analyze which part is suspected to be malware.";
    }

    public static String getSchemaPrompt(JdbcTemplate jdbcTemplate, String condition){
        String tableSchemas = getTableSchema(jdbcTemplate, "ds_file_protect_log") + "(데이터 보호/차단 로그 테이블)";
        tableSchemas += getTableSchema(jdbcTemplate, "ds_policy_admin_sys") + "(시스템 정책 테이블)";
        tableSchemas += getTableSchema(jdbcTemplate, "ds_policy_data_protect_log") + "(데이터 정책 로그 테이블)";
        tableSchemas += getTableSchema(jdbcTemplate, "ds_hr_group") + "(인사연동테이블)";
        tableSchemas += getTableSchema(jdbcTemplate, "ds_client") + "(에이전트/클라이언트 테이블)";

        return "You are a SQL expert. "
                + tableSchemas +
                "\nGenerate a SQL query for this request: \"" + condition + "\""
                + ".Return only the SQL query.";
    }

    private static String getTableSchema(JdbcTemplate jdbcTemplate, String tableName) {
        String query = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = ?";

        StringBuilder schemaBuilder = new StringBuilder("Here is the schema of the \"" + tableName + "\" table:\n");
        jdbcTemplate.query(query, new Object[]{tableName}, (rs) -> {
            schemaBuilder.append("- ")
                    .append(rs.getString("COLUMN_NAME"))
                    .append(": ")
                    .append(rs.getString("DATA_TYPE"))
                    .append(", Nullable: ")
                    .append(rs.getString("IS_NULLABLE"))
                    .append(", COLUMN_COMMENT: ")
                    .append(rs.getString("COLUMN_COMMENT"))
                    .append("\n");
        });

        return schemaBuilder.toString();
    }

    public static Map<String, Object> getRequestTranslateParam(String answer){
        Map<String, Object> requestTranslateParam = new HashMap<>();
        requestTranslateParam.put("q", answer);
        requestTranslateParam.put("source", "en");
        requestTranslateParam.put("target", "ko");
        requestTranslateParam.put("format", "text");
        requestTranslateParam.put("alternatives", 0);
        requestTranslateParam.put("api_kye", "");
        return requestTranslateParam;
    }

    public static Map<String, Object> getRequestTranslateParamToEng(String answer){
        Map<String, Object> requestTranslateParam = new HashMap<>();
        requestTranslateParam.put("q", answer);
        requestTranslateParam.put("source", "ko");
        requestTranslateParam.put("target", "en");
        requestTranslateParam.put("format", "text");
        requestTranslateParam.put("alternatives", 0);
        requestTranslateParam.put("api_kye", "");
        return requestTranslateParam;
    }

}
