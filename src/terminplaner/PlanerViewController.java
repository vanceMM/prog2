package terminplaner;

import adressbuch.Adressbuch;
import adressbuch.UngueltigerSchluesselException;
import adressbuch.ViewHelper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

/**
 * FXML Controller class fuer die Terminplaner-Hauptansicht.
 *
 * @author beuth
 */
public class PlanerViewController implements Initializable {

    private Terminplaner planer;

    private ObservableList<Termin> data;

    private Adressbuch adressen;
    @FXML
    private DatePicker date;
    @FXML
    private Button addButton;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ListView<Termin> terminliste;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        adressen = new Adressbuch();

        try {
            planer = new Terminplaner(adressen.getKontakt("john"));
        } catch (UngueltigerSchluesselException ex) {
            Logger.getLogger(PlanerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Set the actual date in the timetable
        date.setValue(LocalDate.now());
        //Anmelden des Event Handlers
        date.setOnAction((e) -> showTermine());
        //Anmelden des Event Handlers
        addButton.setOnAction((e) -> addTermin());

        /*
         load.setOnAction((e) -> loadTermine());
         save.setOnAction((e) -> saveTermine());
         edit.setOnAction((e) -> editKontakte());
         */
        configureMenu();
        configureList();
        showTermine();
    }

    private void showTermine() {

        terminliste.getItems().clear();
        List<Termin> termineData = planer.getTermineTag(date.getValue());

        if (termineData != null) {
            data.addAll(termineData);
        }

    }

    private void addTermin() {
        URL location = getClass().getResource("terminView.fxml");
        ViewHelper.showView(new TerminViewController(null, this), location);
    }

    private void configureMenu() {

        //Menuaufbau        
        MenuItem load = new MenuItem("Laden");
        MenuItem save = new MenuItem("Speichern");
        MenuItem edit = new MenuItem("Bearbeiten");
        Menu termin = new Menu("Termine");
        Menu kontakt = new Menu("Kontakte");
        
        menuBar.getMenus().addAll(termin, kontakt);
        termin.getItems().addAll(load, save);
        kontakt.getItems().add(edit);
        termin.getItems().get(1).setOnAction(e->saveTermine());
        termin.getItems().get(0).setOnAction(e->loadTermine());
        
    }
    
    private void setHandler(Menu menu, EventHandler<ActionEvent> handler) {
        for(MenuItem it : menu.getItems()) {
            if(!(it instanceof Menu)) it.setOnAction(handler);
        }
    }

    private void configureList() {

        data = FXCollections.observableArrayList();

        terminliste.setItems(data);

        ObservableList<Termin> selection = terminliste.getSelectionModel().getSelectedItems();
        selection.addListener((ListChangeListener.Change<? extends Termin> c) -> editTermin());
    }

    private void editKontakte() {
        /*
        In der Methode editKontakte() soll die GUI des Adressbuchs geöffnet werden.
        Da der AdressbuchViewController das Adressbuch-Objekt dieses Controllers benutzen
        soll, müssen Sie ihn um einen Konstruktor erweitern, dem man eine Adressbuch-Instanz
        übergeben kann. Schreiben Sie einen solchen Konstruktor.
        Verwenden Sie den ViewHelper, um die zur FXML-Datei gehörige URL und eine
        Controller-Instanz in einem Extra-Fenster anzuzeigen. Wenn Sie nicht mehr wissen, wie
        das geht, sehen Sie in dem Skript zu Vorlesung 6 nach (zusätzliche Dialoge). Nutzen
        Sie hierfür die View adressbuchView.fxml und den AdressbuchViewController aus dem
        Paket adressbuch.
        Damit das Ganze funktioniert, müssen sie bei der adressbuchView.fxml den Controller
        löschen, ansonsten kann diese nicht von außen mit einem Controller versehen werden.
        Bauen Sie das Projekt erst neu (mit Run->Clean and Build Project), da sonst Ihre
        Änderungen unter Umständen nicht wirksam werden.
                */
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("adressbuchView.fxml"));
        //fxmlLoader.setLocation("adressbuchView.fxml");
        //fxmlLoader.setController(PlanerViewController);
    }

    private void saveTermine() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save File");
            File selection = chooser.showSaveDialog(null);
            planer.save(selection);
        } catch (IOException ex) {
           ViewHelper.showError(ex.toString());
        }
        
    }

    private void loadTermine() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Load File");
            File selection = chooser.showOpenDialog(null);
            planer.load(selection);
        } catch (IOException ex) {
            Logger.getLogger(PlanerViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PlanerViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TerminUeberschneidungException ex) {
            Logger.getLogger(PlanerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            showTermine();
        }
    }

    private void editTermin() {
        
        Termin selection = terminliste.getSelectionModel().getSelectedItem();
        Initializable controller = null;
        URL location = getClass().getResource("terminView.fxml");
        if(planer.updateErlaubt(selection)) {
            controller = new TerminViewController(selection, this);
        }
        ViewHelper.showView(controller, location);
        
    }

    private void getAdressbuch(){
        adressen.getAlleKontakte();
    }
    
    private LocalDate getselectedDate() {
        LocalDate ld = date.getValue();
        return ld;
        
    }
    
    public void processTermin(Termin t) throws TerminUeberschneidungException {
        planer.updateTermin(t);
    }
    
    
}
