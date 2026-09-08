package br.com.bge.visao.grafica;

// Importacoes do JavaFX para gerenciamento do ciclo de vida da aplicacao.
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
// Importacoes dos componentes de controle visual (botoes, textos e caixas de alerta).
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
// Importacoes dos paineis de layout para organizacao espacial dos elementos.
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Importacoes do nosso motor desacoplado (reaproveitamento direto da Engine sem alteracoes).
import br.com.bge.modelo.BattleshipEngine;
import br.com.bge.modelo.ResultadoTiro;

public class AppGrafica extends Application {

    // Constante para definir o tamanho fixo de cada celula da grade em pixels.
    private static final int TAMANHO_BOTAO = 45;

    // Referencia para o motor de regras de negocio.
    private BattleshipEngine engine;
    
    // Matriz de componentes visuais para manipular os botoes da tela individualmente.
    private Button[][] botoesGrade;
    
    // Elementos de texto na interface para exibir status e mensagens em tempo real.
    private Label rotuloStatus;
    private Label rotuloNaviosRestantes;

    @Override
    public void start(Stage palcoPrincipal) {
        // Inicializa a engine e prepara o tabuleiro 8x8 na memoria.
        this.engine = new BattleshipEngine();
        this.botoesGrade = new Button[8][8];

        // Painel Raiz (Root Node) usando BorderPane: divide a tela em Topo, Centro, Rodape, etc.
        BorderPane painelRaiz = new BorderPane();
        painelRaiz.setPadding(new Insets(15)); // Margem interna de 15 pixels nas bordas da janela.

        // 1. REGIAO SUPERIOR (TOPO): Painel vertical (VBox) com titulos e informacoes da partida.
        VBox painelCabecalho = new VBox(8); // Espacamento vertical de 8 pixels entre os textos.
        painelCabecalho.setAlignment(Pos.CENTER);

        Label rotuloTitulo = new Label("BATTLESHIP GAME ENGINE");
        rotuloTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        rotuloStatus = new Label("Clique em uma coordenada para disparar!");
        rotuloStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155;");

        rotuloNaviosRestantes = new Label("Navios inimigos restantes: 3");
        rotuloNaviosRestantes.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        painelCabecalho.getChildren().addAll(rotuloTitulo, rotuloStatus, rotuloNaviosRestantes);
        painelRaiz.setTop(painelCabecalho);

        // 2. REGIAO CENTRAL: GridPane com a matriz 8x8 de botoes que simula a agua do mar.
        GridPane gradeVisual = construirGradeBotoes();
        painelRaiz.setCenter(gradeVisual);

        // 3. REGIAO INFERIOR: Botao de controle para reiniciar o jogo a qualquer momento.
        Button botaoReiniciar = new Button("Nova Partida");
        botaoReiniciar.setStyle("-fx-font-size: 13px; -fx-padding: 8 16; -fx-cursor: hand;");
        
        // Expressao Lambda (Java 8+): Metodologia de escuta de eventos (Event-Driven).
        // Quando o usuario clica no botao reiniciar, executamos o metodo reiniciarPartida().
        botaoReiniciar.setOnAction(evento -> reiniciarPartida());

        VBox painelRodape = new VBox(botaoReiniciar);
        painelRodape.setAlignment(Pos.CENTER);
        painelRodape.setPadding(new Insets(10, 0, 0, 0));
        painelRaiz.setBottom(painelRodape);

        // Comeca a primeira partida limpando e sorteando os navios na Engine.
        reiniciarPartida();

        // Configuracao da Cena e exibicao da Janela fisica.
        Scene cena = new Scene(painelRaiz, 480, 560);
        palcoPrincipal.setTitle("BGE - Batalha Naval Interativa");
        palcoPrincipal.setResizable(false); // Trava o redimensionamento para manter o layout estavel.
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show();
    }

    // Cria a estrutura visual da grade com botoes interativos.
    // Metodologia: GridPane com espacamento uniforme (gap).
    private GridPane construirGradeBotoes() {
        GridPane grade = new GridPane();
        grade.setAlignment(Pos.CENTER);
        grade.setHgap(4); // Espaco horizontal de 4 pixels entre cada botao.
        grade.setVgap(4); // Espaco vertical de 4 pixels entre cada botao.
        grade.setPadding(new Insets(15, 0, 15, 0));

        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                Button botao = new Button();
                botao.setPrefSize(TAMANHO_BOTAO, TAMANHO_BOTAO);
                
                // Variaveis finais obrigatorias para uso dentro da expressao Lambda (escopo de closure).
                final int linhaAtual = l;
                final int colunaAtual = c;

                // Atribui o ouvinte de clique (Event Handler) em cada botao da matriz.
                botao.setOnAction(e -> executarTiro(linhaAtual, colunaAtual));

                botoesGrade[l][c] = botao;
                // Adiciona o botao no painel: parametro (coluna, linha) no padrao do JavaFX.
                grade.add(botao, c, l);
            }
        }
        return grade;
    }

    // Processa a acao de disparo quando o usuario clica em uma celula do mar.
    private void executarTiro(int linha, int coluna) {
        // Invoca a Engine para calcular a regra do tiro e receber o pacote de diagnostico (DTO).
        ResultadoTiro resultado = engine.shoot(linha, coluna);
        Button botaoClicado = botoesGrade[linha][coluna];

        // Desativa o botao para impedir que o usuario clique duas vezes na mesma celula.
        botaoClicado.setDisable(true);

        // Atualiza a apresentacao visual com base no diagnostico da Engine.
        if (resultado.isAcertou()) {
            botaoClicado.setText("X");
            // Vermelho para impacto confirmado em navio.
            botaoClicado.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-opacity: 1.0;");
        } else {
            botaoClicado.setText("O");
            // Cinza escuro para tiro perdido na agua.
            botaoClicado.setStyle("-fx-background-color: #94a3b8; -fx-text-fill: white; -fx-font-weight: bold; -fx-opacity: 1.0;");
        }

        // Atualiza os textos do cabecalho com as informacoes do turno.
        rotuloStatus.setText(resultado.getMensagem());
        rotuloNaviosRestantes.setText("Navios inimigos restantes: " + resultado.getNaviosRestantes());

        // Metodologia: Dialogo Modal (Alert) para condicao de vitoria.
        if (engine.isVitoria()) {
            exibirAlertaVitoria();
        }
    }

    // Reinicia os dados da Engine e restaura a aparencia grafica de todos os botoes.
    private void reiniciarPartida() {
        engine.startGame();

        rotuloStatus.setText("Nova partida iniciada! Fogo a vontade.");
        rotuloNaviosRestantes.setText("Navios inimigos restantes: 3");

        // Reseta todos os botoes da tela para o estilo padrao de agua.
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                Button btn = botoesGrade[l][c];
                btn.setText("");
                btn.setDisable(false);
                // Azul marinho suave representando o oceano inexplorado.
                btn.setStyle("-fx-background-color: #38bdf8; -fx-cursor: hand;");
            }
        }
    }

    // Exibe uma janela de alerta (pop-up) notificando o fim de jogo.
    private void exibirAlertaVitoria() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Vitoria Naval!");
        alerta.setHeaderText("Parabens, Almirante!");
        alerta.setContentText("Toda a frota inimiga foi ao fundo!");
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}