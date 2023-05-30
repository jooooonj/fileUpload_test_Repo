package com.ll.upload.product.entity.request;

import com.ll.upload.product.entity.ProductFile;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductSaveRequest {
    private String name;
    private int price;
    private MultipartFile mainFile;
    private List<MultipartFile> subFiles;
}

