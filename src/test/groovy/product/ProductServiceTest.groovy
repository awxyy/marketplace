package product

import com.dotdot.marketplace.product.dto.ProductRequestDto
import com.dotdot.marketplace.product.entity.Product
import com.dotdot.marketplace.product.entity.ProductStatus
import com.dotdot.marketplace.product.repository.ProductRepository
import com.dotdot.marketplace.product.service.ProductServiceImpl
import com.dotdot.marketplace.user.entity.User
import com.dotdot.marketplace.user.repository.UserRepository
import org.modelmapper.ModelMapper
import spock.lang.Specification

import java.time.LocalDateTime

class ProductServiceTest extends Specification {
    def productRepository = Mock(ProductRepository)
    def userRepository = Mock(UserRepository)
    def modelMapper = new ModelMapper()

    def productService = new ProductServiceImpl(productRepository, userRepository, modelMapper)


    def "testCreateProduct"() {
        given:
        def request = new ProductRequestDto(
                name: "Test Product",
                description: "This is a test product",
                price: 19.99,
                sellerId: 1
        )
        def seller = new User()
        seller.id = 1


        userRepository.findById(1) >> Optional.of(seller)
        productRepository.save(_ as Product) >> { Product p ->
            p.id = 1
            return p
        }

        when:
        def result = productService.create(request)

        then:
        result.id == 1
        result.name == "Test Product"
        result.description == "This is a test product"
        result.price == 19.99d
        result.sellerId == 1
        result.status == "AVAILABLE"
        result.createdAt != null
    }

    def "create should throw exception if seller not found"() {
        given:
        def request = new ProductRequestDto(
                name: "Test Product",
                description: "This is a test product",
                price: 19.99,
                sellerId: 99
        )
        userRepository.findById(99) >> Optional.empty()

        when:
        productService.create(request)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Seller with ID 99 does not exist"
    }

    def "getById returns product when product exists"() {
        given:
        def productId = 1
        def product = new Product(
                id: 1,
                name: "Existing Product",
                description: "Product description",
                price: 100.0,
                seller: new User(id: 1),
                status: ProductStatus.AVAILABLE,
                createdAt: LocalDateTime.of(2023, 10, 1, 12, 0)
        )
        productRepository.findById(productId) >> Optional.of(product)
        when:
        def result = productService.getById(productId)

        then:
        result.id == 1
        result.name == "Existing Product"
        result.description == "Product description"
        result.price == 100.0d
        result.sellerId == 1
        result.status == "AVAILABLE"
        result.createdAt == LocalDateTime.of(2023, 10, 1, 12, 0)
    }

    def "getById throws exception when product does not exist"() {
        given:
        def productId = 999L
        productRepository.findById(productId) >> Optional.empty()

        when:
        productService.getById(productId)

        then:
        def e = thrown(jakarta.persistence.EntityNotFoundException)
        e.message == "Product with ID 999 not found"
    }

    def "getAll returns list of ProductResponseDto correctly mapped from repository"() {
        given:
        def product1 = new Product()
        product1.id = 1
        product1.name = "Product 1"
        product1.description = "Description 1"
        product1.price = 100.0
        product1.seller = new User(id: 1)
        product1.status = ProductStatus.AVAILABLE
        product1.createdAt = LocalDateTime.of(2023, 10, 1, 12, 0)

        def product2 = new Product()
        product2.id = 2
        product2.name = "Product 2"
        product2.description = "Description 2"
        product2.price = 200.0
        product2.seller = new User(id: 2)
        product2.status = ProductStatus.AVAILABLE
        product2.createdAt = LocalDateTime.of(2023, 10, 2, 12, 0)

        productRepository.findAll() >> [product1, product2]

        when:
        def result = productService.getAll()

        then:
        result.size() == 2
        result[0].id == 1
        result[0].name == "Product 1"
        result[0].description == "Description 1"
        result[0].price == 100.0d
        result[0].sellerId == 1
        result[0].status == "AVAILABLE"
        result[0].createdAt == LocalDateTime.of(2023, 10, 1, 12, 0)

        result[1].id == 2
        result[1].name == "Product 2"
        result[1].description == "Description 2"
        result[1].price == 200.0d
        result[1].sellerId == 2
        result[1].status == "AVAILABLE"
        result[1].createdAt == LocalDateTime.of(2023, 10, 2, 12, 0)
    }

    def "getAll returns empty list when repository returns nothing"() {
        given:
        productRepository.findAll() >> []

        when:
        def result = productService.getAll()

        then:
        result.isEmpty()
    }

    def "updateProduct updates product details correctly"() {
        given:
        def productId = 1
        def existingProduct = new Product()
        existingProduct.id = productId
        existingProduct.name = "Product 1"
        existingProduct.description = "Description 1"
        existingProduct.price = 50.0
        existingProduct.seller = new User(id: 1)
        existingProduct.status = ProductStatus.AVAILABLE
        existingProduct.createdAt = LocalDateTime.of(2023, 10, 1, 12, 0)

        productRepository.findById(productId) >> Optional.of(existingProduct)

        def newSeller = new User(id: 2)
        userRepository.findById(2) >> Optional.of(newSeller)

        def updateRequest = new ProductRequestDto()
        updateRequest.name = "Updated Product"
        updateRequest.description = "Updated Description"
        updateRequest.price = 75.0
        updateRequest.sellerId = 2

        productRepository.save(_ as Product) >> { it[0] }
        when:
        def updatedProduct = productService.update(productId, updateRequest)

        then:
        updatedProduct.id == productId
        updatedProduct.name == "Updated Product"
        updatedProduct.description == "Updated Description"
        updatedProduct.price == 75.0d
        updatedProduct.sellerId == 2
        updatedProduct.status == "AVAILABLE"
    }

    def "updateProduct throws exception when product does not exist"() {
        def productId = 999
        def updateRequest = new ProductRequestDto()
        updateRequest.name = "Updated Product"
        updateRequest.description = "Updated Description"
        updateRequest.price = 75.0
        updateRequest.sellerId = 2
        productRepository.findById(productId) >> Optional.empty()
        when:
        productService.update(productId, updateRequest)

        then:
        def e = thrown(jakarta.persistence.EntityNotFoundException)
        e.message == "Product with ID 999 not found"
    }

    def "updateProduct throws exception when seller does not exist"() {
        given:
        def productId = 1
        def existingProduct = new Product()
        existingProduct.id = productId
        existingProduct.name = "Product 1"
        existingProduct.description = "Description 1"
        existingProduct.price = 50.0
        existingProduct.seller = new User(id: 1)
        existingProduct.status = ProductStatus.AVAILABLE
        existingProduct.createdAt = LocalDateTime.of(2023, 10, 1, 12, 0)

        productRepository.findById(productId) >> Optional.of(existingProduct)
        userRepository.findById(99) >> Optional.empty()

        def updateRequest = new ProductRequestDto()
        updateRequest.name = "Updated Product"
        updateRequest.description = "Updated Description"
        updateRequest.price = 75.0
        updateRequest.sellerId = 99

        when:
        productService.update(productId, updateRequest)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Seller with ID 99 does not exist"
    }

    def "deleteProduct deletes product when it exists"() {
        given:
        def productId = 1
        def existingProduct = new Product()
        existingProduct.id = productId
        existingProduct.name = "Product 1"
        existingProduct.description = "Description 1"
        existingProduct.price = 50.0
        existingProduct.seller = new User(id: 1)
        existingProduct.status = ProductStatus.AVAILABLE
        existingProduct.createdAt = LocalDateTime.of(2023, 10, 1, 12, 0)

        productRepository.findById(productId) >> Optional.of(existingProduct)

        when:
        productService.delete(productId)

        then:
        1 * productRepository.delete(existingProduct)
    }

    def "deleteProduct throws exception when product does not exist"() {
        given:
        def productId = 999
        productRepository.findById(productId) >> Optional.empty()

        when:
        productService.delete(productId)

        then:
        def e = thrown(jakarta.persistence.EntityNotFoundException)
        e.message == "Product with ID 999 not found"
    }

}