package br.com.bge.visao.grafica;

// Importa os blocos de construcao fundamentais do JavaFX.
// Application: Classe base obrigatoria para qualquer aplicativo grafico JavaFX.
import javafx.application.Application;
// Scene: Representa o cenario/conteudo ativo carregado no palco da aplicacao.
import javafx.scene.Scene;
// Label: Componente de texto grafico (equivale visualmente ao texto do console).
import javafx.scene.control.Label;
// StackPane: Gerenciador de layout basico que centraliza os componentes na tela.
import javafx.scene.layout.StackPane;
// Stage: Representa a janela fisica do sistema operacional (botoes fechar, minimizar, etc).
import javafx.stage.Stage;

// Metodologia: Heranca (Extends).
// AppGrafica torna-se um aplicativo de interface grafica herdando o ciclo de vida da classe Application.
public class AppGrafica extends Application {

    // Metodo de ciclo de vida obrigatorio do JavaFX.
    // Ele e chamado automaticamente pela 'JavaFX Application Thread' logo apos o metodo launch().
    @Override
    public void start(Stage palcoPrincipal) {
        
        // 1. Criacao do elemento visual (Node).
        Label rotuloBoasVindas = new Label("Battleship Game Engine - Interface Grafica Ativa!");
        
        // Metodologia: Estilizacao declarativa com CSS inline (-fx-...).
        // Permite customizar cores, tamanhos e pesos visuais dos componentes nativos.
        rotuloBoasVindas.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3a8a;");

        // 2. Criacao do no raiz de layout (Root Node).
        // Contem os componentes visuais e define como eles serao organizados no espaco.
        StackPane raiz = new StackPane();
        raiz.getChildren().add(rotuloBoasVindas);

        // 3. Criacao da Cena (Scene).
        // Vincula o no raiz e define o tamanho padrao da tela em pixels: 600 de largura por 400 de altura.
        Scene cena = new Scene(raiz, 600, 400);

        // 4. Montagem e apresentacao do Palco (Stage).
        palcoPrincipal.setTitle("BGE - Batalha Naval");
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show(); // Exibe a janela criada na tela do sistema operacional.
    }

    // Ponto de entrada padrao da JVM.
    // launch(args): Carrega as bibliotecas nativas, inicializa o toolkit visual e chama start().
    public static void main(String[] args) {
        launch(args);
    }
}