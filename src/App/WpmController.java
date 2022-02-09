package App;

import javafx.scene.control.Label;

import static java.lang.Integer.max;

public class WpmController {

    private Label lbl_wpm;
    private CountController countController;
    private TimerController timerController;

    WpmController(CountController countController, TimerController timerController, Label lbl_wpm){
        this.countController = countController;
        this.timerController = timerController;
        this.lbl_wpm = lbl_wpm;
    }

    public int getWpm() {
        double minutes = timerController.getHours() * 60 + timerController.getMinutes() + timerController.getSeconds() / 60.0;
        return max(0, (int)((((countController.getTotalCntCorrect() + countController.getTotalCntWrong()) / 5) - countController.getTotalCntWrong()) / minutes));
    }

    public double getAccuracy(){
        return (countController.getTotalCntCorrect() / (double) max(1, countController.getTotalCntWrong() + countController.getTotalCntCorrect()));
    }

    public void setColor(int result){
        if(result >= 0 && result <= 30){
            lbl_wpm.setStyle("-fx-text-fill: gray;");
        }
        else if (result > 30 && result <= 60){
            lbl_wpm.setStyle("-fx-text-fill: green;");
        }
        else if (result > 60 && result <= 90){
            lbl_wpm.setStyle("-fx-text-fill: #23c6b6;");
        }
        else if (result > 90 && result <= 120)
            lbl_wpm.setStyle("-fx-text-fill: purple;");
        else
            lbl_wpm.setStyle("-fx-text-fill: red;");
    }

}
