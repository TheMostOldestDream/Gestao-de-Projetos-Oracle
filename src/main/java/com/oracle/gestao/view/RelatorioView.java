package com.oracle.gestao.view;

import com.oracle.gestao.controller.ProjetoController;
import com.oracle.gestao.controller.RelatorioController;
import com.oracle.gestao.controller.UsuarioController;
import com.oracle.gestao.model.Projeto;
import com.oracle.gestao.model.Usuario;
import com.oracle.gestao.util.Util;

import java.util.List;
import java.util.Optional;

public class RelatorioView {

    private final RelatorioController controller;
    private final ProjetoController projetoController;
    private final UsuarioController usuarioController;

    public RelatorioView(RelatorioController controller, ProjetoController projetoController,
                         UsuarioController usuarioController) {
        this.controller = controller;
        this.projetoController = projetoController;
        this.usuarioController = usuarioController;
    }

    public void menu() {
        boolean continuar = true;
        while (continuar) {
            Util.imprimirTitulo("RELATORIOS");
            System.out.println("  1. Relatorio geral de projetos");
            System.out.println("  2. Relatorio detalhado de um projeto");
            System.out.println("  3. Relatorio por colaborador");
            System.out.println("  4. Relatorio de equipes");
            System.out.println("  5. Relatorio de tarefas com prazo vencido");
            System.out.println("  0. Voltar");
            Util.imprimirLinha();

            int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 5);
            switch (opcao) {
                case 1 -> relatorioGeral();
                case 2 -> relatorioDetalhado();
                case 3 -> relatorioColaborador();
                case 4 -> relatorioEquipes();
                case 5 -> relatorioTarefasVencidas();
                case 0 -> continuar = false;
            }
        }
    }

    private void relatorioGeral() {
        controller.relatorioGeralProjetos();
        Util.pausar();
    }

    private void relatorioDetalhado() {
        List<Projeto> projetos = projetoController.listarTodos();
        if (projetos.isEmpty()) {
            System.out.println("  Nenhum projeto cadastrado.");
            Util.pausar();
            return;
        }
        System.out.println("  Projetos:");
        for (Projeto p : projetos) {
            System.out.println("  " + p);
        }
        Util.imprimirLinha();
        int id = Util.lerInteiro("  ID do projeto (0 para cancelar): ");
        if (id == 0) return;
        Optional<Projeto> opt = projetoController.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Projeto nao encontrado.");
            Util.pausar();
            return;
        }
        controller.relatorioDetalhadoProjeto(opt.get());
        Util.pausar();
    }

    private void relatorioColaborador() {
        List<Usuario> usuarios = usuarioController.listarTodos();
        if (usuarios.isEmpty()) {
            System.out.println("  Nenhum usuario cadastrado.");
            Util.pausar();
            return;
        }
        for (Usuario u : usuarios) {
            System.out.println("  " + u);
        }
        Util.imprimirLinha();
        int id = Util.lerInteiro("  ID do colaborador (0 para cancelar): ");
        if (id == 0) return;
        Optional<Usuario> opt = usuarioController.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Usuario nao encontrado.");
            Util.pausar();
            return;
        }
        controller.relatorioColaborador(opt.get());
        Util.pausar();
    }

    private void relatorioEquipes() {
        controller.relatorioEquipes();
        Util.pausar();
    }

    private void relatorioTarefasVencidas() {
        controller.relatorioTarefasVencidas();
        Util.pausar();
    }
}
