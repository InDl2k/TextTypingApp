package App;

import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


import java.util.Vector;

public class OutputText extends TextFlow {
    private TextFlow tflow_output;
    private PageController pageController;

    OutputText(TextFlow textFlow, PageController pageController){
        this.tflow_output = textFlow;
        this.pageController = pageController;
    }

    public void show(int l, int r, Vector<Vector<Label>> text){
        for(int i = l; i < r; ++i){
            tflow_output.getChildren().addAll(text.get(i));
            tflow_output.getChildren().add(new Text("\n"));
        }
    }

    public void clear(Vector<Vector<Label>> text){
        for(int i = pageController.getFirstIndex(); i < pageController.getLastIndex(); ++i){
            for(var col : text.get(i)){
                col.setStyle("-fx-background-color: none;");
            }
        }
    }
}
