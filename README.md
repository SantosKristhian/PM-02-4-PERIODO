# Sistema de gerenciamento de estoque e vendas   

## Aplicação desenvolvida em Java+Springboot para controle de estoque e vendas de uma loja de motos.

Projeto se trata de um sistem de gerenciamento de estoque, feito em JAVA + Springboot, com o objetivo de suprir a demanda de uma aplicação que contivesse a regra de negócio CurvaABC. Para isso foi implementado também, um sistema de vendas completo.


*Gerenciamento e autenticação de usuários (com Roles)

*Gerenciamento de itens e categorias

*Gerenciamento de comprador 

*Gerenciamento de vendas e relatórios completos 

---------------------------------------------------------------------------

## Como rodar (Docker Compose)

Sobe backend + frontend + MySQL com um único comando, sem depender de IP fixo de rede.

### Pré-requisitos

- Docker Desktop instalado e rodando.
- O repositório do frontend clonado como pasta **irmã** deste repositório:

```
IdeaProjects/
├── estoquemanager-backend/   (este repositório)
└── estoquemanager-frontend/  (https://github.com/SantosKristhian/PM-02-4-PERIODO-FRONT)
```

### Passos

1. Copie `.env.example` para `.env` nesta pasta e preencha as senhas e o `JWT_SECRET` (gere um com `openssl rand -base64 32`).
2. Suba a stack:
   ```bash
   docker compose up -d --build
   ```
3. Acesse o frontend em `http://localhost:8081` (porta configurável via `FRONTEND_PORT` no `.env`).
4. O banco é vazio na primeira execução - não há tela de primeiro acesso ainda, então é preciso criar o usuário administrador inicial direto no banco:
   ```bash
   docker compose exec database mysql -uroot -p"$DB_ROOT_PASSWORD" estoquemanagerdb -e \
     "INSERT INTO usuario_table (nome, cpf, idade, login, senha, cargo) VALUES ('Admin', '00000000000', 30, 'admin', '<hash bcrypt>', 'ADM');"
   ```
   O campo `senha` precisa ser um hash bcrypt (o mesmo formato usado pelo Spring Security). Os dados ficam persistidos em um volume Docker nomeado (`mysql_data`) e sobrevivem a `docker compose down` / `up` - só se perdem com `docker compose down -v`.
5. Para derrubar tudo: `docker compose down` (ou `down -v` para apagar também os dados do banco).

---------------------------------------------------------------------------

# Inventory and Sales Management System
## Java + Spring Boot application for managing the inventory and sales of a motorcycle shop.

This project is an inventory management system developed in Java with Spring Boot, designed to meet the need for an application that implements the ABC classification method (Curva ABC). In addition, it includes a complete sales management module.

*User management and authentication (with roles)

*Item and category management

*Buyer management 

*Sales management and comprehensive reporting
