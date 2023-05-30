package com.ll.upload.product.controller;

import com.ll.upload.domain.Item;
import com.ll.upload.product.entity.ProductEx;
import com.ll.upload.product.entity.ProductFile;
import com.ll.upload.product.entity.request.ProductSaveRequest;
import com.ll.upload.product.repository.ProductRepository;
import com.ll.upload.product.terminator.FileTerminator;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/products")
@Controller
public class ProductController {

    private final FileTerminator fileTerminator;
    private final ProductRepository productRepository;

    @GetMapping("/register")
    public String registerForm(ProductSaveRequest productSaveRequest) {
        return "product-form";
    }

    @PostMapping("/register")
    public String register(ProductSaveRequest productSaveRequest, Model model) throws IOException {

        ProductFile mainFile = fileTerminator.terminateFile(productSaveRequest.getMainFile());
        List<ProductFile> subFiles = fileTerminator.terminateFileList(productSaveRequest.getSubFiles());

        for(ProductFile productFile : subFiles){
            productFile.connectMainFile(mainFile);
        }

        ProductEx productEx = ProductEx
                .builder()
                .name(productSaveRequest.getName())
                .price(productSaveRequest.getPrice())
                .productFile(mainFile)
                .build();

        Long productId = productRepository.save(productEx).getId();
        return "redirect:/products/%s".formatted(productId);
    }

    @GetMapping("/{productId}")
    public String shoProduct(@PathVariable Long productId, Model model) {
        ProductEx productEx = productRepository.findById(productId).orElseThrow();

        model.addAttribute("product", productEx);
        return "product-view2";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileTerminator.getFilePath(filename));
    }

    @GetMapping("/attach/{productId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long productId) throws MalformedURLException {
        ProductEx productEx = productRepository.findById(productId).orElseThrow();
        String storeFileName = productEx.getProductFile().getTerminatedFileName();
        String uploadFileName = productEx.getProductFile().getOriginalFileName();

        UrlResource resource = new UrlResource("file:" + fileTerminator.getFilePath(storeFileName));

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

//    @PostMapping("/edit/{productId}")
//    public String modifyProduct(@PathVariable Long productId, ProductSaveRequest productSaveRequest, Model model) {
//
//    }
//
//    @DeleteMapping("/{productId}")
//    public String deleteProduct(@PathVariable Long productId) {
//        productService.delete(productId);
//        return "redirect:/trendpick/products/list";
//    }
//
//    @GetMapping("/{productId}")
//    public String showProduct(@PathVariable Long productId, Model model) {
//        model.addAttribute("productResponse", productService.show(productId));
//        return "/trendpick/products/detailpage";
//    }
//
//    @GetMapping("/list")
//    public String showAllProduct(@RequestParam("page") int offset, @RequestParam("main-category") String mainCategory,
//                                 @RequestParam("sub-category") String subCategory, Model model) {
//        model.addAttribute("productResponse", productService.showAll(offset, mainCategory, mainCategory));
//        return "/trendpick/products/detailpage";
//    }
}

