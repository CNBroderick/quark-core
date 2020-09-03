package org.bklab.quark.util.zip;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.io.File.separator;

public class ZipCompressor {

    private final Path root;

    public ZipCompressor(Path root) {
        this.root = root;
    }

    public File zip() throws Exception {
        File zip = new File(System.getProperty("java.io.tmpdir") + separator + "Broderick Labs"
                + separator + System.currentTimeMillis() + separator + root.toFile().getName() + ".zip");
        if (!zip.getParentFile().exists() && !zip.getParentFile().mkdirs() && zip.createNewFile())
            throw new RuntimeException("创建临时文件失败");

        File zipRootFile = root.toFile();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zip), StandardCharsets.UTF_8);
        if (zipRootFile.isFile()) {
            zipFile(zipRootFile, "", zipOutputStream);
        } else {
            zipDirectory(zipRootFile.getAbsolutePath(), zipOutputStream, "");
        }

        zipOutputStream.close();
        return zip;
    }

    private void zipDirectory(String directoryName, ZipOutputStream zos, String basePath) throws Exception {
        File file = new File(directoryName);
        // 每一级别的递归 basePath 不应该被改变所以添加一个 参数 copyBasePath
        String copyBasePath;
        if (file.exists()) {
            File[] fileList = file.listFiles();
            if (fileList == null) return;
            for (File f : fileList) {
                if (f.isDirectory()) {
                    // 拼接文件夹目录
                    if (!"".equals(basePath)) {
                        copyBasePath = basePath + separator + f.getName();
                    } else {
                        copyBasePath = f.getName();
                    }
                    // 继续递归文件夹
                    zipDirectory(directoryName + separator + f.getName(), zos, copyBasePath);
                } else {
                    zipFile(f, basePath, zos);
                }
            }
        }
    }

    private void zipFile(File f, String basePath, ZipOutputStream zos) throws Exception {
        // 压缩单个文件到 zos
        String zipName;
        if (!"".equals(basePath)) {
            zipName = basePath + separator + f.getName();
        } else {
            zipName = f.getName();
        }

        // zos.putNextEntry  开始添加压缩文件  ZipEntry传入的参数 zipName如果包含了层级关系就会生成文件夹
        zos.putNextEntry(new ZipEntry(zipName));
        int len;
        FileInputStream is = new FileInputStream(f);
        byte[] bytes = new byte[1024];
        while ((len = is.read(bytes)) != -1) {
            zos.write(bytes, 0, len);
        }
        zos.flush();
        // zos.closeEntry()   结束当前压缩文件的添加
        zos.closeEntry();
        is.close();
    }

}
