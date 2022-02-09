package App;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;

public class Parser {

    public static void parseFile(File file, int l, int r, Vector<Vector<Label>> text){
        text.clear();
        try(BufferedReader fin = new BufferedReader(new FileReader(file))) {
            Scanner scan = new Scanner(fin);
            for(int i = 0; i < l; ++i)
                scan.nextLine();
            for(int i = l; i < r; ++i) {
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

    public static int getCountChars(File file){
        int sumChars = 0;
        try(BufferedReader fin = new BufferedReader(new FileReader(file))) {
            Scanner scan = new Scanner(fin);
            while(scan.hasNextLine()){
                sumChars += scan.nextLine().length();
            }
        }
        catch (IOException exc){
            System.out.printf("Something goes wrong with %s.txt\n", file.getName());
        }
        return sumChars;
    }

    public static int getCountLines(File file){
        int len = 0;
        try(BufferedReader fin = new BufferedReader(new FileReader(file))) {
            Scanner scan = new Scanner(fin);
            while(scan.hasNextLine()){
                scan.nextLine();
                len++;
            }
        }
        catch (IOException exc){
            System.out.printf("Something goes wrong with %s.txt\n", file.getName());
        }
        return len;
    }

}
