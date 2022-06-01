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
import model.Objeto;
import model.ObjetosDAO;

public class ObjetosController{

	//Objecte per gestionar la persistència de les dades
	private ObjetosDAO objetosDAO;
	//Objecte per gestionar el objecte actual
	private Objeto objeto = null;
	//indicador de nou registre
	private boolean nouRegistre = false;
	//objecte per afegir les files de la taula
	private ObservableList<Objeto> objetosData;

	//Elements gràfics de la UI
	@FXML
	private AnchorPane anchorPane;
	private Stage ventana;
	@FXML private TextField idTextField;
	@FXML private TextField nomTextField;
	@FXML private TextField valorTextField;
	@FXML private TextField efectoTextField;
	@FXML private TableView<Objeto> objetosTable;
	@FXML private TableColumn<Objeto, Integer> idColumn;
	@FXML private TableColumn<Objeto, String> nomColumn;

	//Validació dades
	private ValidationSupport vs;

	public void setConexionBD(Connection conexionBD) {	
		//Crear objecte DAO de persones
		objetosDAO = new ObjetosDAO(conexionBD);
		
		// Aprofitar per carregar la taula de persones (no ho podem fer al initialize() perque encara no tenim l'objecte conexionBD)
		// https://code.makery.ch/es/library/javafx-tutorial/part2/
		objetosData = FXCollections.observableList(objetosDAO.getObjetosList());
		objetosTable.setItems(objetosData);
	}
	
	/**
	 * Inicialitza la classe. JAVA l'executa automàticament després de carregar el fitxer fxml amb loader.load()
	 * i abans de rebre l'objecte conexionBD o qualsevol altre que pasem des del IniciMenuController
	 */
	@FXML private void initialize() {
		idColumn.setCellValueFactory(new PropertyValueFactory<Objeto,Integer>("id"));
		nomColumn.setCellValueFactory(new PropertyValueFactory<Objeto,String>("nom"));

		// Quan l'usuari canvia de linia executem el métode mostrarPersona
		objetosTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> mostrarPersona(newValue));

		//Validació dades
		//https://github.com/controlsfx/controlsfx/issues/1148
		//produeix error si no posem a les VM arguments això: --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED
		vs = new ValidationSupport();
		vs.registerValidator(idTextField, true, Validator.createEmptyValidator("ID obligatory"));
		vs.registerValidator(nomTextField, true, Validator.createEmptyValidator("Nom obligatory"));
		vs.registerValidator(valorTextField, true, Validator.createEmptyValidator("Position obligatory"));
		vs.registerValidator(efectoTextField, Validator.createRegexValidator("E-mail incorrecte", "^(.+)@(.+)$", Severity.ERROR));	
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
			objeto = objetosDAO.find(Integer.parseInt(idTextField.getText()));
			mostrarPersona(objeto);
			//seleccionar la fila de la taula asociada al codi introduit
			objetosTable.getSelectionModel().select(objeto);
			objetosTable.refresh();
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
				if(objetosDAO.delete(Integer.parseInt(idTextField.getText()))){ 
					objetosData.remove(objetosTable.getSelectionModel().getSelectedIndex());

					limpiarFormulario();
					objetosDAO.showAll();
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
				objeto = new Objeto(Integer.parseInt(idTextField.getText()), nomTextField.getText(),
						Integer.parseInt(valorTextField.getText()), efectoTextField.getText());

				objetosData.add(objeto);
			}else{
				//modificació registre existent
				objeto = objetosTable.getSelectionModel().getSelectedItem();
				objeto.setName(nomTextField.getText()); 
				objeto.setValor(Integer.parseInt(valorTextField.getText())); 
				objeto.setEfecto(efectoTextField.getText()); 
			}
			objetosDAO.save(objeto);
			limpiarFormulario();

			objetosTable.refresh();

			objetosDAO.showAll();
		}
	}

	public void sortir(){
		objetosDAO.showAll();
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

	private void mostrarPersona(Objeto objeto) {
		if(objeto != null){ 
			//llegir persona (posar els valors als controls per modificar-los)
			nouRegistre = false;
			idTextField.setText(String.valueOf(objeto.getId()));
			nomTextField.setText(objeto.getName());
			valorTextField.setText(String.valueOf(objeto.getValor()));
			efectoTextField.setText(objeto.getEfecto());
		}else{ 
			//nou registre
			nouRegistre = true;
			//idTextField.setText(""); no hem de netejar la PK perquè l'usuari ha posat un valor
			nomTextField.setText("");
			efectoTextField.setText("");
		}
	}

	private void limpiarFormulario(){
		idTextField.setText("");
		nomTextField.setText("");
		valorTextField.setText("");
		efectoTextField.setText("");
	}
}
