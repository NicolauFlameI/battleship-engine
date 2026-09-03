package br.com.bge.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tabuleiro {
	
	
	public static final int LINHAS_PADRAO = 8;
	public static final int COLUNAS_PADRAO = 8;
	
	public static final char MARCA_VAZIO = ' ';
	public static final char MARCA_ERRO = 'O';
	public static final char MARCA_ACERTO = 'X';
	
	private final int totalLinhas;
	private final int totalColunas;
	
	private final char [][] gradeTiros;
	
	private final List<Navio> frota;
	
	private final Random geradorAleatorio;
	
	public Tabuleiro() {
		this.totalLinhas = LINHAS_PADRAO;
		this.totalColunas = COLUNAS_PADRAO;
		this.gradeTiros = new char [totalLinhas][totalColunas];
		this.frota = new ArrayList<>();
		this.geradorAleatorio = new Random();
		
		inicializarGrade();
		gerarFrotaAleatoria();
		
	}
	
	private void inicializarGrade() {
		for (int l = 0; l < totalLinhas; l++) {
			for  (int c = 0; c < totalColunas; c++) {
				this.gradeTiros[l][c] = MARCA_VAZIO;
			}
		}
	}
	
	private void gerarFrotaAleatoria() {
		posicionarNavioAleatoriamente(new Navio("Destroyer", 2));
		posicionarNavioAleatoriamente(new Navio("Cruiser", 3));
		posicionarNavioAleatoriamente(new Navio("Battleship", 4));
	}
	
	private void posicionarNavioAleatoriamente(Navio navio) {
		boolean posicionadoComSucesso = false;
		
		while(!posicionadoComSucesso) {
			boolean horizontal = geradorAleatorio.nextBoolean();
			
			int linhaInicial;
            int colunaInicial;

            
            if (horizontal) {
                linhaInicial = geradorAleatorio.nextInt(totalLinhas);
                colunaInicial = geradorAleatorio.nextInt(totalColunas - navio.getTamanho() + 1);
            } else {
                linhaInicial = geradorAleatorio.nextInt(totalLinhas - navio.getTamanho() + 1);
                colunaInicial = geradorAleatorio.nextInt(totalColunas);
            }


            List<Posicao> posicoesCandidatas = new ArrayList<>();
            for (int i = 0; i < navio.getTamanho(); i++) {
                if (horizontal) {
                    posicoesCandidatas.add(new Posicao(linhaInicial, colunaInicial + i));
                } else {
                    posicoesCandidatas.add(new Posicao(linhaInicial + i, colunaInicial));
                }
            }

            
            if (!haColisao(posicoesCandidatas)) {
                for (Posicao p : posicoesCandidatas) {
                    navio.adicionarPosicao(p);
                }
                frota.add(navio);
                posicionadoComSucesso = true;
            }
        }
    }
	
	private boolean haColisao(List<Posicao> posicoesCandidatas) {
		for (Navio navioExistente : frota) {
			for (Posicao candidata : posicoesCandidatas) {
				if (navioExistente.ocupaPosicao(candidata)) {
					return true; // Colisao detectada
				}
			}
		}
		return false;
	}
	
	public ResultadoTiro processarTiro(int linha, int coluna) {
        // Validacao defensiva de parametros de entrada.
        if (linha < 0 || linha >= totalLinhas || coluna < 0 || coluna >= totalColunas) {
            return new ResultadoTiro(false, false, null, contarNaviosRestantes(), "Coordenada fora dos limites do tabuleiro!");
        }

        // Checagem de redundancia: o jogador nao pode disparar duas vezes no mesmo quadrado.
        if (gradeTiros[linha][coluna] != MARCA_VAZIO) {
            return new ResultadoTiro(false, false, null, contarNaviosRestantes(), "Voce ja atirou nessa posicao anteriormente!");
        }

        Posicao coordenadaDisparo = new Posicao(linha, coluna);
        Navio navioAtingido = null;

        // Procura se ha algum navio na coordenada disparada.
        for (Navio navio : frota) {
            if (navio.ocupaPosicao(coordenadaDisparo)) {
                navioAtingido = navio;
                break; // Encontrou o alvo, interrompe a busca.
            }
        }

        // Cenário 1: O tiro acertou um navio (HIT).
        if (navioAtingido != null) {
            gradeTiros[linha][coluna] = MARCA_ACERTO;
            navioAtingido.registrarAcerto();

            boolean afundou = navioAtingido.estaAfundado();
            String mensagem = afundou 
                ? "FOGO CERTEIRO! Voce afundou o " + navioAtingido.getNome() + "!" 
                : "ACERTOU uma embarcacao!";

            return new ResultadoTiro(true, afundou, navioAtingido.getNome(), contarNaviosRestantes(), mensagem);
        }

        // Cenário 2: O tiro caiu na agua (MISS).
        gradeTiros[linha][coluna] = MARCA_ERRO;
        return new ResultadoTiro(false, false, null, contarNaviosRestantes(), "AGUA! Nenhum navio nessa posicao.");
    }

    // Regra de Negocio: Conta quantos navios ainda possuem ao menos uma parte intacta.
    public int contarNaviosRestantes() {
        int restantes = 0;
        for (Navio n : frota) {
            if (!n.estaAfundado()) {
                restantes++;
            }
        }
        return restantes;
    }

    // Condicao de Vitoria: O jogo encerra quando nao restar nenhuma embarcacao flutuando.
    public boolean todosNaviosAfundados() {
        return contarNaviosRestantes() == 0;
    }

    // Metodo de leitura da grade (copia defensiva):
    // Impede que classes de fora modifiquem a matriz original diretamente por referencia.
    public char[][] getCopiaGradeTiros() {
        char[][] copia = new char[totalLinhas][totalColunas];
        for (int l = 0; l < totalLinhas; l++) {
            System.arraycopy(this.gradeTiros[l], 0, copia[l], 0, totalColunas);
        }
        return copia;
    }

    public int getTotalLinhas() {
        return totalLinhas;
    }

    public int getTotalColunas() {
        return totalColunas;
    }
}


