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
    private ScrollPane scrollPane_output;

    @FXML
    private TextFlow tflow_output;

    @FXML
    private TextArea txtarea_input;

    //INIT
    int ptr;
    int rowPtr;
    KeyCode key;
    Vector<Vector<Label>> text = null;
    double procentTextScroll;
    FileInputStream fin = null;
    Scanner scan = null;
    File file = new File("C:\\Users\\Comp\\IdeaProjects\\TextTypingApp\\resources\\books\\test.txt");
    FileChooser fileChooser = null;
    boolean isLocked;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }

    private void init(){
        try {
            fin = new FileInputStream(file);
        }
        catch (IOException exc){
            System.out.println("Missing test.txt in folder books");
        }
        txtarea_input.setEditable(true);
        txtarea_input.setText("");
        tflow_output.getChildren().clear();
        text = new Vector<Vector<Label>>();
        ptr = 0;
        rowPtr = 0;
        procentTextScroll = 0.0;
        fileChooser = new FileChooser();
        isLocked = false;

        scan = new Scanner(fin);
        int i = 0;
        while(scan.hasNextLine()){
            String str = scan.nextLine();
            Vector<Label> temp = new Vector<>();
            for(int j = 0; j < str.length(); ++j){
                Label lbl = new Label(String.valueOf(str.charAt(j)));
                lbl.setFont(Font.font("Cambria", 20));
                temp.add(lbl);
            }
            temp.add(new Label("\n"));
            text.add(temp);
        }
        text.get(text.size()-1).add(new Label(" "));

        for(var rows : text){
            tflow_output.getChildren().addAll(rows);
            tflow_output.getChildren().add(new Text("\n"));
        }

        text.get(0).get(0).setStyle("-fx-background-color: orange;");
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
            if (key == KeyCode.ENTER && !txtarea_input.isEditable()) {
                text.get(rowPtr).get(ptr).setStyle("-fx-background-color: none;");
                ptr = 0;
                txtarea_input.setText("");
                rowPtr++;
                txtarea_input.setEditable(true);
            }


            text.get(rowPtr).get(ptr).setStyle("-fx-background-color: orange;");
        }
    }

    //key typed
    @FXML
    void input(KeyEvent event) {
        if(!isLocked) {
            if (txtarea_input.isEditable() && (key.isLetterKey() || key.isDigitKey() || key.isArrowKey() || KeyCode.SPACE == key)) {
                if (ptr < text.get(rowPtr).size() - 1 && isCorrect()) {
                    text.get(rowPtr).get(ptr).setStyle("-fx-text-fill: green;");
                } else {
                    if (text.get(rowPtr).get(ptr).getText().equals(" "))
                        text.get(rowPtr).get(ptr).setStyle("-fx-background-color: red;");
                    else
                        text.get(rowPtr).get(ptr).setStyle("-fx-text-fill: red;");
                }
                ptr = txtarea_input.getCaretPosition();
                System.out.printf("rowPtr = %d, ptr = %d\n", rowPtr, ptr);
                if (text.get(rowPtr).get(ptr).getText().equals("\n") || ptr > text.get(rowPtr).size() - 1) { // <----- PROBLEMS
                    txtarea_input.setText(txtarea_input.getText().substring(0, ptr));
                    txtarea_input.setEditable(false);
                    if (rowPtr == text.size() - 1) {
                        System.out.println("Complete!"); //need func to next list or if it will be sizeable need open window with info about end of text.
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
        System.out.println("ALERT");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("End of file");
        alert.setHeaderText("U just typed all text");
        alert.setContentText("U can reset or open new file");
        alert.showAndWait();
    }

    @FXML
    void reset(ActionEvent event) {
        init();
    }

    @FXML
    void openFile(ActionEvent event) {
        Stage stage = new Stage();
        file = fileChooser.showOpenDialog(stage);
        init();
    }

}
