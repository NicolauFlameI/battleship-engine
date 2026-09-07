package br.com.bge.ia;

import br.com.bge.modelo.Posicao;
import br.com.bge.modelo.ResultadoTiro;

public interface EstrategiaBot {
	
	Posicao escolherProximoTiro(char[][] gradeVisivel, ResultadoTiro ultimoResultado);

}
