package recruit.jotang2025.info_manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import recruit.jotang2025.info_manager.controller.ProductController;
import recruit.jotang2025.info_manager.exception.ProductNotFoundException;
import recruit.jotang2025.info_manager.pojo.Product;

@SpringBootTest
@Disabled
@Transactional // 测试后数据库会进行回滚, 确保测试不会污染数据库
class ProductApplicationTests {

	@Autowired
	ProductController productController;

	Product testProduct;

	@BeforeEach // 在每个测试方法之前都运行一次, 为每个测试准备好测试数据
	public void init() {
		BigDecimal price = new BigDecimal("13.14");
		Long publisherId = 1L;
		LocalDateTime now = LocalDateTime.now();
		testProduct = new Product(null, "测试商品", "大家好啊，我是测试商品，给大家一些好看的东西",
				price, publisherId, Product.Type.item, null, now, now);
		productController.createProduct(testProduct);
	}

	// 测试的编写一般使用"AAA"规则编排, 即Arrange-Act-Assert
	@Test
	void testQueryAll() {
		// Arrange - 准备测试方法所用的数据, 这里已经准备好了

		// Act - 进行要测试的内容
		ResponseEntity<List<Product>> response = productController.queryAllProduct();
		List<Product> products = response.getBody();

		// Assert - 预测测试的结果
		assertNotNull(products, "查询结果不应为空");
		assertFalse(products.isEmpty(), "查询结果不应为空");
	}

	@Test
	void testQueryById() {
		// Arrange

		// Act
		ResponseEntity<Product> response = productController.queryProductById(testProduct.getProductId());
		Product foundProduct = response.getBody();

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
		LocalDateTime createTime = testProduct.getCreateTime();
		LocalDateTime updateTime = testProduct.getUpdateTime().plusDays(1);
		Product newProduct = new Product(newProductId , "测试商品2号", "大家好啊，我是测试商品2号，给大家一些好看的东西",
				newPrice, newPublisherId, Product.Type.item, Product.Status.unsold,
				createTime, updateTime);

		// Act
		productController.updateProduct(newProduct);
		ResponseEntity<Product> foundProduct = productController.queryProductById(newProduct.getProductId());

		// Assert
		assertThrows(
			IllegalArgumentException.class,
			() -> productController.updateProduct(null),
			"传入空指针时报错");
		assertNotNull(foundProduct, "更新后查询结果不应为空");
		assertNotEquals(updateTime, testProduct.getUpdateTime(), "更新时间应更新");

	}

	@Test
	void testDelete() {
		// Arrange
		Long toBeDeletedId = testProduct.getProductId();

		// Act
		productController.removeProduct(toBeDeletedId);

		// Assert
		assertThrowsExactly(
				ProductNotFoundException.class,
				() -> productController.removeProduct(666666L),
				"删除不存在的商品时报错");
		assertThrowsExactly(
			ProductNotFoundException.class, 
			() -> productController.queryProductById(toBeDeletedId), 
			"查询结果应为空时会报错");
	}

	@Test
	void testQueryByFilters() {
		// Arrange
		// 原本的数据内容
		// +------------+-----------------------+--------+--------------+---------+--------+---------------------+---------------------+
		// | product_id | product_name          | price  | publisher_id | type    | status | create_time         | update_time         |
		// +------------+-----------------------+--------+--------------+---------+--------+---------------------+---------------------+
		// |          1 | 爱因斯坦的大脑         |  10.00 |             1 | item    | unsold | 2025-09-21 11:33:57 | 2025-09-21 11:44:29 |
		// |          2 | 奶龙玩偶              |  114.51 |            2 | item    | unsold | 2025-09-22 11:45:34 | 2025-09-22 11:45:34 |
		// |          3 | 库里南碎片            |    0.01 |            3 | item    | sold   | 2025-09-23 11:46:39 | 2025-09-23 11:46:39 |
		// |         44 | 二教帮我签个到        |   40.00 |            3 | service | unsold | 2025-09-24 11:48:16 | 2025-09-24 11:48:16 |
		// +------------+-----------------------+--------+--------------+---------+--------+---------------------+---------------------+
		BigDecimal smallPrice = new BigDecimal("5.00");
		BigDecimal bigPrice = new BigDecimal("50.00");
		LocalDateTime startDateTime = LocalDateTime.now().minusDays(1);

		// Act 
		// 什么都不筛选 
		ResponseEntity<List<Product>> noFilter = productController.queryProductByFilters(null, null, null, null);
		List<Product> allProducts = noFilter.getBody();

		// type的筛选
		ResponseEntity<List<Product>> typeFilter = productController.queryProductByFilters("item", null, null, null);
		List<Product> typeProducts = typeFilter.getBody();

		// minPrice的筛选
		ResponseEntity<List<Product>> minPriceFilter = productController.queryProductByFilters(null, smallPrice, null, null);
		List<Product> minPriceProducts = minPriceFilter.getBody();

		// maxPrice的筛选
		ResponseEntity<List<Product>> maxPriceFilter = productController.queryProductByFilters(null, null, bigPrice, null);
		List<Product> maxPriceProducts = maxPriceFilter.getBody();

		// minPrice和maxPrice的筛选
		ResponseEntity<List<Product>> betweenPriceFilter = productController.queryProductByFilters(null, smallPrice, bigPrice, null);
		List<Product> betweenPriceProducts = betweenPriceFilter.getBody();

		// startTime的筛选
		ResponseEntity<List<Product>> startTimeFilter = productController.queryProductByFilters(null, null, null, startDateTime);
		List<Product> startTimeProducts = startTimeFilter.getBody();

		// Assert
		assertEquals(5, allProducts.size(), "无条件: 一共应有5个元素");
		assertEquals(4, typeProducts.size(), "type: 一共应有4个元素");
		assertEquals(4, minPriceProducts.size(), "minPrice: 一共应有4个元素");
		assertEquals(4, maxPriceProducts.size(), "maxPrice: 一共应有4个元素");
		assertEquals(3, betweenPriceProducts.size(), "betweenPrice: 一共应有3个元素");
		assertEquals(1, startTimeProducts.size(), "startTime: 一共应有1个元素");
		assertThrowsExactly(IllegalArgumentException.class, ()->productController.queryProductByFilters(null, bigPrice, smallPrice, null), "最小价格不应低于最大价格");

	}
}
