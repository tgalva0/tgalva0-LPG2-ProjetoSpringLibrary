# 📚 Sistema de Biblioteca

Aplicação para gerenciamento de biblioteca, desenvolvida com **React** no frontend e **Spring Boot (Java)** no backend. A interface gráfica utiliza **CSS customizado**, garantindo um visual moderno e responsivo. O backend conta com **Spring Security** para autenticação, **JPA/Hibernate** para persistência e **Maven** como ferramenta de gerenciamento de dependências e build.

---

## 🖥️ Uso da Aplicação (Usuário Final)

O sistema pode ser acessado diretamente pelo navegador após a execução dos servidores locais.

### Requisitos mínimos (Para execução)

- **Sistema Operacional:** Windows 10 ou superior, Linux ou macOS
- **Java:** JDK 17 ou superior
- **Node.js:** Versão 18+ (LTS recomendada)
- **Navegador:** Chrome, Edge, Firefox ou Safari (atualizado)
- **Memória RAM:** 2 GB ou mais
- **Espaço em Disco:** ~200 MB para rodar backend + frontend localmente

## ▶️ Como executar

---

### 1. Clone o repositório:

```bash
git clone https://github.com/tgalva0/tgalva0-LPG2-ProjetoSpringLibrary
```

### 2. Backend (Spring Boot):

```bash
cd project
mvn spring-boot:run
```
- Com o servidor rodando, já é possível acessar a documentação swagger:
```markdown
http://localhost:8080/swagger-ui/index.html
```

### 3. Frontend (React):

```bash
cd FRONTEND/biblioteca-frontend
npm install
npm run dev
```

### 5. Acesse no navegador:

```markdown
http://localhost:5173
```

## 6. Dados de Teste: Login

Aqui estão as credenciais para testar os diferentes níveis de acesso da aplicação.

---

### 👤 Usuário Comum

Este usuário tem o papel `ROLE_COMUM`.
Permissões: Pesquisar livros, realizar empréstimos, devolver livros e visualizar o próprio perfil.

* **Usuário:** `aluno`
* **Senha:** `123456`

---

### 👑 Administrador

Este usuário tem os papéis `ROLE_ADMIN` e `ROLE_COMUM`.
Permissões: Todas as permissões do usuário comum, mais acesso total ao Painel de Administração (Gerir Livros, Gerir Usuários, Ver Estatísticas).

* **Usuário:** `admin`
* **Senha:** `admin123`

## 7. Testes Swagger

### 7.1. Autenticação (Pegar o Token)

- POST /api/auth/login
- Cenário: Login como Administrador
```json
Body:
{
  "username": "admin",
  "password": "admin123"
}
```
- (Copie o token da resposta para usar no botão Authorize)
- Cenário: Login como Usuário Comum
```json
Body:
{
  "username": "aluno",
  "password": "123456"
}
```
---

### 7.2. Perfil do Usuário

- GET /api/profile/dashboard
  - Parâmetros: Nenhum.
  - Resultado Esperado: JSON com a lista de empréstimos ativos do usuário logado.

- POST /api/profile/change-password
```json
Body:
{
  "currentPassword": "admin123",
  "newPassword": "novaSenha123"
}
```
- (Atenção: Se testar isso, seu login antigo vai parar de funcionar!)

---

### 7.3. Livros (Gestão de Acervo)

- POST /api/livros (Requer Admin)
```json
Body:
{
  "titulo": "O Senhor dos Anéis",
  "autor": "J.R.R. Tolkien",
  "isbn": "9788595084742",
  "quantidadeDisponivel": 5
}
```
- GET /api/livros/search
  - Parâmetro q: senhor (ou tolkien)
  - Resultado Esperado: Deve retornar o livro criado acima.

- GET /api/livros/{id}
  - Parâmetro id: 1 (O livro "Clean Code" criado pelo DataLoader).

- PUT /api/livros/{id} (Requer Admin)
```json
Body:
{
  "titulo": "Clean Code - Edição Especial",
  "autor": "Robert C. Martin",
  "isbn": "9780132350884",
  "quantidadeDisponivel": 10
}
```

- DELETE /api/livros/{id} (Requer Admin)
  - Parâmetro id: O ID do livro "O Senhor dos Anéis" (provavelmente 3 ou 4).

---

### 7.4. Empréstimos (Fluxo de Uso)

- POST /api/emprestimos
```json
Body:
{
  "livroId": 2
}
```

- POST /api/emprestimos/{id}/devolucao
  - Parâmetro id: ID do Empréstimo (não do livro).
  - Dica: Olhe a resposta do POST anterior ou use GET /api/profile/dashboard.

---

### 7.5. Admin (Gestão de Usuários)

- POST /api/admin/usuarios
```json
Body:
{
  "username": "bibliotecario",
  "password": "senhaSegura123",
  "nomeCompleto": "João da Silva",
  "roles": [
    "ROLE_ADMIN",
    "ROLE_COMUM"
  ]
}
```

- GET /api/admin/usuarios
  - Resultado Esperado: Lista com admin, aluno e bibliotecario.

- GET /api/admin/usuarios/search
  - Parâmetro q: joao

- PUT /api/admin/usuarios/{id}
```json
Body:
{
  "nomeCompleto": "João da Silva (Editado)",
  "roles": [
    "ROLE_COMUM"
  ]
}
```

- DELETE /api/admin/usuarios/{id}
  - Parâmetro id: O ID do "bibliotecario".

- GET /api/admin/dashboard/stats
  - Resultado Esperado: JSON com contadores (ex: total de usuários, total de livros, empréstimos ativos).
