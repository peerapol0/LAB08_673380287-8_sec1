package com.example.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String category;
	private String brand;
	private Integer stock;
	private Double price;
	private String discountType;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "detail_id", referencedColumnName = "id")
	private ProductDetail detail;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews = new ArrayList<>();

	@Transient
	public Double getDiscountedPrice() {
		if (price == null) {
			return 0.0;
		}
		double rate = "MEMBER".equalsIgnoreCase(discountType) ? 0.10
				: "SEASONAL".equalsIgnoreCase(discountType) ? 0.20 : 0.0;
		return price * (1 - rate);
	}

	public void addReview(Review review) {
		if (review != null) {
			reviews.add(review);
			review.setProduct(this);
		}
	}

	public void removeReview(Review review) {
		if (reviews.remove(review)) {
			review.setProduct(null);
		}
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getBrand() { return brand; }
	public void setBrand(String brand) { this.brand = brand; }
	public Integer getStock() { return stock; }
	public void setStock(Integer stock) { this.stock = stock; }
	public Double getPrice() { return price; }
	public void setPrice(Double price) { this.price = price; }
	public String getDiscountType() { return discountType; }
	public void setDiscountType(String discountType) { this.discountType = discountType; }
	public ProductDetail getDetail() { return detail; }
	public void setDetail(ProductDetail detail) {
		this.detail = detail;
		if (detail != null && detail.getProduct() != this) {
			detail.setProduct(this);
		}
	}
	public List<Review> getReviews() { return reviews; }
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews == null ? new ArrayList<>() : reviews;
		this.reviews.forEach(review -> review.setProduct(this));
	}
}
