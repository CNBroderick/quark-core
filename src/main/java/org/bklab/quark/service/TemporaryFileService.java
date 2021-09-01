package org.bklab.quark.service;

import org.bklab.quark.util.file.TemplateFileCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TemporaryFileService {

    public static String ROOT_DIRECTORY_NAME = "bklab";

    private final static String separator = File.separator;
    private static final Logger logger = LoggerFactory.getLogger(TemporaryFileService.class);
    private static TemporaryFileService instance;
    private final File root;
    private LocalDateTime cleanTime = LocalDateTime.MIN;
    private ScheduledFuture<TemporaryFileService> schedule;

    public TemporaryFileService() {
        this(ROOT_DIRECTORY_NAME);
    }

    public TemporaryFileService(String rootDirectoryName) {
        File file = new File(System.getProperty("java.io.tmpdir") + separator + rootDirectoryName);
        if (!file.exists() && !file.mkdirs()) throw new RuntimeException("创建临时文件夹失败");
        this.root = file;
    }

    public static TemporaryFileService getInstance() {
        if (instance == null) {
            instance = new TemporaryFileService().createSchedule();
        }
        if (instance.schedule.isCancelled() || instance.schedule.isDone()) instance.createSchedule();
        return instance.cleanTime.isBefore(LocalDateTime.now().minusDays(7)) ? instance.clean() : instance;
    }

    private TemporaryFileService createSchedule() {
        schedule = Executors.newScheduledThreadPool(1).schedule(
                () -> clean().createSchedule(),
                ChronoUnit.MILLIS.between(LocalDateTime.now(), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(4, 0, 0))),
                TimeUnit.MILLISECONDS
        );
        return this;
    }

    private TemporaryFileService clean() {
        try {
            new Thread(new TemplateFileCleaner().setRoot(root).setBeforeLastModifiedEpochSecond(
                    System.currentTimeMillis() - 0x5265c00)).start();
            cleanTime = LocalDateTime.now();
        } catch (Exception e) {
            logger.error("清理失败", e);
        }
        return this;
    }

    public File getRootDirectory() {
        File file = new File( root.getAbsolutePath() + separator + "Broderick Labs");
        if (!file.exists() && !file.mkdirs()) throw new RuntimeException("创建用户临时文件夹失败：" + file.getAbsolutePath());
        return file;
    }

    public File createTemplateFileForUpload(String parentName, String fileName) {
        return createFileForMulti(getRootDirectory(), fileName, "上传", parentName);
    }

    public File createTemplateDirectory(String directoryName) {
        File parentRoot = new File(getRootDirectory().getAbsolutePath() + separator + directoryName + separator + System.currentTimeMillis());
        if (!parentRoot.exists() && !parentRoot.mkdirs())
            throw new RuntimeException("创建用户二级临时文件夹失败：" + parentRoot.getAbsolutePath());
        return parentRoot;
    }

    public File createDirectoryInParent(File parent, String directoryName) {
        File dir = new File(parent.getAbsolutePath() + separator + directoryName + separator + System.currentTimeMillis());
        if (!dir.exists() && !dir.mkdirs())
            throw new RuntimeException("创建用户多级临时文件夹失败：" + dir.getAbsolutePath());
        return dir;
    }

    public File createTemplateFile(String parentName, String fileName) {
        File file = new File(createTemplateDirectory(parentName).getAbsolutePath() + separator + fileName);
        if (file.exists() && !file.delete()) throw new RuntimeException("临时文件被占用，无法删除。：" + file.getAbsolutePath());
        try {
            if (file.createNewFile()) {
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("创建临时文件出错：" + file.getAbsolutePath());
        }
        throw new RuntimeException("无法创建临时文件。：" + file.getAbsolutePath());
    }

    public File createFileForMulti(File parentDirectory, String fileName, String... directoryNames) {
        if (parentDirectory.isFile()) throw new RuntimeException("parentDirectory is a file");
        File f = new File(parentDirectory.getAbsolutePath() + separator + String.join(separator, directoryNames));
        if (!f.exists() && !f.mkdirs()) throw new RuntimeException("创建临时文件夹失败：" + f.getAbsolutePath());
        f = new File(f.getAbsolutePath() + separator + fileName);
        try {
            if (!f.createNewFile()) throw new RuntimeException("创建临时文件失败：" + f.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("创建临时文件[" + f.getAbsolutePath() + "]出错: " + e.getLocalizedMessage());
        }
        return f;
    }

    public File createDirectoryForBackup(String directoryName) {
        return createDirectoryInParent(createTemplateDirectory("备份"), directoryName);
    }

    public File createDirectoryForTemplate(String directoryName) {
        return createDirectoryInParent(createTemplateDirectory("模板"), directoryName);
    }

    public File createDirectoryForMulti(File parentDirectory, String... directoryNames) {
        if (parentDirectory.isFile()) throw new RuntimeException("parentDirectory is a file");
        File f = new File(parentDirectory.getAbsolutePath() + separator + String.join(separator, directoryNames));
        if (!f.mkdirs()) throw new RuntimeException("创建临时文件夹失败：" + f.getAbsolutePath());
        return f;
    }

    public File createDirectoryForUpload(String directoryName) {
        return createDirectoryInParent(createTemplateDirectory("上传"), directoryName);
    }

    public File createDirectoryForImage(String directoryName) {
        return createDirectoryInParent(createTemplateDirectory("图片"), directoryName);
    }

    public File createDirectoryForData(String directoryName) {
        return createDirectoryInParent(createTemplateDirectory("数据"), directoryName);
    }

    public File createFileForBackup(String fileName) {
        return createTemplateFile("备份", fileName);
    }

    public File createFileForUpload(String fileName) {
        return createTemplateFile("上传", fileName);
    }

    public File createFileForTemplate(String fileName) {
        return createTemplateFile("模板", fileName);
    }

    public File createFileForImage(String fileName) {
        return createTemplateFile("图片", fileName);
    }

    public File createFileForData(String fileName) {
        return createTemplateFile("数据", fileName);
    }
}
