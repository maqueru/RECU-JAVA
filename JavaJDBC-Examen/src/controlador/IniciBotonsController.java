package controlador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Locale.Category;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class IniciBotonsController extends Application {
	
	private Connection conexionBD;
	@FXML private Button btnChampions;
	@FXML private Button btnPlayers;
	@FXML private Button btnSortir;

	@FXML
    private BorderPane borderPane;
	private ResourceBundle texts;

	public IniciBotonsController() {
		try{
			//Establir la connexio amb la BD
			String urlBaseDades = "jdbc:postgresql://localhost/ManuelQuesada_Recuperacion2022";
			String usuari = "postgres";
			String contrasenya = "1234";
			conexionBD = DriverManager.getConnection(urlBaseDades , usuari, contrasenya);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		//Carrega el fitxer amb la interficie d'usuari inicial (Scene)
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/IniciBotonsView.fxml"));
		Scene fm_inici = new Scene(loader.load());

		//Li assigna la escena a la finestra inicial (primaryStage) i la mostra
		primaryStage.setScene(fm_inici);
		primaryStage.setTitle("Agenda");
		primaryStage.setMaximized(false);
		primaryStage.show();
	}
	@FXML
	private void onAction(ActionEvent e) throws Exception {
		if(e.getSource() == btnChampions){//verifica si el botón es igual al que llamo al evento	
			changeScene("/vista/ChampionsView.fxml", "Champions");
		}else if(e.getSource() == btnPlayers){//verifica si el botón es igual al que llamo al evento	
			changeScene("/vista/PlayersView.fxml", "Players");
		}else if(e.getSource() == btnSortir){
			Platform.exit();
		}
	}
	private void changeScene(String path, String title) throws IOException {
		//Carrega el fitxer amb la interficie d'usuari
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		
		//Carregar fitxer de textos multiidioma de la localització actual
		Locale localitzacioDisplay = Locale.getDefault(Category.DISPLAY);
		texts = ResourceBundle.getBundle("vista.Texts", localitzacioDisplay);
		//fins aquí tot igual, només falta assignar el fitxer de recursos al formulari
		loader.setResources(texts);
		
		
		//Crea una nova finestra i l'obre 
		Stage stage = new Stage();
		Scene fm_scene = new Scene(loader.load());
		stage.setTitle(title);
		stage.setScene(fm_scene);
		stage.show();
		
		/************** Modificar ************/
		//Crear un objecte de la clase PersonasController ja que necessitarem accedir al mètodes d'aquesta classe
		
		if(title.equals("Champions")) {
			ChampionsController championsController = loader.getController();
			championsController.setVentana(stage);
			stage.setOnCloseRequest((WindowEvent we) -> {championsController.sortir();});

		}else if(title.equals("Players")) {
			ObjetosController playersController = loader.getController();
			playersController.setVentana(stage);
			stage.setOnCloseRequest((WindowEvent we) -> {playersController.sortir();});
		}
	}
	@FXML
	void onActionMenuItemPlayers(ActionEvent event) throws IOException {
		//Carrega el fitxer amb la interficie d'usuari
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PlayersView.fxml"));
		Pane panell = (AnchorPane)loader.load();
		//Crear un objecte de la clase PersonasController ja que necessitarem accedir al mÃ¨todes d'aquesta classe
		ObjetosController playersController = (ObjetosController)loader.getController();
		playersController.setConexionBD(conexionBD);
		
		borderPane.setCenter(panell); 
		
	}
	@FXML
	void onActionMenuItemChampions(ActionEvent event) throws IOException {
		//Carrega el fitxer amb la interficie d'usuari
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/ChampionsView.fxml"));
		Pane panell = (AnchorPane)loader.load();
		//Crear un objecte de la clase PersonasController ja que necessitarem accedir al mÃ¨todes d'aquesta classe
		ChampionsController championsController = (ChampionsController)loader.getController();
		championsController.setConexionBD(conexionBD);
		
		borderPane.setCenter(panell); 
		
	}

	@FXML
	void onActionMenuItemSortir(ActionEvent event) {
		Platform.exit();
	}
	

	@Override
	public void stop() throws Exception {
		super.stop();
		
		try {
			if (conexionBD != null) conexionBD.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
