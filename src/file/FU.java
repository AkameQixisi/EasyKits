package file;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * File Utils, including file operation and file io.
 */
public class FU {

    private static boolean notDirectory(@NotNull File dir) {
        return !dir.exists() || !dir.isDirectory();
    }

    private static boolean canWrite(@NotNull File file) {
        return file.exists() && !file.isDirectory() && file.isFile() && file.canWrite();
    }

    private static boolean canRead(@NotNull File file) {
        return file.exists() && !file.isDirectory() && file.isFile() && file.canRead();
    }

    /**
     * File Operation.
     */
    public static class OP {

        @NotNull
        public static String[] getSubItems(@NotNull File dir) {
            if (notDirectory(dir)) return new String[]{};
            return dir.list();
        }

        @NotNull
        public static File[] getSubFiles(@NotNull File dir) {
            if (notDirectory(dir)) return new File[]{};
            return dir.listFiles();
        }

        public static void printSubItems(@NotNull File dir) {
            String[] subItems = getSubItems(dir);
            for (String s : subItems) {
                System.out.println(s);
            }
        }

        /**
         * 删除文件夹及其所有子文件
         *
         * @return 所删除的文件和文件夹的数目
         */
        public static int deleteFolder(@NotNull File dir) {
            return operate(dir,
                    new Filter() {
                        @Override
                        public boolean match(File file) {
                            return true;
                        }
                    },
                    new Operator() {
                        @Override
                        public boolean operate(File file) {
                            return file.delete();
                        }

                        @Override
                        public boolean IsOperateDir() {
                            return true;
                        }

                        @Override
                        public boolean operateDir(File dir) {
                            return dir.delete();
                        }
                    });
        }

        /**
         * 删除文件夹及其所有子文件夹中，满足条件的文件
         *
         * @return 所删除的文件的数目
         */
        public static int deleteFileAt(File dir, @NotNull Filter filter) {
            return operate(dir, filter, new Operator() {
                @Override
                public boolean operate(File file) {
                    return file.delete();
                }
            });
        }

        /**
         * 使用自实现的文件操作类对匹配的文件进行操作，返回影响的文件个数
         */
        public static int operate(File dir, @NotNull Filter filter, @NotNull Operator operator) {
            File[] subs = getSubFiles(dir);
            int num = 0;
            for (File sub : subs) {
                if (sub.isDirectory()) {
                    num += operate(sub, filter, operator);
                } else if (filter.match(sub)) {
                    num += operator.operate(sub) ? 1 : 0;
                }
            }
            if (operator.IsOperateDir()) {
                num += operator.operateDir(dir) ? 1 : 0;
            }
            return num;
        }

    }

    /**
     * File IO.
     */
    public static class IO {

        /**
         * 读取文件中的所有内容，除换行符外
         */
        @NotNull
        public static String readNoLineSeparator(@NotNull File file) {
            return read(file).replaceAll(System.lineSeparator(), "");
        }

        /**
         * 读取文件中的所有内容，保留所有信息，包括各种符号
         */
        @NotNull
        public static String read(@NotNull File file) {
            return read(file, Charset.defaultCharset(), 8192);  // 8192 Byte = 8 KB
        }

        /**
         * 读取文件中的所有内容，保留所有信息，包括各种符号
         */
        @NotNull
        public static String read(@NotNull File file, Charset charset) {
            return read(file, charset, 8192);  // 8192 Byte = 8 KB
        }

        /**
         * 读取文件中的所有内容，保留所有信息，包括各种符号
         */
        @NotNull
        public static String read(@NotNull File file, Charset charset, int onceByte) {
            if (!canRead(file)) return "";
            byte[] buf = new byte[onceByte];
            StringBuilder sb = new StringBuilder();
            try {
                InputStream is = new FileInputStream(file);
                while (is.read(buf) != -1) {
                    sb.append(new String(buf, charset));
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        /**
         * 按行读取文件中的所有行
         */
        @NotNull
        public static String[] readLines(@NotNull File file) {
            return readLines2(file, Charset.defaultCharset(), System.lineSeparator());
        }

        /**
         * 按行读取文件中的所有行，直接按行读取
         */
        @NotNull
        public static String[] readLines1(@NotNull File file) {
            return readLines1(file, Charset.defaultCharset());
        }

        /**
         * 按行读取文件中的所有行，直接按行读取
         */
        @NotNull
        public static String[] readLines1(@NotNull File file, Charset charset) {
            if (!canRead(file)) return new String[]{};
            ArrayList<String> ls = new ArrayList<>();
            String str;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
                while ((str = br.readLine()) != null) {
                    ls.add(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ls.toArray(new String[0]);
        }

        /**
         * 按行读取文件中的所有行，全读完用换行符分割
         */
        @NotNull
        public static String[] readLines2(@NotNull File file) {
            return readLines2(file, Charset.defaultCharset(), System.lineSeparator());
        }

        /**
         * 按行读取文件中的所有行，全读完用换行符分割
         */
        @NotNull
        public static String[] readLines2(@NotNull File file, Charset charset) {
            return readLines2(file, charset, System.lineSeparator());
        }

        /**
         * 按行读取文件中的所有行，全读完用换行符分割
         */
        @NotNull
        public static String[] readLines2(@NotNull File file, Charset charset, String lineSeparator) {
            return read(file, charset).split(lineSeparator);
        }

        /**
         * 将字符串内容全部写入到文件中
         *
         * @return 是否有成功向文件中写入内容
         */
        public static boolean write(@NotNull File file, String string) {
            return write(file, string, Charset.defaultCharset());
        }

        /**
         * 将字符串内容全部写入到文件中
         *
         * @return 是否有成功向文件中写入内容
         */
        public static boolean write(@NotNull File file, String string, Charset charset) {
            if (string == null || string.length() == 0) return false;
            if (!canWrite(file)) return false;
            try {
                OutputStream os = new FileOutputStream(file);
                os.write(string.getBytes(charset));
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    /**
     * 自定义文件筛选接口，用于匹配文件，匹配成功返回true
     */
    public interface Filter {
        default boolean match(File file) {
            return false;
        }
    }

    /**
     * 自定义文件操作接口，操作成功返回true
     */
    public interface Operator {
        default boolean operate(File file) {
            return false;
        }

        default boolean operate(File f1, File f2) {
            return false;
        }

        default boolean IsOperateDir() {
            return false;
        }

        default boolean operateDir(File dir) {
            return false;
        }
    }
}
