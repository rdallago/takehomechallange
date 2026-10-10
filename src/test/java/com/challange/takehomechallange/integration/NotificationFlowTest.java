package com.challange.takehomechallange.integration;

import com.challange.takehomechallange.support.IntegrationTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationFlowTest extends IntegrationTestBase {

    private static final String PUSH_TOKEN = "abcdefghij1234567890xyz";

    @Autowired ObjectMapper objectMapper;
    @Autowired JdbcTemplate jdbc;

    // ---------- helpers ----------

    private String json(Map<String, Object> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private void register(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", password))))
                .andExpect(status().isCreated());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("accessToken").asText();
    }

    private String registerAndLogin(String email) throws Exception {
        register(email, "clave12345");
        return loginAndGetToken(email, "clave12345");
    }

    private MvcResult createNotification(String token, String channel, String recipient, String content)
            throws Exception {
        return mockMvc.perform(post("/api/notifications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "title", "Titulo",
                                "content", content,
                                "channel", channel,
                                "recipient", recipient))))
                .andReturn();
    }

    private String createdId(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private int count(String sql) {
        Integer n = jdbc.queryForObject(sql, Integer.class);
        return n == null ? 0 : n;
    }

    // ---------- autenticacion ----------

    @Test
    void registerTwiceWithSameEmailReturns409() throws Exception {
        register("juan@mail.com", "clave12345");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "juan@mail.com", "password", "clave12345"))))
                .andExpect(status().isConflict());
    }

    @Test
    void registerWithInvalidDataReturns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "no-es-mail", "password", "corta"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        register("juan@mail.com", "clave12345");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "juan@mail.com", "password", "incorrecta1"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithGarbageTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/notifications").header("Authorization", "Bearer basura"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- creacion por canal (Strategy) ----------

    @Test
    void createsEmailNotificationAndRegistersDelivery() throws Exception {
        String token = registerAndLogin("juan@mail.com");

        MvcResult result = createNotification(token, "EMAIL", "alguien@mail.com", "Hola");

        assertEquals(201, result.getResponse().getStatus());
        assertEquals(1, count("SELECT count(*) FROM notifications"));
        assertEquals(1, count(
                "SELECT count(*) FROM notification_deliveries WHERE channel = 'EMAIL' AND status = 'SENT'"));
    }

    @Test
    void createsSmsNotificationAndRegistersDelivery() throws Exception {
        String token = registerAndLogin("juan@mail.com");

        MvcResult result = createNotification(token, "SMS", "+5493794123456", "Mensaje corto");

        assertEquals(201, result.getResponse().getStatus());
        assertEquals(1, count(
                "SELECT count(*) FROM notification_deliveries WHERE channel = 'SMS' AND status = 'SENT'"));
    }

    @Test
    void createsPushNotificationAndRegistersDelivery() throws Exception {
        String token = registerAndLogin("juan@mail.com");

        MvcResult result = createNotification(token, "PUSH", PUSH_TOKEN, "Cuerpo");

        assertEquals(201, result.getResponse().getStatus());
        assertEquals(1, count(
                "SELECT count(*) FROM notification_deliveries WHERE channel = 'PUSH' AND status = 'SENT'"));
    }

    // ---------- validaciones por canal ----------

    @Test
    void smsOver160CharsReturns400AndSavesNothing() throws Exception {
        String token = registerAndLogin("juan@mail.com");

        MvcResult result = createNotification(token, "SMS", "+5493794123456", "a".repeat(161));

        assertEquals(400, result.getResponse().getStatus());
        assertEquals(0, count("SELECT count(*) FROM notifications"));
        assertEquals(0, count("SELECT count(*) FROM notification_deliveries"));
    }

    @Test
    void malformedEmailRecipientReturns400() throws Exception {
        String token = registerAndLogin("juan@mail.com");

        MvcResult result = createNotification(token, "EMAIL", "no-es-un-mail", "Hola");

        assertEquals(400, result.getResponse().getStatus());
        assertEquals(0, count("SELECT count(*) FROM notifications"));
    }

    @Test
    void invalidPushTokenReturns400() throws Exception {
        String token = registerAndLogin("juan@mail.com");

        MvcResult result = createNotification(token, "PUSH", "corto", "Cuerpo");

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    void unknownChannelReturns400() throws Exception {
        String token = registerAndLogin("juan@mail.com");

        MvcResult result = createNotification(token, "FAX", "alguien@mail.com", "Hola");

        assertEquals(400, result.getResponse().getStatus());
    }

    // ---------- CRUD ----------

    @Test
    void listReturnsOnlyOwnNotifications() throws Exception {
        String tokenA = registerAndLogin("a@mail.com");
        String tokenB = registerAndLogin("b@mail.com");
        createNotification(tokenA, "EMAIL", "x@mail.com", "De A");

        mockMvc.perform(get("/api/notifications").header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/notifications").header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updatesOwnNotification() throws Exception {
        String token = registerAndLogin("juan@mail.com");
        String id = createdId(createNotification(token, "EMAIL", "x@mail.com", "Original"));

        mockMvc.perform(put("/api/notifications/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "title", "Editado",
                                "content", "Contenido nuevo",
                                "channel", "EMAIL",
                                "recipient", "y@mail.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Editado"))
                .andExpect(jsonPath("$.recipient").value("y@mail.com"));

        // Editar no reenvia: sigue habiendo un solo envio
        assertEquals(1, count("SELECT count(*) FROM notification_deliveries"));
    }

    @Test
    void deleteRemovesNotificationAndItsDeliveries() throws Exception {
        String token = registerAndLogin("juan@mail.com");
        String id = createdId(createNotification(token, "EMAIL", "x@mail.com", "Hola"));

        mockMvc.perform(delete("/api/notifications/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertEquals(0, count("SELECT count(*) FROM notifications"));
        // Verifica el ON DELETE CASCADE de la migracion V4
        assertEquals(0, count("SELECT count(*) FROM notification_deliveries"));
    }

    // ---------- autorizacion entre usuarios ----------

    @Test
    void otherUserCannotUpdateNotificationAndGets404() throws Exception {
        String owner = registerAndLogin("owner@mail.com");
        String intruder = registerAndLogin("intruder@mail.com");
        String id = createdId(createNotification(owner, "EMAIL", "x@mail.com", "Privada"));

        mockMvc.perform(put("/api/notifications/" + id)
                        .header("Authorization", "Bearer " + intruder)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "title", "Hackeada",
                                "content", "x",
                                "channel", "EMAIL",
                                "recipient", "z@mail.com"))))
                .andExpect(status().isNotFound());

        // La original quedo intacta
        mockMvc.perform(get("/api/notifications").header("Authorization", "Bearer " + owner))
                .andExpect(jsonPath("$[0].title").value("Titulo"));
    }

    @Test
    void otherUserCannotDeleteNotificationAndGets404() throws Exception {
        String owner = registerAndLogin("owner@mail.com");
        String intruder = registerAndLogin("intruder@mail.com");
        String id = createdId(createNotification(owner, "EMAIL", "x@mail.com", "Privada"));

        mockMvc.perform(delete("/api/notifications/" + id)
                        .header("Authorization", "Bearer " + intruder))
                .andExpect(status().isNotFound());

        assertEquals(1, count("SELECT count(*) FROM notifications"));
    }

        // ---------- CORS ----------

    @Test
    void preflightFromAllowedOriginIsAccepted() throws Exception {
        mockMvc.perform(options("/api/notifications")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void preflightFromUnknownOriginIsRejected() throws Exception {
        mockMvc.perform(options("/api/notifications")
                        .header("Origin", "http://sitio-malicioso.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}