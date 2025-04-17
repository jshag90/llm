package com.smt.ai.llm.util;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class jdbcTemplateUtil {

    public static List<String> executeQueryAndReturnStrings(JdbcTemplate jdbcTemplate, String sql) {
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            StringBuilder result = new StringBuilder();
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                result.append(resultSet.getString(i));
                if (i < columnCount) {
                    result.append(", ");
                }
            }
            return result.toString();
        });
    }
}
