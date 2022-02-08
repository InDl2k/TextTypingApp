package App;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class MainController implements Initializable {

    @FXML
    private Label lbl_accuracy;

    @FXML
    private Label lbl_WPM;

    @FXML
    private Button btn_pause;

    @FXML
    private Label lbl_timer;

    @FXML
    private Button btn_reset;

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
    File file = null;
    WpmController wpmController;

    TimerController timerController;
    Timer timer = new Timer(true);
    TimerTask updateTime = new TimerTask() {
        @Override
        public void run() {
            if(timerController.getActive()) {
                timerController.incrementTime();
                Platform.runLater(() -> setLabels());
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timerController = new TimerController();
        timer.schedule(updateTime, 0, 1000);

        pageController = new PageController();
        btn_reset.setDisable(true);
        Parser.parseLine("Hello\n", text);
        init();
        outputText.show(text);
    }

    private void init(){
        timerController.reset();
        timerController.setTimerActive(true);

        outputScroll = new ScrollController(scrollPane_output);
        outputText = new OutputText(tflow_output, pageController);
        countController = new CountController();
        pointer = new Pointer();
        inputText = new InputText(txtarea_input, pointer, countController, text);
        wpmController = new WpmController(countController, timerController, lbl_WPM);

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
        lbl_timer.setText(String.format("Timer:\t %02d:%02d:%02d", timerController.getHours(), timerController.getMinutes(), timerController.getSeconds()));
        lbl_WPM.setText(String.format("WPM =\t %d", wpmController.getWpm())); wpmController.setColor(wpmController.getWpm());
        lbl_accuracy.setText(String.format("Accuracy:\t %.2f", wpmController.getAccuracy() * 100) + "%");
    }


    //key pressed
    @FXML
    private void getKey(KeyEvent event) {
        if(!isLocked) {
            key = event.getCode();
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

            if (isEndOfText()) {
                pauseTimer(new ActionEvent());
                btn_pause.setDisable(true);
                callEndTypeAlert();
            }

            text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: orange;");
            outputScroll.scrollText(pointer.getX(), text.size());
        }
    }

    private boolean isEndOfText(){
        return pointer.getX() == text.size() - 1 && pointer.getY() == text.get(text.size() - 1).size() - 1 && pageController.getCurPage() == pageController.getSumPages();
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
        timerController.reset();

        tflow_output.getChildren().clear();
        pageController.setCurPage(1);
        Parser.parseFile(file,pageController.getFirstIndex(), pageController.getLastIndex(), text);
        init();
        outputText.show(text);
    }

    @FXML
    private void openFile(ActionEvent event) {
        timerController.setTimerActive(false);

        text.clear();
        tflow_output.getChildren().clear();

        Stage stage = new Stage();
        file = new FileChooser().showOpenDialog(stage);
        pageController = new PageController(file);
        btn_reset.setDisable(false);

        Parser.parseFile(file,pageController.getFirstIndex(), pageController.getLastIndex(), text);
        init();
        outputText.show(text);
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
        Parser.parseFile(file, pageController.getFirstIndex(), pageController.getLastIndex(), text);
        tflow_output.getChildren().clear();
        setDefaultSettings();
        outputText.show(text);
    }

    @FXML
    private void clearPage(ActionEvent event) {
        outputText.clear(text);
        setDefaultSettings();
    }

    @FXML
    void pauseTimer(ActionEvent event) {
        isLocked = !isLocked;
        timerController.setTimerActive(!timerController.getActive());
    }

    private void setDefaultSettings(){
        btn_pause.setDisable(false);
        txtarea_input.setEditable(true);
        isLocked = false;
        timerController.setTimerActive(true);
        countController.resetCur();
        pointer.setY(0);
        pointer.setX(0);
        txtarea_input.setText("");
        txtarea_input.requestFocus();
        scrollPane_output.setVvalue(0.0d);
        setLabels();
        text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: orange;");
    }

}
