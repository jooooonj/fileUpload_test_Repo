package com.ll.upload.product.terminator;

import com.ll.upload.domain.UploadFile;
import com.ll.upload.file.FileStore;
import com.ll.upload.product.entity.ProductFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileTerminator {

    @Value("${file.dir}")
    private String filePath;

    //경로
    public String getFilePath(String filename) {
        return filePath + filename;
    }

    //단일 멀티파일 들어왔을때 저장하고 반환
    public ProductFile terminateFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String terminatedFileName = terminateFileName(originalFilename);
        multipartFile.transferTo(new File(getFilePath(terminatedFileName)));

        return ProductFile
                .builder()
                .originalFileName(originalFilename)
                .terminatedFileName(terminatedFileName)
                .build()
                ;
    }

//    //실제 상품 등록에 사용할 로직 (리팩토링 필요)
//    //상품 등록은 무조건 메인 사진 1장과 서브 사진들을 가지고 있어야 한다.
//    public ProductFile terminateProductFile(MultipartFile mainMultipartFile, List<MultipartFile> subMultipartFiles) throws IOException {
//        if (mainMultipartFile.isEmpty() || subMultipartFiles.size() == 0) {
//            return null;
//        }
//
//        String originalFilename = mainMultipartFile.getOriginalFilename();
//        String terminatedFileName = terminateFileName(originalFilename);
//
//        ProductFile mainFile = terminateFile(mainMultipartFile);
//        List<ProductFile> subFiles = makeSubFiles(mainFile, subMultipartFiles);
//
//
//        return mainFile;
//    }

    public List<ProductFile> terminateFileList(List<MultipartFile> MultipartFiles) throws IOException {
        List<ProductFile> fileList = new ArrayList<>();
        for(MultipartFile multipartFile : MultipartFiles){
            if(!multipartFile.isEmpty()){
                fileList.add(terminateFile(multipartFile));
            }
        }
        return fileList;
    }

    private String terminateFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public List<ProductFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<ProductFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public ProductFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = FileStore.createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFilePath(storeFileName)));
        return new ProductFile(originalFilename, storeFileName);
    }
}