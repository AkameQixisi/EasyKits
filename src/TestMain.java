import file.FU;

import java.io.File;

public class TestMain {
    public static void main(String[] args) {
        File dir = new File("E:\\Cache");
        int num = FU.OP.operate(dir,
                new FU.Filter() {
                    @Override
                    public boolean match(File file) {
                        String name = file.getName().toLowerCase();
                        return name.endsWith(".mp3") || name.endsWith(".wav");
                    }
                },
                new FU.Operator() {
                    @Override
                    public boolean operate(File file) {
                        System.out.println(file.getName());
                        return true;
                    }
                }
        );
        System.out.println(num);
    }
}
