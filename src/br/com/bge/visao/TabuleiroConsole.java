package br.com.bge.visao;

// Importa a Facade e o DTO do nosso pacote de modelo/regra de negocio.
// Metodologia: Desacoplamento. A camada visual so conhece a Fachada e o Objeto de Transferencia.
import br.com.bge.modelo.BattleshipEngine;
import br.com.bge.modelo.ResultadoTiro;

// Importa ferramentas padrao para leitura de teclado e tratamento de excecoes de entrada.
import java.util.InputMismatchException;
import java.util.Scanner;

public class TabuleiroConsole {

    // Scanner unico compartilhado para todo o ciclo de vida da interface de texto.
    private final Scanner teclado;
    
    // Referencia para o motor desacoplado do jogo.
    private final BattleshipEngine engine;

    // Construtor: Inicializa a conexao com o teclado e a engine de regras.
    public TabuleiroConsole() {
        this.teclado = new Scanner(System.in);
        this.engine = new BattleshipEngine();
    }

    // Ponto de entrada padrao da JVM para rodar o aplicativo.
    public static void main(String[] args) {
        TabuleiroConsole app = new TabuleiroConsole();
        app.executarLoopPrincipal();
    }

    // Metodologia: Game Loop (Laco Principal de Jogo).
    // Gerencia o fluxo completo: iniciar partida, turnos de disparos e opcao de revanche.
    public void executarLoopPrincipal() {
        boolean continuarJogando = true;

        System.out.println("=========================================");
        System.out.println("       BATTLESHIP GAME ENGINE (BGE)      ");
        System.out.println("=========================================");

        while (continuarJogando) {
            jogarPartida();

            // Requisito da especificacao: perguntar se o jogador deseja jogar novamente ao final.
            continuarJogando = perguntarRevanche();
        }

        System.out.println("\nObrigado por jogar BGE! Encerrando sessao.");
        this.teclado.close(); // Fecha o fluxo de I/O para evitar vazamento de recursos (Resource Leak).
    }

    // Controla uma partida individual do inicio ate a condicao de vitoria.
    private void jogarPartida() {
        // Inicializa o motor e recebe a grade limpa de 8x8.
        engine.startGame();
        System.out.println("\nNova partida iniciada! Navios posicionados em segredo.");

        // O loop roda enquanto a engine indicar que o jogo esta em andamento.
        while (engine.isJogoEmAndamento()) {
            // 1. Renderiza a grade atualizada na tela.
            renderizarTabuleiro(engine.getGradeAtual());

            // 2. Leitura com validacao defensiva das coordenadas de linha e coluna.
            int linha = lerCoordenada("Informe a LINHA (0 a " + (engine.getLinhas() - 1) + "): ", engine.getLinhas());
            int coluna = lerCoordenada("Informe a COLUNA (0 a " + (engine.getColunas() - 1) + "): ", engine.getColunas());

            // 3. Invoca a acao no motor de regras e recebe o pacote de diagnostico (DTO).
            ResultadoTiro resultado = engine.shoot(linha, coluna);

            // 4. Apresenta o feedback textual imediato do tiro.
            System.out.println("\n>>> [RESULTADO]: " + resultado.getMensagem());
            System.out.println(">>> [FROTA]: Embarcacoes restantes na agua: " + resultado.getNaviosRestantes());
            System.out.println("-----------------------------------------");
        }

        // Se saiu do loop com vitoria, renderiza o tabuleiro final e a mensagem de felicitacoes.
        if (engine.isVitoria()) {
            renderizarTabuleiro(engine.getGradeAtual());
            System.out.println("\n***************************************************");
            System.out.println(" PARABENS, ALMIRANTE! TODA A FROTA INIMIGA AFUNDOU! ");
            System.out.println("***************************************************");
        }
    }

    // Formata a matriz bidimensional em uma grade alfanumerica limpa e legivel.
    private void renderizarTabuleiro(char[][] grade) {
        System.out.println("\n   0 1 2 3 4 5 6 7  (Colunas)");
        System.out.println("  +-----------------+");

        for (int l = 0; l < grade.length; l++) {
            System.out.print(l + " |"); // Indice da linha a esquerda
            for (int c = 0; c < grade[l].length; c++) {
                char simbolo = grade[l][c];
                // Se a celula for vazia, imprimimos um ponto '.' para guiar a visao do usuario.
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

    // Leitura defensiva: Protege a aplicacao contra entradas nao numericas ou fora dos limites.
    // Metodologia: Tratamento de excecoes com 'try-catch' e prevencao contra travamentos.
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
                    System.out.println("Valor invalido! Deve estar entre 0 e " + (limiteMaximo - 1) + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada invalida! Por favor, digite apenas numeros inteiros.");
                teclado.nextLine(); // Limpa o buffer de entrada do Scanner.
            }
        }
        return valor;
    }

    // Pergunta se o jogador quer uma nova partida, aceitando 'S' ou 'N'.
    private boolean perguntarRevanche() {
        System.out.print("\nDeseja jogar novamente? (S/N): ");
        String resposta = teclado.next().trim().toUpperCase();
        return resposta.startsWith("S");
    }
}