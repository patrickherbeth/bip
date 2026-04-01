package com.example.backend.controller;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.dto.TransferenciaResponse;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Benefícios", description = "CRUD de benefícios e operação de transferência")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar benefícios")
    public ResponseEntity<List<BeneficioResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por id")
    public ResponseEntity<BeneficioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar benefício")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Benefício criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<BeneficioResponse> create(@Valid @RequestBody BeneficioRequest request) {
        BeneficioResponse response = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício")
    public ResponseEntity<BeneficioResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody BeneficioRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir benefício")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transferir")
    @Operation(summary = "Transferir saldo entre benefícios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência concluída"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada")
    })
    public ResponseEntity<TransferenciaResponse> transfer(
            @Valid @RequestBody TransferenciaRequest request) {
        return ResponseEntity.ok(service.transfer(request));
    }
}