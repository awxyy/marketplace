package order

import com.dotdot.marketplace.order.dto.OrderRequestDto
import com.dotdot.marketplace.order.entity.Order
import com.dotdot.marketplace.order.entity.OrderStatus
import com.dotdot.marketplace.order.repository.OrderRepository
import com.dotdot.marketplace.order.service.OrderServiceImpl
import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto
import com.dotdot.marketplace.orderitem.entity.OrderItem
import com.dotdot.marketplace.product.entity.Product
import com.dotdot.marketplace.product.entity.ProductStatus
import com.dotdot.marketplace.product.repository.ProductRepository
import com.dotdot.marketplace.reservation.service.ReservationService
import com.dotdot.marketplace.user.entity.User
import com.dotdot.marketplace.user.repository.UserRepository
import org.modelmapper.ModelMapper
import spock.lang.Specification

import java.time.LocalDateTime

class OrderServiceTest extends Specification {
    OrderRepository orderRepository = Mock()
    UserRepository userRepository = Mock()
    ProductRepository productRepository = Mock()
    ReservationService reservationService = Mock()
    ModelMapper modelMapper = new ModelMapper()
    OrderServiceImpl orderService

    def setup() {
        orderService = new OrderServiceImpl(
                orderRepository,
                userRepository,
                productRepository,
                modelMapper,
                reservationService
        )
    }

    def "createOrder should create a new order"() {
        given:
        def userId = 1L
        def productId = 1L
        def user = new User(id: userId, login: "testuser")
        def product = new Product(
                id: productId,
                name: "Test Product",
                price: 100.0,
                quantity: 10,
                status: ProductStatus.AVAILABLE
        )

        def orderItemDto = new OrderItemRequestDto(
                productId: productId,
                quantity: 2
        )

        def requestDto = new OrderRequestDto(
                user: userId,
                status: OrderStatus.PENDING,
                orderItems: [orderItemDto]
        )

        userRepository.findById(userId) >> Optional.of(user)
        productRepository.findById(productId) >> Optional.of(product)
        orderRepository.save(_ as Order) >> { Order o ->
            o.id = 1L
            return o
        }

        when:
        def result = orderService.createOrder(requestDto)

        then:
        result.id == 1L
        result.totalPrice == 200.0
    }

    def "createOrder should throw exception if user not found"() {
        given:
        def orderRequest = new OrderRequestDto(
                user: 99L,
                status: OrderStatus.PENDING,
                orderItems: [
                        new OrderItemRequestDto(productId: 2L, quantity: 1)
                ]
        )

        userRepository.findById(99L) >> Optional.empty()

        when:
        orderService.createOrder(orderRequest)

        then:
        thrown(RuntimeException)
    }

    def "createOrder should throw exception if product not found"() {
        given:
        def userId = 1L
        def orderRequest = new OrderRequestDto(
                user: userId,
                status: OrderStatus.PENDING,
                orderItems: [
                        new OrderItemRequestDto(productId: 99L, quantity: 1)
                ]
        )
        userRepository.findById(userId) >> Optional.of(new User(id: userId))
        productRepository.findById(99L) >> Optional.empty()

        when:
        orderService.createOrder(orderRequest)

        then:
        thrown(RuntimeException)
    }

    def "createOrder should throw exception if order items are empty"() {
        given:
        def userId = 1L
        def orderRequest = new OrderRequestDto(
                user: userId,
                status: OrderStatus.PENDING,
                orderItems: []
        )
        userRepository.findById(userId) >> Optional.of(new User(id: userId))

        when:
        orderService.createOrder(orderRequest)

        then:
        thrown(IllegalArgumentException)
    }

    def "getOrderById should return order when it exists"() {
        given:
        def orderRequest = new Order(
                id: 1L,
                status: OrderStatus.PENDING,
                createdAt: LocalDateTime.now(),
                user: new User(id: 1L),
                orderItems: []
        )

        orderRepository.findById(1) >> Optional.of(orderRequest)

        when:
        def result = orderService.getOrderById(1L)

        then:
        result.id == 1l
        result.status == OrderStatus.PENDING
        result.userId == 1l
        result.orderItems.isEmpty()
    }

    def "getOrderById should throw exception if order does not exist"() {
        given:
        orderRepository.findById(99L) >> Optional.empty()

        when:
        orderService.getOrderById(99L)

        then:
        thrown(RuntimeException)
    }

    def "getAllOrders should return all orders"() {
        given:
        def order1 = new Order(
                id: 1L,
                status: OrderStatus.PENDING,
                createdAt: LocalDateTime.now(),
                user: new User(id: 1L),
                orderItems: [])
        def order2 = new Order(id: 2L,
                status: OrderStatus.COMPLETED,
                createdAt: LocalDateTime.now(),
                user: new User(id: 2L),
                orderItems: [])
        orderRepository.findAll() >> [order1, order2]

        when:
        def result = orderService.getAllOrders()

        then:
        result.size() == 2
        result[0].id == 1L
        result[1].id == 2L
    }

    def "updateOrderStatus should update order status"() {
        given:
        def orderId = 1L
        def existingOrder = new Order(id: orderId,
                status: OrderStatus.PENDING,
                createdAt: LocalDateTime.now(),
                user: new User(id: 1L),
                orderItems: []
        )
        orderRepository.findById(orderId) >> Optional.of(existingOrder)

        def updateOrder = new OrderRequestDto(
                status: OrderStatus.COMPLETED,
                user: 1L,
                orderItems: [
                        new OrderItemRequestDto(productId: 2L, quantity: 1)
                ]
        )
        def product = new Product(id: 2L)
        productRepository.findById(2L) >> Optional.of(product)

        orderRepository.save(existingOrder) >> existingOrder
        when:
        def result = orderService.updateOrder(updateOrder, orderId)

        then:
        result.id == orderId
        result.status == OrderStatus.COMPLETED
        result.userId == 1L
        result.orderItems.size() == 1
    }

    def "updateOrderStatus should throw exception if order does not exist"() {
        given:
        def orderId = 99L
        orderRepository.findById(orderId) >> Optional.empty()

        when:
        orderService.updateOrder(new OrderRequestDto(), orderId)

        then:
        thrown(RuntimeException)
    }

    def "updateOrderStatus should throw exception if product not found"() {
        given:
        def orderId = 1L
        def existingOrder = new Order(id: orderId,
                status: OrderStatus.PENDING,
                createdAt: LocalDateTime.now(),
                user: new User(id: 1L),
                orderItems: []
        )
        orderRepository.findById(orderId) >> Optional.of(existingOrder)
        def updateOrder = new OrderRequestDto(
                status: OrderStatus.COMPLETED,
                user: 1L,
                orderItems: [
                        new OrderItemRequestDto(productId: 99L, quantity: 1)
                ]
        )
        productRepository.findById(99L) >> Optional.empty()
        when:
        orderService.updateOrder(updateOrder, orderId)
        then:
        thrown(RuntimeException)
    }

    def "deleteOrder should delete an order"() {
        given:
        def orderId = 1L
        orderRepository.existsById(orderId) >> Optional.of(true)

        when:
        orderService.deleteOrderById(orderId)
        then:
        1 * orderRepository.deleteById(orderId)
    }

    def "deleteOrder should throw exception if order does not exist"() {
        given:
        def orderId = 99L
        orderRepository.existsById(orderId) >> false

        when:
        orderService.deleteOrderById(orderId)

        then:
        thrown(RuntimeException)
    }

    def "calculateTotalPrice should return the correct total price for an order"() {
        given:
        def orderItems = [
                new OrderItem(priceAtPurchase: 50.0, quantity: 2),
                new OrderItem(priceAtPurchase: 30.0, quantity: 1),
        ]

        when:
        def totalPrice = orderService.calculateTotalPrice(orderItems)

        then:
        totalPrice == 130.0d
    }

    def "calculateTotalPrice should throw IllegalArgumentException"() {
        given:
        def orderItems = []

        when:
        orderService.calculateTotalPrice(orderItems)

        then:
        thrown(IllegalArgumentException)
    }

    def "validateStatus should throw exception for null"() {
        given:
        def invalidStatus = null

        when:
        orderService.validateStatus(invalidStatus)

        then:
        thrown(IllegalArgumentException)
    }

    def "validateStatus should return without exception for valid status"() {
        given:
        def validStatus = OrderStatus.PENDING

        when:
        orderService.validateStatus(validStatus)

        then:
        noExceptionThrown()
    }

}