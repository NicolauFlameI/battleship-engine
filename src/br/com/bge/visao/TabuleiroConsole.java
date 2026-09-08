package br.com.bge.visao;

// Importa os componentes da IA e o Padrao Factory.
import br.com.bge.ia.DificuldadeBot;
import br.com.bge.ia.EstrategiaBot;
import br.com.bge.ia.FabricaBot;

// Importa o dominio da Engine desacoplada.
import br.com.bge.modelo.BattleshipEngine;
import br.com.bge.modelo.Posicao;
import br.com.bge.modelo.ResultadoTiro;

// Importa utilitarios padrao.
import java.util.InputMismatchException;
import java.util.Scanner;

public class TabuleiroConsole {

    private final Scanner teclado;
    
    // Instancia duas engines isoladas para manter o estado independente de cada competidor.
    // Metodologia: Desacoplamento e Reutilizacao de Componentes.
    private BattleshipEngine engineJogador;
    private BattleshipEngine engineBot;

    // Construtor: Inicializa a comunicacao com o teclado.
    public TabuleiroConsole() {
        this.teclado = new Scanner(System.in);
    }

    public static void main(String[] args) {
        TabuleiroConsole app = new TabuleiroConsole();
        app.executarLoopPrincipal();
    }

    // Gerencia o fluxo geral: apresentacao, escolha de dificuldade e partidas.
    public void executarLoopPrincipal() {
        boolean continuarJogando = true;

        System.out.println("=================================================");
        System.out.println("      BATTLESHIP GAME ENGINE - MODO VERSUS BOT   ");
        System.out.println("=================================================");

        while (continuarJogando) {
            DificuldadeBot dificuldade = selecionarDificuldade();
            EstrategiaBot bot = FabricaBot.criarBot(dificuldade);

            jogarPartidaVersus(bot, dificuldade);

            continuarJogando = perguntarRevanche();
        }

        System.out.println("\nObrigado por jogar! Encerrando sessao.");
        this.teclado.close();
    }

    // Menu interativo para selecao polimorfica de dificuldade.
    private DificuldadeBot selecionarDificuldade() {
        System.out.println("\nSelecione o nivel de dificuldade da IA:");
        System.out.println("1 - " + DificuldadeBot.FACIL.getDescricao());
        System.out.println("2 - " + DificuldadeBot.MEDIO.getDescricao());
        System.out.println("3 - " + DificuldadeBot.DIFICIL.getDescricao());

        int opcao = 0;
        while (opcao < 1 || opcao > 3) {
            System.out.print("Escolha uma opcao (1-3): ");
            try {
                opcao = teclado.nextInt();
                if (opcao < 1 || opcao > 3) {
                    System.out.println("Opcao invalida! Digite 1, 2 ou 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada invalida! Por favor, digite um numero.");
                teclado.nextLine();
            }
        }

        return switch (opcao) {
            case 1 -> DificuldadeBot.FACIL;
            case 2 -> DificuldadeBot.MEDIO;
            case 3 -> DificuldadeBot.DIFICIL;
            default -> DificuldadeBot.FACIL;
        };
    }

    // Game Loop do combate em turnos entre Humano e Maquina.
    private void jogarPartidaVersus(EstrategiaBot bot, DificuldadeBot dificuldade) {
        this.engineJogador = new BattleshipEngine();
        this.engineBot = new BattleshipEngine();

        engineJogador.startGame();
        engineBot.startGame();

        ResultadoTiro ultimoResultadoBot = null;
        boolean turnoJogador = true;

        System.out.println("\nPartida iniciada contra IA [" + dificuldade.name() + "]!");

        // Loop enquanto ambos os jogadores possuirem navios ativos.
        while (engineJogador.isJogoEmAndamento() && engineBot.isJogoEmAndamento()) {
            
            if (turnoJogador) {
                System.out.println("\n-----------------------------------------");
                System.out.println("           SEU TURNO DE DISPARO          ");
                System.out.println("-----------------------------------------");
                
                // Mostra o radar com os tiros dados na frota do Bot.
                System.out.println("RADAR DO INIMIGO (Seus Tiros):");
                renderizarTabuleiro(engineBot.getGradeAtual());

                int linha = lerCoordenada("Informe a LINHA (0 a " + (engineBot.getLinhas() - 1) + "): ", engineBot.getLinhas());
                int coluna = lerCoordenada("Informe a COLUNA (0 a " + (engineBot.getColunas() - 1) + "): ", engineBot.getColunas());

                ResultadoTiro res = engineBot.shoot(linha, coluna);
                System.out.println("\n>>> [VOCE]: " + res.getMensagem());
                System.out.println(">>> [FROTA INIMIGA]: Embarcacoes restantes: " + res.getNaviosRestantes());

                turnoJogador = false; // Passa o turno para a maquina.
            } else {
                System.out.println("\n-----------------------------------------");
                System.out.println("            TURNO DA MAQUINA             ");
                System.out.println("-----------------------------------------");

                // Metodologia: Polimorfismo. Invoca o algoritmo da IA sem saber qual classe concreta esta rodando.
                Posicao tiroBot = bot.escolherProximoTiro(engineJogador.getGradeAtual(), ultimoResultadoBot);
                
                ultimoResultadoBot = engineJogador.shoot(tiroBot.getLinha(), tiroBot.getColuna());
                
                System.out.println(">>> [BOT DISPAROU EM]: (" + tiroBot.getLinha() + ", " + tiroBot.getColuna() + ")");
                System.out.println(">>> [FEEDBACK]: " + ultimoResultadoBot.getMensagem());

                System.out.println("\nSEU TABULEIRO DE DEFESA (Tiros Sofridos):");
                renderizarTabuleiro(engineJogador.getGradeAtual());
                System.out.println(">>> [SUA FROTA]: Embarcacoes restantes: " + ultimoResultadoBot.getNaviosRestantes());

                turnoJogador = true; // Devolve o turno ao jogador.
            }
        }

        // Verificacao do vencedor da partida.
        System.out.println("\n***************************************************");
        if (engineBot.isVitoria()) {
            System.out.println("        VITORIA! VOCE DESTRUIU A FROTA DA IA!       ");
        } else {
            System.out.println("        DERROTA! A IA DESTRUIU TODA A SUA FROTA!    ");
        }
        System.out.println("***************************************************");
    }

    // Renderizador da grade alfanumerica formatada.
    private void renderizarTabuleiro(char[][] grade) {
        System.out.println("   0 1 2 3 4 5 6 7");
        System.out.println("  +-----------------+");
        for (int l = 0; l < grade.length; l++) {
            System.out.print(l + " |");
            for (int c = 0; c < grade[l].length; c++) {
                char simbolo = grade[l][c];
                if (simbolo == ' ') {
                    System.out.print(" .");
                } else {
                    System.out.print(" " + simbolo);
                }
            }
            System.out.println(" |");
        }
        System.out.println("  +-----------------+");
    }

    // Leitura defensiva de coordenadas numericas.
    private int lerCoordenada(String prompt, int limiteMaximo) {
        int valor = -1;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print(prompt);
            try {
                valor = teclado.nextInt();
                if (valor >= 0 && valor < limiteMaximo) {
                    entradaValida = true;
                } else {
                    System.out.println("Valor fora dos limites! Deve estar entre 0 e " + (limiteMaximo - 1) + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada invalida! Digite apenas numeros inteiros.");
                teclado.nextLine();
            }
        }
        return valor;
    }

    // Pergunta de repeticao de partida.
    private boolean perguntarRevanche() {
        System.out.print("\nDeseja disputar outra partida? (S/N): ");
        String resposta = teclado.next().trim().toUpperCase();
        return resposta.startsWith("S");
    }
}