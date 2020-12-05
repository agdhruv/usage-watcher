package com.example.usagewatcher.datastorage;

import android.content.Context;
import android.util.Log;

import com.example.usagewatcher.R;
import com.example.usagewatcher.Utils;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudAppendBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class BlobStorage {

    private static final String TAG = "BlobStorage";

    /**
     * Sends data to the azure cloud storage creates a container if not available for update
     * creates a file in the container if not available for update.
     *
     * @param logFile A {@link FileInputStream} object that is connected to local matlog.txt file.
     *
     * @param context A {@link Context} object that represents the current application context.
     *
     * @return <code>true</code> if file uploaded successfully else <code>false</code>
     */

    static boolean sendToCloud(String fileType, FileInputStream logFile, Context context) {
        String containerName = "";
        try {
            String deviceId = Utils.getDeviceUniqueId(context);
            containerName =  deviceId;

            //Check for block name length constraint kept by azure
            if (containerName.length() > 63) {
                containerName = deviceId;
            }

            Log.d(TAG, containerName);

            //initializing cloud storage account object from the connection string provided
            //BLOB STORAGE CONNECTION STRING
            final String connectionString = context.getString(R.string.azure_storage_string);
            CloudStorageAccount account = CloudStorageAccount
                    .parse(connectionString);

            //using the account object create a blob client to access the containers in storage
            CloudBlobClient blobClient = account.createCloudBlobClient();

            //get the container object from the blob client
            CloudBlobContainer container = blobClient.getContainerReference(containerName);
            container.createIfNotExists();

            //create container permissions object to set the permissions to the container
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

            //setting permissions
            containerPermissions
                    .setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

            //apply permissions to container
            container.uploadPermissions(containerPermissions);

            //append blob is a special kind of block blob which allows to append the new data at the
            // end of the existing block blob

            String appendBlobName = fileType + ".csv";
            Log.d(TAG, appendBlobName);

            //get append blob instance from the container object
            CloudAppendBlob appendBlob = container
                    .getAppendBlobReference(appendBlobName);

            appendBlob.setStreamWriteSizeInBytes(1000000);

            long fileSize = logFile.getChannel().size();

            if (fileSize > 0L) {
                //if append blob with the specified name not exists then create one
                if (!appendBlob.exists()) {
                    appendBlob.createOrReplace();
                    if (fileType.equals("gps")) {
                        appendBlob.appendText("Timestamp,Latitude,Longitude" + "\n");
                    } else {
                        // acc and gyro
                        appendBlob.appendText("Timestamp,X,Y,Z" + "\n");
                    }
                }
                appendBlob.append(logFile, fileSize);
                Log.d(TAG, "UPLOAD SUCCESS SIZE: " + fileSize);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.d(TAG, "BLOB connection failed");
            Log.d(TAG, "BLOB connection failed-->" + e);
            Log.d(TAG, containerName);
            return false;
        }
    }
}