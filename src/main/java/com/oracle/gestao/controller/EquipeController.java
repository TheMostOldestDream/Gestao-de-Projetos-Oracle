package com.oracle.gestao.controller;

import com.oracle.gestao.model.Equipe;
import com.oracle.gestao.model.Projeto;
import com.oracle.gestao.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipeController {

    private final List<Equipe> equipes;
    private int proximoId;

    public EquipeController() {
        this.equipes = new ArrayList<>();
        this.proximoId = 1;
    }

    public Equipe cadastrar(String nome, String descricao) {
        Equipe e = new Equipe(proximoId++, nome, descricao);
        equipes.add(e);
        return e;
    }

    public boolean atualizar(int id, String nome, String descricao) {
        Optional<Equipe> opt = buscarPorId(id);
        if (opt.isEmpty()) return false;
        Equipe e = opt.get();
        e.setNome(nome);
        e.setDescricao(descricao);
        return true;
    }

    public boolean remover(int id) {
        return equipes.removeIf(e -> e.getId() == id);
    }

    public Optional<Equipe> buscarPorId(int id) {
        return equipes.stream().filter(e -> e.getId() == id).findFirst();
    }

    public List<Equipe> listarTodos() {
        return new ArrayList<>(equipes);
    }

    public boolean adicionarMembro(int equipeId, Usuario usuario) {
        Optional<Equipe> opt = buscarPorId(equipeId);
        if (opt.isEmpty()) return false;
        opt.get().adicionarMembro(usuario);
        return true;
    }

    public boolean removerMembro(int equipeId, Usuario usuario) {
        Optional<Equipe> opt = buscarPorId(equipeId);
        if (opt.isEmpty()) return false;
        opt.get().removerMembro(usuario);
        return true;
    }

    public boolean alocarNoProjeto(int equipeId, Projeto projeto) {
        Optional<Equipe> opt = buscarPorId(equipeId);
        if (opt.isEmpty()) return false;
        opt.get().adicionarProjeto(projeto);
        return true;
    }

    public boolean desalocarDoProjeto(int equipeId, Projeto projeto) {
        Optional<Equipe> opt = buscarPorId(equipeId);
        if (opt.isEmpty()) return false;
        opt.get().removerProjeto(projeto);
        return true;
    }

    public List<Equipe> listarPorProjeto(Projeto projeto) {
        List<Equipe> resultado = new ArrayList<>();
        for (Equipe e : equipes) {
            if (e.getProjetos().contains(projeto)) resultado.add(e);
        }
        return resultado;
    }
}
