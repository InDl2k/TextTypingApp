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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.lang.Integer.max;

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

    //INIT
    int ptr;
    int rowPtr;
    KeyCode key;
    Vector<Vector<Label>> text = new Vector<>(); //Bad news my app is going down for big files((((
    double procentTextScroll;                    //i think java cant handle alot Labels need to change
    Scanner scan = null;                         //output from sizeable to pages <3
    File file = null;                            //Need stressTest to know how Labels he can handle in 1 stage
    FileChooser fileChooser = null;
    boolean isLocked;
    int countCorrect;
    int countWrong;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        parseLine("Hello");
        init();
        showOutput();
    }

    private void init(){
        txtarea_input.setEditable(true);
        txtarea_input.setText("");
        ptr = 0;
        rowPtr = 0;
        procentTextScroll = 0.0;
        fileChooser = new FileChooser();
        isLocked = false;
        countCorrect = 0;
        countWrong = 0;
        lbl_cntCorrect.setText("Correct\t = 0");
        lbl_cntWrong.setText("Wrong\t = 0");
        text.get(0).get(0).setStyle("-fx-background-color: orange;");
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
        Vector<Label> temp = new Vector<>();
        for(int j = 0; j < str.length(); ++j){
            Label lbl = new Label(String.valueOf(str.charAt(j)));
            lbl.setFont(Font.font("Cambria", 20));
            temp.add(lbl);
        }
        temp.add(new Label("\n"));
        text.add(temp);
    }

    private void showOutput(){
        for(var rows : text){
            tflow_output.getChildren().addAll(rows);
            tflow_output.getChildren().add(new Text("\n"));
        }
    }

    //key pressed
    @FXML
    void getKey(KeyEvent event) {
        if(!isLocked) {
            key = event.getCode();

            if (key == KeyCode.BACK_SPACE) {
                if (ptr >= text.get(rowPtr).size() - 1) {
                    txtarea_input.setEditable(true);
                    txtarea_input.setText(txtarea_input.getText().substring(0, text.get(rowPtr).size() - 2));
                    txtarea_input.positionCaret(ptr);
                }
                text.get(rowPtr).get(ptr).setStyle("-fx-background-color: none;");
                ptr = max(0, --ptr);
            }
            if (key == KeyCode.ENTER || key == KeyCode.TAB) {                                       //lock enter and tab because crashing input
                if(key == KeyCode.ENTER && !txtarea_input.isEditable()) {                           //Bug with ctrl combo need to lock ctrl
                    text.get(rowPtr).get(ptr).setStyle("-fx-background-color: none;");
                    ptr = 0;
                    txtarea_input.setText("");
                    rowPtr++;
                    txtarea_input.setEditable(true);
                }
                else {
                    txtarea_input.setText(txtarea_input.getText().substring(0, ptr));
                    txtarea_input.positionCaret(ptr);
                }
            }


            text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");
        }
    }

    //key typed
    @FXML
    void input(KeyEvent event) {
        if(!isLocked) {
            if (txtarea_input.isEditable() && (key.isLetterKey() || key.isDigitKey() || KeyCode.SPACE == key)) {
                if (ptr < text.get(rowPtr).size() - 1 && isCorrect()) {
                    text.get(rowPtr).get(ptr).setStyle("-fx-text-fill: green;");
                    countCorrect++;
                }
                else {
                    if (text.get(rowPtr).get(ptr).getText().equals(" "))
                        text.get(rowPtr).get(ptr).setStyle("-fx-background-color: red;");
                    else
                        text.get(rowPtr).get(ptr).setStyle("-fx-text-fill: red;");
                    countWrong++;
                }
                ptr = txtarea_input.getCaretPosition();
                lbl_cntCorrect.setText(String.format("Correct\t= %d", countCorrect)); //Need stuf to decrement when back_space
                lbl_cntWrong.setText(String.format("Wrong\t= %d", countWrong));
                System.out.printf("rowPtr = %d, ptr = %d\n", rowPtr, ptr);
                if (text.get(rowPtr).get(ptr).getText().equals("\n") || ptr > text.get(rowPtr).size() - 1) {
                    txtarea_input.setText(txtarea_input.getText().substring(0, ptr));
                    txtarea_input.setEditable(false);
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
        for(var row : text){
            for(var col : row){
                col.setStyle("-fx-background-color: none;");
            }
        }
        init();
        txtarea_input.requestFocus();
        scrollPane_output.setVvalue(0.0d);
    }

    private void clearOutPut(){
        tflow_output.getChildren().clear();
    }

    @FXML
    void openFile(ActionEvent event) {
        Stage stage = new Stage();
        file = fileChooser.showOpenDialog(stage);
        clearOutPut();
        parseFile();
        init();
        showOutput();
    }

    @FXML
    void focusOnInput(MouseEvent event) {
        txtarea_input.requestFocus();
    }

}
