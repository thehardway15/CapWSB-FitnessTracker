package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

    /**
     * Retrieves all users from the user service and maps them to a list of UserDto objects.
     *
     * @return  a list of UserDto objects representing all users
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all users from the user service and maps them to a list of UserSimpleDto objects.
     *
     * @return  a list of UserSimpleDto objects representing all users
     */
    @GetMapping("/simple")
    public List<UserSimpleDto> getAllUsersSimple() {
        return userService.findAllUsers()
                          .stream()
                          .map(userMapper::toSimpleDto)
                          .toList();
    }

    /**
     * Retrieves a user with the specified ID from the user service and maps it to a UserDto object.
     *
     * @param  id  the ID of the user to retrieve
     * @return     the UserDto object representing the user with the specified ID, or throws a ResponseStatusException if the user is not found
     */
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id)
                          .map(userMapper::toDto)
                          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found"));
    }

    /**
     * Adds a new user to the system.
     *
     * @param  userDto  the user data to be added, passed in the request body
     * @return          the created user object
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody UserDto userDto) throws InterruptedException {

        // Demonstracja how to use @RequestBody
        System.out.println("User with e-mail: " + userDto.email() + "passed to the request");

        return userService.createUser(userMapper.toEntity(userDto));
    }

    /**
     * Deletes a user with the specified ID from the user service.
     *
     * @param  id  the ID of the user to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        User user = userService.getUser(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found"));
        userService.deleteUser(user);
    }

    /**
     * Retrieves users with the specified email from the user service and maps them to a list of UserEmailDto objects.
     *
     * @param  email  the email address to search for in user emails
     * @return        a list of UserEmailDto objects representing the users with the specified email
     */
    @GetMapping("/email")
    public List<UserEmailDto> getUsersByEmail(@RequestParam String email) {
        return userService.findUsersByEmail(email)
                          .stream()
                          .map(userMapper::toEmailDto)
                          .toList();
    }


    /**
     * Retrieves a list of users who are older than the specified date.
     *
     * @param  time  the date in the format "yyyy-MM-dd" to compare the users' birthdates against
     * @return       a list of UserDto objects representing the users who are older than the specified date
     */
    @GetMapping("/older/{time}")
    public List<UserDto> getUserOlderThan(@PathVariable("time") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return userService.findUsersOlderThan(date)
                          .stream()
                          .map(userMapper::toDto)
                          .toList();
    }

    /**
     * Updates a user with the specified ID in the user service.
     *
     * @param  id       the ID of the user to update
     * @param  userDto  the updated user data, passed in the request body
     * @return          the updated user object
     */
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        userService.getUser(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found"));
        return userService.updateUser(userMapper.toEntityWithId(userDto, id));
    }
}