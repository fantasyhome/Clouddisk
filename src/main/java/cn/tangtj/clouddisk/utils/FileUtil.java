package cn.tangtj.clouddisk.utils;

import cn.tangtj.clouddisk.entity.UploadFile;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;


public class FileUtil {

    public static ResponseEntity<byte[]> createResponseEntityByFileInfo(UploadFile uploadFile, String filePath){

        HttpHeaders httpHeaders = new HttpHeaders();
        String downloadName = "downloadError";
        if (uploadFile != null && uploadFile.getFileName() != null){
            downloadName = uploadFile.getFileName();
        }
        httpHeaders.setContentDispositionFormData("attachment",   downloadName);
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] bytes = null;
        //打开文件
        File file = new File(filePath+ (uploadFile != null ? uploadFile.getMappingName() : null));
        if (file.exists()) {
            try {
                //将文件转换为byte数组
                bytes = FileUtils.readFileToByteArray(file);
            } catch (IOException e) {
                downloadName = "downloadError";
                e.printStackTrace();
            }
        }
        //传给前端
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.CREATED);
    }
}
