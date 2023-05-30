package com.ll.upload.product.terminator;

import com.ll.upload.domain.UploadFile;
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
    private String getFilePath(String filename) {
        return filePath + filename;
    }

    //단일 멀티파일 들어왔을때 저장하고 반환
    public ProductFile terminateFile(MultipartFile multipartFile) throws IOException {
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

    //실제 상품 등록에 사용할 로직 (리팩토링 필요)
    //상품 등록은 무조건 메인 사진 1장과 서브 사진들을 가지고 있어야 한다.
    public ProductFile terminateProductFile(MultipartFile mainMultipartFile, List<MultipartFile> subMultipartFiles) throws IOException {
        if (mainMultipartFile.isEmpty() || subMultipartFiles.size() == 0) {
            return null;
        }

        String originalFilename = mainMultipartFile.getOriginalFilename();
        String terminatedFileName = terminateFileName(originalFilename);

        ProductFile mainFile = terminateFile(mainMultipartFile);
        List<ProductFile> subFiles = makeSubFiles(subMultipartFiles);

        //메인파일에 서브파일 연결
        mainFile.connectSubFiles(subFiles);

        return mainFile;
    }

    //상품 이미지 등록에만 쓰일듯하다.
    private List<ProductFile> makeSubFiles(List<MultipartFile> MultipartFiles) throws IOException {
        List<ProductFile> files = new ArrayList<>();
        for(MultipartFile multipartFile : MultipartFiles){
            files.add(terminateFile(multipartFile));
        }

        return files;
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
}