package com.rootcode.backend.controller;


import com.rootcode.backend.dto.request.GetBorrowedBooksRequestDTO;
import com.rootcode.backend.dto.request.UserAuthRequestDTO;
import com.rootcode.backend.dto.request.UserUpdateRequestDTO;
import com.rootcode.backend.dto.response.AuthSuccessDTO;
import com.rootcode.backend.dto.response.BorrowedBookResponseDTO;
import com.rootcode.backend.dto.response.StandardResponse;
import com.rootcode.backend.dto.response.UserResponseDTO;
import com.rootcode.backend.entity.User;
import com.rootcode.backend.service.BorrowedService;
import com.rootcode.backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final BorrowedService borrowedService;

    public UserController(UserService userService, BorrowedService borrowedService) {
        this.userService = userService;
        this.borrowedService = borrowedService;
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthSuccessDTO> registerUser(@Valid @RequestBody UserAuthRequestDTO userAuthRequestDTO, HttpServletResponse response)
        {
            userService.saveUser(userAuthRequestDTO, response);
            AuthSuccessDTO success = new AuthSuccessDTO(true, "User registered successfully");
            return ResponseEntity.ok().body(success);
        }

    @PostMapping("/login")
    public ResponseEntity<AuthSuccessDTO> loginUser(@Valid @RequestBody UserAuthRequestDTO userAuthRequestDTO, HttpServletResponse response){
        userService.userLogin(userAuthRequestDTO, response);
        AuthSuccessDTO success = new AuthSuccessDTO(true, "User logged in successfully");
        return ResponseEntity.ok().body(success);
    }

    @GetMapping("/logout")
    public ResponseEntity<AuthSuccessDTO> logoutUser(HttpServletResponse response){
        userService.userLogout(response);
        AuthSuccessDTO success = new AuthSuccessDTO(true, "User logged out successfully");
        return ResponseEntity.ok().body(success);
    }



    @PutMapping("/update")
    public ResponseEntity<AuthSuccessDTO> updateUser(@Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO, @AuthenticationPrincipal User user){
        User updatedUser = userService.updateUser(userUpdateRequestDTO, user);
        AuthSuccessDTO success = new AuthSuccessDTO(true, updatedUser);
        return ResponseEntity.ok().body(success);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<AuthSuccessDTO> deleteUser(@AuthenticationPrincipal User user, HttpServletResponse response){
        userService.deleteUser(user.getUsername(), response);
        AuthSuccessDTO success = new AuthSuccessDTO(true, "User deleted successfully");
        return ResponseEntity.ok().body(success);
    }


    @GetMapping("/all")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<User> userPage = userService.getAllUsers(page, size, sortDirection);

        Page<UserResponseDTO> dtoPage = userPage.map(user -> {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setId(user.getId());
            userResponseDTO.setUsername(user.getUsername());
            userResponseDTO.setName(user.getName());
            return userResponseDTO;
        });

        return ResponseEntity.ok(dtoPage);
    }


    //Borrowed books by the authentcated user
    @PostMapping("/borrowed-books")
    public ResponseEntity<StandardResponse> getUserBorrowedBooks(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid GetBorrowedBooksRequestDTO dto
    ) {
        StandardResponse response = new StandardResponse(true, borrowedService.getBorrowedBooksByUser(user, dto));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrowed-books/{userId}")
    public ResponseEntity<StandardResponse> getBorrowedBooksByUserId(
            @PathVariable Long userId,
            @Valid GetBorrowedBooksRequestDTO requestDTO
    ) {
        Page<BorrowedBookResponseDTO> result = borrowedService.getBorrowedBooksByUserId(userId, requestDTO);
        StandardResponse response = new StandardResponse(true, result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/test2")
    public String test2() {
        return "test2";
    }

}
