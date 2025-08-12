package user

import com.dotdot.marketplace.exception.UserNotFoundException
import com.dotdot.marketplace.user.dto.UserRequestDto
import com.dotdot.marketplace.user.entity.User
import com.dotdot.marketplace.user.repository.UserRepository
import com.dotdot.marketplace.user.service.UserServiceImpl
import org.modelmapper.ModelMapper
import spock.lang.Specification

class UserServiceTest extends Specification {
    def userRepository = Mock(UserRepository)
    def modelMapper = new ModelMapper()

    def userService = new UserServiceImpl(userRepository, modelMapper)

    def "createUser should save user"() {
        given:
        def user = new UserRequestDto(
                login: "Test User",
                fullName: "123",
                password: "123"
        )

        userRepository.save(_ as User) >> { User u ->
            u.id = 1
            return u
        }

        when:
        def result = userService.createUser(user)
        then:
        result.id == 1
        result.login == "Test User"
        result.fullName == "123"
    }

    def "createUser should throw exception if user already exists"() {
        given:
        def user = new UserRequestDto(
                login: "Test User",
                fullName: "123",
                password: "123"
        )

        userRepository.findByLogin("Test User") >> Optional.of(new User())

        when:
        userService.createUser(user)

        then:
        thrown(IllegalArgumentException)
    }

    def "getUserById should return user"() {
        given:
        def user = new User()
        user.id = 1
        user.login = "Test User"
        user.fullName = "123"

        userRepository.findById(1) >> Optional.of(user)

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
        def userId = 1
        def userRequest = new UserRequestDto(
                login: "Updated User",
                fullName: "Updated Full Name",
                password: "newpassword"
        )

        def existingUser = new User()
        existingUser.id = userId
        existingUser.login = "Old User"
        existingUser.fullName = "Old Full Name"

        userRepository.findById(userId) >> Optional.of(existingUser)
        userRepository.save(_ as User) >> { User u ->
            u.id = userId
            return u
        }

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
        ex.message == "Password must not be empty"

        where:
        badPassword << [null, "", "   "]
    }

}
