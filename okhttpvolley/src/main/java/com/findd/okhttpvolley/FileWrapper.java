package com.findd.okhttpvolley;

import java.io.File;

/**
 * Created by Troy Tang on 2015/7/23.
 */
public class FileWrapper {

    public String fileType;
    public File file;

    public FileWrapper(String type, File file) {
        this.fileType = type;
        this.file = file;
    }
}
