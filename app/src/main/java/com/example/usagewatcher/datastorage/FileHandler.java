package com.example.usagewatcher.datastorage;

import android.content.Context;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A <code>FileHandler</code> handles all the writing operations on logfile,
 * it also has the ability to call cloud service sendToCloud in {@link BlobStorage} to send data to azure cloud.
 * using uploadLogFile() method.
 *
 * @see     BlobStorage
 */

public class FileHandler {

    private static final String TAG = "FileHandler";

    //stores the current app context
    private Context context;
    private File duplicate_file;
    private String file_type;


    /**
     * creates a <code>FileHandler</code> objects that takes current app context.
     * @param context A {@link Context} object that represents current app context.
     */
    public FileHandler(Context context, File duplicate_file, String file_type) {
        this.context = context;
        this.duplicate_file = duplicate_file;
        this.file_type = file_type;
    }


    /**
     * uploads logfile to cloud storage
     * and clears the local file if file is uploaded successfully.
     */
    public synchronized void uploadLogFile() {
        try (FileInputStream fis = new FileInputStream(duplicate_file)) {
            if (fis.getChannel().size() > 0L) {
                if(BlobStorage.sendToCloud(file_type, fis, context)) {
                    try {
                        FileOutputStream fos = new FileOutputStream(duplicate_file);
                        fos.write("".getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeLogsToFile(File original_file) {
        FileReader fr = null;
        FileWriter fw = null;
        try {
            fr = new FileReader(original_file);
            fw = new FileWriter(duplicate_file,true);
            int c = fr.read();
            fw.write("\r\n");
            while(c!=-1) {
                fw.write(c);
                c = fr.read();
            }
            if(original_file.delete()){
                Log.d(TAG, "Deleted Original File");
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            close(fr);
            close(fw);
        }
    }

    private static void close(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}