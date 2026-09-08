package br.com.bge.ia;

// Padrao de Projeto: Simple Factory (Fabrica Simples).
// Centraliza a criacao dos objetos de estrategia em um unico ponto.
// Se criarmos um novo Bot no futuro, apenas esta classe precisa ser atualizada.
public class FabricaBot {

    // Construtor privado para impedir instanciacao.
    // Metodologia: Classe Utilitaria. Todos os metodos sao estaticos (static),
    // portanto nao faz sentido permitir 'new FabricaBot()'.
    private FabricaBot() {
    }

    // Metodo Fabrica Estatico (Static Factory Method).
    // Recebe o enum da dificuldade e retorna a instancia polimorfica correspondente.
    // Metodologia: Retornar a Interface (EstrategiaBot) em vez da classe concreta.
    public static EstrategiaBot criarBot(DificuldadeBot dificuldade) {
        
        // Validacao defensiva para garantir integridade caso receba parametro nulo.
        if (dificuldade == null) {
            return new BotFacil(); // Fallback padrao
        }

        // Metodologia: Switch Expression (disponivel no Java moderno).
        // Mapeia de forma limpa e concisa cada constante do Enum para sua respectiva classe.
        switch (dificuldade) {
            case FACIL:
                return new BotFacil();
            case MEDIO:
                return new BotMedio();
            case DIFICIL:
                return new BotDificil();
            default:
                throw new IllegalArgumentException("Dificuldade desconhecida: " + dificuldade);
        }
    }
}