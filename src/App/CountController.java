package App;

public class CountController {
    private int totalCntCorrect;
    private int totalCntWrong;
    private int curCntCorrect;
    private int curCntWrong;

    CountController(){
        totalCntCorrect = 0;
        totalCntWrong = 0;
        curCntCorrect = 0;
        curCntWrong = 0;
    }

    public int getTotalCntCorrect() {
        return totalCntCorrect + curCntCorrect;
    }

    public int getTotalCntWrong() {
        return totalCntWrong + curCntWrong;
    }

    public void plusCorrect(){
        curCntCorrect++;
    }

    public void minusCorrect(){
        curCntCorrect--;
    }

    public void plusWrong(){
        curCntWrong++;
    }

    public void minusWrong(){
        curCntWrong--;
    }

    public void sumTotal(){
        totalCntCorrect += curCntCorrect;
        totalCntWrong += curCntWrong;
        curCntCorrect = 0;
        curCntWrong = 0;
    }

    public void resetAll(){
        totalCntCorrect = 0;
        totalCntWrong = 0;
        resetCur();
    }

    public void resetCur(){
        curCntCorrect = 0;
        curCntWrong = 0;
    }
}
