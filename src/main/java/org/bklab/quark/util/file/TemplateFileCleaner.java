package building.util.file;

import java.io.File;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class TemplateFileCleaner implements Runnable {

    private String prefix;
    private String suffix;
    private String regex;
    private Long beforeLastModifiedEpochSecond;
    private File root;
    private Consumer<Exception> exceptionConsumer = Throwable::printStackTrace;

    public static void main(String[] args) {
        // 删除 data-325423.png
        String regex = "\\bdata-\\b[0-9]+(.JPEG|.jpeg|.JPG|.jpg|.png|.PNG)$";
        new TemplateFileCleaner().setRegex(regex).start();
        new TemplateFileCleaner().setPrefix("data-").start();
        new TemplateFileCleaner().setSuffix(".png").start();
        new TemplateFileCleaner().setPrefix("data-").setSuffix(".png").start();
    }

    public void start() {
        try {
            if (root == null) root = new File(System.getProperty("java.io.tmpdir"));
            findFile(root);
        } catch (Exception e) {
            exceptionConsumer.accept(e);
        }
    }

    private void findFile(File root) {
        if (root == null) return;
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files == null) return;
            for (File file : files) {
                findFile(file);
            }
            if (files.length == 0) delete(root);
        } else {
            delete(root);
        }
    }

    private void delete(File file) {
        String name = file.getName();
        if (prefix != null && !name.startsWith(prefix)) return;
        if (suffix != null && !name.endsWith(suffix)) return;
        if (regex != null && !Pattern.matches(regex, name)) return;
        if (beforeLastModifiedEpochSecond != null && beforeLastModifiedEpochSecond <= file.lastModified()) return;

        try {
            if (!file.delete()) file.deleteOnExit();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
            System.out.println(this.getClass().getName() + "[删除临时文件失败]：" + file.getAbsolutePath());
        }
    }


    public TemplateFileCleaner setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public TemplateFileCleaner setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public TemplateFileCleaner setRegex(String regex) {
        this.regex = regex;
        return this;
    }

    public TemplateFileCleaner setBeforeLastModifiedEpochSecond(Long beforeLastModifiedEpochSecond) {
        this.beforeLastModifiedEpochSecond = beforeLastModifiedEpochSecond;
        return this;
    }

    public TemplateFileCleaner setExceptionConsumer(Consumer<Exception> exceptionConsumer) {
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    public TemplateFileCleaner setRoot(File root) {
        this.root = root;
        return this;
    }

    @Override
    public void run() {
        start();
    }
}
