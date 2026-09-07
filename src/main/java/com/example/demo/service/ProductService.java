package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.repository.ProductRepository;
import com.example.demo.strategy.DiscountContext;
import com.example.demo.strategy.MemberDiscountStrategy;
import com.example.demo.strategy.NoDiscountStrategy;
import com.example.demo.strategy.SeasonalSaleStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Product findById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
	}

	public Product prepareForForm(Product product) {
		if (product.getDetail() == null) {
			product.setDetail(new ProductDetail());
		}
		if (product.getReviews().isEmpty()) {
			product.getReviews().add(new Review());
		}
		return product;
	}

	public Product save(Product product) {
		if (product.getDetail() == null) {
			product.setDetail(new ProductDetail());
		}
		product.setDiscountType(normalizeDiscountType(product.getDiscountType()));
		product.getReviews().removeIf(review -> isBlank(review.getReviewer())
				&& isBlank(review.getComment()));
		product.getReviews().forEach(review -> {
			review.setProduct(product);
			if (review.getReviewDate() == null) {
				review.setReviewDate(LocalDate.now());
			}
		});
		product.getDetail().setProduct(product);
		return productRepository.save(product);
	}

	public void deleteById(Long id) {
		productRepository.deleteById(id);
	}

	public double calculateDiscountedPrice(Product product) {
		DiscountContext context = new DiscountContext(new NoDiscountStrategy());
		if ("MEMBER".equals(product.getDiscountType())) {
			context.setStrategy(new MemberDiscountStrategy());
		} else if ("SEASONAL".equals(product.getDiscountType())) {
			context.setStrategy(new SeasonalSaleStrategy());
		}
		return context.calculateDiscountedPrice(product.getPrice() == null ? 0 : product.getPrice());
	}

	private String normalizeDiscountType(String discountType) {
		return discountType == null || discountType.isBlank() ? "NONE" : discountType.toUpperCase();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
