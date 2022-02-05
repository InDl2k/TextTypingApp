package App;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class MainController implements Initializable {

    @FXML
    private Label lbl_cntCorrect;

    @FXML
    private Label lbl_cntWrong;

    @FXML
    private ScrollPane scrollPane_output;

    @FXML
    private TextFlow tflow_output;

    @FXML
    private TextArea txtarea_input;

    @FXML
    private Label lbl_pages;

    //INIT
    Vector<Vector<Label>> text = new Vector<>();

    //???
    boolean isLocked;

    KeyCode key;
    Pointer pointer;
    OutputText outputText;
    ScrollController outputScroll;
    CountController countController;
    PageController pageController;
    InputText inputText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Parser.parseLine("Hello\n", text);
        init();
        outputText.show(pageController.getFirstIndex(), pageController.getLastIndex(), text);
    }

    private void init(){
        pageController = new PageController(text);                  //look at this OOP <3
        outputText = new OutputText(tflow_output, pageController);
        outputScroll = new ScrollController(scrollPane_output, pageController);
        countController = new CountController();
        pointer = new Pointer();
        inputText = new InputText(txtarea_input, pointer, countController, text);

        countController.resetAll();
        outputText.clear(text);
        setDefaultSettings();

        checkEnter();
    }

    //???
    private void checkEnter(){
        if(text.get(pointer.getX()).get(pointer.getY()).getText().equals(" ")) {
            txtarea_input.setEditable(false);
        }
    }

    public void setLabels(){
        lbl_cntCorrect.setText(String.format("Correct\t = %d", countController.getTotalCntCorrect()));
        lbl_cntWrong.setText(String.format("Wrong\t = %d", countController.getTotalCntWrong()));
        lbl_pages.setText(String.format("Pages:\t %d/%d", pageController.getCurPage(), pageController.getSumPages()));
    }

    //key pressed
    @FXML
    private void getKey(KeyEvent event) {
        if(!isLocked) {
            key = event.getCode();                      //Now thats looks cleaner)))
            inputText.keyPressed(key);
            setLabels();

            if(pointer.getX() >= pageController.getLastIndex()){
                countController.sumTotal();
                nextPage(new ActionEvent());
            }

            text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: orange;");
        }
    }

    //key typed
    @FXML
    private void input(KeyEvent event) {
        if(!isLocked) {
            inputText.keyTyped(key);
            setLabels();

            if (pointer.getX() == text.size() - 1 && pointer.getY() == text.get(text.size() - 1).size() - 1) {
                isLocked = true;
                callEndTypeAlert();
            }

            text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: orange;");
            outputScroll.scrollText(pointer.getX());
        }
    }

    private void callEndTypeAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("End of file");
        alert.setHeaderText("U just typed all text");
        alert.setContentText("U can reset or open new file");
        alert.showAndWait();
    }

    @FXML
    private void reset(ActionEvent event) {
        tflow_output.getChildren().clear();
        init();
        outputText.show(pageController.getFirstIndex(), pageController.getLastIndex(), text);
    }

    @FXML
    private void openFile(ActionEvent event) {
        text.clear();
        Stage stage = new Stage();
        File file = new FileChooser().showOpenDialog(stage);
        tflow_output.getChildren().clear();
        Parser.parseFile(file, text);
        init();
        outputText.show(pageController.getFirstIndex(), pageController.getLastIndex(), text);
    }

    @FXML
    private void focusOnInput(MouseEvent event) {
        txtarea_input.requestFocus();
    }

    @FXML
    private void nextPage(ActionEvent event) {
        if(pageController.getCurPage() <pageController.getSumPages()) {
            pageController.plusCurPage();
            lbl_pages.setText(String.format("Pages:\t %d/%d", pageController.getCurPage(), pageController.getSumPages()));
            toPage();
            checkEnter();
        }
    }

    @FXML
    private void previousPage(ActionEvent event) {
        if(pageController.getCurPage() > 1) {
            pageController.minusCurPage();
            lbl_pages.setText(String.format("Pages:\t %d/%d", pageController.getCurPage(), pageController.getSumPages()));
            toPage();
            checkEnter();
        }
    }

    private void toPage(){
        clearPage(new ActionEvent());
        tflow_output.getChildren().clear();
        pointer.setX(pageController.getFirstIndex());
        outputText.show(pageController.getFirstIndex(), pageController.getLastIndex(), text);
        text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: orange;");
    }

    @FXML
    private void clearPage(ActionEvent event) {
        outputText.clear(text);
        setDefaultSettings();
    }

    private void setDefaultSettings(){
        txtarea_input.setEditable(true);
        isLocked = false;
        countController.resetCur();
        pointer.setY(0);
        pointer.setX(pageController.getFirstIndex());
        txtarea_input.setText("");
        txtarea_input.requestFocus();
        scrollPane_output.setVvalue(0.0d);
        setLabels();
        text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: orange;");
    }

}
