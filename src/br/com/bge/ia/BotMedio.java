package br.com.bge.ia;

// Importa estruturas de dados padrao para gerenciar filas e listas.
// Metodologia: Uso da interface Queue (Fila FIFO - First In, First Out) com LinkedList.
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import br.com.bge.modelo.Posicao;
import br.com.bge.modelo.ResultadoTiro;
import br.com.bge.modelo.Tabuleiro;

public class BotMedio implements EstrategiaBot {

    private final Random random;
    
    // Fila de alvos imediatos (Modo Destruicao / Target Mode).
    // Metodologia: Estrutura FIFO. O primeiro vizinho enfileirado e o primeiro a ser testado.
    private final Queue<Posicao> alvosPendentes;
    
    // Guarda a coordenada do ultimo disparo realizado para saber de onde tirar os vizinhos se acertar.
    private Posicao ultimoDisparoFeito;

    public BotMedio() {
        this.random = new Random();
        this.alvosPendentes = new LinkedList<>();
        this.ultimoDisparoFeito = null;
    }

    @Override
    public Posicao escolherProximoTiro(char[][] gradeVisivel, ResultadoTiro ultimoResultado) {
        
        // 1. Atualizacao de Memoria com base no feedback do turno anterior.
        if (ultimoResultado != null && ultimoDisparoFeito != null) {
            
            // Se o tiro anterior afundou um navio por completo, limpamos a fila para nao insistir no vazio.
            if (ultimoResultado.isAfundouNavio()) {
                alvosPendentes.clear();
            } 
            // Se acertou mas nao afundou, colocamos as 4 celulas adjacentes na fila de destruicao.
            else if (ultimoResultado.isAcertou()) {
                enfileirarVizinhos(ultimoDisparoFeito, gradeVisivel);
            }
        }

        Posicao proximaEscolha = null;

        // 2. MODO ALVO (TARGET MODE): Dispara nos vizinhos enfileirados enquanto houver posicoes validas.
        while (!alvosPendentes.isEmpty()) {
            Posicao candidata = alvosPendentes.poll(); // Remove o primeiro da fila.
            
            // Verifica se a posicao ainda esta intacta (nao foi alvejada em turnos anteriores).
            if (gradeVisivel[candidata.getLinha()][candidata.getColuna()] == Tabuleiro.MARCA_VAZIO) {
                proximaEscolha = candidata;
                break;
            }
        }

        // 3. MODO CACA (HUNT MODE): Se a fila estiver vazia, faz a busca pelo padrao quadriculado (xadrez).
        if (proximaEscolha == null) {
            proximaEscolha = buscarPorPadraoXadrez(gradeVisivel);
        }

        // Salva a escolha atual como referencia para o proximo turno.
        this.ultimoDisparoFeito = proximaEscolha;
        return proximaEscolha;
    }

    // Identifica as 4 posicoes adjacentes ortogonais (Norte, Sul, Leste, Oeste).
    // Metodologia: Validacao de Limites de Matriz (Boundary Check).
    private void enfileirarVizinhos(Posicao origem, char[][] gradeVisivel) {
        int l = origem.getLinha();
        int c = origem.getColuna();
        int totalLinhas = gradeVisivel.length;
        int totalColunas = gradeVisivel[0].length;

        // Norte
        if (l - 1 >= 0 && gradeVisivel[l - 1][c] == Tabuleiro.MARCA_VAZIO) {
            alvosPendentes.add(new Posicao(l - 1, c));
        }
        // Sul
        if (l + 1 < totalLinhas && gradeVisivel[l + 1][c] == Tabuleiro.MARCA_VAZIO) {
            alvosPendentes.add(new Posicao(l + 1, c));
        }
        // Oeste
        if (c - 1 >= 0 && gradeVisivel[l][c - 1] == Tabuleiro.MARCA_VAZIO) {
            alvosPendentes.add(new Posicao(l, c - 1));
        }
        // Leste
        if (c + 1 < totalColunas && gradeVisivel[l][c + 1] == Tabuleiro.MARCA_VAZIO) {
            alvosPendentes.add(new Posicao(l, c + 1));
        }
    }

    // Busca celulas livres no padrao (linha + coluna) % 2 == 0.
    // Se acabarem as celulas do padrao, busca qualquer celula livre restante (Fail-safe).
    private Posicao buscarPorPadraoXadrez(char[][] gradeVisivel) {
        List<Posicao> candidatosXadrez = new ArrayList<>();
        List<Posicao> todosVazios = new ArrayList<>();

        for (int l = 0; l < gradeVisivel.length; l++) {
            for (int c = 0; c < gradeVisivel[l].length; c++) {
                if (gradeVisivel[l][c] == Tabuleiro.MARCA_VAZIO) {
                    todosVazios.add(new Posicao(l, c));
                    if ((l + c) % 2 == 0) {
                        candidatosXadrez.add(new Posicao(l, c));
                    }
                }
            }
        }

        // Prioriza a lista em padrao quadriculado; se estiver vazia, usa os outros espacos disponiveis.
        List<Posicao> listaFinal = !candidatosXadrez.isEmpty() ? candidatosXadrez : todosVazios;

        if (listaFinal.isEmpty()) {
            return new Posicao(0, 0); // Fallback defensivo
        }

        int indice = random.nextInt(listaFinal.size());
        return listaFinal.get(indice);
    }
}