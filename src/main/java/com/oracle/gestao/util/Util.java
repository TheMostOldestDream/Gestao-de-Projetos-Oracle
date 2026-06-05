package com.oracle.gestao.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Util {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Scanner scanner = new Scanner(System.in);

    private Util() {}

    public static String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String lerStringObrigatoria(String prompt) {
        String valor;
        do {
            System.out.print(prompt);
            valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("  Campo obrigatorio. Tente novamente.");
            }
        } while (valor.isEmpty());
        return valor;
    }

    public static int lerInteiro(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("  Valor invalido. Digite um numero inteiro.");
            }
        }
    }

    public static int lerInteiroNoIntervalo(String prompt, int min, int max) {
        int valor;
        do {
            valor = lerInteiro(prompt);
            if (valor < min || valor > max) {
                System.out.printf("  Opcao invalida. Escolha entre %d e %d.%n", min, max);
            }
        } while (valor < min || valor > max);
        return valor;
    }

    public static LocalDate lerData(String prompt) {
        while (true) {
            System.out.print(prompt + " (dd/MM/yyyy): ");
            String entrada = scanner.nextLine().trim();
            try {
                return LocalDate.parse(entrada, FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("  Data invalida. Use o formato dd/MM/yyyy.");
            }
        }
    }

    public static String formatarData(LocalDate data) {
        if (data == null) return "N/A";
        return data.format(FORMATTER);
    }

    public static boolean confirmar(String mensagem) {
        System.out.print(mensagem + " (s/n): ");
        String resposta = scanner.nextLine().trim().toLowerCase();
        return resposta.equals("s") || resposta.equals("sim");
    }

    public static void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    public static void imprimirLinha() {
        System.out.println("--------------------------------------------------");
    }

    public static void imprimirTitulo(String titulo) {
        System.out.println();
        imprimirLinha();
        System.out.println("  " + titulo.toUpperCase());
        imprimirLinha();
    }

    public static boolean validarCpf(String cpf) {
        String digits = cpf.replaceAll("[^0-9]", "");
        if (digits.length() != 11) return false;
        // Verifica sequencias iguais
        if (digits.matches("(\\d)\\1{10}")) return false;
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(digits.charAt(i)) * (10 - i);
        }
        int r1 = (soma * 10) % 11;
        if (r1 == 10) r1 = 0;
        if (r1 != Character.getNumericValue(digits.charAt(9))) return false;
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(digits.charAt(i)) * (11 - i);
        }
        int r2 = (soma * 10) % 11;
        if (r2 == 10) r2 = 0;
        return r2 == Character.getNumericValue(digits.charAt(10));
    }

    public static boolean validarEmail(String email) {
        return email != null && email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }
}
