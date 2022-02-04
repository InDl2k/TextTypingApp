package App;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import static java.lang.Integer.max;

public class MainController implements Initializable {

    @FXML
    private Button btn_clear;

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
    int ptr;
    int rowPtr;
    KeyCode key;
    Vector<Vector<Label>> text = new Vector<>();
    boolean isLocked;

    OutputText outputText;

    //class CountController
    int totalCntCorrect;
    int totalCntWrong;
    int curCntCorrect;
    int curCntWrong;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Parser.parseLine("Hello\n", text);
        outputText = new OutputText(tflow_output);
        init();
        outputText.show(PageController.getFirstIndex(), PageController.getLastIndex(), text);
    }

    private void init(){
        totalCntWrong = 0;
        totalCntCorrect = 0;
        curCntCorrect = 0;
        curCntWrong = 0;

        PageController.curPage = 1;
        PageController.getPages(text);

        outputText.clear(text);
        setDefaultSettings();

        checkEnter();
    }

    private void checkEnter(){
        if(text.get(rowPtr).get(ptr).getText().equals(" ")) {
            txtarea_input.setEditable(false);
            btn_clear.setDisable(true);
        }
    }

    private void setLabels(){
        lbl_cntCorrect.setText(String.format("Correct\t = %d", totalCntCorrect+curCntCorrect));
        lbl_cntWrong.setText(String.format("Wrong\t = %d", totalCntWrong+curCntWrong));
        lbl_pages.setText(String.format("Pages:\t %d/%d", PageController.curPage, PageController.sumPages));
    }

    //key pressed
    @FXML
    void getKey(KeyEvent event) {
        if(!isLocked) {
            key = event.getCode();

            if (key == KeyCode.BACK_SPACE) {
                if (ptr >= text.get(rowPtr).size() - 1) {
                    txtarea_input.setEditable(true);
                    btn_clear.setDisable(false);
                    txtarea_input.setText(txtarea_input.getText().substring(0, text.get(rowPtr).size() - 2));
                    txtarea_input.positionCaret(ptr);
                }

                try {
                    String charColor = text.get(rowPtr).get(ptr - 1).getStyle();
                    if(charColor.contains("red"))
                        lbl_cntWrong.setText(String.format("Wrong\t= %d", totalCntCorrect+(--curCntWrong)));
                    else if(charColor.contains("green"))
                        lbl_cntCorrect.setText(String.format("Correct\t= %d", totalCntCorrect+(--curCntCorrect)));
                }
                catch (ArrayIndexOutOfBoundsException exc){
                    System.out.println("Out of bounds");
                }
                text.get(rowPtr).get(ptr).setStyle("-fx-background-color: none;");
                ptr = max(0, --ptr);
            }
            if(key == KeyCode.ENTER && (!txtarea_input.isEditable())) {                           //Bug with ctrl combo need to lock ctrl   i dont know how)
                text.get(rowPtr).get(ptr).setStyle("-fx-background-color: none;");
                ptr = 0;
                txtarea_input.setText("");
                rowPtr++;
                if(!text.get(rowPtr).get(ptr).getText().equals(" ")) {
                    txtarea_input.setEditable(true);
                    btn_clear.setDisable(false);
                }
                else{
                    txtarea_input.setEditable(false);
                    btn_clear.setDisable(true);
                }
                if(rowPtr >= PageController.getLastIndex()){
                    totalCntCorrect += curCntCorrect;
                    totalCntWrong += curCntWrong;
                    curCntWrong = 0;
                    curCntCorrect = 0;
                    nextPage(new ActionEvent());
                }
            }
            else {
                txtarea_input.setText(txtarea_input.getText().substring(0, ptr));
                txtarea_input.positionCaret(ptr);
            }

            text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");
        }
    }

    //key typed
    @FXML
    void input(KeyEvent event) {
        if(!isLocked) {
            if (txtarea_input.isEditable() && key != KeyCode.BACK_SPACE && key != KeyCode.DELETE) {
                if (ptr < text.get(rowPtr).size() - 1 && key != KeyCode.ENTER && isCorrect()) {                         //SOMEBODY TOUCHA MY SPAGHET
                    text.get(rowPtr).get(ptr).setStyle("-fx-text-fill: green;");                                        
                    lbl_cntCorrect.setText(String.format("Correct\t= %d", totalCntCorrect+(++curCntCorrect)));
                }
                else if(key != KeyCode.ENTER) {
                    if (text.get(rowPtr).get(ptr).getText().equals(" "))
                        text.get(rowPtr).get(ptr).setStyle("-fx-background-color: red;");
                    else
                        text.get(rowPtr).get(ptr).setStyle("-fx-text-fill: red;");
                    lbl_cntWrong.setText(String.format("Wrong\t= %d", totalCntWrong+(++curCntWrong)));
                }
                ptr = txtarea_input.getCaretPosition();
                //System.out.printf("rowPtr = %d, ptr = %d\n", rowPtr, ptr);                                            //DEBUG
                if (text.get(rowPtr).get(ptr).getText().equals(" ") && ptr >= text.get(rowPtr).size() - 1) {
                    txtarea_input.setText(txtarea_input.getText().substring(0, ptr));
                    txtarea_input.setEditable(false);
                    btn_clear.setDisable(true);
                    if (rowPtr == text.size() - 1) {
                        isLocked = true;
                        callEndTypeAlert();
                    }
                }
            }

            text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");
            scrollText();
        }
    }

    private boolean isCorrect(){
        return txtarea_input.getText().charAt(ptr) == text.get(rowPtr).get(ptr).getText().charAt(0);
    }


    //class AutoScroll
    private void scrollText() {
        float procentTextScroll =  Float.parseFloat(String.format(Locale.US,"%.2f", getProcentTypedOfText()));
        scrollPane_output.setVvalue(procentTextScroll * 1.0d);
    }
    private float getProcentTypedOfText(){
        return ((rowPtr - PageController.getFirstIndex()) / ((float)PageController.getLastIndex() - (float)PageController.getFirstIndex()));
    }

    private void callEndTypeAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("End of file");
        alert.setHeaderText("U just typed all text");
        alert.setContentText("U can reset or open new file");
        alert.showAndWait();
    }

    @FXML
    void reset(ActionEvent event) {
        tflow_output.getChildren().clear();
        PageController.curPage = 1;
        init();
        outputText.show(PageController.getFirstIndex(), PageController.getLastIndex(), text);
    }

    @FXML
    void openFile(ActionEvent event) {
        text.clear();
        Stage stage = new Stage();
        File file = new FileChooser().showOpenDialog(stage);
        tflow_output.getChildren().clear();
        Parser.parseFile(file, text);
        init();
        outputText.show(PageController.getFirstIndex(), PageController.getLastIndex(), text);
    }

    @FXML
    void focusOnInput(MouseEvent event) {
        txtarea_input.requestFocus();
    }

    @FXML
    void nextPage(ActionEvent event) {
        if(PageController.curPage < PageController.sumPages) {
            lbl_pages.setText(String.format("Pages:\t %d/%d", ++PageController.curPage, PageController.sumPages));
            toPage();
            checkEnter();
        }
    }

    @FXML
    void previousPage(ActionEvent event) {
        if(PageController.curPage > 1) {
            lbl_pages.setText(String.format("Pages:\t %d/%d", --PageController.curPage, PageController.sumPages));
            toPage();
            checkEnter();
        }
    }

    private void toPage(){
        clearPage(new ActionEvent());
        tflow_output.getChildren().clear();
        rowPtr = PageController.getFirstIndex();
        outputText.show(PageController.getFirstIndex(), PageController.getLastIndex(), text);
        text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");
    }

    @FXML
    void clearPage(ActionEvent event) {
        outputText.clear(text);
        setDefaultSettings();
    }

    private void setDefaultSettings(){
        txtarea_input.setEditable(true);
        btn_clear.setDisable(false);
        isLocked = false;
        curCntWrong = 0;
        curCntCorrect = 0;
        ptr = 0;
        rowPtr = PageController.getFirstIndex();
        txtarea_input.setText("");
        txtarea_input.requestFocus();
        scrollPane_output.setVvalue(0.0d);
        setLabels();
        text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");
    }

}
