package App;

import java.io.File;

public class MyFile {
    private File file;
    private int sumChars;
    private int sumLines;

    MyFile(File file){
        this.file = file;
        sumChars = Parser.getCountChars(file);
        sumLines = Parser.getCountLines(file);
    }

    public int getSumChars() {
        return sumChars;
    }

    public int getSumLines() {
        return sumLines;
    }

    public File getFile() {
        return file;
    }
}
