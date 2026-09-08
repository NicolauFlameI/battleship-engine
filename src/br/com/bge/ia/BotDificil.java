package br.com.bge.ia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.bge.modelo.Posicao;
import br.com.bge.modelo.ResultadoTiro;
import br.com.bge.modelo.Tabuleiro;

// Metodologia: Algoritmo de Densidade de Probabilidade Matricial (Heatmap).
public class BotDificil implements EstrategiaBot {

    // Lista com os tamanhos das embarcacoes que o bot sabe que estao em jogo inicialmente.
    private final List<Integer> tamanhosNaviosVivos;
    private final Random random;

    public BotDificil() {
        this.random = new Random();
        this.tamanhosNaviosVivos = new ArrayList<>();
        reiniciarFrotaConhecida();
    }

    // Inicializa a frota padrao exigida pela especificacao do desafio.
    private void reiniciarFrotaConhecida() {
        tamanhosNaviosVivos.clear();
        tamanhosNaviosVivos.add(2); // Destroyer
        tamanhosNaviosVivos.add(3); // Cruiser
        tamanhosNaviosVivos.add(4); // Battleship
    }

    @Override
    public Posicao escolherProximoTiro(char[][] gradeVisivel, ResultadoTiro ultimoResultado) {
        
        // 1. Atualizacao de Estado: se um navio afundou, deduzimos seu tamanho e removemos da lista.
        if (ultimoResultado != null && ultimoResultado.isAfundouNavio()) {
            removerNavioAfundado(ultimoResultado.getNomeNavioAtingido());
        }

        int totalLinhas = gradeVisivel.length;
        int totalColunas = gradeVisivel[0].length;
        
        // Matriz de calor temporaria (zerada a cada turno de disparo).
        int[][] heatmap = new int[totalLinhas][totalColunas];

        // 2. Calcula as probabilidades somando pesos para cada navio que ainda esta flutuando.
        for (int tamanhoNavio : tamanhosNaviosVivos) {
            calcularPesosParaNavio(gradeVisivel, heatmap, tamanhoNavio);
        }

        // 3. Localiza a celula vazia com a maior pontuacao estatistica.
        int pontuacaoMaxima = -1;
        List<Posicao> melhoresCandidatos = new ArrayList<>();

        for (int l = 0; l < totalLinhas; l++) {
            for (int c = 0; c < totalColunas; c++) {
                // O bot so pode atirar onde o quadrado ainda for virgem/vazio (' ').
                if (gradeVisivel[l][c] == Tabuleiro.MARCA_VAZIO) {
                    int pesoAtual = heatmap[l][c];

                    if (pesoAtual > pontuacaoMaxima) {
                        pontuacaoMaxima = pesoAtual;
                        melhoresCandidatos.clear();
                        melhoresCandidatos.add(new Posicao(l, c));
                    } else if (pesoAtual == pontuacaoMaxima) {
                        melhoresCandidatos.add(new Posicao(l, c));
                    }
                }
            }
        }

        // Programacao Defensiva: fallback caso a matriz nao gere candidatos.
        if (melhoresCandidatos.isEmpty()) {
            return new Posicao(0, 0);
        }

        // Desempate aleatorio entre coordenadas com a mesma pontuacao maxima.
        int indiceSorteado = random.nextInt(melhoresCandidatos.size());
        return melhoresCandidatos.get(indiceSorteado);
    }

    // Testa todos os encaixes horizontais e verticais de um navio no mapa visivel.
    private void calcularPesosParaNavio(char[][] grade, int[][] heatmap, int tamanho) {
        int linhas = grade.length;
        int colunas = grade[0].length;

        // Simulacao Horizontal
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c <= colunas - tamanho; c++) {
                if (encaixeValido(grade, l, c, tamanho, true)) {
                    aplicarPontuacao(grade, heatmap, l, c, tamanho, true);
                }
            }
        }

        // Simulacao Vertical
        for (int l = 0; l <= linhas - tamanho; l++) {
            for (int c = 0; c < colunas; c++) {
                if (encaixeValido(grade, l, c, tamanho, false)) {
                    aplicarPontuacao(grade, heatmap, l, c, tamanho, false);
                }
            }
        }
    }

    // Valida se o segmento nao colide com tiros dados na agua ('O').
    private boolean encaixeValido(char[][] grade, int linha, int coluna, int tamanho, boolean horizontal) {
        for (int i = 0; i < tamanho; i++) {
            int l = horizontal ? linha : linha + i;
            int c = horizontal ? coluna + i : coluna;

            // Se tocar em agua marcada ('O'), esse encaixe e impossivel de existir.
            if (grade[l][c] == Tabuleiro.MARCA_ERRO) {
                return false;
            }
        }
        return true;
    }

    // Atribui pontos as celulas daquele segmento valido.
    // Se o segmento passar por cima de um 'X' (hit previo), o peso de todas as celulas vazias salta brutalmente.
    private void aplicarPontuacao(char[][] grade, int[][] heatmap, int linha, int coluna, int tamanho, boolean horizontal) {
        int bonusAcerto = 1;

        // Checagem de proximidade com alvos ja atingidos.
        for (int i = 0; i < tamanho; i++) {
            int l = horizontal ? linha : linha + i;
            int c = horizontal ? coluna + i : coluna;
            if (grade[l][c] == Tabuleiro.MARCA_ACERTO) {
                bonusAcerto += 40; // Peso matematico de prioridade agressiva.
            }
        }

        for (int i = 0; i < tamanho; i++) {
            int l = horizontal ? linha : linha + i;
            int c = horizontal ? coluna + i : coluna;
            heatmap[l][c] += bonusAcerto;
        }
    }

    // Metodo de apoio para deduzir o tamanho do navio pelo nome fornecido pelo DTO ResultadoTiro.
    private void removerNavioAfundado(String nomeNavio) {
        if (nomeNavio == null) return;

        Integer tamanhoParaRemover = null;
        if (nomeNavio.equalsIgnoreCase("Destroyer")) {
            tamanhoParaRemover = 2;
        } else if (nomeNavio.equalsIgnoreCase("Cruiser")) {
            tamanhoParaRemover = 3;
        } else if (nomeNavio.equalsIgnoreCase("Battleship")) {
            tamanhoParaRemover = 4;
        }

        if (tamanhoParaRemover != null) {
            tamanhosNaviosVivos.remove(tamanhoParaRemover);
        }
    }
}