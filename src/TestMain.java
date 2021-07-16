import file.FU;
import text.TextHandler;

import java.io.File;

public class TestMain {
    public static void main(String[] args) {
        String str = FU.IO.read(new File("in.txt"));
        System.out.println(str);
        System.out.println("================");
        str = TextHandler.removeSpaces(str);
        System.out.println(str);
        System.out.println("================");
        str = TextHandler.removeLineSeparator(str);
        System.out.println(str);
    }
}
