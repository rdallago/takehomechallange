package com.challange.takehomechallange.support;

import org.springframework.beans.factory.annotation.Value;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // usa takehome_test_db, nunca la BD de desarrollo
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    // Antes de cada test la BD arranca vacia, asi ningun test depende de otro
    @BeforeEach
    void cleanDatabase() {
        // Seguro anti-desastres: nunca truncar si no es la BD de test
        if (datasourceUrl == null || !datasourceUrl.contains("test")) {
            throw new IllegalStateException("Los tests de integracion deben usar una BD de test: " + datasourceUrl);
        }
        
        jdbcTemplate.execute("TRUNCATE TABLE users, notifications, notification_deliveries CASCADE");
    }
}
