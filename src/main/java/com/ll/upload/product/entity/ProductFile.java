package com.ll.upload.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String terminatedFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_file_id")
    private ProductFile mainFile;

    //종속
    @OneToMany(mappedBy = "mainFile", cascade = CascadeType.ALL)
    private List<ProductFile> subFiles = new ArrayList<>();

    @OneToOne(mappedBy = "productFile")
    private ProductEx productEx;

    public ProductFile(String originalFileName, String terminatedFileName) {
        this.originalFileName = originalFileName;
        this.terminatedFileName = terminatedFileName;
    }

    //반대는 굳이 필요없을듯 하다.
    public void connectMainFile(ProductFile mainFile){
        this.mainFile = mainFile;
    }


}
