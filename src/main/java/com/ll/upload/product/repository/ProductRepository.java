package com.ll.upload.product.repository;

import com.ll.upload.product.entity.ProductEx;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEx, Long> {
}
