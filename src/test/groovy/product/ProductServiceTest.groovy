package product

import com.dotdot.marketplace.exception.UnauthorizedException
import com.dotdot.marketplace.product.dto.ProductRequestDto
import com.dotdot.marketplace.product.entity.Product
import com.dotdot.marketplace.product.entity.ProductStatus
import com.dotdot.marketplace.product.repository.ProductRepository
import com.dotdot.marketplace.product.service.ProductServiceImpl
import com.dotdot.marketplace.review.service.ReviewService
import com.dotdot.marketplace.user.entity.User
import com.dotdot.marketplace.user.entity.UserRole
import com.dotdot.marketplace.user.repository.UserRepository
import com.dotdot.marketplace.user.security.UserDetailsServiceImpl
import com.dotdot.marketplace.user.security.UserPrincipal
import jakarta.persistence.EntityNotFoundException
import org.modelmapper.ModelMapper
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

import java.time.LocalDateTime

class ProductServiceTest extends Specification {
    ProductRepository productRepository = Mock()
    UserRepository userRepository = Mock()
    UserDetailsServiceImpl userDetailsService = Stub()
    ReviewService reviewService = Mock()
    ModelMapper modelMapper = new ModelMapper()
    ProductServiceImpl productService

    def setup() {
        productService = new ProductServiceImpl(
                productRepository,
                userRepository,
                modelMapper,
                userDetailsService,
                reviewService
        )
    }

    void mockLoggedInUser(Long userId) {
        def authentication = Mock(Authentication)
        def securityContext = Mock(SecurityContext)
        def userPrincipal = Mock(UserPrincipal)

        def mockUser = new User(id: userId)

        securityContext.getAuthentication() >> authentication
        authentication.getPrincipal() >> userPrincipal

        userPrincipal.getUser() >> mockUser
        userPrincipal.getId() >> userId

        SecurityContextHolder.setContext(securityContext)
    }

    def "testCreateProduct"() {
        given:
        def request = new ProductRequestDto(
                name: "Test Product",
                description: "This is a test product",
                price: 19.99,
                quantity: 10
        )
        def currentUser = new User(id: 1L)

        mockLoggedInUser(1L)
        userDetailsService.hasRole(*_) >> true
        userDetailsService.getCurrentUserId(*_) >> 1L

        userRepository.findById(1L) >> Optional.of(currentUser)

        productRepository.save(_ as Product) >> { Product p ->
            p.id = 1L
            return p
        }

        when:
        def result = productService.create(request)

        then:
        result.id == 1L
        result.name == "Test Product"
    }

    def "create should throw exception if seller not found"() {
        given:
        def request = new ProductRequestDto(
                name: "Test Product",
                description: "This is a test product",
                price: 19.99,
                quantity: 10
        )

        mockLoggedInUser(1L)
        userDetailsService.hasRole(*_) >> false

        when:
        productService.create(request)

        then:
        thrown(UnauthorizedException)
    }

    def "getById returns product when product exists"() {
        given:
        def productId = 1L
        def product = new Product(
                id: 1L,
                name: "Existing Product",
                description: "Product description",
                price: 100.0,
                seller: new User(id: 1L),
                status: ProductStatus.AVAILABLE,
                createdAt: LocalDateTime.of(2023, 10, 1, 12, 0)
        )
        productRepository.findById(productId) >> Optional.of(product)
        reviewService.getAverageRating(productId) >> 0.0
        reviewService.getReviewCount(productId) >> 0

        when:
        def result = productService.getById(productId)

        then:
        result.id == 1L
        result.name == "Existing Product"
    }

    def "getById throws exception when product does not exist"() {
        given:
        def productId = 999L
        productRepository.findById(productId) >> Optional.empty()

        when:
        productService.getById(productId)

        then:
        def e = thrown(EntityNotFoundException)
        e.message == "Product with ID 999 not found"
    }

    def "getAll returns list of ProductResponseDto correctly mapped from repository"() {
        given:
        def product1 = new Product(id: 1L, name: "Product 1", seller: new User(id: 1L))
        def product2 = new Product(id: 2L, name: "Product 2", seller: new User(id: 2L))

        productRepository.findAll() >> [product1, product2]

        when:
        def result = productService.getAll()

        then:
        result.size() == 2
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
        def productId = 1L
        def userId = 1L
        def currentUser = new User(id: userId)

        mockLoggedInUser(userId)
        userDetailsService.hasRole(*_) >> true

        def existingProduct = new Product(
                id: productId,
                name: "Product 1",
                price: 50.0,
                seller: currentUser,
                status: ProductStatus.AVAILABLE
        )
        productRepository.findById(productId) >> Optional.of(existingProduct)

        def updateRequest = new ProductRequestDto(
                name: "Updated Product",
                description: "Updated Description",
                price: 75.0
        )

        productRepository.save(_ as Product) >> { args -> args[0] }

        when:
        def updatedProduct = productService.update(productId, updateRequest)

        then:
        updatedProduct.id == productId
        updatedProduct.name == "Updated Product"
        updatedProduct.price == 75.0d
    }

    def "updateProduct throws exception when product does not exist"() {
        given:
        def productId = 999L
        def updateRequest = new ProductRequestDto(name: "Updated Product")
        def userId = 1L

        mockLoggedInUser(userId)
        userDetailsService.hasRole(*_) >> true

        productRepository.findById(productId) >> Optional.empty()

        when:
        productService.update(productId, updateRequest)

        then:
        thrown(EntityNotFoundException)
    }

    def "deleteProduct deletes product when it exists"() {
        given:
        def productId = 1L
        def userId = 1L
        def currentUser = new User(id: userId)

        mockLoggedInUser(userId)
        userDetailsService.hasRole(*_) >> true

        def existingProduct = new Product(
                id: productId,
                seller: currentUser
        )
        productRepository.findById(productId) >> Optional.of(existingProduct)

        when:
        productService.delete(productId)

        then:
        1 * productRepository.delete(existingProduct)
    }

    def "deleteProduct throws exception when product does not exist"() {
        given:
        def productId = 999L

        mockLoggedInUser(1L)
        userDetailsService.hasRole(*_) >> true

        productRepository.findById(productId) >> Optional.empty()

        when:
        productService.delete(productId)

        then:
        def e = thrown(EntityNotFoundException)
        e.message == "Product with ID 999 not found"
    }
}