package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.dto.TransferenciaResponse;
import com.example.backend.service.BeneficioService;
import com.example.ejb.exception.BusinessException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/test-reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BeneficioServiceIntegrationTest {

    @Autowired
    private BeneficioService service;


    @Test
    void shouldListSeedData() {
        assertThat(service.list()).hasSize(2);
    }

    @Test
    void shouldCreateBenefit() {
        BeneficioResponse created = service.create(new BeneficioRequest("Novo Benefício", "Criado no teste", new BigDecimal("250.00"), true));
        assertThat(created.id()).isNotNull();
        assertThat(created.nome()).isEqualTo("Novo Benefício");
    }

    @Test
    void shouldUpdateBenefit() {
        Long id = service.list().get(0).id();
        BeneficioResponse updated = service.update(id, new BeneficioRequest("Benefício Atualizado", "Descrição alterada", new BigDecimal("999.99"), false));
        assertThat(updated.nome()).isEqualTo("Benefício Atualizado");
        assertThat(updated.ativo()).isFalse();
    }

    @Test
    void shouldDeleteBenefit() {
        Long id = service.list().get(0).id();
        service.delete(id);
        assertThat(service.list()).hasSize(1);
    }

    @Test
    void shouldTransferSuccessfully() {
        var list = service.list();
        Long fromId = list.get(0).id();
        Long toId = list.get(1).id();
        TransferenciaResponse response = service.transfer(new TransferenciaRequest(fromId, toId, new BigDecimal("200.00")));
        assertThat(response.message()).isEqualTo("Transferência realizada com sucesso.");
        assertThat(service.findById(fromId).valor()).isEqualByComparingTo("800.00");
        assertThat(service.findById(toId).valor()).isEqualByComparingTo("700.00");
    }

    @Test
    void shouldRejectTransferWithInsufficientBalance() {
        var list = service.list();
        Long fromId = list.get(1).id();
        Long toId = list.get(0).id();
        assertThatThrownBy(() -> service.transfer(new TransferenciaRequest(fromId, toId, new BigDecimal("999.00"))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Saldo insuficiente para realizar a transferência.");
    }

    @Test
    void shouldRejectTransferToSameBenefit() {
        Long id = service.list().get(0).id();
        assertThatThrownBy(() -> service.transfer(new TransferenciaRequest(id, id, new BigDecimal("10.00"))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Origem e destino devem ser diferentes.");
    }

    @Test
    void shouldRejectInactiveOrigin() {
        Long fromId = service.list().get(0).id();
        Long toId = service.list().get(1).id();
        service.update(fromId, new BeneficioRequest("Beneficio A", "Descrição A", new BigDecimal("1000.00"), false));
        assertThatThrownBy(() -> service.transfer(new TransferenciaRequest(fromId, toId, new BigDecimal("10.00"))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Origem da transferência está inativo.");
    }

}
