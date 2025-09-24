package recruit.jotang2025.info_manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import recruit.jotang2025.info_manager.controller.ProductController;
import recruit.jotang2025.info_manager.pojo.Product;

@SpringBootTest
@Transactional // 测试后数据库会进行回滚, 确保测试不会污染数据库
class ProductApplicationTests {

	@Autowired
	ProductController productController;

	Product testProduct;

	@BeforeEach // 在每个测试方法之前都运行一次, 为每个测试准备好测试数据
	public void init() {
		Long productId = 100L;
		BigDecimal price = new BigDecimal("13.14");
		Long publisherId = 1L;
		LocalDateTime now = LocalDateTime.now();
		testProduct = new Product(productId, "测试商品", "大家好啊，我是测试商品，给大家一些好看的东西",
				price, publisherId, Product.Type.item, Product.Status.unsold, now, now);
		productController.createProduct(testProduct);
	}

	// 测试的编写一般使用"AAA"规则编排, 即Arrange-Act-Assert
	@Test
	void testQueryAll() {
		// Arrange - 准备测试方法所用的数据, 这里已经准备好了

		// Act - 进行要测试的内容
		List<Product> products = productController.queryAllProduct();

		// Assert - 预测测试的结果
		assertNotNull(products, "查询结果不应为空");
		assertFalse(products.isEmpty(), "查询结果不应为空");
	}

	@Test
	void testQueryById() {
		// Arrange

		// Act
		Product foundProduct = productController.queryProductById(testProduct.getProductId());

		// Assert
		assertNotNull(foundProduct, "查询结果不应为空");
		assertEquals(foundProduct.getProductId(), testProduct.getProductId(), "ID应匹配");
		assertEquals(foundProduct.getProductName(), testProduct.getProductName(), "名字应匹配");
		assertEquals(foundProduct.getProductDescription(), testProduct.getProductDescription(), "描述应匹配");
		assertEquals(foundProduct.getPrice(), testProduct.getPrice(), "价格应匹配");
		// 懒得接着往下写了= =
	}

	@Test
	void testUpdate() {
		// Arrange
		Long newProductId = testProduct.getProductId();
		BigDecimal newPrice = new BigDecimal("5.20");
		Long newPublisherId = 2L;
		LocalDateTime now = LocalDateTime.now();
		Product newProduct = new Product(newProductId, "测试商品2号", "大家好啊，我是测试商品2号，给大家一些好看的东西",
				newPrice, newPublisherId, Product.Type.item, Product.Status.unsold, now, now);

		// Act
		Integer returnNum = productController.updateProduct(newProduct);
		Product foundProduct = productController.queryProductById(newProduct.getProductId());

		// Assert
		assertNotEquals(0, returnNum, "返回值不应为0");
		assertEquals(newProduct.getProductId(), foundProduct.getProductId(), "ID应匹配");
		assertEquals(newProduct.getPrice(), foundProduct.getPrice(), "价格应更新");
		assertEquals(newProduct.getPublisherId(), foundProduct.getPublisherId(), "发布者ID应更新");
		assertNotEquals(testProduct.getUpdateTime(), foundProduct.getUpdateTime(), "更新时间应更新");
	}

	@Test
	void testDelete() {
		// Arrange
		Long toBeDeletedId = testProduct.getProductId();

		// Act
		Integer returnNum = productController.removeProduct(toBeDeletedId);
		Product foundProduct = productController.queryProductById(toBeDeletedId);

		// Assert
		assertEquals(1, returnNum, "返回值应为1");
		assertEquals(null, foundProduct, "查询结果应为空");
	}
}
