package com.oracle.gestao.view;

import java.util.Optional;

import com.oracle.gestao.controller.*;
import com.oracle.gestao.model.Perfil;
import com.oracle.gestao.model.Usuario;
import com.oracle.gestao.util.Util;

public class MenuPrincipal {

    private final UsuarioController usuarioController;
    private final ProjetoController projetoController;
    private final TarefaController tarefaController;
    private final EquipeController equipeController;
    private final RelatorioController relatorioController;

    private final UsuarioView usuarioView;
    private final ProjetoView projetoView;
    private final TarefaView tarefaView;
    private final EquipeView equipeView;
    private final RelatorioView relatorioView;

    private Usuario usuarioLogado;

    public MenuPrincipal() {
        this.usuarioController = new UsuarioController();
        this.projetoController = new ProjetoController();
        this.tarefaController = new TarefaController();
        this.equipeController = new EquipeController();
        this.relatorioController = new RelatorioController(
                projetoController, tarefaController, equipeController);

        this.usuarioView = new UsuarioView(usuarioController);
        this.projetoView = new ProjetoView(projetoController, usuarioController);
        this.tarefaView = new TarefaView(tarefaController, projetoController, usuarioController);
        this.equipeView = new EquipeView(equipeController, usuarioController, projetoController);
        this.relatorioView = new RelatorioView(relatorioController, projetoController, usuarioController);
    }

    public void iniciar() {
        

        // Se nao existe nenhum usuario, obriga o cadastro do primeiro admin
        if (usuarioController.listaVazia()) {
            cadastrarPrimeiroAdmin();
        }

        // Loop de autenticacao
        boolean autenticado = false;
        while (!autenticado) {
            autenticado = menuAcesso();
            if (!autenticado) {
                if (!Util.confirmar("  Deseja tentar novamente?")) {
                    System.out.println("  Sistema encerrado.");
                    return;
                }
            }
        }

        executarMenu();
    }

    // ------------------------------------------------------------------
    // Tela de primeiro acesso
    // ------------------------------------------------------------------

    private void cadastrarPrimeiroAdmin() {
        Util.imprimirTitulo("PRIMEIRO ACESSO - CADASTRO DO ADMINISTRADOR");
        System.out.println("  Cadastre o administrador do sistema.");
        Util.imprimirLinha();

        String nome = Util.lerStringObrigatoria("  Nome completo: ");
        String email;
        while (true) {
            email = Util.lerStringObrigatoria("  E-mail: ");
            if (Util.validarEmail(email)) break;
            System.out.println("  E-mail invalido. Tente novamente.");
        }
        String cargo = "Admnistrador";
        String login = Util.lerStringObrigatoria("  Login: ");
        String senha = Util.lerStringObrigatoria("  Senha: ");

        // CPF opcional na tela de primeiro acesso — aceita qualquer valor ou deixa vazio
        String cpf = Util.lerString("  CPF (somente numeros): ");
        if (cpf.isEmpty()) cpf = "00000000000";

        usuarioController.cadastrar(nome, cpf, email, cargo, login, senha, Perfil.ADMINISTRADOR);
        System.out.println("\n  Administrador cadastrado! Faca login para continuar.");
        Util.pausar();
    }

    // ------------------------------------------------------------------
    // Tela de acesso: Login ou Cadastro
    // ------------------------------------------------------------------

    private boolean menuAcesso() {
        Util.imprimirTitulo("ACESSO AO SISTEMA");
        System.out.println("  1. Login");
        System.out.println("  2. Cadastrar novo usuario");
        System.out.println("  0. Sair");
        Util.imprimirLinha();

        int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 2);
        switch (opcao) {
            case 1 -> { return fazerLogin(); }
            case 2 -> {
                cadastrarNovoUsuario();
                return false; // volta para o menu de acesso apos cadastrar
            }
            case 0 -> {
                System.out.println("  Sistema encerrado.");
                System.exit(0);
            }
        }
        return false;
    }

    private boolean fazerLogin() {
        Util.imprimirTitulo("LOGIN");
        int tentativas = 0;
        while (tentativas < 3) {
            String login = Util.lerStringObrigatoria("  Login: ");
            String senha = Util.lerStringObrigatoria("  Senha: ");

            Optional<Usuario> opt = usuarioController.autenticar(login, senha);
            if (opt.isPresent()) {
                usuarioLogado = opt.get();
                System.out.printf("%n  Bem-vindo(a), %s! [%s]%n",
                        usuarioLogado.getNomeCompleto(),
                        usuarioLogado.getPerfil().getDescricao());
                Util.pausar();
                return true;
            }
            tentativas++;
            System.out.printf("  Login ou senha incorretos. Tentativa %d de 3.%n", tentativas);
        }
        System.out.println("  Numero maximo de tentativas atingido.");
        return false;
    }

    private void cadastrarNovoUsuario() {
        Util.imprimirTitulo("CADASTRO DE USUARIO");

        String nome = Util.lerStringObrigatoria("  Nome completo: ");

        String cpf;
        while (true) {
            cpf = Util.lerStringObrigatoria("  CPF: ");
            if (!Util.validarCpf(cpf)) {
                System.out.println("  CPF invalido. Tente novamente.");
            } else if (usuarioController.cpfExiste(cpf)) {
                System.out.println("  CPF ja cadastrado.");
            } else {
                break;
            }
        }

        String email;
        while (true) {
            email = Util.lerStringObrigatoria("  E-mail: ");
            if (!Util.validarEmail(email)) {
                System.out.println("  E-mail invalido. Tente novamente.");
            } else if (usuarioController.emailExiste(email)) {
                System.out.println("  E-mail ja cadastrado.");
            } else {
                break;
            }
        }

        String cargo = " ";

        String login;
        while (true) {
            login = Util.lerStringObrigatoria("  Login: ");
            if (usuarioController.loginExiste(login)) {
                System.out.println("  Login ja em uso. Escolha outro.");
            } else {
                break;
            }
        }

        String senha = Util.lerStringObrigatoria("  Senha: ");

        // Perfil disponivel apenas para Colaborador no auto-cadastro;
        // somente um admin logado pode promover perfis (via Gestao de Usuarios)
        Perfil perfil = Perfil.COLABORADOR;
        System.out.println("  Perfil definido automaticamente: Colaborador");
        System.out.println("  (Um administrador pode alterar seu perfil apos o login)");

        Usuario u = usuarioController.cadastrar(nome, cpf, email, cargo, login, senha, perfil);
        System.out.printf("%n  Usuario '%s' cadastrado com sucesso! Faca login para acessar.%n",
                u.getLogin());
        Util.pausar();
    }

    // ------------------------------------------------------------------
    // Menu principal pos-login
    // ------------------------------------------------------------------

    private void executarMenu() {
        boolean continuar = true;
        while (continuar) {
            Util.imprimirTitulo("MENU PRINCIPAL");
            System.out.printf("  Usuario: %s | Perfil: %s%n",
                    usuarioLogado.getNomeCompleto(),
                    usuarioLogado.getPerfil().getDescricao());
            Util.imprimirLinha();

            if (usuarioLogado.getPerfil() == Perfil.ADMINISTRADOR) {
                exibirMenuAdmin();
                int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 5);
                continuar = processarMenuAdmin(opcao);
            } else if (usuarioLogado.getPerfil() == Perfil.GERENTE) {
                exibirMenuGerente();
                int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 5);
                continuar = processarMenuGerente(opcao);
            } else {
                exibirMenuColaborador();
                int opcao = Util.lerInteiroNoIntervalo("  Opcao: ", 0, 2);
                continuar = processarMenuColaborador(opcao);
            }
        }
        System.out.println("\n  Ate logo, " + usuarioLogado.getNomeCompleto() + "!");
    }

    private void exibirMenuAdmin() {
        System.out.println("  1. Gestao de Usuarios");
        System.out.println("  2. Gestao de Projetos");
        System.out.println("  3. Gestao de Tarefas");
        System.out.println("  4. Gestao de Equipes");
        System.out.println("  5. Relatorios");
        System.out.println("  0. Sair");
        Util.imprimirLinha();
    }

    private boolean processarMenuAdmin(int opcao) {
        switch (opcao) {
            case 1 -> usuarioView.menu(usuarioLogado);
            case 2 -> projetoView.menu(usuarioLogado);
            case 3 -> tarefaView.menu(usuarioLogado);
            case 4 -> equipeView.menu(usuarioLogado);
            case 5 -> relatorioView.menu();
            case 0 -> { return false; }
        }
        return true;
    }

    private void exibirMenuGerente() {
        System.out.println("  1. Gestao de Projetos");
        System.out.println("  2. Gestao de Tarefas");
        System.out.println("  3. Gestao de Equipes");
        System.out.println("  4. Relatorios");
        System.out.println("  5. Alterar minha senha");
        System.out.println("  0. Sair");
        Util.imprimirLinha();
    }

    private boolean processarMenuGerente(int opcao) {
        switch (opcao) {
            case 1 -> projetoView.menu(usuarioLogado);
            case 2 -> tarefaView.menu(usuarioLogado);
            case 3 -> equipeView.menu(usuarioLogado);
            case 4 -> relatorioView.menu();
            case 5 -> alterarSenha();
            case 0 -> { return false; }
        }
        return true;
    }

    private void exibirMenuColaborador() {
        System.out.println("  1. Minhas tarefas");
        System.out.println("  2. Visualizar projetos");
        System.out.println("  0. Sair");
        Util.imprimirLinha();
    }

    private boolean processarMenuColaborador(int opcao) {
        switch (opcao) {
            case 1 -> {
                relatorioController.relatorioColaborador(usuarioLogado);
                Util.pausar();
            }
            case 2 -> {
                relatorioController.relatorioGeralProjetos();
                Util.pausar();
            }
            case 0 -> { return false; }
        }
        return true;
    }

    private void alterarSenha() {
        Util.imprimirTitulo("ALTERAR SENHA");
        String senhaAtual = Util.lerStringObrigatoria("  Senha atual: ");
        if (!senhaAtual.equals(usuarioLogado.getSenha())) {
            System.out.println("  Senha incorreta. Operacao cancelada.");
            Util.pausar();
            return;
        }
        String novaSenha = Util.lerStringObrigatoria("  Nova senha: ");
        String confirmacao = Util.lerStringObrigatoria("  Confirmar nova senha: ");
        if (!novaSenha.equals(confirmacao)) {
            System.out.println("  As senhas nao conferem. Operacao cancelada.");
            Util.pausar();
            return;
        }
        usuarioLogado.setSenha(novaSenha);
        System.out.println("  Senha alterada com sucesso!");
        Util.pausar();
    }
}
