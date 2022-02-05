package App;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import java.util.Vector;

import static java.lang.Integer.max;

public class InputText extends TextArea {
    private TextArea txtarea_input;
    private Pointer pointer;
    private CountController countController;
    private Vector<Vector<Label>> text;

    InputText(TextArea txtarea_input, Pointer pointer, CountController countController, Vector<Vector<Label>> text){
        this.txtarea_input = txtarea_input;
        this.pointer = pointer;
        this.countController = countController;
        this.text = text;
    }

    public void keyPressed(KeyCode key) {
        if (key == KeyCode.BACK_SPACE) {
            if (pointer.getY() >= text.get(pointer.getX()).size() - 1) {
                txtarea_input.setEditable(true);
                txtarea_input.setText(txtarea_input.getText().substring(0, text.get(pointer.getX()).size() - 2));
                txtarea_input.positionCaret(pointer.getY());
            }
            String charColor = text.get(pointer.getX()).get(max(0,pointer.getY() - 1)).getStyle();
            if(charColor.contains("red")) {
                countController.minusWrong();
            }
            else if(charColor.contains("green")) {
                countController.minusCorrect();
            }
            text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: none;");
            pointer.setY(max(0, pointer.getY() - 1));
        }
        if(key == KeyCode.ENTER && (!txtarea_input.isEditable())) {                           //Bug with ctrl combo need to lock ctrl   i dont know how)
            text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: none;");
            pointer.setY(0);
            txtarea_input.setText("");
            pointer.setX(pointer.getX()+1);
            if(!text.get(pointer.getX()).get(pointer.getY()).getText().equals(" ")) {
                txtarea_input.setEditable(true);
            }
            else{
                txtarea_input.setEditable(false);
            }
        }
        else {
            txtarea_input.setText(txtarea_input.getText().substring(0, pointer.getY()));
            txtarea_input.positionCaret(pointer.getY());
        }
    }

    public void keyTyped(KeyCode key) {
        if (txtarea_input.isEditable() && key != KeyCode.BACK_SPACE && key != KeyCode.DELETE) {
            if (pointer.getY() < text.get(pointer.getX()).size() - 1 && key != KeyCode.ENTER && isCorrect()) {                         //SOMEBODY TOUCHA MY SPAGHET
                text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-text-fill: green;");
                countController.plusCorrect();
            }
            else if(key != KeyCode.ENTER) {
                if (text.get(pointer.getX()).get(pointer.getY()).getText().equals(" "))
                    text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-background-color: red;");
                else
                    text.get(pointer.getX()).get(pointer.getY()).setStyle("-fx-text-fill: red;");
                countController.plusWrong();
            }
            pointer.setY(txtarea_input.getCaretPosition());
            //System.out.printf("pointer.getX() = %d, pointer.getY() = %d\n", pointer.getX(), pointer.getY());                                            //DEBUG
            if (text.get(pointer.getX()).get(pointer.getY()).getText().equals(" ") && pointer.getY() >= text.get(pointer.getX()).size() - 1) {
                txtarea_input.setText(txtarea_input.getText().substring(0, pointer.getY()));
                txtarea_input.setEditable(false);
            }
        }
    }

    private boolean isCorrect(){
        return txtarea_input.getText().charAt(pointer.getY()) == text.get(pointer.getX()).get(pointer.getY()).getText().charAt(0);
    }

}
