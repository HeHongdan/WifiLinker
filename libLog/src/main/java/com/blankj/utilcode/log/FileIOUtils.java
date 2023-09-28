package com.blankj.utilcode.log;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class FileIOUtils {


     /**
      * Write file from string.
      *
      * @param filePath The path of file.
      * @param content  The string of content.
      * @param append   True to append, false otherwise.
      * @return {@code true}: success<br>{@code false}: fail
      */
     public static boolean writeFileFromString(final String filePath,
                                               final String content,
                                               final boolean append) {
         return writeFileFromString(UtilsBridge.getFileByPath(filePath), content, append);
     }

    /**
     * Write file from string.
     *
     * @param file    The file.
     * @param content The string of content.
     * @param append  True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean writeFileFromString(final File file,
                                              final String content,
                                              final boolean append) {
        if (file == null || content == null) return false;
        if (!UtilsBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <" + file + "> failed.");
            return false;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static File getFileByPath(final String filePath) {
        return FileUtils.getFileByPath(filePath);
    }


}
