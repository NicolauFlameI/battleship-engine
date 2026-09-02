package br.com.bge.modelo;

import java.util.ArrayList;
import java.util.List;

public class Navio {
	
	private final String nome;
	private final int tamanho;
	
	private final List<Posicao> posicoesOcupadas;
	
	private int acertosRecebidos;
	
	public Navio(String nome, int tamanho) {
		this.nome = nome;
		this.tamanho = tamanho;
		this.posicoesOcupadas = new ArrayList<>();
		this.acertosRecebidos = 0;
	}
	
	public void adicionarPosicao(Posicao posicao) {
		if (posicoesOcupadas.size() < this.tamanho){
			posicoesOcupadas.add(posicao);
		}
	}
	
	public boolean ocupaPosicao(Posicao posicaoDisparada) {
		return this.posicoesOcupadas.contains(posicaoDisparada);
	}
	
	public void registrarAcerto() {
		if (!estaAfundado()) {
			this.acertosRecebidos++;
		}
	}
	
	public boolean estaAfundado() {
		return this.acertosRecebidos >= this.tamanho;
	}

	public String getNome() {
		return nome;
	}

	public int getTamanho() {
		return tamanho;
	}
	
	public List<Posicao> getPosicoesOcupadas(){
		return new ArrayList<>(this.posicoesOcupadas);
	}

}
