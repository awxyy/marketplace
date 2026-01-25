package user

import com.dotdot.marketplace.exception.UserNotFoundException
import com.dotdot.marketplace.user.dto.UserRequestDto
import com.dotdot.marketplace.user.dto.UserResponseDto
import com.dotdot.marketplace.user.entity.User
import com.dotdot.marketplace.user.repository.UserRepository
import com.dotdot.marketplace.user.service.UserServiceImpl
import org.modelmapper.ModelMapper
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceTest extends Specification {
    UserRepository userRepository = Mock()
    ModelMapper modelMapper = Mock()
    PasswordEncoder passwordEncoder = Mock()
    UserServiceImpl userService

    def setup() {
        userService = new UserServiceImpl(userRepository, modelMapper, passwordEncoder)
    }

    def "createUser should save user"() {
        given:
        def requestDto = new UserRequestDto(
                login: "testuser",
                password: "123",
                fullName: "Test User"
        )
        def user = new User(id: 1L, login: "testuser", password: "encodedPassword")
        def responseDto = new UserResponseDto(id: 1L, login: "testuser")

        when:
        def result = userService.createUser(requestDto)

        then:
        1 * userRepository.existsByLogin("testuser") >> false
        1 * modelMapper.map(requestDto, User.class) >> user
        1 * userRepository.save(_ as User) >> user
        1 * modelMapper.map(user, UserResponseDto.class) >> responseDto

        result.id == 1L
        result.login == "testuser"
    }

    def "createUser should throw exception if user already exists"() {
        given:
        def userRequest = new UserRequestDto(
                login: "testuser",
                fullName: "Test User",
                password: "123"
        )

        when:
        userService.createUser(userRequest)

        then:
        1 * userRepository.existsByLogin("testuser") >> true
        0 * userRepository.save(_)
        thrown(RuntimeException)
    }

    def "createUser should throw exception for invalid password"() {
        given:
        def user = new UserRequestDto(
                login: "Test User",
                fullName: "123",
                password: badPassword
        )

        when:
        userService.createUser(user)

        then:
        thrown(IllegalArgumentException)
        0 * userRepository.save(_)

        where:
        badPassword << [null, "", "   "]
    }

    def "getUserById should return user"() {
        given:
        def user = new User(id: 1L, login: "Test User", fullName: "123")
        def responseDto = new UserResponseDto(id: 1L, login: "Test User", fullName: "123")

        userRepository.findById(1) >> Optional.of(user)
        modelMapper.map(user, UserResponseDto.class) >> responseDto

        when:
        def result = userService.getUserById(1)

        then:
        result.id == 1
        result.login == "Test User"
        result.fullName == "123"
    }

    def "getUserById  when doesn't exist"() {
        given:
        def userId = 9999l

        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.getUserById(userId)

        then:
        thrown(UserNotFoundException)

    }

    def "updateUser should update user"() {
        given:
        def userId = 1L
        def userRequest = new UserRequestDto(
                login: "Updated User",
                fullName: "Updated Full Name",
                password: "newpassword"
        )
        def existingUser = new User(id: userId, login: "Old User", fullName: "Old Full Name")
        def responseDto = new UserResponseDto(id: userId, login: "Updated User", fullName: "Updated Full Name")

        userRepository.findById(userId) >> Optional.of(existingUser)
        userRepository.save(_ as User) >> existingUser
        modelMapper.map(userRequest, existingUser) >> null // void method, just ensure it's called
        modelMapper.map(existingUser, UserResponseDto.class) >> responseDto

        when:
        def result = userService.updateUser(userId, userRequest)

        then:
        result.id == userId
        result.login == "Updated User"
        result.fullName == "Updated Full Name"
    }

    def "updateUser should throw exception if user does not exist"() {
        given:
        def userId = 9999l
        def userRequest = new UserRequestDto(
                login: "Updated User",
                fullName: "Updated Full Name",
                password: "newpassword"
        )

        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.updateUser(userId, userRequest)

        then:
        thrown(UserNotFoundException)
    }

    def "deleteUser should delete user"() {
        given:
        def userId = 1
        def user = new User()
        user.setId(userId)
        userRepository.existsById(userId) >> Optional.of(true)

        when:
        userService.deleteUser(userId)

        then:
        1 * userRepository.deleteById(userId)
    }

    def "deleteUser should throw exception if user does not exist"() {
        given:
        def userId = 9999l

        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.deleteUser(userId)

        then:
        thrown(UserNotFoundException)
    }

    def "validatePassword should return true for correct password"() {
        given:
        def request = new UserRequestDto(
                login: "John",
                fullName: "john@example.com",
                password: badPassword
        )

        when:
        userService.createUser(request)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message != null

        where:
        badPassword << [null, "", "   "]
    }

}
