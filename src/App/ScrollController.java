package App;

import javafx.scene.control.ScrollPane;

import java.util.Locale;

public class ScrollController extends ScrollPane {

    private ScrollPane scrollPane;

    ScrollController(ScrollPane scrollPane){
        this.scrollPane = scrollPane;
    }

    public void scrollText(int x, int length) {
        float procentTextScroll =  Float.parseFloat(String.format(Locale.US,"%.2f", x / ((float)length)));
        scrollPane.setVvalue(procentTextScroll * 1.0d);
    }

}
