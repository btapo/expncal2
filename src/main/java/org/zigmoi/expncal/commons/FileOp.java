package org.zigmoi.expncal.commons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

public class FileOp {
    
    public static String getFileContent(File oFile) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(oFile.getAbsolutePath()));
        return new String(encoded, StandardCharsets.UTF_8);
    }
    
    public static String convertToUnix(File oFile) throws FileNotFoundException, IOException {
        return getFileContent(oFile).replaceAll("\r\n", "\n");
    }
    
    public static String convertToUnix(String content) {
        return content.replaceAll("\r\n", "\n");
    }
    
    public static String getRandomFileName() {
        return StringOp.getRandomString(30, "`~!@#$%^&*()_+-=|;:,<.>?*") + ".dat";
    }
    
    public static boolean createFile(File oFile, boolean createIfDoesNotExist) throws IOException {
        if (!oFile.getParentFile().exists() && !oFile.getParentFile().mkdirs()) {return false;}
        if (oFile.exists() && !createIfDoesNotExist) {return oFile.createNewFile();}
        if (!oFile.exists()) {return oFile.createNewFile();}
        return true;
    }
    
    public static boolean appendToFile(File oFile, String content) throws FileNotFoundException, IOException {
        FileWriter fileWritter = new FileWriter(oFile.getAbsolutePath(),true);
        try (BufferedWriter bufferWritter = new BufferedWriter(fileWritter)) {
            bufferWritter.write(content);
            bufferWritter.flush();
            bufferWritter.close();
            return true;
        }
    }
    
    public static boolean createAndWrite(File oFile, String content) throws IOException {
        return createFile(oFile, false) && appendToFile(oFile, content);
    }
    
    public static ArrayList<String> scanAndListOnlyFiles(String dir) {
        ArrayList<String> fileList = new ArrayList<>();
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        try {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    fileList.add(listOfFile.getAbsolutePath());
                }
            }
        } catch (NullPointerException e) {}
        return fileList;
    }
    
    public static void move(String fromFile, String toFile) throws IOException {
        FileUtils.moveFile(new File(fromFile), new File(toFile));
    }
}
