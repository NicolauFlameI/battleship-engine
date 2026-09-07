package br.com.bge.ia;

// Importa classes necessarias para colecoes e sorteio pseudo-aleatorio.
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Importa os modelos de dados e constantes do nosso pacote de regras.
import br.com.bge.modelo.Posicao;
import br.com.bge.modelo.ResultadoTiro;
import br.com.bge.modelo.Tabuleiro;

// Metodologia: Implementacao de Interface (Polimorfismo).
// Ao declarar 'implements EstrategiaBot', o compilador Java obriga esta classe
// a fornecer corpo e comportamento para o metodo escolherProximoTiro().
public class BotFacil implements EstrategiaBot {

    // Instancia do gerador de numeros aleatorios mantida como atributo da classe.
    // Metodologia: Reutilizacao de Recursos. Evitamos criar 'new Random()' a cada turno.
    private final Random random;

    // Construtor: Inicializa o gerador aleatorio na memoria.
    public BotFacil() {
        this.random = new Random();
    }

    // Sobrescrita obrigatoria do metodo contratado na interface EstrategiaBot.
    // Metodologia: Anotacao @Override sinaliza explicitamente ao compilador que estamos
    // substituindo a assinatura abstrata da interface por uma implementacao real.
    @Override
    public Posicao escolherProximoTiro(char[][] gradeVisivel, ResultadoTiro ultimoResultado) {
        
        // Lista dinamica criada para armazenar temporariamente todas as celulas que ainda nao foram atingidas.
        // Metodologia: Programar para Interface (List como tipo, ArrayList como objeto).
        List<Posicao> celulasDisponiveis = new ArrayList<>();

        // Percorremos toda a matriz bidimensional recebida via laços aninhados (nested loops).
        for (int l = 0; l < gradeVisivel.length; l++) {
            for (int c = 0; c < gradeVisivel[l].length; c++) {
                
                // Usamos a constante centralizada MARCA_VAZIO (' ') definida na classe Tabuleiro.
                // Isso evita "Magic Characters" soltos pelo codigo.
                if (gradeVisivel[l][c] == Tabuleiro.MARCA_VAZIO) {
                    celulasDisponiveis.add(new Posicao(l, c));
                }
            }
        }

        // Programacao Defensiva (Fail-safe): Se por algum motivo nao houver celulas livres,
        // retornamos a primeira coordenada para evitar crash (IndexOutOfBoundsException).
        if (celulasDisponiveis.isEmpty()) {
            return new Posicao(0, 0);
        }

        // Sorteia um indice aleatorio entre 0 e (tamanho da lista - 1).
        int indiceSorteado = random.nextInt(celulasDisponiveis.size());

        // Retorna o objeto Posicao correspondente ao indice sorteado.
        return celulasDisponiveis.get(indiceSorteado);
    }
}