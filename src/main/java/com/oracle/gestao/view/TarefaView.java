package com.oracle.gestao.view;

import com.oracle.gestao.controller.ProjetoController;
import com.oracle.gestao.controller.TarefaController;
import com.oracle.gestao.controller.UsuarioController;
import com.oracle.gestao.model.*;
import com.oracle.gestao.util.Util;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TarefaView {

    private final TarefaController controller;
    private final ProjetoController projetoController;
    private final UsuarioController usuarioController;

    public TarefaView(TarefaController controller, ProjetoController projetoController,
                      UsuarioController usuarioController) {
        this.controller = controller;
        this.projetoController = projetoController;
        this.usuarioController = usuarioController;
    }

    public void menu(Usuario usuarioLogado) {
        boolean continuar = true;
        while (continuar) {
            Util.imprimirTitulo("GESTAO DE TAREFAS");
            System.out.println("  1. Listar tarefas");
            System.out.println("  2. Cadastrar tarefa");
            System.out.println("  3. Atualizar tarefa");
            System.out.println("  4. Remover tarefa");
            System.out.println("  0. Voltar");
            Util.imprimirLinha();

            int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 4);
            switch (opcao) {
                case 1 -> listar();
                case 2 -> cadastrar();
                case 3 -> atualizar();
                case 4 -> remover();
                case 0 -> continuar = false;
            }
        }
    }

    private void listar() {
        Util.imprimirTitulo("LISTA DE TAREFAS");
        List<Tarefa> lista = controller.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  Nenhuma tarefa cadastrada.");
        } else {
            for (Tarefa t : lista) {
                System.out.printf("  [Projeto: %s] %s%n", t.getProjeto().getNome(), t);
            }
        }
        Util.pausar();
    }

    private void cadastrar() {
        Util.imprimirTitulo("CADASTRAR TAREFA");

        List<Projeto> projetos = projetoController.listarTodos();
        if (projetos.isEmpty()) {
            System.out.println("  Nenhum projeto cadastrado. Cadastre um projeto primeiro.");
            Util.pausar();
            return;
        }

        System.out.println("  Projetos disponiveis:");
        for (Projeto p : projetos) {
            System.out.println("  " + p);
        }
        Util.imprimirLinha();
        int projetoId = Util.lerInteiro("  ID do projeto: ");
        Optional<Projeto> projetoOpt = projetoController.buscarPorId(projetoId);
        if (projetoOpt.isEmpty()) {
            System.out.println("  Projeto nao encontrado.");
            Util.pausar();
            return;
        }
        Projeto projeto = projetoOpt.get();

        String titulo = Util.lerStringObrigatoria("  Titulo da tarefa: ");
        String descricao = Util.lerStringObrigatoria("  Descricao: ");
        LocalDate dataInicio = Util.lerData("  Data de inicio");
        LocalDate dataTermino;
        while (true) {
            dataTermino = Util.lerData("  Data de termino prevista");
            if (!dataTermino.isBefore(dataInicio)) break;
            System.out.println("  Data de termino deve ser igual ou posterior a data de inicio.");
        }

        StatusTarefa status = selecionarStatus();
        Usuario responsavel = selecionarResponsavel();

        Tarefa t = controller.cadastrar(titulo, descricao, dataInicio, dataTermino,
                status, responsavel, projeto);
        System.out.println("\n  Tarefa cadastrada com sucesso! ID: " + t.getId());
        Util.pausar();
    }

    private void atualizar() {
        Util.imprimirTitulo("ATUALIZAR TAREFA");
        listarSimples();

        int id = Util.lerInteiro("  ID da tarefa a atualizar (0 para cancelar): ");
        if (id == 0) return;

        Optional<Tarefa> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Tarefa nao encontrada.");
            Util.pausar();
            return;
        }

        Tarefa t = opt.get();
        System.out.println("  Tarefa atual: " + t);

        String titulo = Util.lerString("  Novo titulo [" + t.getTitulo() + "]: ");
        if (titulo.isEmpty()) titulo = t.getTitulo();

        String descricao = Util.lerString("  Nova descricao [" + t.getDescricao() + "]: ");
        if (descricao.isEmpty()) descricao = t.getDescricao();

        LocalDate dataInicio = Util.lerData("  Nova data de inicio");
        LocalDate dataTermino;
        do {
            dataTermino = Util.lerData("  Nova data de termino");
            if (dataTermino.isBefore(dataInicio))
                System.out.println("  Data de termino deve ser igual ou posterior a data de inicio.");
        } while (dataTermino.isBefore(dataInicio));

        StatusTarefa status = selecionarStatus();
        Usuario responsavel = selecionarResponsavel();

        boolean ok = controller.atualizar(id, titulo, descricao, dataInicio, dataTermino,
                status, responsavel, t.getProjeto());
        System.out.println(ok ? "  Tarefa atualizada com sucesso!" : "  Erro ao atualizar tarefa.");
        Util.pausar();
    }

    private void remover() {
        Util.imprimirTitulo("REMOVER TAREFA");
        listarSimples();

        int id = Util.lerInteiro("  ID da tarefa a remover (0 para cancelar): ");
        if (id == 0) return;

        Optional<Tarefa> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Tarefa nao encontrada.");
            Util.pausar();
            return;
        }

        System.out.println("  Tarefa: " + opt.get());
        if (Util.confirmar("  Confirmar remocao?")) {
            boolean ok = controller.remover(id);
            System.out.println(ok ? "  Tarefa removida com sucesso!" : "  Erro ao remover tarefa.");
        } else {
            System.out.println("  Operacao cancelada.");
        }
        Util.pausar();
    }

    private StatusTarefa selecionarStatus() {
        System.out.println("  Status disponiveis:");
        StatusTarefa[] status = StatusTarefa.values();
        for (int i = 0; i < status.length; i++) {
            System.out.printf("    %d. %s%n", i + 1, status[i].getDescricao());
        }
        int idx = Util.lerInteiroNoIntervalo("  Status: ", 1, status.length);
        return status[idx - 1];
    }

    private Usuario selecionarResponsavel() {
        System.out.println("  Responsaveis disponiveis:");
        List<Usuario> colaboradores = usuarioController.listarTodos();
        for (Usuario u : colaboradores) {
            System.out.println("    " + u);
        }
        System.out.println("  (0 = sem responsavel)");
        int uid = Util.lerInteiro("  ID do responsavel: ");
        if (uid == 0) return null;
        return usuarioController.buscarPorId(uid).orElse(null);
    }

    private void listarSimples() {
        List<Tarefa> lista = controller.listarTodos();
        if (!lista.isEmpty()) {
            for (Tarefa t : lista) {
                System.out.printf("  [Projeto: %s] %s%n", t.getProjeto().getNome(), t);
            }
            Util.imprimirLinha();
        }
    }
}
