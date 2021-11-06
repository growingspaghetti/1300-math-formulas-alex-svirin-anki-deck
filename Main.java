import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Main {

    static void writeText(File f, String text) {
        try (FileWriter w = new FileWriter(f, true);
        ) {
            w.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean parentContainsDir(File f) {
        File[] files = f.getParentFile().listFiles();
        return Arrays.asList(files).stream().anyMatch(File::isDirectory);
    }

    static String chapter(File f) {
        File p = f.getParentFile();
        while (true) {
            if (p.getParentFile().getName().equals("slices-1300")) {
                return p.getName();
            }
            p = p.getParentFile();
        }
    }

    public static void mainc(String[] args) throws IOException {
        String dir = "/media/archive/collection.media/slices-1300/";
        File rootDir = new File(dir);
        File[] files = rootDir.listFiles();
        Stack<File> stack = new Stack<>();
        stack.addAll(Arrays.asList(files));
        List<File> imgs = new ArrayList<>();
        while (!stack.isEmpty()) {
            File f = stack.pop();
            if (f.isDirectory()) {
                stack.addAll(Arrays.asList(f.listFiles()));
            } else {
                imgs.add(f);
            }
        }
        for (File img : imgs) {
            System.out.println(img.getAbsolutePath());
            Path copied = Paths.get(img.getAbsolutePath().substring("/media/archive/collection.media/".length()).replace("/", "-"));
            Path originalPath = img.toPath();
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void mainb(String[] args) throws IOException {
        String dir = "/media/dev/1300-math-formulas-alex-svirin-anki-dec/slices-1300";
        File rootDir = new File(dir);
        File[] files = rootDir.listFiles();
        Arrays.sort(files);
        Stack<File> stack = new Stack<>();
        stack.addAll(Arrays.asList(files));
        List<File> concats = new ArrayList<>();
        while (!stack.isEmpty()) {
            File f = stack.pop();
            if (f.isDirectory() && !f.getName().startsWith("pg_")) {
                File[] fs = f.listFiles();
                stack.addAll(Arrays.asList(fs));
            }
            if (f.isDirectory() && f.getName().startsWith("pg_")) {
                concats.add(f);
            }
        }
        for (File f : concats) {
            File[] images = f.listFiles();
            Arrays.sort(images);
            StringBuilder sb = new StringBuilder();
            for (File a : images) {
                sb.append(a.getName());
                sb.append(" ");
            }
            System.out.println("(cd \"" + f.getAbsolutePath() + "\" && convert " + sb.toString() + " -append ../" + f.getName() + ".jpg && rm -rf \"" + f.getAbsolutePath() + "\")");
        }
    }

    public static void main(String[] args) throws IOException {
        String dir = "/media/dev/1300-math-formulas-alex-svirin-anki-dec/slices-1300";
        File rootDir = new File(dir);
        File[] files = rootDir.listFiles();
        Arrays.sort(files);
        Stack<File> stack = new Stack<>();
        stack.addAll(Arrays.asList(files));
        List<File> formula = new ArrayList<>();
        while (!stack.isEmpty()) {
            File f = stack.pop();
            if (f.isDirectory()) {
                File[] fs = f.listFiles();
                Arrays.sort(fs);
                stack.addAll(Arrays.asList(fs));
            }
            if (!f.isDirectory() && !parentContainsDir(f)) {
                formula.add(f);
            }
        }
        Collections.reverse(formula);
        for (File f : formula) {
            List<File> page = new ArrayList<>();
            page.add(f);
            File p = f.getParentFile().getParentFile();
            while (!p.equals(rootDir)) {
                List<File> pfs = Arrays.asList(p.listFiles());
                Collections.sort(pfs);
                Collections.reverse(pfs);
                for (File pf : pfs) {
                    if (!pf.isDirectory()) {
                        page.add(pf);
                    }
                }
                p = p.getParentFile();
            }
            String pa = page.stream().map(a -> "<img src=\"" + a.getAbsolutePath().substring("/media/dev/1300-math-formulas-alex-svirin-anki-dec/".length()) + "\">").collect(Collectors.joining("<br>"));
            System.out.println("<hr>" + pa);
            writeText(new File(chapter(f) + ".html"), "<hr>" + pa + "\n");
        }
    }
}
