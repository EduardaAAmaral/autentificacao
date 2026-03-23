# 🔐 Sistema de Autenticação com Spring Boot

API de autenticação desenvolvida com Java e Spring Boot, implementando boas práticas de segurança com JWT, controle de acesso e fluxo completo de autenticação de usuários.

---

## 🚀 Funcionalidades

* ✔ Cadastro de usuários
* ✔ Login com geração de JWT
* ✔ Refresh Token
* ✔ Logout seguro
* ✔ Recuperação de senha via email
* ✔ Redefinição de senha com token
* ✔ Verificação de email
* ✔ Proteção de rotas com Spring Security
* ✔ CRUD de usuário (editar / deletar / dados do usuário logado)

---

## 🛠 Tecnologias

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA (Hibernate)
* PostgreSQL
* JWT (JSON Web Token)
* Swagger (OpenAPI)
* JavaMailSender

---

## 📁 Estrutura do Projeto

```
src/
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 └── security
```

---

## ⚙️ Configuração

### 1. Clone o projeto

```
git clone https://github.com/seu-usuario/seu-repositorio.git
```

---

### 2. Configure o banco de dados (PostgreSQL)

No arquivo `application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/seubanco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
```

---

### 3. Configurar envio de email

```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seuemail@gmail.com
spring.mail.password=sua_senha_de_app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

⚠️ Use senha de aplicativo do Gmail

---

## 🔑 Autenticação

A API utiliza JWT para autenticação.

Para acessar endpoints protegidos, envie no header:

```
Authorization: Bearer SEU_TOKEN
```

---

## 📌 Endpoints principais

### 🔐 Autenticação

| Método | Endpoint       | Descrição        |
| ------ | -------------- | ---------------- |
| POST   | /auth/register | Cadastro         |
| POST   | /auth/login    | Login            |
| POST   | /auth/refresh  | Gerar novo token |
| POST   | /auth/logout   | Logout           |

---

### 👤 Usuário

| Método | Endpoint              | Descrição               |
| ------ | --------------------- | ----------------------- |
| GET    | /auth/me              | Dados do usuário logado |
| PUT    | /auth/me              | Atualizar usuário       |
| DELETE | /auth/me              | Deletar conta           |
| PUT    | /auth/change-password | Alterar senha           |

---

### 📧 Email / Senha

| Método | Endpoint              | Descrição                   |
| ------ | --------------------- | --------------------------- |
| POST   | /auth/forgot-password | Enviar email de recuperação |
| POST   | /auth/reset-password  | Resetar senha               |
| GET    | /auth/verify-email    | Verificar email             |

---

## 🧪 Testes

Acesse a documentação interativa via Swagger:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔒 Segurança

* Senhas criptografadas com BCrypt
* Autenticação stateless com JWT
* Proteção de rotas com Spring Security
* Tokens com expiração
* Validação de refresh token

---

## 📈 Melhorias futuras

* Implementação de roles (ADMIN / USER)
* Blacklist de tokens
* Testes automatizados
* Deploy em nuvem (AWS / Render)

---

## 👩‍💻 Autora

Desenvolvido por Eduarda Amaral 💙

---

## 📌 Observações

Este projeto foi desenvolvido com foco em aprendizado e evolução prática em backend, autenticação e segurança.
