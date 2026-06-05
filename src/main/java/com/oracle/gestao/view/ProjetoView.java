package com.oracle.gestao.view;

import com.oracle.gestao.controller.ProjetoController;
import com.oracle.gestao.controller.UsuarioController;
import com.oracle.gestao.model.*;
import com.oracle.gestao.util.Util;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProjetoView {

    private final ProjetoController controller;
    private final UsuarioController usuarioController;

    public ProjetoView(ProjetoController controller, UsuarioController usuarioController) {
        this.controller = controller;
        this.usuarioController = usuarioController;
    }

    public void menu(Usuario usuarioLogado) {
        boolean continuar = true;
        while (continuar) {
            Util.imprimirTitulo("GESTAO DE PROJETOS");
            System.out.println("  1. Listar projetos");
            System.out.println("  2. Cadastrar projeto");
            System.out.println("  3. Atualizar projeto");
            System.out.println("  4. Remover projeto");
            System.out.println("  0. Voltar");
            Util.imprimirLinha();

            int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 4);
            switch (opcao) {
                case 1 -> listar();
                case 2 -> cadastrar(usuarioLogado);
                case 3 -> atualizar();
                case 4 -> remover();
                case 0 -> continuar = false;
            }
        }
    }

    private void listar() {
        Util.imprimirTitulo("LISTA DE PROJETOS");
        List<Projeto> lista = controller.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  Nenhum projeto cadastrado.");
        } else {
            for (Projeto p : lista) {
                System.out.println("  " + p);
            }
        }
        Util.pausar();
    }

    private void cadastrar(Usuario usuarioLogado) {
        Util.imprimirTitulo("CADASTRAR PROJETO");

        String nome = Util.lerStringObrigatoria("  Nome do projeto: ");
        String descricao = Util.lerStringObrigatoria("  Descricao: ");
        LocalDate dataInicio = Util.lerData("  Data de inicio");
        LocalDate dataTermino;
        while (true) {
            dataTermino = Util.lerData("  Data de termino prevista");
            if (!dataTermino.isBefore(dataInicio)) break;
            System.out.println("  Data de termino deve ser igual ou posterior a data de inicio.");
        }

        StatusProjeto status = selecionarStatus();
        Usuario gerente = selecionarGerente(usuarioLogado);

        Projeto p = controller.cadastrar(nome, descricao, dataInicio, dataTermino, status, gerente);
        System.out.println("\n  Projeto cadastrado com sucesso! ID: " + p.getId());
        Util.pausar();
    }

    private void atualizar() {
        Util.imprimirTitulo("ATUALIZAR PROJETO");
        listarSimples();

        int id = Util.lerInteiro("  ID do projeto a atualizar (0 para cancelar): ");
        if (id == 0) return;

        Optional<Projeto> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Projeto nao encontrado.");
            Util.pausar();
            return;
        }

        Projeto p = opt.get();
        System.out.println("  Projeto atual: " + p);

        String nome = Util.lerString("  Novo nome [" + p.getNome() + "]: ");
        if (nome.isEmpty()) nome = p.getNome();

        String descricao = Util.lerString("  Nova descricao [" + p.getDescricao() + "]: ");
        if (descricao.isEmpty()) descricao = p.getDescricao();

        System.out.println("  Data de inicio atual: " + Util.formatarData(p.getDataInicio()));
        LocalDate dataInicio = Util.lerData("  Nova data de inicio");

        LocalDate dataTermino;
        do {
            dataTermino = Util.lerData("  Nova data de termino prevista");
            if (dataTermino.isBefore(dataInicio))
                System.out.println("  Data de termino deve ser igual ou posterior a data de inicio.");
        } while (dataTermino.isBefore(dataInicio));

        StatusProjeto status = selecionarStatus();

        List<Usuario> gerentes = usuarioController.listarPorPerfil(Perfil.GERENTE);
        if (gerentes.isEmpty()) gerentes = usuarioController.listarPorPerfil(Perfil.ADMINISTRADOR);
        System.out.println("  Gerentes disponiveis:");
        for (Usuario u : gerentes) {
            System.out.println("    " + u);
        }
        int gerenteId = Util.lerInteiro("  ID do gerente responsavel: ");
        Optional<Usuario> gerenteOpt = usuarioController.buscarPorId(gerenteId);
        Usuario gerente = gerenteOpt.orElse(p.getGerente());

        boolean ok = controller.atualizar(id, nome, descricao, dataInicio, dataTermino, status, gerente);
        System.out.println(ok ? "  Projeto atualizado com sucesso!" : "  Erro ao atualizar projeto.");
        Util.pausar();
    }

    private void remover() {
        Util.imprimirTitulo("REMOVER PROJETO");
        listarSimples();

        int id = Util.lerInteiro("  ID do projeto a remover (0 para cancelar): ");
        if (id == 0) return;

        Optional<Projeto> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Projeto nao encontrado.");
            Util.pausar();
            return;
        }

        System.out.println("  Projeto: " + opt.get());
        if (!opt.get().getTarefas().isEmpty()) {
            System.out.printf("  Atencao: Este projeto possui %d tarefa(s) vinculada(s).%n",
                    opt.get().getTarefas().size());
        }

        if (Util.confirmar("  Confirmar remocao?")) {
            boolean ok = controller.remover(id);
            System.out.println(ok ? "  Projeto removido com sucesso!" : "  Erro ao remover projeto.");
        } else {
            System.out.println("  Operacao cancelada.");
        }
        Util.pausar();
    }

    private StatusProjeto selecionarStatus() {
        System.out.println("  Status disponiveis:");
        StatusProjeto[] status = StatusProjeto.values();
        for (int i = 0; i < status.length; i++) {
            System.out.printf("    %d. %s%n", i + 1, status[i].getDescricao());
        }
        int idx = Util.lerInteiroNoIntervalo("  Status: ", 1, status.length);
        return status[idx - 1];
    }

    private Usuario selecionarGerente(Usuario usuarioLogado) {
        List<Usuario> gerentes = usuarioController.listarPorPerfil(Perfil.GERENTE);
        List<Usuario> admins = usuarioController.listarPorPerfil(Perfil.ADMINISTRADOR);
        gerentes.addAll(admins);

        if (gerentes.isEmpty()) {
            System.out.println("  Nenhum gerente encontrado. Usando usuario logado como gerente.");
            return usuarioLogado;
        }

        System.out.println("  Gerentes disponiveis:");
        for (Usuario u : gerentes) {
            System.out.println("    " + u);
        }
        int gerenteId = Util.lerInteiro("  ID do gerente responsavel: ");
        return usuarioController.buscarPorId(gerenteId).orElse(usuarioLogado);
    }

    private void listarSimples() {
        List<Projeto> lista = controller.listarTodos();
        if (!lista.isEmpty()) {
            System.out.println("  Projetos cadastrados:");
            for (Projeto p : lista) {
                System.out.println("  " + p);
            }
            Util.imprimirLinha();
        }
    }
}
