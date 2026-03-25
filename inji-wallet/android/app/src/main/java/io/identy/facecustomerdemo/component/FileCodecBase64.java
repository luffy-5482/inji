package io.identy.facecustomerdemo.component;

import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCodecBase64 {


    /**
     * This method converts the content of a source file into Base64 encoded data and saves that to a target file.
     * If isChunked parameter is set to true, there is a hard wrap of the output  encoded text.
     */
    private static void encode(String sourceFile, String targetFile) throws Exception {

        byte[] base64EncodedData = Base64.encode(loadFileAsBytesArray(sourceFile), Base64.DEFAULT);

        writeByteArraysToFile(targetFile, base64EncodedData);
    }

    public static byte[] encode(String sourceFile,int base64encodeType) throws Exception {
        byte[] base64EncodedData = Base64.encode(loadFileAsBytesArray(sourceFile), base64encodeType);
        return base64EncodedData;
    }

    public static byte[] encode(byte[] sourceFile,int base64encodeType) throws Exception {
        byte[] base64EncodedData = Base64.encode(sourceFile, base64encodeType);
        return base64EncodedData;
    }

    public static void decode(String sourceFile, String targetFile) throws Exception {

        byte[] decodedBytes = Base64.decode(loadFileAsBytesArray(sourceFile), Base64.DEFAULT);

        writeByteArraysToFile(targetFile, decodedBytes);
    }

    public static byte[] decode(String sourceStr, int base64encodeType) throws Exception {

        byte[] decodedBytes = Base64.decode(sourceStr.getBytes(), base64encodeType);
        return decodedBytes;
    }

    public static byte[] decode(String sourceStr) throws Exception {

        byte[] decodedBytes = Base64.decode(sourceStr.getBytes(), Base64.DEFAULT);
        return decodedBytes;
    }

    /**
     * This method loads a file from file system and returns the byte array of the content.
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static byte[] loadFileAsBytesArray(String fileName) throws Exception {

        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;

    }

    /**
     * This method writes byte array content into a file.
     *
     * @param fileName
     * @param content
     * @throws IOException
     */
    public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException {

        File file = new File(fileName);
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
        writer.write(content);
        writer.flush();
        writer.close();

    }
}