package com.findd.okhttpvolley.util;

import java.io.File;

/**
 * Created by Troy Tang on 2015/7/23.
 */
public class Util {

    public static String httpFileType(File file) {
        String fileType = "";
        if (null != file && file.exists()) {
            String fileName = file.getName();
            if (fileName.endsWith("gif")) {
                fileType = "image/gif";
            } else if (fileName.endsWith("png")) {
                fileType = "image/png";
            } else if (fileName.endsWith("jpeg") || fileName.endsWith("jpg")) {
                fileType = "image/jpeg";
            } else if (fileName.endsWith("bmp")) {
                fileType = "image/bmp";
            } else if (fileName.endsWith("psd")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("ico")) {
                fileType = "image/x-icon";
            } else if (fileName.endsWith("rar")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("zip")) {
                fileType = "application/zip";
            } else if (fileName.endsWith("7z")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("exe")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("avi")) {
                fileType = "video/avi";
            } else if (fileName.endsWith("rmvb")) {
                fileType = "application/vnd.rn-realmedia-vbr";
            } else if (fileName.endsWith("3gp")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("flv")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("mp3")) {
                fileType = "audio/mpeg";
            } else if (fileName.endsWith("wav")) {
                fileType = "audio/wav";
            } else if (fileName.endsWith("krc")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("lrc")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("txt")) {
                fileType = "text/plain";
            } else if (fileName.endsWith("doc")) {
                fileType = "application/msword";
            } else if (fileName.endsWith("xls")) {
                fileType = "application/vnd.ms-excel";
            } else if (fileName.endsWith("ppt")) {
                fileType = "application/vnd.ms-powerpoint";
            } else if (fileName.endsWith("pdf")) {
                fileType = "application.pdf";
            } else if (fileName.endsWith("chm")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("mdb")) {
                fileType = "application/msaccess";
            } else if (fileName.endsWith("sql")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("con")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("log")) {
                fileType = "text/plain";
            } else if (fileName.endsWith("dat")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("ini")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("php")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("html")) {
                fileType = "text/html";
            } else if (fileName.endsWith("htm")) {
                fileType = "text/html";
            } else if (fileName.endsWith("ttf")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("fon")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("js")) {
                fileType = "application/x-javascript";
            } else if (fileName.endsWith("xml")) {
                fileType = "application/octet-stream";
            } else if (fileName.endsWith("dll")) {
                fileType = "application/octet-stream";
            }
        }
        return fileType;
    }

}
