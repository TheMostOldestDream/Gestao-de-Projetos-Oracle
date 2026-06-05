package com.oracle.gestao.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Equipe {

    private int id;
    private String nome;
    private String descricao;
    private List<Usuario> membros;
    private List<Projeto> projetos;

    public Equipe(int id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
        this.projetos = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Usuario> getMembros() { return membros; }
    public void adicionarMembro(Usuario usuario) {
        if (!membros.contains(usuario)) {
            membros.add(usuario);
        }
    }
    public void removerMembro(Usuario usuario) { membros.remove(usuario); }

    public List<Projeto> getProjetos() { return projetos; }
    public void adicionarProjeto(Projeto projeto) {
        if (!projetos.contains(projeto)) {
            projetos.add(projeto);
        }
    }
    public void removerProjeto(Projeto projeto) { projetos.remove(projeto); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipe equipe = (Equipe) o;
        return id == equipe.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("[%d] %s | Membros: %d | Projetos: %d | Descricao: %s",
                id, nome, membros.size(), projetos.size(), descricao);
    }
}
