package App;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class Parser {

    public static void parseFile(File file, Vector<Vector<Label>> text){
        try(FileInputStream fin = new FileInputStream(file)) {
            Scanner scan = new Scanner(fin);
            while(scan.hasNextLine()){
                String str = scan.nextLine();
                parseLine(str, text);
            }
            text.get(text.size()-1).add(new Label(" "));
        }
        catch (IOException exc){
            System.out.printf("Something goes wrong with %s.txt\n", file.getName());
        }
    }

    public static void parseLine(String str, Vector<Vector<Label>> text){
        str = str.strip();
        //sumChars += str.length();
        Vector<Label> temp = new Vector<>();
        for(int j = 0; j < str.length(); ++j){
            Label lbl = new Label(String.valueOf(str.charAt(j)));
            lbl.setFont(Font.font("Cambria", 20));
            temp.add(lbl);
        }
        Label lbl = new Label(" ");
        lbl.setStyle("-fx-text-fill: white;");
        temp.add(lbl);
        text.add(temp);
    }
}
