package App;

import javafx.scene.control.ScrollPane;

import java.util.Locale;

public class ScrollController extends ScrollPane {

    private ScrollPane scrollPane;
    private PageController pageController;

    ScrollController(ScrollPane scrollPane, PageController pageController){
        this.scrollPane = scrollPane;
        this.pageController = pageController;
    }

    public void scrollText(int rowPtr) {
        float procentTextScroll =  Float.parseFloat(String.format(Locale.US,"%.2f", getProcentTypedOfText(rowPtr)));
        scrollPane.setVvalue(procentTextScroll * 1.0d);
    }
    private float getProcentTypedOfText(int rowPtr){
        return ((rowPtr - pageController.getFirstIndex()) / ((float)pageController.getLastIndex() - (float)pageController.getFirstIndex()));
    }
}
