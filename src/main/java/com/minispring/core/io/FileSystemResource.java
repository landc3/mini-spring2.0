package com.minispring.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文件资源实现类
 */
public class FileSystemResource implements Resource{
    private final String path;//文件路径
    private final File file;//文件

    /**
     * 构造方法
     * @param path
     */
    /**
     * （1）为什么分开写两个构造方法？
     * 职责单一：每个构造方法专注于处理一种输入类型（路径或文件）。
     * 灵活性：用户可以根据需要选择提供路径或文件。
     * 避免冗余：不需要同时提供路径和文件，简化了调用方式。
     * （2）为什么使用 getAbsoluteFile()？
     * 确保路径是绝对路径：提高路径的唯一性和可移植性。
     * 规范化路径：将相对路径或复杂路径转换为标准的绝对路径。
     * 增强健壮性：减少因路径问题导致的错误。
     *
     */
    public FileSystemResource(String path) {
        if (path == null) {
            throw new IllegalArgumentException("路径不能为空");
        }
        this.path = path;
        this.file = new File(path).getAbsoluteFile();
    }

    public FileSystemResource(File file) {
        if (file == null) {
            throw new IllegalArgumentException("文件不能为空");
        }
        this.path = file.getPath();
        this.file = file.getAbsoluteFile();
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean isReadable() {
        return file.canRead();
    }

    /**
     * 获取资源输入流
     * @return
     * @throws IOException
     * 在有throw 抛出异常时，最好try catch，否则编译器会报错
     */
    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return new FileInputStream(file);
        }catch (IOException ex){
            throw new IOException("无法打开文件 [" + path + "]", ex);
        }
    }

    /**
     * 获取资源描述
     * @return
     */
    @Override
    public String getDescription() {
        return "文件系统资源 [" + path + "]";
    }

    /**
     * 获取文件的最后修改时间
     */
    public long lastModified() throws IOException{
        return Files.getLastModifiedTime(file.toPath()).toMillis();

    }
    /**
     * 创建相对于此资源的新资源
     * @param relativePath 相对路径
     *
     */
    public Resource createRelative(String relativePath) {
        if (relativePath == null) {
            throw new IllegalArgumentException("相对路径不能为空");
        }

        try {
            // 获取当前文件的路径
            Path basePath = file.toPath();

            // 如果当前路径是文件，使用其父目录作为基础路径
            if (Files.isRegularFile(basePath)) {
                basePath = basePath.getParent();
            }

            // 解析相对路径
            Path resolvedPath = basePath.resolve(relativePath).normalize();
            return new FileSystemResource(resolvedPath.toFile());
        }catch (Exception ex){
            throw new IllegalArgumentException("无法创建相对路径资源 [" + relativePath + "]", ex);
        }
    }

}
