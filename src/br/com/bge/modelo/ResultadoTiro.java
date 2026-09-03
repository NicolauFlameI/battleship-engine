package br.com.bge.modelo;

public class ResultadoTiro {
	
	private final boolean acertou;
	private final boolean afundouNavio;
	private final String nomeNavioAtingido;
	private final int naviosRestantes;
	private final String mensagem;
	
	public ResultadoTiro(boolean acertou, 
			boolean afundouNavio, 
			String nomeNavioAtingido, 
			int naviosRestantes,
			String mensagem) {
		this.acertou = acertou;
		this.afundouNavio = afundouNavio;
		this.nomeNavioAtingido = nomeNavioAtingido;
		this.naviosRestantes = naviosRestantes;
		this.mensagem = mensagem;
	}

	public boolean isAcertou() {
		return acertou;
	}

	public boolean isAfundouNavio() {
		return afundouNavio;
	}

	public String getNomeNavioAtingido() {
		return nomeNavioAtingido;
	}

	public int getNaviosRestantes() {
		return naviosRestantes;
	}

	public String getMensagem() {
		return mensagem;
	}
	

}
