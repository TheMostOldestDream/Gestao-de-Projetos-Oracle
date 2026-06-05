package com.oracle.gestao.controller;

import com.oracle.gestao.model.Perfil;
import com.oracle.gestao.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioController {

    private final List<Usuario> usuarios;
    private int proximoId;

    public UsuarioController() {
        this.usuarios = new ArrayList<>();
        this.proximoId = 1;
    }

    public boolean listaVazia() {
        return usuarios.isEmpty();
    }

    public Usuario cadastrar(String nomeCompleto, String cpf, String email,
                             String cargo, String login, String senha, Perfil perfil) {
        Usuario u = new Usuario(proximoId++, nomeCompleto, cpf, email, cargo, login, senha, perfil);
        usuarios.add(u);
        return u;
    }

    public boolean atualizar(int id, String nomeCompleto, String cpf, String email,
                             String cargo, String login, String senha, Perfil perfil) {
        Optional<Usuario> opt = buscarPorId(id);
        if (opt.isEmpty()) return false;
        Usuario u = opt.get();
        u.setNomeCompleto(nomeCompleto);
        u.setCpf(cpf);
        u.setEmail(email);
        u.setCargo(cargo);
        u.setLogin(login);
        if (senha != null && !senha.isBlank()) u.setSenha(senha);
        u.setPerfil(perfil);
        return true;
    }

    public boolean remover(int id) {
        return usuarios.removeIf(u -> u.getId() == id);
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarios.stream().filter(u -> u.getId() == id).findFirst();
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarios.stream().filter(u -> u.getLogin().equalsIgnoreCase(login)).findFirst();
    }

    public boolean loginExiste(String login) {
        return usuarios.stream().anyMatch(u -> u.getLogin().equalsIgnoreCase(login));
    }

    public boolean cpfExiste(String cpf) {
        return usuarios.stream().anyMatch(u -> u.getCpf().replaceAll("[^0-9]", "")
                .equals(cpf.replaceAll("[^0-9]", "")));
    }

    public boolean emailExiste(String email) {
        return usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login) && u.getSenha().equals(senha))
                .findFirst();
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    public List<Usuario> listarPorPerfil(Perfil perfil) {
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.getPerfil() == perfil) resultado.add(u);
        }
        return resultado;
    }
}
