# API de Gestão de Academia

API REST em Java 17 e Spring Boot para gerenciamento de alunos, treinos, exercícios e avaliações físicas de uma academia. Autenticação e autorização são feitas via Spring Security com JWT, seguindo o padrão stateless (sem sessão em memória no servidor).

---

## Tecnologias

- Java 17
- Spring Boot 4.0.4
- Spring Security
- Spring Data JPA & Hibernate
- JJWT (io.jsonwebtoken) 0.12.6
- MySQL Connector/J
- Lombok
- Springdoc OpenAPI (Swagger UI)

---

## Funcionalidades

### Autenticação e autorização

- Login e registro de usuário com geração de token JWT.
- Validação de token feita em um filtro (`JwtAuthenticationFilter`), executado uma vez por requisição, antes do filtro padrão de autenticação do Spring.
- Controle de acesso por papéis (RBAC): rotas e métodos específicos exigem roles como `ADMIN`.
- Autorização em nível de método com `@PreAuthorize` e SpEL. Por exemplo, um aluno só acessa a própria avaliação física, a menos que seja admin.
- Senhas armazenadas com hash via `BCryptPasswordEncoder`, nunca em texto plano.
- Tratamento de exceções de segurança: 401 para requisições não autenticadas, 403 para acesso negado.

### Modelagem de dados

- `AlunosEntity` implementa `UserDetails` (email como username, senha com hash, roles associadas).
- `RoleEntity` implementa `GrantedAuthority`, com papéis definidos pelo enum `RoleTypeEnum` (`ROLE_ALUNO`, `ROLE_ADMIN`).
- Relacionamentos: `OneToOne` entre Aluno e Avaliação Física, `OneToMany` entre Aluno e Treinos, `ManyToMany` entre Treinos e Exercícios, `ManyToMany` entre Alunos e Roles.

### Consultas e persistência

- Projeções customizadas (`AvaliacoesFisicasProjection`) para retornar apenas os campos necessários, evitando overfetching.
- Paginação com `Pageable` e `countQuery` dedicada.
- Três formas de consulta implementadas para comparação: Derived Query, JPQL e SQL nativo (`IExerciciosRepository`).

### Tratamento de erros

Manipulador global de exceções via `@RestControllerAdvice`:

- `BadRequestException` → 400 (ex: email já cadastrado, credenciais inválidas)
- `NotFoundException` → 404
- `AccessDeniedException` → 403 (bloqueios do Spring Security / `@PreAuthorize`)
- `Exception` → 500 (fallback)
- Validação de entrada com Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@NotNull`)

## Banco de dados

MySQL. As entidades principais são Alunos, Treinos, Exercícios, Avaliações Físicas e Roles, com os relacionamentos descritos na seção de Funcionalidades.

---

## Endpoints

### Cadastro de usuário (público)

`POST /v1/auth/register`

```json
{
  "nome": "Carlos Silva",
  "email": "carlos@email.com",
  "senha": "senhaSegura123"
}
```

### Login (público)

`POST /v1/auth/login`

```json
{
  "email": "carlos@email.com",
  "senha": "senhaSegura123"
}
```

Resposta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiration": 900000
}
```

### Endpoints protegidos

Rotas como `/v1/treinos`, `/v1/exercicios` e `/v1/alunos/{alunoId}/avaliacao` exigem o token no header:

```text
Authorization: Bearer <seu_token_jwt>
```
