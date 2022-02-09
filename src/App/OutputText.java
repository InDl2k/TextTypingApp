package App;

import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


import java.util.Vector;

public class OutputText extends TextFlow {
    private TextFlow tflow_output;

    OutputText(TextFlow textFlow){
        this.tflow_output = textFlow;
    }

    public void show(Vector<Vector<Label>> text){
        for(var row : text){
            tflow_output.getChildren().addAll(row);
            tflow_output.getChildren().add(new Text("\n"));
        }
    }

    public void clear(Vector<Vector<Label>> text){
        for(var row : text){
            for(var col : row){
                col.setStyle("-fx-background-color: none;");
            }
        }
    }
}
