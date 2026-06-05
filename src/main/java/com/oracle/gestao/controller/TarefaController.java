package com.oracle.gestao.controller;

import com.oracle.gestao.model.Projeto;
import com.oracle.gestao.model.StatusTarefa;
import com.oracle.gestao.model.Tarefa;
import com.oracle.gestao.model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaController {

    private final List<Tarefa> tarefas;
    private int proximoId;

    public TarefaController() {
        this.tarefas = new ArrayList<>();
        this.proximoId = 1;
    }

    public Tarefa cadastrar(String titulo, String descricao, LocalDate dataInicio,
                            LocalDate dataTermino, StatusTarefa status,
                            Usuario responsavel, Projeto projeto) {
        Tarefa t = new Tarefa(proximoId++, titulo, descricao, dataInicio,
                dataTermino, status, responsavel, projeto);
        projeto.adicionarTarefa(t);
        tarefas.add(t);
        return t;
    }

    public boolean atualizar(int id, String titulo, String descricao, LocalDate dataInicio,
                             LocalDate dataTermino, StatusTarefa status,
                             Usuario responsavel, Projeto projeto) {
        Optional<Tarefa> opt = buscarPorId(id);
        if (opt.isEmpty()) return false;
        Tarefa t = opt.get();
        // Remove da lista anterior se trocou de projeto
        if (!t.getProjeto().equals(projeto)) {
            t.getProjeto().removerTarefa(t);
            projeto.adicionarTarefa(t);
        }
        t.setTitulo(titulo);
        t.setDescricao(descricao);
        t.setDataInicio(dataInicio);
        t.setDataTerminoPrevista(dataTermino);
        t.setStatus(status);
        t.setResponsavel(responsavel);
        t.setProjeto(projeto);
        return true;
    }

    public boolean remover(int id) {
        Optional<Tarefa> opt = buscarPorId(id);
        opt.ifPresent(t -> t.getProjeto().removerTarefa(t));
        return tarefas.removeIf(t -> t.getId() == id);
    }

    public Optional<Tarefa> buscarPorId(int id) {
        return tarefas.stream().filter(t -> t.getId() == id).findFirst();
    }

    public List<Tarefa> listarTodos() {
        return new ArrayList<>(tarefas);
    }

    public List<Tarefa> listarPorProjeto(Projeto projeto) {
        List<Tarefa> resultado = new ArrayList<>();
        for (Tarefa t : tarefas) {
            if (t.getProjeto().equals(projeto)) resultado.add(t);
        }
        return resultado;
    }

    public List<Tarefa> listarPorResponsavel(Usuario usuario) {
        List<Tarefa> resultado = new ArrayList<>();
        for (Tarefa t : tarefas) {
            if (t.getResponsavel() != null && t.getResponsavel().equals(usuario)) resultado.add(t);
        }
        return resultado;
    }

    public List<Tarefa> listarPorStatus(StatusTarefa status) {
        List<Tarefa> resultado = new ArrayList<>();
        for (Tarefa t : tarefas) {
            if (t.getStatus() == status) resultado.add(t);
        }
        return resultado;
    }
}
