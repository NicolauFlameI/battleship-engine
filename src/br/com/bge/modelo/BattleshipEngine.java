package br.com.bge.modelo;

public class BattleshipEngine {
	
	private Tabuleiro tabuleiro;
	
	private boolean jogoEmAndamento;
	
	public BattleshipEngine() {
		this.jogoEmAndamento = false;
	}

	public char[][] startGame() {
        this.tabuleiro = new Tabuleiro();
        this.jogoEmAndamento = true;
        return this.tabuleiro.getCopiaGradeTiros();
    }

	public ResultadoTiro shoot(int row, int col) {      
		if (!jogoEmAndamento || tabuleiro == null) {
            return new ResultadoTiro(false, false, null, 0, "O jogo ainda nao foi iniciado! Chame startGame() primeiro.");
        }

       
		ResultadoTiro resultado = tabuleiro.processarTiro(row, col);

       
        if (tabuleiro.todosNaviosAfundados()) {
            this.jogoEmAndamento = false;
        }
        
        return resultado;
	}
	
	public char [][] getGradeAtual(){
		if (tabuleiro == null) {
			return new char[0][0];
		}
		return tabuleiro.getCopiaGradeTiros();
	}
	
	public boolean isVitoria() {
		return tabuleiro != null && tabuleiro.todosNaviosAfundados();
	}
	
	public boolean isJogoEmAndamento() {
		return jogoEmAndamento;
	}
	
	public int getLinhas() {
		return tabuleiro !=null ? tabuleiro.getTotalLinhas() : Tabuleiro.LINHAS_PADRAO;
	}
	
	public int getColunas() {
		return tabuleiro != null ? tabuleiro.getTotalColunas() : Tabuleiro.COLUNAS_PADRAO;
	}
	
	public int getNaviosRestantes() {
	    return tabuleiro != null ? tabuleiro.contarNaviosRestantes() : 0;
	}
	
	
}
