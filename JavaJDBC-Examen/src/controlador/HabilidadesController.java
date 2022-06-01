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
import model.Habilidad;
import model.HabilidadesDAO;

public class HabilidadesController{

	//Objecte per gestionar la persistència de les dades
	private HabilidadesDAO habilidadesDAO;
	//Objecte per gestionar el objecte actual
	private Habilidad habilidad = null;
	//indicador de nou registre
	private boolean nouRegistre = false;
	//objecte per afegir les files de la taula
	private ObservableList<Habilidad> habilidadesData;

	//Elements gràfics de la UI
	@FXML
	private AnchorPane anchorPane;
	private Stage ventana;
	@FXML private TextField idTextField;
	@FXML private TextField nomTextField;
	@FXML private TextField costeTextField;
	@FXML private TextField descripcionTextField;
	@FXML private TableView<Habilidad> habilidadesTable;
	@FXML private TableColumn<Habilidad, Integer> idColumn;
	@FXML private TableColumn<Habilidad, String> nomColumn;

	//Validació dades
	private ValidationSupport vs;

	public void setConexionBD(Connection conexionBD) {	
		//Crear objecte DAO de persones
		habilidadesDAO = new HabilidadesDAO(conexionBD);
		
		// Aprofitar per carregar la taula de persones (no ho podem fer al initialize() perque encara no tenim l'objecte conexionBD)
		// https://code.makery.ch/es/library/javafx-tutorial/part2/
		habilidadesData = FXCollections.observableList(habilidadesDAO.getHabilidadesList());
		habilidadesTable.setItems(habilidadesData);
	}
	
	/**
	 * Inicialitza la classe. JAVA l'executa automàticament després de carregar el fitxer fxml amb loader.load()
	 * i abans de rebre l'objecte conexionBD o qualsevol altre que pasem des del IniciMenuController
	 */
	@FXML private void initialize() {
		idColumn.setCellValueFactory(new PropertyValueFactory<Habilidad,Integer>("id"));
		nomColumn.setCellValueFactory(new PropertyValueFactory<Habilidad,String>("nom"));

		// Quan l'usuari canvia de linia executem el métode mostrarPersona
		habilidadesTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> mostrarPersona(newValue));

		//Validació dades
		//https://github.com/controlsfx/controlsfx/issues/1148
		//produeix error si no posem a les VM arguments això: --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED
		vs = new ValidationSupport();
		vs.registerValidator(idTextField, true, Validator.createEmptyValidator("ID obligatory"));
		vs.registerValidator(nomTextField, true, Validator.createEmptyValidator("Nom obligatory"));
		vs.registerValidator(costeTextField, true, Validator.createEmptyValidator("Position obligatory"));
		vs.registerValidator(descripcionTextField, Validator.createRegexValidator("E-mail incorrecte", "^(.+)@(.+)$", Severity.ERROR));	
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
			habilidad = habilidadesDAO.find(Integer.parseInt(idTextField.getText()));
			mostrarPersona(habilidad);
			//seleccionar la fila de la taula asociada al codi introduit
			habilidadesTable.getSelectionModel().select(habilidad);
			habilidadesTable.refresh();
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
				if(habilidadesDAO.delete(Integer.parseInt(idTextField.getText()))){ 
					habilidadesData.remove(habilidadesTable.getSelectionModel().getSelectedIndex());

					limpiarFormulario();
					habilidadesDAO.showAll();
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
				habilidad = new Habilidad(Integer.parseInt(idTextField.getText()), nomTextField.getText(),
						Double.parseDouble(costeTextField.getText()), descripcionTextField.getText());

				habilidadesData.add(habilidad);
			}else{
				//modificació registre existent
				habilidad = habilidadesTable.getSelectionModel().getSelectedItem();
				habilidad.setName(nomTextField.getText()); 
				habilidad.setCoste(Double.parseDouble(costeTextField.getText())); 
				habilidad.setDescripcion(descripcionTextField.getText()); 
			}
			habilidadesDAO.save(habilidad);
			limpiarFormulario();

			habilidadesTable.refresh();

			habilidadesDAO.showAll();
		}
	}

	public void sortir(){
		habilidadesDAO.showAll();
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

	private void mostrarPersona(Habilidad habilidad) {
		if(habilidad != null){ 
			//llegir persona (posar els costes als controls per modificar-los)
			nouRegistre = false;
			idTextField.setText(String.valueOf(habilidad.getId()));
			nomTextField.setText(habilidad.getName());
			costeTextField.setText(String.valueOf(habilidad.getCost()));
			descripcionTextField.setText(habilidad.getDescripcion());
		}else{ 
			//nou registre
			nouRegistre = true;
			//idTextField.setText(""); no hem de netejar la PK perquè l'usuari ha posat un coste
			nomTextField.setText("");
			costeTextField.setText("");
			descripcionTextField.setText("");
		}
	}

	private void limpiarFormulario(){
		idTextField.setText("");
		nomTextField.setText("");
		costeTextField.setText("");
		descripcionTextField.setText("");
	}
}
