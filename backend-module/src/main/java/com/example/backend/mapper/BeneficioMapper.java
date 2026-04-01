package com.example.backend.mapper;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.ejb.domain.Beneficio;

public final class BeneficioMapper {
    private BeneficioMapper() {
    }

    public static BeneficioResponse toResponse(Beneficio entity) {
        return new BeneficioResponse(entity.getId(), entity.getNome(), entity.getDescricao(), entity.getValor(), entity.getAtivo(), entity.getVersion());
    }

    public static void copy(BeneficioRequest request, Beneficio entity) {
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        entity.setValor(request.valor());
        entity.setAtivo(request.ativo());
    }
}
