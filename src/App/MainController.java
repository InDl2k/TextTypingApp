package App;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import static java.lang.Integer.max;

public class MainController implements Initializable {

    @FXML
    private TextFlow tflow_output;

    @FXML
    private TextArea txtarea_input;

    //INIT
    int ptr = 0;
    KeyCode key;
    String test = "type f type f type f type f type f type f type f type f type f type f type f type f type f type f";
    String in = "";
    Vector<Label> text = new Vector<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(int i = 0; i < test.length(); ++i){
            Label temp = new Label(String.valueOf(test.charAt(i)));
            temp.setFont(Font.font("Cambria", 20));
            text.add(temp);
        }
        text.add(new Label(" ")); // C++ can do this vector<Text> text(test.size()+1);

        tflow_output.getChildren().addAll(text);
        text.get(0).setStyle("-fx-background-color: orange;");

    }

    //key pressed
    @FXML
    void getKey(KeyEvent event) {
        key = event.getCode();

        if(key == KeyCode.BACK_SPACE && txtarea_input.isEditable()) { // control lower out of bounds <--- BUG fixed? new Bug with pressed bspace <--- BUG fixed?
            text.get(ptr).setStyle("-fx-background-color: none;");
            ptr = max(0, --ptr);
        }
        text.get(ptr).setStyle("-fx-background-color: orange;");
    }

    //key released
    @FXML
    void input(KeyEvent event) {
        in = txtarea_input.getText();
        if(txtarea_input.isEditable() && key.isLetterKey() || KeyCode.SPACE == key) {
            if(ptr <= test.length() - 1 && isCorrect()) {
                //System.out.printf("Correct, ptr = %d, key = %c, txt char = %c\n", ptr, in.charAt(ptr), txtarea_output.getText().charAt(ptr)); //color char to GREEN
                text.get(ptr).setStyle("-fx-text-fill: green;");
            }
            else {
                //System.out.printf("Err, ptr = %d, key = %c, txt char = %c\n", ptr, in.charAt(ptr), txtarea_output.getText().charAt(ptr)); //color char to RED
                if(text.get(ptr).getText().equals(" "))
                    text.get(ptr).setStyle("-fx-background-color: red;");
                else
                    text.get(ptr).setStyle("-fx-text-fill: red;");
            }
            ptr++;
            if(ptr > test.length() - 1) { // control upper out of bounds <--- BUG fixed? in some chance can throw exception dont know why
                txtarea_input.setText(txtarea_input.getText().substring(0, ptr));
                System.out.println("Complete!"); //need func to next list or if it will be sizeable need open window with info about end of text.
                txtarea_input.setEditable(false);
                ptr = test.length();
            }
        }

        text.get(ptr).setStyle("-fx-background-color: orange;");
    }

    private boolean isCorrect(){
        return in.charAt(ptr) == text.get(ptr).getText().charAt(0);
    }


}
