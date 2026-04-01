package com.example.backend;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/test-reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldListBenefits() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").exists());
    }

    @Test
    void shouldCreateBenefit() throws Exception {
        String json = """
                {
                  "nome":"Benefício MVC",
                  "descricao":"Criado via teste",
                  "valor":123.45,
                  "ativo":true
                }
                """;

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Benefício MVC"));
    }

    @Test
    void shouldValidateCreateRequest() throws Exception {
        String json = """
                {
                  "nome":"",
                  "valor":-1,
                  "ativo":true
                }
                """;

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldUpdateBenefit() throws Exception {
        String json = """
                {
                  "nome":"Benefício Alterado",
                  "descricao":"Atualizado",
                  "valor":555.55,
                  "ativo":true
                }
                """;

        mockMvc.perform(put("/api/v1/beneficios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Benefício Alterado"));
    }

    @Test
    void shouldDeleteBenefit() throws Exception {
        mockMvc.perform(delete("/api/v1/beneficios/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldTransfer() throws Exception {
        String json = """
            {
              "fromId":1,
              "toId":2,
              "amount":100.00
            }
            """;

        mockMvc.perform(post("/api/v1/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Transferência realizada com sucesso."));
    }

    @Test
    void shouldFailTransferSameId() throws Exception {
        String json = """
            {
              "fromId":1,
              "toId":1,
              "amount":100.00
            }
            """;

        mockMvc.perform(post("/api/v1/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()) // 🔥 CORRIGIDO
                .andExpect(jsonPath("$.message")
                        .value("Origem e destino devem ser diferentes."));
    }
}