# Sistema de HelpDesk

## Descrição
Sistema de gerenciamento de chamados técnicos (HelpDesk) desenvolvido em Java com Spring Boot. Esta aplicação permite o cadastro de usuários, abertura e acompanhamento de chamados técnicos, gerenciamento de categorias e departamentos, além de estatísticas para gestores.

## Tecnologias Utilizadas
- Java 17
- Spring Boot
- Spring Security com JWT
- Spring Data JPA
- PostgreSQL
- JavaMail para envio de e-mails
- Swagger/OpenAPI para documentação

## Estrutura do Projeto

### Principais Pacotes
- **config**: Configurações do sistema (CORS, OpenAPI, etc.)
- **controller**: Endpoints da API REST
- **dto**: Objetos de transferência de dados
- **enums**: Enumerações como Status, Prioridade e Tipo de Usuário
- **model**: Entidades de domínio
- **repository**: Interfaces de acesso ao banco de dados
- **security**: Implementações de segurança com JWT
- **service**: Regras de negócio

### Principais Funcionalidades

#### Autenticação e Usuários
- Registro de usuários com verificação por e-mail
- Login com autenticação JWT
- Hierarquia de perfis: GESTOR > TECNICO > CLIENTE

#### Gestão de Chamados
- Abertura de chamados por clientes
- Atribuição de técnicos aos chamados
- Atualização de status dos chamados
- Histórico completo de ações
- Comentários em chamados
- SLA (Service Level Agreement) configurável por categoria e prioridade

#### Notificações
- Notificações no sistema
- Notificações por e-mail (configuráveis pelo usuário)
- Alertas para gestores sobre prazos de SLA

#### Administração
- Gestão de usuários
- Gestão de categorias e departamentos
- Definição de SLAs

#### Estatísticas
- Dashboard com métricas principais
- Desempenho de técnicos
- Tempo médio de resolução por categoria
- Distribuição de chamados por categoria

## Variáveis de Ambiente
```
DB_URL=jdbc:postgresql://localhost:5432/helpdesk
DB_USER=postgres
DB_PASS=senha

JWT_SECRET=chave_secreta_de_pelo_menos_256_bits
MAIL_USER=seu_email@gmail.com
MAIL_PASS=sua_senha_ou_app_password

BACKUP_MAIL_TO=email_para_backups@dominio.com
FRONT_ORIGIN=http://localhost:3000

PORT=8080 (opcional, padrão 8080)
```

## Execução

### Pré-requisitos
- JDK 17
- PostgreSQL
- Maven

### Compilação
```bash
mvn clean package
```

### Execução
```bash
java -jar target/helpdesk-0.0.1-SNAPSHOT.jar
```

### Execução com Docker (Opcional)
```bash
docker build -t helpdesk-api .
docker run -p 8080:8080 --env-file .env helpdesk-api
```

## Endpoints Principais

### Autenticação
- POST `/auth/register` - Registro de usuário
- POST `/auth/verify-code` - Verificação de código
- POST `/auth/login` - Login

### Chamados
- GET `/api/v1/tickets` - Listar chamados
- POST `/api/v1/tickets` - Criar chamado
- GET `/api/v1/tickets/{id}` - Detalhes do chamado
- PUT `/api/v1/tickets/{id}/status` - Alterar status
- PUT `/api/v1/tickets/{id}/categoria` - Alterar categoria
- PUT `/api/v1/tickets/{id}/assign` - Atribuir técnico
- PUT `/api/v1/tickets/{id}/resolve` - Resolver chamado
- POST `/api/v1/tickets/{id}/comment` - Adicionar comentário

### Gestão
- GET `/api/v1/statistics/dashboard` - Dashboard de estatísticas
- GET/POST `/api/v1/categories` - Listar/criar categorias
- GET/POST `/api/v1/departamentos` - Listar/criar departamentos
- GET/POST `/api/v1/sla` - Configuração de SLA

### Usuários
- GET/PUT `/api/v1/usuarios/preferencias` - Configuração de notificações
- GET/PUT `/api/v1/usuarios/nome` - Gerenciar nome
- PUT `/api/v1/usuarios/senha` - Alterar senha

## Recursos Adicionais

### Backup Automático
O sistema realiza backup automático diário do banco de dados e envia por e-mail.

### Atualização de Estatísticas
Views materializadas são atualizadas periodicamente para otimizar consultas de estatísticas.

### Alertas para Gestores
Monitoramento automático de SLAs e envio de alertas para chamados críticos.

## Testes
O projeto inclui testes unitários para serviços principais:
- CategoryService
- DepartmentService
- EmailService
- NotificacaoService
- SlaService
- StatisticsService
- TicketHistoryService

Para executar os testes:
```bash
mvn test
```

## Documentação da API
A documentação da API está disponível através do Swagger UI em:
```
http://localhost:8080/swagger-ui.html
```


