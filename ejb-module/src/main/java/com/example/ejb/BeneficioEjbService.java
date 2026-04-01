package com.example.ejb;

import com.example.ejb.domain.Beneficio;
import com.example.ejb.exception.BusinessException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Stateless
@Service
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    /**
     *  MÉTODO PRINCIPAL DE TRANSFERÊNCIA
     * Responsável por realizar a transferência de valor entre dois benefícios.
     * Fluxo:
     * 1. Valida dados de entrada
     * 2. Ordena os IDs para evitar deadlock
     * 3. Busca os benefícios com lock otimista
     * 4. Identifica origem e destino
     * 5. Valida regras de negócio (ativo + saldo)
     * 6. Executa a transferência
     * 7. Força sincronização com o banco (flush)

     *  CORREÇÕES REALIZADAS:
     * - Garantia de consistência com Lock otimista (OPTIMISTIC_FORCE_INCREMENT)
     * - Prevenção de deadlock com ordenação de IDs
     * - Atualização correta de saldo (subtração e soma)
     */
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {

        // 1. Valida entrada
        validarTransferencia(fromId, toId, amount);

        // 2. 🔥 CORREÇÃO: ordenação para evitar deadlock em concorrência
        Long firstId = Math.min(fromId, toId);
        Long secondId = Math.max(fromId, toId);

        // 3. Busca com lock otimista (evita concorrência inconsistente)
        Beneficio first = buscarComLock(firstId);
        Beneficio second = buscarComLock(secondId);

        // 4. Identifica corretamente origem e destino
        Beneficio from = fromId.equals(firstId) ? first : second;
        Beneficio to = toId.equals(firstId) ? first : second;

        // 5. Regras de negócio
        validarBeneficioAtivo(from, "Origem");
        validarBeneficioAtivo(to, "Destino");
        validarSaldo(from, amount);

        // 6. CORREÇÃO CRÍTICA: atualização correta dos saldos
        from.setValor(from.getValor().subtract(amount)); // debita
        to.setValor(to.getValor().add(amount));           // credita

        // 7. CORREÇÃO: flush para garantir persistência imediata e detectar erro de versão
        em.flush();
    }

    /**
     *  Valida os dados básicos da transferência

     * Regras:
     * - IDs não podem ser nulos
     * - Origem e destino não podem ser iguais
     * - Valor deve ser maior que zero
     *  CORREÇÃO:
     * - Centralização das validações (antes estavam espalhadas ou incompletas)
     */
    private void validarTransferencia(Long fromId, Long toId, BigDecimal amount) {

        if (fromId == null || toId == null) {
            throw new BusinessException("Os identificadores de origem e destino são obrigatórios.");
        }

        if (fromId.equals(toId)) {
            throw new BusinessException("Origem e destino devem ser diferentes.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O valor da transferência deve ser maior que zero.");
        }
    }

    /**
     *  Busca o benefício no banco com lock otimista

     *  IMPORTANTE:
     * - Usa OPTIMISTIC_FORCE_INCREMENT → incrementa version automaticamente
     * - Garante controle de concorrência (evita sobrescrita silenciosa)
     *  CORREÇÃO:
     * - Adicionado lock otimista para consistência em cenários concorrentes
     */
    private Beneficio buscarComLock(Long id) {

        Beneficio beneficio = em.find(
                Beneficio.class,
                id,
                LockModeType.OPTIMISTIC_FORCE_INCREMENT
        );

        if (beneficio == null) {
            throw new BusinessException("Benefício com id " + id + " não encontrado.");
        }

        return beneficio;
    }

    /**
     *  Valida se o benefício está ativo

     * Regra:
     * - Apenas benefícios ativos podem participar da transferência
     *  CORREÇÃO:
     * - Uso de Boolean.TRUE.equals para evitar NullPointerException
     */
    private void validarBeneficioAtivo(Beneficio beneficio, String tipo) {

        if (!Boolean.TRUE.equals(beneficio.getAtivo())) {
            throw new BusinessException(tipo + " da transferência está inativo.");
        }
    }

    /**
     *  Valida se há saldo suficiente

     * Regra:
     * - Saldo da origem deve ser >= valor da transferência
     *  CORREÇÃO CRÍTICA:
     * - Uso correto de compareTo (BigDecimal)
     * - Evita erro de lógica em comparação de valores
     */
    private void validarSaldo(Beneficio from, BigDecimal amount) {

        if (from.getValor().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente para realizar a transferência.");
        }
    }
}