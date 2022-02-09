package App;

public class PageController {

    final public int maxCharsInOnePage = 1000; //config for optimization

    private int curPage;
    private int sumPages;
    private int showStep;
    private int sumChars;

    PageController(MyFile file){
        curPage = 1;
        sumChars = file.getSumChars();
        showStep = calcStep(file);
        sumPages = calcSumPages(file);
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

    private int calcSumPages(MyFile file){
        return file.getSumLines() / showStep;
    }

    private int calcStep(MyFile file){
        return file.getSumLines() / ((sumChars / maxCharsInOnePage == 0) ? 1 : (sumChars / maxCharsInOnePage));
    }


}
