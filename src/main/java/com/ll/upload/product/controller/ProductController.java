package com.ll.upload.product.controller;

import com.ll.upload.product.entity.ProductEx;
import com.ll.upload.product.entity.ProductFile;
import com.ll.upload.product.entity.request.ProductSaveRequest;
import com.ll.upload.product.repository.ProductRepository;
import com.ll.upload.product.terminator.FileTerminator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/product")
@Controller
public class ProductController {

    private FileTerminator fileTerminator;
    private ProductRepository productRepository;

    @GetMapping("/register")
    public String registerForm(ProductSaveRequest productSaveRequest) {
        return "product-form";
    }

    @PostMapping("/register")
    public String register(ProductSaveRequest productSaveRequest, Model model) throws IOException {
        String name = productSaveRequest.getName();
        int price = productSaveRequest.getPrice();

        ProductFile productFile = fileTerminator.terminateProductFile(productSaveRequest.getMainFile(), productSaveRequest.getSubFiles());
        ProductEx productEx = ProductEx
                .builder()
                .name(productSaveRequest.getName())
                .price(productSaveRequest.getPrice())
                .productFile(productFile)
                .build();

        productEx = productRepository.save(productEx);

        return "redirect:/products/%s".formatted(productEx.getId());
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

