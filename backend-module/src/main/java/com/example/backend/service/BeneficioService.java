package com.example.backend.service;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.dto.TransferenciaResponse;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.BeneficioEjbService;
import com.example.ejb.domain.Beneficio;
import com.example.ejb.exception.BusinessException;
import jakarta.persistence.OptimisticLockException;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BeneficioService {

    private final BeneficioRepository repository;
    private final BeneficioEjbService beneficioEjbService;

    public BeneficioService(BeneficioRepository repository, BeneficioEjbService beneficioEjbService) {
        this.repository = repository;
        this.beneficioEjbService = beneficioEjbService;
    }

    @Transactional(readOnly = true)
    public List<BeneficioResponse> list() {
        return repository.findAll().stream().map(BeneficioMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BeneficioResponse findById(Long id) {
        return BeneficioMapper.toResponse(getEntity(id));
    }

    @Transactional
    public BeneficioResponse create(BeneficioRequest request) {
        Beneficio entity = new Beneficio();
        BeneficioMapper.copy(request, entity);
        return BeneficioMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public BeneficioResponse update(Long id, BeneficioRequest request) {
        Beneficio entity = getEntity(id);
        BeneficioMapper.copy(request, entity);
        return BeneficioMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("Benefício com id " + id + " não encontrado.");
        }
        repository.deleteById(id);
    }

    @Transactional
    public TransferenciaResponse transfer(TransferenciaRequest request) {
        try {
            beneficioEjbService.transfer(request.fromId(), request.toId(), request.amount());
            return new TransferenciaResponse(request.fromId(), request.toId(), request.amount(), OffsetDateTime.now(), "Transferência realizada com sucesso.");
        } catch (OptimisticLockException ex) {
            throw new BusinessException("Conflito de concorrência detectado. Tente novamente.");
        }
    }

    private Beneficio getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Benefício com id " + id + " não encontrado."));
    }
}
