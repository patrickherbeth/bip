# Desafio Fullstack Integrado - Solução Completa

## O que foi entregue

- correção do bug no `BeneficioEjbService`;
- CRUD completo de benefícios;
- endpoint de transferência integrado ao módulo EJB;
- validações de regra de negócio;
- locking otimista com `@Version`;
- rollback transacional;
- testes automatizados;
- documentação Swagger/OpenAPI;
- frontend Angular com consumo da API;
- CI com GitHub Actions.

## Estrutura do projeto

```text
.
├── backend-module
├── db
├── docs
├── ejb-module
├── frontend
└── pom.xml
```

## Requisitos

- Java 17
- Maven 3.9+
- Node 20+ e Angular CLI 17+ para o frontend

## Como executar o backend

```bash
mvn clean verify
mvn -pl backend-module spring-boot:run
```

## Swagger

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/api-docs`

## Exemplos de uso

### Criar benefício

```http
POST /api/v1/beneficios
Content-Type: application/json

{
  "nome": "Auxílio Transporte",
  "descricao": "Benefício mensal",
  "valor": 350.00,
  "ativo": true
}
```

### Transferir saldo

```http
POST /api/v1/beneficios/transferencias
Content-Type: application/json

{
  "fromId": 1,
  "toId": 2,
  "amount": 100.00
}
```

## Regras de negócio aplicadas na transferência

- IDs obrigatórios;
- origem e destino devem ser diferentes;
- valor precisa ser maior que zero;
- benefícios precisam existir;
- benefícios precisam estar ativos;
- origem precisa ter saldo suficiente;
- uso de locking otimista para reduzir inconsistência concorrente;
- rollback em caso de falha.

## Frontend Angular

```bash
cd frontend
npm install
npm start
```
