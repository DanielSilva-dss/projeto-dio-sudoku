package br.com.pessoal;

import br.com.pessoal.model.Board;
import br.com.pessoal.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.util.stream.Stream;

import static br.com.pessoal.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static Board board;
    private static final int BOARD_LIMIT = 9;


    public static void main(String[] args) {
        final var positions = Stream.of(args)
                .collect(toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ));
        var option = -1;
        while (true) {
            System.out.println("Selecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar o Jogo atual");
            System.out.println("5 - Verificar status do Jogo");
            System.out.println("6 - Limpar o Jogo");
            System.out.println("7 - Finalizar o Jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Este número é invalido: Por favor selecione um número válido.");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("O Jogo já foi iniciado");
            return;
        }
        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }
        board = new Board(spaces);
        System.out.println("O Jogo iniciou");

        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col: board.getSpaces()) {
                args[argPos ++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }

        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void inputNumber() {
        if(isNull(board)) {
            System.out.println("O Jogo ainda não foi iniciado");
            return;
        }
        System.out.println("Informe a coluna onde o número será inserido");
        var col = runUtilGetValueNumber(0, 8);
        System.out.println("Informe a linha onde o número será inserido");
        var row = runUtilGetValueNumber(0, 8);
        System.out.printf("Infome o número na posição (%s,%s)\n", col, row);
        var value = runUtilGetValueNumber(1, 9);
        if(!board.changesValue(col, row, value)) {
            System.out.printf("A posição (%s,%s) tem valor fixo\n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda não foi iniciado");
            return;
        }
        System.out.println("Informe a coluna onde o número será removido");
        var col = runUtilGetValueNumber(0, 8);
        System.out.println("Informe a linha onde o número será removido");
        var row = runUtilGetValueNumber(0, 8);
        if (!board.clearValue(col, row)) {
            System.out.printf("A posição (%s,%s) tem valor fixo\n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda não foi iniciado");
            return;
        }
        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col: board.getSpaces()) {
                args[argPos ++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma: ");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda não foi iniciado");
            return;
        }
        System.out.printf("O Jogo atualmente se encontra %s\n", board.getStatus().getLabel());
        if (board.hasErrors()) {
            System.out.println("O Jogo contém erros");
        } else {
            System.out.println("O Jogo não contém erros");
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda não foi iniciado");
            return;
        }
        System.out.println("Tem certeza que quer limpar o Jogo?");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("Sim") && !confirm.equalsIgnoreCase("Não")) {
            System.out.println("Responda 'Sim' ou 'Não");
            confirm = scanner.next();
        }
        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
        }
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O Jogo ainda não foi iniciado");
            return;
        }
        if (board.gameIsFinished()) {
            System.out.println("Parabéns você completou o Jogo! xD");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu Jogo contém erro(s), tente corrigi-lo");
        } else {
            System.out.println("Você precisa preencher todos os espaços vazios!");
        }
    }

    private static int runUtilGetValueNumber(final int min, final int max) {
        var current = scanner.nextInt();
        while(current < min || current > max) {
            System.out.printf("Informe um número entre %s e %s %n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }
}