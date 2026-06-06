package com.oracle.gestao.view;

import com.oracle.gestao.controller.UsuarioController;
import com.oracle.gestao.model.Perfil;
import com.oracle.gestao.model.Usuario;
import com.oracle.gestao.util.Util;

import java.util.List;
import java.util.Optional;

public class UsuarioView {

    private final UsuarioController controller;

    public UsuarioView(UsuarioController controller) {
        this.controller = controller;
    }

    public void menu(Usuario usuarioLogado) {
        boolean continuar = true;
        while (continuar) {
            Util.imprimirTitulo("GESTAO DE USUARIOS");
            System.out.println("  1. Listar usuarios");
            System.out.println("  2. Cadastrar usuario");
            System.out.println("  3. Atualizar usuario");
            System.out.println("  4. Remover usuario");
            System.out.println("  0. Voltar");
            Util.imprimirLinha();

            int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 4);
            switch (opcao) {
                case 1 -> listar();
                case 2 -> cadastrar();
                case 3 -> atualizar(usuarioLogado);
                case 4 -> remover(usuarioLogado);
                case 0 -> continuar = false;
            }
        }
    }

    private void listar() {
        Util.imprimirTitulo("LISTA DE USUARIOS");
        List<Usuario> lista = controller.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("  Nenhum usuario cadastrado.");
        } else {
            for (Usuario u : lista) {
                System.out.println("  " + u);
            }
        }
        Util.pausar();
    }

    private void cadastrar() {
        Util.imprimirTitulo("CADASTRAR USUARIO");

        String nome = Util.lerStringObrigatoria("  Nome completo: ");

        String cpf;
        while (true) {
            cpf = Util.lerStringObrigatoria("  CPF: ");
            if (!Util.validarCpf(cpf)) {
                System.out.println("  CPF invalido. Tente novamente.");
            } else if (controller.cpfExiste(cpf)) {
                System.out.println("  CPF ja cadastrado. Tente novamente.");
            } else {
                break;
            }
        }

        String email;
        while (true) {
            email = Util.lerStringObrigatoria("  E-mail: ");
            if (!Util.validarEmail(email)) {
                System.out.println("  E-mail invalido. Tente novamente.");
            } else if (controller.emailExiste(email)) {
                System.out.println("  E-mail ja cadastrado. Tente novamente.");
            } else {
                break;
            }
        }

        String cargo = " ";

        String login;
        while (true) {
            login = Util.lerStringObrigatoria("  Login: ");
            if (controller.loginExiste(login)) {
                System.out.println("  Login ja em uso. Tente outro.");
            } else {
                break;
            }
        }

        String senha = Util.lerStringObrigatoria("  Senha: ");

        Perfil perfil = selecionarPerfil();

        Usuario u = controller.cadastrar(nome, cpf, email, cargo, login, senha, perfil);
        System.out.println("\n  Usuario cadastrado com sucesso! ID: " + u.getId());
        Util.pausar();
    }

    private void atualizar(Usuario usuarioLogado) {
        Util.imprimirTitulo("ATUALIZAR USUARIO");
        listarSimples();

        int id = Util.lerInteiro("  ID do usuario a atualizar (0 para cancelar): ");
        if (id == 0) return;

        Optional<Usuario> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Usuario nao encontrado.");
            Util.pausar();
            return;
        }

        Usuario u = opt.get();
        System.out.println("  Usuario atual: " + u);
        System.out.println("  (Deixe em branco para manter o valor atual)");

        String nome = Util.lerString("  Novo nome completo [" + u.getNomeCompleto() + "]: ");
        if (nome.isEmpty()) nome = u.getNomeCompleto();

        String email = Util.lerString("  Novo e-mail [" + u.getEmail() + "]: ");
        if (email.isEmpty()) {
            email = u.getEmail();
        } else if (!Util.validarEmail(email)) {
            System.out.println("  E-mail invalido. Operacao cancelada.");
            Util.pausar();
            return;
        }

        String cargo = Util.lerString("  Novo cargo [" + u.getCargo() + "]: ");
        if (cargo.isEmpty()) cargo = u.getCargo();

        String login = Util.lerString("  Novo login [" + u.getLogin() + "]: ");
        if (login.isEmpty()) {
            login = u.getLogin();
        } else if (controller.loginExiste(login) && !login.equalsIgnoreCase(u.getLogin())) {
            System.out.println("  Login ja em uso. Operacao cancelada.");
            Util.pausar();
            return;
        }

        String senha = Util.lerString("  Nova senha (ENTER para manter): ");

        System.out.println("  Perfil atual: " + u.getPerfil().getDescricao());
        Perfil perfil = selecionarPerfil();

        boolean ok = controller.atualizar(id, nome, u.getCpf(), email, cargo, login, senha, perfil);
        System.out.println(ok ? "  Usuario atualizado com sucesso!" : "  Erro ao atualizar usuario.");
        Util.pausar();
    }

    private void remover(Usuario usuarioLogado) {
        Util.imprimirTitulo("REMOVER USUARIO");
        listarSimples();

        int id = Util.lerInteiro("  ID do usuario a remover (0 para cancelar): ");
        if (id == 0) return;

        if (id == usuarioLogado.getId()) {
            System.out.println("  Nao e possivel remover o usuario logado.");
            Util.pausar();
            return;
        }

        Optional<Usuario> opt = controller.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("  Usuario nao encontrado.");
            Util.pausar();
            return;
        }

        System.out.println("  Usuario: " + opt.get());
        if (Util.confirmar("  Confirmar remocao?")) {
            boolean ok = controller.remover(id);
            System.out.println(ok ? "  Usuario removido com sucesso!" : "  Erro ao remover usuario.");
        } else {
            System.out.println("  Operacao cancelada.");
        }
        Util.pausar();
    }

    private Perfil selecionarPerfil() {
        System.out.println("  Perfis disponiveis:");
        Perfil[] perfis = Perfil.values();
        for (int i = 0; i < perfis.length; i++) {
            System.out.printf("    %d. %s%n", i + 1, perfis[i].getDescricao());
        }
        int idx = Util.lerInteiroNoIntervalo("  Perfil: ", 1, perfis.length);
        return perfis[idx - 1];
    }

    private void listarSimples() {
        List<Usuario> lista = controller.listarTodos();
        if (!lista.isEmpty()) {
            System.out.println("  Usuarios cadastrados:");
            for (Usuario u : lista) {
                System.out.println("  " + u);
            }
            Util.imprimirLinha();
        }
    }
}
