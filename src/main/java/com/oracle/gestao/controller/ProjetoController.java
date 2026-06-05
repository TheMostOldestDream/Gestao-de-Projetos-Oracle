package com.oracle.gestao.controller;

import com.oracle.gestao.model.Projeto;
import com.oracle.gestao.model.StatusProjeto;
import com.oracle.gestao.model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjetoController {

    private final List<Projeto> projetos;
    private int proximoId;

    public ProjetoController() {
        this.projetos = new ArrayList<>();
        this.proximoId = 1;
    }

    public Projeto cadastrar(String nome, String descricao, LocalDate dataInicio,
                             LocalDate dataTermino, StatusProjeto status, Usuario gerente) {
        Projeto p = new Projeto(proximoId++, nome, descricao, dataInicio, dataTermino, status, gerente);
        projetos.add(p);
        return p;
    }

    public boolean atualizar(int id, String nome, String descricao, LocalDate dataInicio,
                             LocalDate dataTermino, StatusProjeto status, Usuario gerente) {
        Optional<Projeto> opt = buscarPorId(id);
        if (opt.isEmpty()) return false;
        Projeto p = opt.get();
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setDataInicio(dataInicio);
        p.setDataTerminoPrevista(dataTermino);
        p.setStatus(status);
        p.setGerente(gerente);
        return true;
    }

    public boolean remover(int id) {
        return projetos.removeIf(p -> p.getId() == id);
    }

    public Optional<Projeto> buscarPorId(int id) {
        return projetos.stream().filter(p -> p.getId() == id).findFirst();
    }

    public List<Projeto> listarTodos() {
        return new ArrayList<>(projetos);
    }

    public List<Projeto> listarPorStatus(StatusProjeto status) {
        List<Projeto> resultado = new ArrayList<>();
        for (Projeto p : projetos) {
            if (p.getStatus() == status) resultado.add(p);
        }
        return resultado;
    }

    public List<Projeto> listarPorGerente(Usuario gerente) {
        List<Projeto> resultado = new ArrayList<>();
        for (Projeto p : projetos) {
            if (p.getGerente().equals(gerente)) resultado.add(p);
        }
        return resultado;
    }
}
