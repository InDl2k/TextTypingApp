package App;

public class TimerController{
    int hh;
    int mm;
    int ss;
    boolean isTimerActive;

    TimerController(){
        hh = 0;
        mm = 0;
        ss = 0;
        isTimerActive = true;
    }

    public void incrementTime(){
        if(ss >= 60){
            ss%=ss;
            mm++;
        }
        if(mm >= 60){
            mm%=mm;
            hh++;
        }
        if(hh >= 24)
            hh = 0;

        ss++;
    }

    public void reset(){
        hh = 0;
        mm = 0;
        ss = 0;
    }

    public int getSeconds() {
        return ss;
    }

    public int getMinutes() {
        return mm;
    }

    public int getHours() {
        return hh;
    }

    public boolean getActive(){
        return isTimerActive;
    }

    public void setTimerActive(boolean timerActive) {
        isTimerActive = timerActive;
    }
}
