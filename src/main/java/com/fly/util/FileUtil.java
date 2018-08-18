package com.fly.util;

import com.fly.config.SystemConfig;

import java.io.*;
import java.util.Properties;

/**
 * @author david
 * @date 07/08/18 18:25
 */
public class FileUtil {

    private static Properties prop = SystemConfig.global;

    /**
     * 获取当前项目根目录
     * @return
     */
    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取该用户home目录
     * @return
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    /**
     * 根据指定key获取系统参数
     * @param key
     * @return
     */
    public static String getSysValueByKey(String key) {
        return System.getProperty(key);
    }

    /**
     * 通过绝对路径的方式加载配置文件
     * @param abPath 配置文件绝对路径
     * @param fileName
     */
    public static void loadProp(String abPath, String fileName) throws IOException {
        String fullName = abPath + "/" + fileName;
        File file = new File(fullName);
        FileInputStream fis = new FileInputStream(file);
        prop.load(fis);
    }

    /**
     * 从classpath下加载配置文件
     * @param fileName classpath下文件名
     */
    public static void loadProp(String fileName) throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
        prop.load(is);
    }

    /**
     * 保存properties配置信息
     * @param p
     * @param filePath 文件路径
     * @param fileName 文件名
     * @throws IOException
     */
    public static void storeConfigFile(Properties p, String filePath, String fileName) throws IOException {
        String fullPath = filePath + "/" + fileName;
        File file = new File(fullPath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream stream = new FileOutputStream(file);
        p.store(stream, fullPath);
    }

    /**
     * 创建多级文件夹
     * @param dirName 多级文件夹路径
     */
    public static void createDirs(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 创建单级文件夹
     * @param dirName 单机文件夹名
     */
    public static void createDir(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void main(String[] args) throws IOException {

    }

}