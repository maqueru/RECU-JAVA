package controlador;

import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Campeon;
import model.ChampionsDAO;
import model.managerDAO;

public class ChampionsController{

	//Objecte per gestionar la persistència de les dades
	private ChampionsDAO championsDAO;
	//Objecte per gestionar el objecte actual
	private Campeon champions = null;
	//indicador de nou registre
	private boolean nouRegistre = false;
	//objecte per afegir les files de la taula
	private ObservableList<Campeon> championsData;

	//Elements gràfics de la UI
	@FXML
	private AnchorPane anchorPane;
	private Stage ventana;
	@FXML private TextField idTextField;
	@FXML private TextField nomTextField;
	@FXML private TextField razaTextField;
	@FXML private TextField vidaTextField;
	@FXML private TextField energiaTextField;
	@FXML private TextField habilidadesTextField;
	@FXML private TextField objetosTextField;
	@FXML private TableView<Campeon> championsTable;
	@FXML private TableColumn<Campeon, Integer> idColumn;
	@FXML private TableColumn<Campeon, String> nomColumn;

	//Validació dades
	private ValidationSupport vs;

	public void setConexionBD(Connection conexionBD) {	
		//Crear objecte DAO de persones
		championsDAO = new ChampionsDAO(conexionBD);
		
		// Aprofitar per carregar la taula de persones (no ho podem fer al initialize() perque encara no tenim l'objecte conexionBD)
		// https://code.makery.ch/es/library/javafx-tutorial/part2/
		championsData = FXCollections.observableList(championsDAO.getChampionsList());
		championsTable.setItems(championsData);
	}
	
	/**
	 * Inicialitza la classe. JAVA l'executa automàticament després de carregar el fitxer fxml amb loader.load()
	 * i abans de rebre l'objecte conexionBD o qualsevol altre que pasem des del IniciMenuController
	 */
	@FXML private void initialize() {
		idColumn.setCellValueFactory(new PropertyValueFactory<Campeon,Integer>("id"));
		nomColumn.setCellValueFactory(new PropertyValueFactory<Campeon,String>("nom"));

		// Quan l'usuari canvia de linia executem el métode mostrarPersona
		championsTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showChamps(newValue));

		//Validació dades
		//https://github.com/controlsfx/controlsfx/issues/1148
		//produeix error si no posem a les VM arguments això: --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED
		vs = new ValidationSupport();
		vs.registerValidator(idTextField, true, Validator.createEmptyValidator("ID obligatory"));
		vs.registerValidator(nomTextField, true, Validator.createEmptyValidator("Nombre obligatory"));
		vs.registerValidator(razaTextField, true, Validator.createEmptyValidator("Raza obligatory"));
		vs.registerValidator(vidaTextField, true, Validator.createEmptyValidator("Vida obligatory"));
		vs.registerValidator(energiaTextField, true, Validator.createEmptyValidator("Energia obligatory"));
		vs.registerValidator(habilidadesTextField, true, Validator.createEmptyValidator("Habilidades obligatory"));
		vs.registerValidator(objetosTextField, true, Validator.createEmptyValidator("Objetos obligatory"));
}

	public Stage getVentana() {
		return ventana;
	}

	public void setVentana(Stage ventana) {
		this.ventana = ventana;
	}

	@FXML private void onKeyPressedId(KeyEvent e) throws IOException {
		if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.TAB){
			//Comprovar si existeix la persona indicada en el control idTextField
			champions = championsDAO.find(Integer.parseInt(idTextField.getText()));
			showChamps(champions);
			//seleccionar la fila de la taula asociada al codi introduit
			championsTable.getSelectionModel().select(champions);
			championsTable.refresh();
		}
	}

	@FXML
	void onActionEliminar(ActionEvent event) {
		if(isDatosValidos()){
			// Mostrar missatge confirmació
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText("Vol esborrar la persona " + idTextField.getText() + "?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				if(championsDAO.delete(Integer.parseInt(idTextField.getText()))){ 
					championsData.remove(championsTable.getSelectionModel().getSelectedIndex());

					limpiarFormulario();
					championsDAO.showAll();
				}
			}
		}
	}

	@FXML private void onActionSortir(ActionEvent e) throws IOException {
		sortir();
		//tancar el formulari
		((BorderPane)anchorPane.getParent()).setCenter(null);
	}

	@FXML private void onActionGuardar(ActionEvent e) throws IOException {
		//verificar si les dades són vàlides
		if(isDatosValidos()){
			if(nouRegistre){
				Campeon champions = new Campeon(Integer.parseInt(idTextField.getText()), nomTextField.getText(),
						razaTextField.getText(),Integer.parseInt(vidaTextField.getText()),Integer.parseInt(energiaTextField.getText()),managerDAO.getInstance().getSkillDAO().findSkillById(Integer.parseInt(habilidadesTextField.getText())),managerDAO.getInstance().getObjectDAO().findObjectById(Integer.parseInt(objetosTextField.getText())));

				championsData.add(champions);
			}else{
				//modificació registre existent
				champions = championsTable.getSelectionModel().getSelectedItem();

				champions.setName(nomTextField.getText()); 
				champions.setRaza(razaTextField.getText());
				champions.setVida(Integer.parseInt(vidaTextField.getText()));
				champions.setEnergia(Integer.parseInt(energiaTextField.getText()));
				/*,habilidadesTextField.getList(),objetosTextField.getList()*/
			}
			championsDAO.save(champions);
			limpiarFormulario();

			championsTable.refresh();

			championsDAO.showAll();
		}
	}

	public void sortir(){
		championsDAO.showAll();
	}
	private boolean isDatosValidos() {

		//Comprovar si totes les dades són vàlides
		if (vs.isInvalid()) {
			String errors = vs.getValidationResult().getMessages().toString();
			// Mostrar finestra amb els errors
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(ventana);
			alert.setTitle("Camps incorrectes");
			alert.setHeaderText("Corregeix els camps incorrectes");
			alert.setContentText(errors);
			alert.showAndWait();
		
			return false;
		}
		return true;
	}

	private void showChamps(Campeon champions) {
		if(champions != null){ 
			//llegir persona (posar els valors als controls per modificar-los)
			nouRegistre = false;
			idTextField.setText(String.valueOf(champions.getId()));
			nomTextField.setText(champions.getName());
			razaTextField.setText(champions.getRaza());
			vidaTextField.setText(String.valueOf(champions.getVida()));
			energiaTextField.setText(String.valueOf(champions.getEnergia()));
			habilidadesTextField.setText(String.valueOf(champions.getHabilidades()));
			objetosTextField.setText(String.valueOf(champions.getObjetos()));
		}else{ 
			//nou registre
			nouRegistre = true;
			//idTextField.setText(""); no hem de netejar la PK perquè l'usuari ha posat un valor
			nomTextField.setText("");
			razaTextField.setText("");
			vidaTextField.setText("");
			energiaTextField.setText("");
			habilidadesTextField.setText("");
			objetosTextField.setText("");
		}
	}

	private void limpiarFormulario(){
		idTextField.setText("");
		nomTextField.setText("");
		razaTextField.setText("");
		vidaTextField.setText("");
		energiaTextField.setText("");
		habilidadesTextField.setText("");
		objetosTextField.setText("");
	}
}
