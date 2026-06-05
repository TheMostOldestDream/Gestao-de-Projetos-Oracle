package com.oracle.gestao.view;

import com.oracle.gestao.controller.EquipeController;
import com.oracle.gestao.controller.ProjetoController;
import com.oracle.gestao.controller.UsuarioController;
import com.oracle.gestao.model.*;
import com.oracle.gestao.util.Util;

import java.util.List;
import java.util.Optional;

public class EquipeView {

    private final EquipeController controller;
    private final UsuarioController usuarioController;
    private final ProjetoController projetoController;

    public EquipeView(EquipeController controller, UsuarioController usuarioController,
                      ProjetoController projetoController) {
        this.controller = controller;
        this.usuarioController = usuarioController;
        this.projetoController = projetoController;
    }

    public void menu(Usuario usuarioLogado) {
        boolean continuar = true;
        while (continuar) {
            Util.imprimirTitulo("GESTAO DE EQUIPES");
            System.out.println("  1. Listar equipes");
            System.out.println("  2. Cadastrar equipe");
            System.out.println("  3. Atualizar equipe");
            System.out.println("  4. Remover equipe");
            System.out.println("  5. Gerenciar membros");
            System.out.println("  6. Alocar/Desalocar em projeto");
            System.out.println("  0. Voltar");
            Util.imprimirLinha();

            int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 6);
            switch (opcao) {
                case 1 -> listar();
                case 2 -> cadastrar();
                case 3 -> atualizar();
                case 4 -> remover();
                case 5 -> gerenciarMembros();
                case 6 -> gerenciarAlocacao();
                case 0 -> continuar = false;
            }
        }
    }

    private void listar() {
        Util.imprimirTitulo("LISTA DE EQUIPES");
        List<Equipe> lista = controller.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  Nenhuma equipe cadastrada.");
        } else {
            for (Equipe e : lista) {
                System.out.println("  " + e);
                System.out.print("    Membros: ");
                if (e.getMembros().isEmpty()) {
                    System.out.println("(nenhum)");
                } else {
                    System.out.println();
                    for (Usuario m : e.getMembros()) {
                        System.out.printf("      - %s (%s)%n", m.getNomeCompleto(), m.getCargo());
                    }
                }
                System.out.print("    Projetos: ");
                if (e.getProjetos().isEmpty()) {
                    System.out.println("(nenhum)");
                } else {
                    System.out.println();
                    for (Projeto p : e.getProjetos()) {
                        System.out.printf("      - %s [%s]%n", p.getNome(), p.getStatus().getDescricao());
                    }
                }
            }
        }
        Util.pausar();
    }

    private void cadastrar() {
        Util.imprimirTitulo("CADASTRAR EQUIPE");
        String nome = Util.lerStringObrigatoria("  Nome da equipe: ");
        String descricao = Util.lerStringObrigatoria("  Descricao: ");
        Equipe e = controller.cadastrar(nome, descricao);
        System.out.println("\n  Equipe cadastrada com sucesso! ID: " + e.getId());
        Util.pausar();
    }

    private void atualizar() {
        Util.imprimirTitulo("ATUALIZAR EQUIPE");
        listarSimples();

        int id = Util.lerInteiro("  ID da equipe a atualizar (0 para cancelar): ");
        if (id == 0) return;

        Optional<Equipe> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Equipe nao encontrada.");
            Util.pausar();
            return;
        }

        Equipe e = opt.get();
        String nome = Util.lerString("  Novo nome [" + e.getNome() + "]: ");
        if (nome.isEmpty()) nome = e.getNome();

        String descricao = Util.lerString("  Nova descricao [" + e.getDescricao() + "]: ");
        if (descricao.isEmpty()) descricao = e.getDescricao();

        boolean ok = controller.atualizar(id, nome, descricao);
        System.out.println(ok ? "  Equipe atualizada com sucesso!" : "  Erro ao atualizar equipe.");
        Util.pausar();
    }

    private void remover() {
        Util.imprimirTitulo("REMOVER EQUIPE");
        listarSimples();

        int id = Util.lerInteiro("  ID da equipe a remover (0 para cancelar): ");
        if (id == 0) return;

        Optional<Equipe> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Equipe nao encontrada.");
            Util.pausar();
            return;
        }

        System.out.println("  Equipe: " + opt.get());
        if (Util.confirmar("  Confirmar remocao?")) {
            boolean ok = controller.remover(id);
            System.out.println(ok ? "  Equipe removida com sucesso!" : "  Erro ao remover equipe.");
        } else {
            System.out.println("  Operacao cancelada.");
        }
        Util.pausar();
    }

    private void gerenciarMembros() {
        Util.imprimirTitulo("GERENCIAR MEMBROS DA EQUIPE");
        listarSimples();

        int id = Util.lerInteiro("  ID da equipe (0 para cancelar): ");
        if (id == 0) return;

        Optional<Equipe> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Equipe nao encontrada.");
            Util.pausar();
            return;
        }

        Equipe equipe = opt.get();
        System.out.println("  Equipe: " + equipe.getNome());
        System.out.println("  1. Adicionar membro");
        System.out.println("  2. Remover membro");
        System.out.println("  0. Cancelar");

        int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 2);
        if (opcao == 0) return;

        List<Usuario> usuarios = usuarioController.listarTodos();
        System.out.println("  Usuarios disponiveis:");
        for (Usuario u : usuarios) {
            System.out.println("    " + u);
        }

        int uid = Util.lerInteiro("  ID do usuario: ");
        Optional<Usuario> uOpt = usuarioController.buscarPorId(uid);
        if (uOpt.isEmpty()) {
            System.out.println("  Usuario nao encontrado.");
            Util.pausar();
            return;
        }

        if (opcao == 1) {
            controller.adicionarMembro(id, uOpt.get());
            System.out.println("  Membro adicionado com sucesso!");
        } else {
            controller.removerMembro(id, uOpt.get());
            System.out.println("  Membro removido com sucesso!");
        }
        Util.pausar();
    }

    private void gerenciarAlocacao() {
        Util.imprimirTitulo("ALOCAR / DESALOCAR EQUIPE EM PROJETO");
        listarSimples();

        int id = Util.lerInteiro("  ID da equipe (0 para cancelar): ");
        if (id == 0) return;

        Optional<Equipe> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Equipe nao encontrada.");
            Util.pausar();
            return;
        }

        System.out.println("  1. Alocar em projeto");
        System.out.println("  2. Desalocar de projeto");
        System.out.println("  0. Cancelar");

        int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 2);
        if (opcao == 0) return;

        List<Projeto> projetos = projetoController.listarTodos();
        if (projetos.isEmpty()) {
            System.out.println("  Nenhum projeto cadastrado.");
            Util.pausar();
            return;
        }

        System.out.println("  Projetos disponiveis:");
        for (Projeto p : projetos) {
            System.out.println("    " + p);
        }

        int pid = Util.lerInteiro("  ID do projeto: ");
        Optional<Projeto> pOpt = projetoController.buscarPorId(pid);
        if (pOpt.isEmpty()) {
            System.out.println("  Projeto nao encontrado.");
            Util.pausar();
            return;
        }

        if (opcao == 1) {
            controller.alocarNoProjeto(id, pOpt.get());
            System.out.println("  Equipe alocada no projeto com sucesso!");
        } else {
            controller.desalocarDoProjeto(id, pOpt.get());
            System.out.println("  Equipe desalocada do projeto com sucesso!");
        }
        Util.pausar();
    }

    private void listarSimples() {
        List<Equipe> lista = controller.listarTodos();
        if (!lista.isEmpty()) {
            for (Equipe e : lista) {
                System.out.println("  " + e);
            }
            Util.imprimirLinha();
        }
    }
}
