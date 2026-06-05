package com.oracle.gestao.controller;

import com.oracle.gestao.model.*;
import com.oracle.gestao.util.Util;

import java.time.LocalDate;
import java.util.List;

public class RelatorioController {

    private final ProjetoController projetoController;
    private final TarefaController tarefaController;
    private final EquipeController equipeController;

    public RelatorioController(ProjetoController pc, TarefaController tc,
                               EquipeController ec) {
        this.projetoController = pc;
        this.tarefaController = tc;
        this.equipeController = ec;
    }

    public void relatorioGeralProjetos() {
        Util.imprimirTitulo("RELATORIO GERAL DE PROJETOS");
        List<Projeto> projetos = projetoController.listarTodos();
        if (projetos.isEmpty()) {
            System.out.println("  Nenhum projeto cadastrado.");
            return;
        }
        System.out.printf("  Total de projetos: %d%n", projetos.size());
        for (StatusProjeto s : StatusProjeto.values()) {
            long count = projetos.stream().filter(p -> p.getStatus() == s).count();
            System.out.printf("  %-20s: %d%n", s.getDescricao(), count);
        }
        Util.imprimirLinha();
        for (Projeto p : projetos) {
            System.out.println("  " + p);
            System.out.printf("    Tarefas - Total: %d | Pendente: %d | Em Andamento: %d | Concluida: %d%n",
                    p.getTarefas().size(),
                    p.contarTarefasPorStatus(StatusTarefa.PENDENTE),
                    p.contarTarefasPorStatus(StatusTarefa.EM_ANDAMENTO),
                    p.contarTarefasPorStatus(StatusTarefa.CONCLUIDA));
        }
    }

    public void relatorioDetalhadoProjeto(Projeto projeto) {
        Util.imprimirTitulo("RELATORIO DETALHADO - " + projeto.getNome());
        System.out.printf("  ID             : %d%n", projeto.getId());
        System.out.printf("  Nome           : %s%n", projeto.getNome());
        System.out.printf("  Descricao      : %s%n", projeto.getDescricao());
        System.out.printf("  Inicio         : %s%n", Util.formatarData(projeto.getDataInicio()));
        System.out.printf("  Termino Prev.  : %s%n", Util.formatarData(projeto.getDataTerminoPrevista()));
        System.out.printf("  Status         : %s%n", projeto.getStatus().getDescricao());
        System.out.printf("  Gerente        : %s%n", projeto.getGerente().getNomeCompleto());

        // Verificar atraso
        if (projeto.getStatus() != StatusProjeto.CONCLUIDO &&
            projeto.getStatus() != StatusProjeto.CANCELADO &&
            LocalDate.now().isAfter(projeto.getDataTerminoPrevista())) {
            System.out.println("  ** ATENCAO: Projeto com prazo vencido! **");
        }

        // Tarefas
        Util.imprimirLinha();
        System.out.printf("  Tarefas (%d):%n", projeto.getTarefas().size());
        if (projeto.getTarefas().isEmpty()) {
            System.out.println("    Nenhuma tarefa cadastrada neste projeto.");
        } else {
            for (Tarefa t : projeto.getTarefas()) {
                System.out.println("    " + t);
            }
        }

        // Equipes alocadas
        Util.imprimirLinha();
        List<Equipe> equipes = equipeController.listarPorProjeto(projeto);
        System.out.printf("  Equipes alocadas (%d):%n", equipes.size());
        if (equipes.isEmpty()) {
            System.out.println("    Nenhuma equipe alocada neste projeto.");
        } else {
            for (Equipe e : equipes) {
                System.out.println("    " + e);
                for (Usuario m : e.getMembros()) {
                    System.out.println("      - " + m.getNomeCompleto() + " (" + m.getCargo() + ")");
                }
            }
        }
    }

    public void relatorioColaborador(Usuario usuario) {
        Util.imprimirTitulo("RELATORIO DO COLABORADOR - " + usuario.getNomeCompleto());
        System.out.printf("  Nome   : %s%n", usuario.getNomeCompleto());
        System.out.printf("  Cargo  : %s%n", usuario.getCargo());
        System.out.printf("  E-mail : %s%n", usuario.getEmail());
        System.out.printf("  Perfil : %s%n", usuario.getPerfil().getDescricao());

        List<Tarefa> tarefas = tarefaController.listarPorResponsavel(usuario);
        Util.imprimirLinha();
        System.out.printf("  Tarefas atribuidas (%d):%n", tarefas.size());
        if (tarefas.isEmpty()) {
            System.out.println("    Nenhuma tarefa atribuida.");
        } else {
            for (Tarefa t : tarefas) {
                System.out.printf("    [Projeto: %s] %s%n", t.getProjeto().getNome(), t);
            }
        }
    }

    public void relatorioEquipes() {
        Util.imprimirTitulo("RELATORIO DE EQUIPES");
        List<Equipe> equipes = equipeController.listarTodos();
        if (equipes.isEmpty()) {
            System.out.println("  Nenhuma equipe cadastrada.");
            return;
        }
        System.out.printf("  Total de equipes: %d%n", equipes.size());
        Util.imprimirLinha();
        for (Equipe e : equipes) {
            System.out.println("  " + e);
            System.out.printf("    Membros (%d):%n", e.getMembros().size());
            for (Usuario m : e.getMembros()) {
                System.out.printf("      - %s | %s | %s%n",
                        m.getNomeCompleto(), m.getCargo(), m.getPerfil().getDescricao());
            }
            System.out.printf("    Projetos (%d):%n", e.getProjetos().size());
            for (Projeto p : e.getProjetos()) {
                System.out.printf("      - %s [%s]%n", p.getNome(), p.getStatus().getDescricao());
            }
        }
    }

    public void relatorioTarefasVencidas() {
        Util.imprimirTitulo("RELATORIO DE TAREFAS COM PRAZO VENCIDO");
        LocalDate hoje = LocalDate.now();
        List<Tarefa> tarefas = tarefaController.listarTodos();
        boolean encontrou = false;
        for (Tarefa t : tarefas) {
            if ((t.getStatus() == StatusTarefa.PENDENTE || t.getStatus() == StatusTarefa.EM_ANDAMENTO)
                    && hoje.isAfter(t.getDataTerminoPrevista())) {
                System.out.printf("  [Projeto: %s] %s%n", t.getProjeto().getNome(), t);
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("  Nenhuma tarefa com prazo vencido encontrada.");
        }
    }
}
