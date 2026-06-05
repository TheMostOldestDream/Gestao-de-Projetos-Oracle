# Sistema de Gestao de Projetos e Equipes - Oracle

Sistema console desenvolvido em Java com arquitetura MVC para gerenciamento de projetos, equipes e tarefas.

---

## Tecnologia e Requisitos

- Java 17 ou superior
- VS Code com extensao "Extension Pack for Java"
- Sem dependencias externas (puro Java SE)

---

## Como Executar

### Via VS Code
Abra o arquivo `Main.java` e clique em "Run" (ou F5).

---

## Estrutura do Projeto

```
GestaoProjetosOracle/
+-- src/main/java/com/oracle/gestao/
|   +-- Main.java
|   +-- model/
|   |   +-- Usuario.java
|   |   +-- Projeto.java
|   |   +-- Tarefa.java
|   |   +-- Equipe.java
|   |   +-- Perfil.java          (enum)
|   |   +-- StatusProjeto.java   (enum)
|   |   +-- StatusTarefa.java    (enum)
|   +-- controller/
|   |   +-- UsuarioController.java
|   |   +-- ProjetoController.java
|   |   +-- TarefaController.java
|   |   +-- EquipeController.java
|   |   +-- RelatorioController.java
|   +-- view/
|   |   +-- MenuPrincipal.java
|   |   +-- UsuarioView.java
|   |   +-- ProjetoView.java
|   |   +-- TarefaView.java
|   |   +-- EquipeView.java
|   |   +-- RelatorioView.java
|   +-- util/
|       +-- Util.java
+-- docs/
    +-- diagrama-classes.md
+-- README.md
```

---

## Arquitetura MVC

| Camada     | Responsabilidade                                      |
|------------|-------------------------------------------------------|
| Model      | Entidades de dominio: Usuario, Projeto, Tarefa, Equipe|
| Controller | Regras de negocio, CRUD, validacoes                   |
| View       | Interacao com usuario via console (menus, leitura)    |
| Util       | Auxiliares: leitura de dados, validacoes, formatacao  |

---

## Funcionalidades por Perfil

### Administrador
- Gestao completa de usuarios (CRUD, promoção de cargo de usuários)
- Gestao completa de projetos (CRUD)
- Gestao completa de tarefas (CRUD)
- Gestao completa de equipes (CRUD + membros + alocacao)
- Acesso a todos os relatorios

### Gerente
- Gestao de projetos, tarefas e equipes
- Acesso a todos os relatorios
- Alteracao de propria senha

### Colaborador
- Visualizacao das proprias tarefas
- Visualizacao geral de projetos

---

## Relatorios Disponiveis

1. Relatorio geral de projetos (contagem por status)
2. Relatorio detalhado de projeto (tarefas + equipes + alertas de prazo)
3. Relatorio por colaborador (tarefas atribuidas)
4. Relatorio de equipes (membros + projetos)
5. Relatorio de tarefas com prazo vencido

---

## Diagrama de Classes

Ver arquivo `docs/diagrama-classes.md`.
