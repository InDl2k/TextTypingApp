package App;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
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
    double procentTextScroll;
    Scanner scan = null;
    File file = null;
    FileChooser fileChooser = new FileChooser();
    boolean isLocked;
    int totalCntCorrect;
    int totalCntWrong;

    int curCntCorrect;
    int curCntWrong;

    int sumChars = 0;
    int sumPages;
    int showStep;
    int curPage = 1;

    int firstIndex;
    int lastIndex;

    final int maxCharsInOnePage = 1000;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        parseLine("Hello\n");
        init();
        showOutput(firstIndex, lastIndex); //u say what the heck is this, a say i dont know
    }

    private void init(){
        totalCntWrong = 0;
        totalCntCorrect = 0;
        curCntCorrect = 0;
        curCntWrong = 0;

        sumPages = 0;
        showStep = 0;
        curPage = 1;

        getPages();

        txtarea_input.setEditable(true);
        btn_clear.setDisable(false);
        txtarea_input.setText("");
        ptr = 0;
        rowPtr = firstIndex;
        procentTextScroll = 0.0;
        isLocked = false;
        setLabels();
        text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");

        if(text.get(rowPtr).get(ptr).getText().equals(" ")) {
            txtarea_input.setEditable(false);
            btn_clear.setDisable(true);
        }
    }

    private void setLabels(){
        lbl_cntCorrect.setText(String.format("Correct\t = %d", totalCntCorrect+curCntCorrect));
        lbl_cntWrong.setText(String.format("Wrong\t = %d", totalCntWrong+curCntWrong));
        lbl_pages.setText(String.format("Pages:\t %d/%d", curPage, sumPages));
    }

    void parseFile(){
        try(FileInputStream fin = new FileInputStream(file)) {
            text = new Vector<Vector<Label>>();
            scan = new Scanner(fin);
            while(scan.hasNextLine()){
                String str = scan.nextLine();
                parseLine(str);
            }
            text.get(text.size()-1).add(new Label(" "));
        }
        catch (IOException exc){
            System.out.printf("Something goes wrong with %s.txt\n", file.getName());
        }
    }

    private void parseLine(String str){
        str = str.strip();
        sumChars += str.length();
        Vector<Label> temp = new Vector<>();
        for(int j = 0; j < str.length(); ++j){
            Label lbl = new Label(String.valueOf(str.charAt(j)));
            lbl.setFont(Font.font("Cambria", 20));
            temp.add(lbl);
        }
        Label lbl = new Label(" ");
        lbl.setStyle("-fx-text-fill: white;");
        temp.add(lbl);
        text.add(temp);
    }

    private void showOutput(int l, int r){
        for(int i = l; i < r; ++i){
            tflow_output.getChildren().addAll(text.get(i));
            tflow_output.getChildren().add(new Text("\n"));
        }
        text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");
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
                if(rowPtr >= lastIndex){
                    totalCntCorrect += curCntCorrect;
                    totalCntWrong += curCntWrong;
                    curCntWrong = 0;
                    curCntCorrect = 0;
                    toNextPage();
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
                    text.get(rowPtr).get(ptr).setStyle("-fx-text-fill: green;");                                        //why I didnt use OOP((((((
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
                        System.out.println("Complete!");
                        isLocked = true;
                        callAlert();
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

    private void scrollText() {
        procentTextScroll =  Float.parseFloat(String.format(Locale.US,"%.2f", getProcentTypedOfText()));
        scrollPane_output.setVvalue(procentTextScroll * 1.0d);
    }

    private float getProcentTypedOfText(){
        return (rowPtr / (float)text.size());
    }

    private void callAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("End of file");
        alert.setHeaderText("U just typed all text");
        alert.setContentText("U can reset or open new file");
        alert.showAndWait();
    }

    @FXML
    void reset(ActionEvent event) {
        tflow_output.getChildren().clear();
        curPage = 1;
        init();
        clearOutput();
        showOutput(firstIndex, lastIndex);
    }

    @FXML
    void openFile(ActionEvent event) {
        sumChars = 0;
        Stage stage = new Stage();
        file = fileChooser.showOpenDialog(stage);
        tflow_output.getChildren().clear();
        parseFile();
        init();
        showOutput(firstIndex, lastIndex);
    }

    @FXML
    void focusOnInput(MouseEvent event) {
        txtarea_input.requestFocus();
    }

    @FXML
    void nextPage(ActionEvent event) {
        clearOutput();
        toNextPage();
    }

    private void toNextPage(){
        if(curPage < sumPages) {
            lbl_pages.setText(String.format("Pages:\t %d/%d", ++curPage, sumPages));
            tflow_output.getChildren().clear();
            getPages();
            rowPtr = firstIndex;
            showOutput(firstIndex, lastIndex);
        }
    }

    @FXML
    void previousPage(ActionEvent event) {
        clearOutput();
        toPreviousPage();
    }

    private void toPreviousPage(){
        if(curPage > 1){
            lbl_pages.setText(String.format("Pages:\t %d/%d", --curPage, sumPages));
            tflow_output.getChildren().clear();
            getPages();
            rowPtr = firstIndex;
            showOutput(firstIndex, lastIndex);
        }
    }

    @FXML
    void clearPage(ActionEvent event) {
        clearOutput();
        setLabels();
    }

    private void clearOutput(){
        for(var row : text){
            for(var col : row){
                col.setStyle("-fx-background-color: none;");
            }
        }
        curCntWrong = 0;
        curCntCorrect = 0;
        ptr = 0;
        rowPtr = firstIndex;
        txtarea_input.setText("");
        txtarea_input.requestFocus();
        scrollPane_output.setVvalue(0.0d);
        text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");
    }

    private void getPages(){
        sumPages = (sumChars / maxCharsInOnePage == 0) ? 1 : sumChars / maxCharsInOnePage;
        showStep = text.size() / sumPages;
        firstIndex = curPage * showStep - showStep;
        lastIndex = showStep * curPage;
    }

}
