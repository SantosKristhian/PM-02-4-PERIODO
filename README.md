# Sistema de gerenciamento de estoque e vendas   

[![Docker Publish](https://github.com/SantosKristhian/PM-02-4-PERIODO/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/SantosKristhian/PM-02-4-PERIODO/actions/workflows/docker-publish.yml)

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

1. Copie `.env.example` para `.env` nesta pasta e preencha:
   - Senhas do banco (`DB_PASSWORD`, `DB_ROOT_PASSWORD`) - pode ser qualquer valor, é um banco novo dentro do container.
   - `JWT_SECRET` - **precisa ter pelo menos 256 bits decodificados** (a lib de JWT recusa chaves mais curtas). Gere uma com `openssl rand -base64 32` e cole o valor exatamente como saiu (não pode ter `-` ou `_`, só o alfabeto base64 padrão).
   - `ADMIN_LOGIN`/`ADMIN_PASSWORD` (opcional) - veja o item 4 abaixo.
2. Suba a stack:
   ```bash
   docker compose up -d --build
   ```
3. Acesse o frontend em `http://localhost:8081` (porta configurável via `FRONTEND_PORT` no `.env`).
4. **Primeiro acesso**: se o banco estiver vazio (primeira subida, ou depois de um `down -v`), a aplicação cria um usuário ADM automaticamente e mostra a senha no log:
   ```bash
   docker compose logs backend | grep -A4 AdminBootstrapRunner
   ```
   Por padrão o login é `admin` e a senha é gerada aleatoriamente (aparece só essa vez, nesse log). Se quiser definir login/senha fixos em vez de aleatório, preencha `ADMIN_LOGIN`/`ADMIN_PASSWORD` no `.env` **antes** da primeira subida com o banco vazio - depois que já existe um ADM, essas variáveis não têm mais efeito (o jeito de trocar a senha depois é pela própria tela de usuários, logado).
5. Os dados ficam persistidos em um volume Docker nomeado (`mysql_data`) e sobrevivem a `docker compose down` / `up` normalmente.

### Resetar tudo do zero

Pra garantir um estado limpo de verdade, use um comando só:
```bash
docker compose down -v
docker compose up -d --build
```
`down -v` remove containers, rede **e** os volumes do compose (perde os dados do banco). Sem o `-v`, `docker compose down` remove só containers/rede e mantém os dados. `--build` sempre reconstrói as imagens a partir do Dockerfile/código atual, então não é preciso apagar imagem manualmente pra pegar uma mudança de código.

---------------------------------------------------------------------------

# Inventory and Sales Management System
## Java + Spring Boot application for managing the inventory and sales of a motorcycle shop.

This project is an inventory management system developed in Java with Spring Boot, designed to meet the need for an application that implements the ABC classification method (Curva ABC). In addition, it includes a complete sales management module.

*User management and authentication (with roles)

*Item and category management

*Buyer management 

*Sales management and comprehensive reporting
