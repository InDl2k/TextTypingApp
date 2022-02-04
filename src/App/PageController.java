package App;

import javafx.scene.control.Label;

import java.util.Vector;

public class PageController {

    final public static int maxCharsInOnePage = 1000;

    public static int curPage;
    public static int sumPages;
    public static int showStep;
    public static int sumChars;

    public static void getPages(Vector<Vector<Label>> text){
        sumChars = getSumChars(text);
        sumPages = (sumChars / maxCharsInOnePage == 0) ? 1 : sumChars / maxCharsInOnePage;
        showStep = /*text.size()*/ text.size() / sumPages;
    }

    public static int getFirstIndex(){
        return curPage * showStep - showStep;
    }

    public static int getLastIndex(){
        return showStep * curPage;
    }

    private static int getSumChars(Vector<Vector<Label>> text){
        int res = 0;
        for(int i = 0; i < text.size(); ++i){
            res += text.get(i).size();
        }
        return res;
    }


}
