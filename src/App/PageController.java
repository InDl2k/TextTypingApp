package App;

import java.io.File;

public class PageController {

    final public int maxCharsInOnePage = 1000; //config for optimization

    private int curPage;
    private int sumPages;
    private int showStep;
    private int sumChars;

    PageController(File file){
        curPage = 1;
        sumChars = Parser.getCountChars(file);
        sumPages = calcSumPages();
        showStep = calcStep(file);
    }

    PageController(){
        curPage = 1;
        sumChars = 0;
        sumPages = 1;
        showStep = 0;
    }

    public void plusCurPage(){
        curPage++;
    }

    public void minusCurPage(){
        curPage--;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
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

    private int calcStep(File file){
        return Parser.getCountLines(file) / sumPages;
    }


}
