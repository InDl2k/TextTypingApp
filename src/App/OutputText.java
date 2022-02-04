package App;

import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


import java.util.Vector;

public class OutputText extends TextFlow {
    TextFlow tflow_output;

    OutputText(TextFlow textFlow){
        this.tflow_output = textFlow;
        System.out.println(tflow_output);
    }

    public void show(int l, int r, Vector<Vector<Label>> text){
        for(int i = l; i < r; ++i){
            tflow_output.getChildren().addAll(text.get(i));
            tflow_output.getChildren().add(new Text("\n"));
        }
    }

    public void clear(Vector<Vector<Label>> text){
        for(int i = PageController.getFirstIndex(); i < PageController.getLastIndex(); ++i){
            for(var col : text.get(i)){
                col.setStyle("-fx-background-color: none;");
            }
        }
    }
}
