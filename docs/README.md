# Documentação da solução

## Arquitetura

A solução foi separada em dois módulos Java:

- **ejb-module**: contém a entidade `Beneficio`, a exceção de negócio e o `BeneficioEjbService`, responsável pela transferência com validações, rollback e locking otimista.
- **backend-module**: expõe a API REST, CRUD completo, integração com o módulo EJB, testes e Swagger.

## Correção do bug

O bug original permitia inconsistências em transferência. A correção aplicada foi:

1. validação de `fromId`, `toId` e `amount`;
2. bloqueio de transferência para o mesmo benefício;
3. validação de existência dos registros;
4. validação de ativo/inativo;
5. validação de saldo suficiente;
6. uso de `@Version` na entidade;
7. uso de `LockModeType.OPTIMISTIC_FORCE_INCREMENT` na leitura para evitar lost update;
8. `@Transactional(rollbackFor = Exception.class)` para garantir rollback.

## API

### Endpoints principais

- `GET /api/v1/beneficios`
- `GET /api/v1/beneficios/{id}`
- `POST /api/v1/beneficios`
- `PUT /api/v1/beneficios/{id}`
- `DELETE /api/v1/beneficios/{id}`
- `POST /api/v1/beneficios/transferencias`

### Swagger

Com a aplicação em execução:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Banco

Os scripts oficiais do desafio continuam em `db/schema.sql` e `db/seed.sql`.

Para facilitar execução local e testes, o backend também possui `schema.sql` e `data.sql` internos para H2 em memória.

## Frontend

A pasta `frontend/` contém uma aplicação Angular standalone com listagem, cadastro, edição, exclusão e transferência.
