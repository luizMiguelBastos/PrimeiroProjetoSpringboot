# Task Manager API — Todo List

API REST para gerenciamento de tarefas com autenticação de usuários, construída com **Java 25** e **Spring Boot 4.1**.

**Demo ao vivo:** [https://primeiroprojetospringboot.onrender.com](https://primeiroprojetospringboot.onrender.com)

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Multi--stage-2496ED?logo=docker&logoColor=white)
![H2](https://img.shields.io/badge/H2-Database-darkblue)
![Render](https://img.shields.io/badge/Deploy-Render-46E3B7?logo=render&logoColor=white)

> **Nota sobre a demo:** o serviço roda no plano gratuito do Render e hiberna após um período de inatividade. A **primeira requisição pode levar cerca de 1 a 2 minutos** para responder enquanto a instância inicializa — as seguintes respondem normalmente.

---

## Sobre o projeto

Aplicação back-end que permite o cadastro de usuários e o gerenciamento de tarefas pessoais (criação, listagem e edição), garantindo que **cada usuário só possa visualizar e alterar as próprias tarefas**.

O projeto foi desenvolvido durante o **Minicurso de Java da Rocketseat**, como estudo prático dos fundamentos do ecossistema Spring. A partir da base do curso, o código foi evoluído com melhorias próprias, como a **containerização com Docker multi-stage build** e o **deploy em produção no Render**.

Conceitos aplicados na prática:

- **Autenticação Basic Auth** implementada manualmente com um `Filter` customizado (`OncePerRequestFilter`), sem depender do Spring Security — para demonstrar entendimento do ciclo de vida de uma requisição HTTP
- **Hash de senhas com BCrypt** — nenhuma senha é armazenada em texto plano
- **Persistência com Spring Data JPA** e banco H2 em memória
- **Validação de ownership** — usuários não conseguem editar tarefas de outros usuários
- **Update parcial (PATCH-like)** — apenas os campos enviados são atualizados, preservando os demais (`BeanUtils` + reflexão para ignorar propriedades nulas)
- **Tratamento global de exceções** com `@ControllerAdvice`, retornando mensagens de erro amigáveis ao cliente
- **Docker multi-stage build** — a imagem final é gerada a partir do build via Maven wrapper
- **Deploy contínuo no Render** — cada push na branch `main` gera um novo deploy automaticamente

---

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 25 | Linguagem principal |
| Spring Boot 4.1.0 | Framework base (starter `webmvc`) |
| Spring Data JPA | Camada de persistência |
| H2 Database | Banco de dados em memória |
| Lombok | Redução de boilerplate |
| BCrypt (at.favre.lib) | Hash de senhas |
| Maven | Gerenciamento de dependências e build |
| Docker | Containerização (multi-stage) |
| Render | Hospedagem e deploy contínuo |

---

## Estrutura do projeto

```
src/main/java/br/com/LuizMiguel/firstProject
├── FirstProjectApplication.java   # Classe principal
├── user/                          # Domínio de usuários
│   ├── UserController.java        # Endpoint de cadastro
│   ├── UserModel.java             # Entidade JPA (tb_users)
│   └── IUserRepository.java       # Repositório Spring Data
├── task/                          # Domínio de tarefas
│   ├── TaskController.java        # CRUD de tarefas
│   ├── TaskModel.java             # Entidade JPA (tb_tasks)
│   └── ITaskRepository.java       # Repositório Spring Data
├── filter/
│   └── FilterTaskAuth.java        # Filtro de autenticação Basic Auth
├── errors/
│   └── ExceptionHandlesController.java  # Handler global de exceções
└── utils/
    └── Utils.java                 # Cópia de propriedades não-nulas (update parcial)
```

---

## Como executar

### Testar direto na demo (sem instalar nada)

```bash
# Cadastrar um usuário
curl -X POST https://primeiroprojetospringboot.onrender.com/users/ \
  -H "Content-Type: application/json" \
  -d '{ "name": "Recrutador", "username": "recrutador", "password": "12345" }'
```

> Lembrando: se o serviço estiver hibernado, a primeira chamada demora — aguarde a resposta.

### Rodar localmente (Maven wrapper)

Pré-requisito: **JDK 25**

```bash
git clone https://github.com/luizMiguelBastos/PrimeiroProjetoSpringboot.git
cd PrimeiroProjetoSpringboot
./mvnw spring-boot:run
```

### Rodar com Docker

```bash
docker build -t task-manager-api .
docker run -p 8080:8080 task-manager-api
```

A API estará disponível em `http://localhost:8080`.

> O console do H2 fica acessível em `http://localhost:8080/h2-console`
> (JDBC URL: `jdbc:h2:mem:todolist` · usuário: `admin` · senha: `admin`)

---

## Endpoints

Nos exemplos abaixo, substitua a base URL por `https://primeiroprojetospringboot.onrender.com` para testar na demo, ou use `http://localhost:8080` rodando localmente.

### Usuários

#### `POST /users/` — Cadastrar usuário

```bash
curl -X POST http://localhost:8080/users/ \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Luiz Miguel",
    "username": "luizmiguel",
    "password": "12345"
  }'
```

**Resposta** `201 Created` — retorna o usuário criado (senha armazenada como hash BCrypt).
Usernames duplicados retornam `400 Bad Request`.

---

### Tarefas

> Todos os endpoints de tarefas exigem autenticação **Basic Auth** (username + senha cadastrados).

#### `POST /tasks/` — Criar tarefa

```bash
curl -X POST http://localhost:8080/tasks/ \
  -u luizmiguel:12345 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Estudar Spring Boot",
    "description": "Revisar filtros e autenticacao",
    "priority": "ALTA",
    "startAt": "2026-09-10T09:00:00",
    "endAt": "2026-09-10T11:00:00"
  }'
```

**Regras de validação:**
- As datas de início e término devem ser futuras
- A data de início deve ser anterior à data de término
- O título deve ter no máximo 50 caracteres

#### `GET /tasks/` — Listar tarefas do usuário autenticado

```bash
curl http://localhost:8080/tasks/ -u luizmiguel:12345
```

Retorna **apenas** as tarefas pertencentes ao usuário autenticado.

#### `PUT /tasks/{id}` — Atualizar tarefa

```bash
curl -X PUT http://localhost:8080/tasks/{id} \
  -u luizmiguel:12345 \
  -H "Content-Type: application/json" \
  -d '{ "title": "Novo titulo" }'
```

**Comportamento de update parcial:** apenas os campos enviados no corpo são alterados — os demais permanecem intactos.
Tentativas de editar tarefas de **outro usuário** são bloqueadas com `400 Bad Request`.

---

## Fluxo de autenticação

1. O cliente envia o header `Authorization: Basic base64(username:password)`
2. O `FilterTaskAuth` intercepta requisições para `/tasks/**`
3. As credenciais são decodificadas e a senha é verificada contra o hash **BCrypt** armazenado
4. Se válidas, o `idUser` é injetado como atributo da requisição e usado pelos controllers para escopo de dados
5. Se inválidas, a API responde `401 Unauthorized`

---

## Deploy

O deploy é feito no **Render** a partir do `Dockerfile` do repositório, com build multi-stage:

1. **Etapa de build:** imagem `eclipse-temurin:25-jdk` compila o projeto com o Maven wrapper (`./mvnw clean package`)
2. **Etapa final:** apenas o `.jar` gerado é copiado para a imagem de runtime, reduzindo o tamanho final
3. Cada push na branch `main` dispara um novo deploy automaticamente

Como o banco H2 roda em memória, **os dados são reiniciados a cada restart da instância** — comportamento esperado para um ambiente de demonstração.

---

## Roadmap de melhorias

Próximos passos planejados para evoluir o projeto além do escopo do curso:

- [ ] Endpoint `DELETE /tasks/{id}`
- [ ] Migração para **Spring Security + JWT**
- [ ] Testes unitários e de integração (JUnit 5 + MockMvc)
- [ ] Banco PostgreSQL para persistência real em produção
- [ ] Documentação interativa com **Swagger / OpenAPI**
- [ ] DTOs de request/response com Bean Validation (`@Valid`)
- [ ] Paginação na listagem de tarefas

---

## Autor

**Luiz Miguel Bastos**

Projeto desenvolvido durante o Minicurso de Java da [Rocketseat](https://www.rocketseat.com.br/), com melhorias e evoluções próprias.

[![GitHub](https://img.shields.io/badge/GitHub-luizMiguelBastos-181717?logo=github)](https://github.com/luizMiguelBastos)
