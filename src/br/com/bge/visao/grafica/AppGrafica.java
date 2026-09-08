package br.com.bge.visao.grafica;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import br.com.bge.ia.DificuldadeBot;
import br.com.bge.ia.EstrategiaBot;
import br.com.bge.ia.FabricaBot;
import br.com.bge.modelo.BattleshipEngine;
import br.com.bge.modelo.Posicao;
import br.com.bge.modelo.ResultadoTiro;

public class AppGrafica extends Application {

    private static final int TAMANHO_CELULA = 42;

    private Stage palco;
    private BattleshipEngine engineJogador;
    private BattleshipEngine engineBot;
    
    private EstrategiaBot bot;
    private DificuldadeBot dificuldadeSelecionada;
    private ResultadoTiro ultimoResultadoBot;

    private Button[][] botoesRadarInimigo;
    private Label[][] celulasDefesaJogador;

    private Label rotuloStatus;
    private Label rotuloPlacar;

    @Override
    public void start(Stage palcoPrincipal) {
        this.palco = palcoPrincipal;
        this.palco.setTitle("BGE - Batalha Naval Tática");
        this.palco.setResizable(false);

        exibirTelaMenu();
        this.palco.show();
    }

    private void exibirTelaMenu() {
        VBox layoutMenu = new VBox(22);
        layoutMenu.setAlignment(Pos.CENTER);
        layoutMenu.setPadding(new Insets(40));
        layoutMenu.setStyle("-fx-background-color: #0b192c;");

        Label icone = new Label("⚓ 🚢 ⚓");
        icone.setStyle("-fx-font-size: 32px;");

        Label titulo = new Label("BATTLESHIP ENGINE");
        titulo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #38bdf8; -fx-letter-spacing: 2px;");

        Label subtitulo = new Label("Selecione a Dificuldade da Frota Inimiga:");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");
        
        ComboBox<DificuldadeBot> comboDificuldade = new ComboBox<>();
        comboDificuldade.getItems().addAll(DificuldadeBot.FACIL, DificuldadeBot.MEDIO, DificuldadeBot.DIFICIL);
        comboDificuldade.setValue(DificuldadeBot.MEDIO);
     // Estilização com texto preto e fundo claro dentro do combo para não ocultar opções
        comboDificuldade.setStyle("-fx-font-size: 13px; -fx-background-color: #f1f5f9; -fx-font-weight: bold;");
        
        Button botaoIniciar = new Button("INICIAR BATALHA");
        botaoIniciar.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #0284c7; -fx-text-fill: white; -fx-padding: 12 28; -fx-cursor: hand; -fx-background-radius: 6;");

        botaoIniciar.setOnAction(e -> {
            this.dificuldadeSelecionada = comboDificuldade.getValue();
            this.bot = FabricaBot.criarBot(this.dificuldadeSelecionada);
            iniciarPartidaVersus();
        });

        layoutMenu.getChildren().addAll(icone, titulo, subtitulo, comboDificuldade, botaoIniciar);

        Scene cenaMenu = new Scene(layoutMenu, 520, 380);
        palco.setScene(cenaMenu);
    }

    private void iniciarPartidaVersus() {
        this.engineJogador = new BattleshipEngine();
        this.engineBot = new BattleshipEngine();

        this.engineJogador.startGame();
        this.engineBot.startGame();

        this.ultimoResultadoBot = null;
        this.botoesRadarInimigo = new Button[8][8];
        this.celulasDefesaJogador = new Label[8][8];

        BorderPane raizJogo = new BorderPane();
        raizJogo.setPadding(new Insets(16));
        raizJogo.setStyle("-fx-background-color: #081220;");

        // Painel Superior: Placar e Logs
        VBox painelTopo = new VBox(6);
        painelTopo.setAlignment(Pos.CENTER);

        Label rotuloModo = new Label("RADAR TÁTICO — IA: " + dificuldadeSelecionada.name());
        rotuloModo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #38bdf8;");

        rotuloStatus = new Label("Sua vez! Selecione uma coordenada no radar inimigo.");
        // Texto em branco puro com peso médio para leitura nítida sobre o azul escuro
        rotuloStatus.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");

        rotuloPlacar = new Label("Navios Inimigos: " + engineBot.getNaviosRestantes() + " | Seus Navios: " + engineJogador.getNaviosRestantes());
        // Verde esmeralda brilhante para contraste com o preto/azul
        rotuloPlacar.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #4ade80;");
        
        painelTopo.getChildren().addAll(rotuloModo, rotuloStatus, rotuloPlacar);
        raizJogo.setTop(painelTopo);

        // Painel Central: Tabuleiros
        HBox painelTabuleiros = new HBox(36);
        painelTabuleiros.setAlignment(Pos.CENTER);
        painelTabuleiros.setPadding(new Insets(16, 0, 16, 0));

        Label tituloDefesa = new Label("🛡️ SUA FROTA (Defesa)");
        tituloDefesa.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #60a5fa;");
        VBox secaoDefesa = new VBox(8, tituloDefesa, criarGradeDefesaJogador());
        secaoDefesa.setAlignment(Pos.CENTER);

        Label tituloAtaque = new Label("🎯 RADAR DE DISPARO (Ataque)");
        tituloAtaque.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f87171;");
        VBox secaoAtaque = new VBox(8, tituloAtaque, criarGradeRadarInimigo());
        secaoAtaque.setAlignment(Pos.CENTER);

        painelTabuleiros.getChildren().addAll(secaoDefesa, secaoAtaque);
        raizJogo.setCenter(painelTabuleiros);

        // Painel Inferior
        Button botaoMenu = new Button("Abandonar / Menu");
        botaoMenu.setOnAction(e -> exibirTelaMenu());
        botaoMenu.setStyle("-fx-background-color: #334155; -fx-text-fill: #f8fafc; -fx-cursor: hand; -fx-padding: 8 16; -fx-background-radius: 4;");

        VBox painelBase = new VBox(botaoMenu);
        painelBase.setAlignment(Pos.CENTER);
        raizJogo.setBottom(painelBase);

        Scene cenaJogo = new Scene(raizJogo, 860, 560);
        palco.setScene(cenaJogo);
    }

    private GridPane criarGradeRadarInimigo() {
        GridPane grade = new GridPane();
        grade.setAlignment(Pos.CENTER);
        grade.setHgap(4);
        grade.setVgap(4);

        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                Button btn = new Button();
                btn.setPrefSize(TAMANHO_CELULA, TAMANHO_CELULA);
                btn.setStyle("-fx-background-color: #0369a1; -fx-border-color: #0284c7; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;");

                final int linha = l;
                final int coluna = c;
                btn.setOnAction(e -> processarTurnoCompleto(linha, coluna));

                botoesRadarInimigo[l][c] = btn;
                grade.add(btn, c, l);
            }
        }
        return grade;
    }

    private GridPane criarGradeDefesaJogador() {
        GridPane grade = new GridPane();
        grade.setAlignment(Pos.CENTER);
        grade.setHgap(4);
        grade.setVgap(4);

        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                Label celula = new Label();
                celula.setPrefSize(TAMANHO_CELULA, TAMANHO_CELULA);
                celula.setAlignment(Pos.CENTER);
                celula.setStyle("-fx-background-color: #0f172a; -fx-border-color: #1e293b; -fx-border-radius: 4;");

                celulasDefesaJogador[l][c] = celula;
                grade.add(celula, c, l);
            }
        }
        return grade;
    }

    private void processarTurnoCompleto(int linha, int coluna) {
        // 1. TIRO DO JOGADOR
        ResultadoTiro resultadoJogador = engineBot.shoot(linha, coluna);
        Button btnClicado = botoesRadarInimigo[linha][coluna];
        btnClicado.setDisable(true);

        if (resultadoJogador.isAcertou()) {
            btnClicado.setText("💥");
            btnClicado.setStyle("-fx-background-color: #991b1b; -fx-border-color: #ef4444; -fx-font-size: 16px; -fx-opacity: 1.0; -fx-background-radius: 4;");
        } else {
            btnClicado.setText("🌊");
            btnClicado.setStyle("-fx-background-color: #1e293b; -fx-border-color: #475569; -fx-font-size: 15px; -fx-opacity: 1.0; -fx-background-radius: 4;");
        }

        atualizarPlacar();

        if (engineBot.isVitoria()) {
            finalizarPartida(true);
            return;
        }

        // 2. TIRO DO BOT
        Posicao tiroBot = bot.escolherProximoTiro(engineJogador.getGradeAtual(), ultimoResultadoBot);
        this.ultimoResultadoBot = engineJogador.shoot(tiroBot.getLinha(), tiroBot.getColuna());

        Label celulaAtingida = celulasDefesaJogador[tiroBot.getLinha()][tiroBot.getColuna()];
        if (ultimoResultadoBot.isAcertou()) {
            celulaAtingida.setText("💥");
            celulaAtingida.setStyle("-fx-background-color: #7f1d1d; -fx-border-color: #dc2626; -fx-font-size: 16px;");
        } else {
            celulaAtingida.setText("🌊");
            celulaAtingida.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-font-size: 15px;");
        }

        rotuloStatus.setText("Você atacou (" + linha + "," + coluna + "). A IA disparou em (" + tiroBot.getLinha() + "," + tiroBot.getColuna() + ")!");
        atualizarPlacar();

        if (engineJogador.isVitoria()) {
            finalizarPartida(false);
        }
    }

    private void atualizarPlacar() {
        rotuloPlacar.setText("Navios Inimigos: " + engineBot.getNaviosRestantes() + " | Seus Navios: " + engineJogador.getNaviosRestantes());
    }

    private void finalizarPartida(boolean vitoriaJogador) {
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                botoesRadarInimigo[l][c].setDisable(true);
            }
        }

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        if (vitoriaJogador) {
            alerta.setTitle("Vitória Naval!");
            alerta.setHeaderText("Almirante, a frota inimiga sucumbiu!");
            alerta.setContentText("Todos os navios da IA foram afundados.");
        } else {
            alerta.setTitle("Derrota!");
            alerta.setHeaderText("Sua frota foi destruída!");
            alerta.setContentText("A IA eliminou todas as suas embarcações.");
        }
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}