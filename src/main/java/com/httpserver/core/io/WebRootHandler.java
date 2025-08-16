package com.httpserver.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler {
    private File webRoot;

    public WebRootHandler(String webRootPath) throws WebRootNotFoundException {
        webRoot = new File(webRootPath);
        if (!webRoot.exists() || !webRoot.isDirectory()) {
            throw new WebRootNotFoundException("Webroot provided does not exist or is not a folder");
        }
    }

    private boolean CheckIfEndsWithSlash(String relativePath) {
        return relativePath.endsWith("/");
    }

    /**
     *  This method checks if the relative path provided exists inside WeebRoot
     * 
     * @param relativePath
     * @return true if the path exists inside WebRoot otherwise false
     */
    private boolean checkIfProvidedRelativePathExists(String relativePath) {
        File file = new File(webRoot, relativePath);

        if (!file.exists())
        return false;

        try {
            if (file.getCanonicalPath().startsWith(webRoot.getCanonicalPath())) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }

        return false;
    }

    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        if (CheckIfEndsWithSlash(relativePath)) {
            relativePath += "index.html"; // By default server the index.html, if it exists
        }

        if (!checkIfProvidedRelativePathExists(relativePath)) {
            throw new FileNotFoundException("File Not Found: " + relativePath);
        }
        File file = new File(webRoot, relativePath);

        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());

        if (mimeType == null) {
            // specified in RFC-7231 within section 4.3.3
            return "application/octet-stream";
        }

        return mimeType;
    }

    /**
     *  Returns a byte array of the content of a file
     * 
     * TODO: For large files, a new strategy will be necessary
     * 
     * @param relativePath the path to the file inside the webroot folder
     * @return a byte array of the data
     * @throws FileNotFoundException if the file cannot be found
     * @throws ReadFileException if there was a problem reading the file
     */
    public byte[] getFileByteArrayData(String relativePath) throws FileNotFoundException, ReadFileException {
        if (CheckIfEndsWithSlash(relativePath)) {
            relativePath += "index.html"; // by default serves index.html, if it exists
        }

        if (!checkIfProvidedRelativePathExists(relativePath)) {
            throw new FileNotFoundException("File Not Found: " + relativePath);
        }
        File file = new File(webRoot, relativePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int)file.length()];
        try {
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        } catch (IOException e) {
            throw new ReadFileException(e);
        }
        return fileBytes;
    }
}
