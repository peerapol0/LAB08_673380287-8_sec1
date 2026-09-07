package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("products", productService.findAll());
		return "products/list";
	}

	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("product", productService.prepareForForm(new Product()));
		return "products/add";
	}

	@PostMapping("/save")
	public String save(Product product, RedirectAttributes redirectAttributes) {
		productService.save(product);
		redirectAttributes.addFlashAttribute("message", "บันทึกสินค้าเรียบร้อยแล้ว");
		return "redirect:/products";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("product", productService.prepareForForm(productService.findById(id)));
		return "products/edit";
	}

	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, Product product, RedirectAttributes redirectAttributes) {
		Product existingProduct = productService.findById(id);
		existingProduct.setName(product.getName());
		existingProduct.setCategory(product.getCategory());
		existingProduct.setBrand(product.getBrand());
		existingProduct.setStock(product.getStock());
		existingProduct.setPrice(product.getPrice());
		existingProduct.setDiscountType(product.getDiscountType());

		ProductDetail submittedDetail = product.getDetail();
		if (submittedDetail != null) {
			ProductDetail existingDetail = existingProduct.getDetail();
			if (existingDetail == null) {
				existingDetail = new ProductDetail();
				existingProduct.setDetail(existingDetail);
			}
			existingDetail.setDescription(submittedDetail.getDescription());
			existingDetail.setWarranty(submittedDetail.getWarranty());
			existingDetail.setWeight(submittedDetail.getWeight());
			existingDetail.setDimensions(submittedDetail.getDimensions());
			existingDetail.setManufacturedCountry(submittedDetail.getManufacturedCountry());
		}

		productService.save(existingProduct);
		redirectAttributes.addFlashAttribute("message", "แก้ไขสินค้าเรียบร้อยแล้ว");
		return "redirect:/products";
	}

	@GetMapping("/delete/{id}")
	public String deleteForm(@PathVariable Long id, Model model) {
		model.addAttribute("product", productService.findById(id));
		return "products/delete";
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		productService.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "ลบสินค้าเรียบร้อยแล้ว");
		return "redirect:/products";
	}
}
