package br.com.bge.ia;

public enum DificuldadeBot {
	FACIL("Facil (Tiros Aleatorios)"),
	MEDIO("Medio (Caça e Destruição)"),
	DIFICIL("Dificil (Mapa de Probabilidade)");
	
	private final String descricao;
	
	DificuldadeBot(String descricao){
		this.descricao = descricao;
		
	}
	
	public String getDescricao() {
		return descricao;
	}

}
