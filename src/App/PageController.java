package App;

import javafx.scene.control.Label;

import java.util.Vector;

public class PageController {

    final public int maxCharsInOnePage = 1000; //config for optimization

    private int curPage;
    private int sumPages;
    private int showStep;
    private int sumChars;

    PageController(Vector<Vector<Label>> text){
        curPage = 1;
        sumChars = getSumChars(text);
        sumPages = calcSumPages();
        showStep = calcStep(text);
    }

    public void plusCurPage(){
        curPage++;
    }

    public void minusCurPage(){
        curPage--;
    }

    public int getCurPage() {
        return curPage;
    }

    public int getSumPages() {
        return sumPages;
    }

    public int getFirstIndex(){
        return curPage * showStep - showStep;
    }

    public int getLastIndex(){
        return showStep * curPage;
    }

    private int calcSumPages(){
        return (sumChars / maxCharsInOnePage == 0) ? 1 : sumChars / maxCharsInOnePage;
    }

    private int calcStep(Vector<Vector<Label>> text){
        return text.size() / sumPages;
    }

    private int getSumChars(Vector<Vector<Label>> text){
        int res = 0;
        for(int i = 0; i < text.size(); ++i){
            res += text.get(i).size();
        }
        return res;
    }


}
