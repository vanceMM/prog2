/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adressbuch;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;

/**
 *
 * @author Arbeitsursel
 */
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

/**
 *
 * @author Arbeitsursel
 */
public class AdressbuchViewController implements Initializable {

    /*Kontextmenu auf fxml datei und "make controller */
    private Adressbuch adressbuch;
    @FXML
    private TextField searchField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Button addButton;
    @FXML
    private TableView<Kontakt> tableView;
    @FXML
    private TableColumn<Kontakt, String> name;
    @FXML
    private TableColumn<Kontakt, String> phone;
    @FXML
    private TableColumn<Kontakt, String> email; 

    private ObservableList<Kontakt> tableContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        adressbuch = new Adressbuch();
        tableContent = FXCollections.observableArrayList();
        showKontakte(adressbuch.getAlleKontakte());
        //Suchfeld mit eventhandler versehen
        searchField.textProperty().addListener((e) -> filterList());

        //Eingabefelder zum Editieren des Namens
        name.setCellFactory(TextFieldTableCell.<Kontakt>forTableColumn());
        name.setOnEditCommit((e) -> updateKontaktName(e));

        //Eingabefelder zum Editieren der Telefonnummer
         phone.setCellFactory(TextFieldTableCell.<Kontakt>forTableColumn());
         phone.setOnEditCommit((e)->updateKontaktPhone(e));
         
        
        //Eingabefelder zum Editieren der E-Mail Adresse
        email.setCellFactory(TextFieldTableCell.<Kontakt>forTableColumn());
        email.setOnEditCommit((e)->updateKontaktEmail(e));
        
        addButton.setOnAction((e) -> addnewKontakt());
        
        configureTable();
    }

    /**
     * Diese Methode soll die TextArea zunächst löschen und dann alle Kontakte
     * des Arrays in der TextArea anzeigen.
     *
     * @param kontakte
     */
    private void showKontakte(Kontakt[] kontakte) {

        //loescht zunaechst alle eingaben aus der textarea
        tableContent.clear();
        //fuegt alle Kontakte in TextArea ein  
        //textArea.appendText(kontakte.toString());

        for (Kontakt k : kontakte) {

            tableContent.add(k);
        }

    }

    private void filterList() {
        String s = searchField.getText();
        Kontakt ka[] = adressbuch.getKontakte(s);
        showKontakte(ka);
    }

    private void configureTable() {
        name.setCellValueFactory(new PropertyValueFactory<Kontakt, String>("name"));
        tableView.setEditable(true);
        phone.setCellValueFactory(new PropertyValueFactory<Kontakt, String>("telefon"));
        phone.setEditable(true);
        email.setCellValueFactory(new PropertyValueFactory<Kontakt, String>("email"));
        email.setEditable(true);
        tableView.setItems(tableContent);
    }

    public void updateKontaktName(TableColumn.CellEditEvent<Kontakt, String> t) {
        //Zugriff auf alten und neuen Wert
        String alt = t.getOldValue();
        String neu = t.getNewValue();

        if (alt.equals(neu)) {
            return;
        }
        int index = t.getTablePosition().getRow();
        Kontakt k = tableView.getItems().get(index);
        //setzte die neuen informationen in das neu erzeugte Kontaktobjekt
        Kontakt k1 = new Kontakt(neu, k.getTelefon(), k.getEmail());

        try {
            //Setze die neuen Kontaktinformationen an die Stelle der alten Werte
            adressbuch.updateKontakt(alt, k1);
        } catch (UngueltigerSchluesselException ex) {
            Logger.getLogger(AdressbuchViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Methode holt Präfix und erneuert Kontakte
        filterList();

        //teste ob name leer ist, am besten in methode (wird 3x benötigt name, phone, email)
    }
    
    public void updateKontaktPhone(TableColumn.CellEditEvent<Kontakt, String> t){
        //Zugriff auf alten und neuen Wert
        String alt = t.getOldValue();
        String neu = t.getNewValue();

        if (alt.equals(neu)) {
            return;
        }
        int index = t.getTablePosition().getRow();
        Kontakt k = tableView.getItems().get(index);
        //setzte die neuen informationen in das neu erzeugte Kontaktobjekt
        Kontakt k1 = new Kontakt(k.getName(), neu, k.getEmail());

        try {
            //Setze die neuen Kontaktinformationen an die Stelle der alten Werte
            adressbuch.updateKontakt(alt, k1);
        } catch (UngueltigerSchluesselException ex) {
            Logger.getLogger(AdressbuchViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Methode holt Präfix und erneuert Kontakte
        filterList();
    }
    public void updateKontaktEmail(TableColumn.CellEditEvent<Kontakt, String> t){
        //Zugriff auf alten und neuen Wert
        String alt = t.getOldValue();
        String neu = t.getNewValue();

        if (alt.equals(neu)) {
            return;
        }
        int index = t.getTablePosition().getRow();
        Kontakt k = tableView.getItems().get(index);
        //setzte die neuen informationen in das neu erzeugte Kontaktobjekt
        Kontakt k1 = new Kontakt(k.getName(), k.getTelefon(), neu);

        try {
            //Setze die neuen Kontaktinformationen an die Stelle der alten Werte
            adressbuch.updateKontakt(alt, k1);
        } catch (UngueltigerSchluesselException ex) {
            Logger.getLogger(AdressbuchViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Methode holt Präfix und erneuert Kontakte
        filterList();
    }

    private void addnewKontakt() {

        try {
            //Werte des neuen Wertes holen und Methode des Adressbuches aufrufen
            Kontakt neu = new Kontakt(nameField.getText(), phoneField.getText(), emailField.getText());
            
            adressbuch.addKontakt(neu);
            //Leeren der Eingabefelder
            nameField.clear();
            phoneField.clear();
            emailField.clear();
        } catch (DoppelterSchluesselException ex) {
            Logger.getLogger(AdressbuchViewController.class.getName()).log(Level.SEVERE, null, ex);
            ViewHelper.showError(ex.toString());
        }
        catch (IllegalStateException ex){
            Logger.getLogger(AdressbuchViewController.class.getName()).log(Level.SEVERE, null, ex);
            ViewHelper.showError(ex.toString());
        }
    }
    
    
}
