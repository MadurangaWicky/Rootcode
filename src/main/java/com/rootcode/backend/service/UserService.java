package com.rootcode.backend.service;

import com.rootcode.backend.dto.request.UserAuthRequestDTO;
import com.rootcode.backend.dto.request.UserUpdateRequestDTO;
import com.rootcode.backend.entity.User;
import com.rootcode.backend.exception.CustomException;
import com.rootcode.backend.repository.UserRepository;
import com.rootcode.backend.utility.JwtUtil;
import com.rootcode.backend.utility.errorcodes.CommonErrorCodes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service

public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);

    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void saveUser(UserAuthRequestDTO userAuthRequestDTO, HttpServletResponse response) {
        try {
            logger.info("Save user for the user: {}", userAuthRequestDTO.getUsername());
            Optional<User> existingUser = userRepository.findByUsername(userAuthRequestDTO.getUsername());
            if (existingUser.isPresent()) {
                throw new CustomException(CommonErrorCodes.USER_ALREADY_EXISTS_ERROR_CODE, "User already exists for the username");
            }

            String hashedPassword = encoder.encode(userAuthRequestDTO.getPassword());

            User user = new User();
            user.setUsername(userAuthRequestDTO.getUsername());
            user.setPassword(hashedPassword);
            User savedUser = userRepository.save(user);

            Map<String, Object> claims = Map.of("id", savedUser.getId(), "username", savedUser.getUsername());
            String accessToken = jwtUtil.generateAccessToken(claims, savedUser.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(claims, savedUser.getUsername());

            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 60);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setHttpOnly(true);

            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setHttpOnly(true);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }


    public void userLogin(UserAuthRequestDTO userAuthRequestDTO, HttpServletResponse response) {
        try {
            logger.info("User logging in");
            Optional<User> user = userRepository.findByUsername(userAuthRequestDTO.getUsername());
            if (user.isEmpty()) {
                throw new CustomException(CommonErrorCodes.USER_NOT_FOUND_ERROR_CODE, "User not found");
            }
            boolean isPasswordValid = encoder.matches(userAuthRequestDTO.getPassword(), user.get().getPassword());
            if (!isPasswordValid) {
                throw new CustomException(CommonErrorCodes.PASSWORD_DOES_NOT_MATCH, "Password is incorrect");
            }

            Map<String, Object> claims = Map.of("id", user.get().getId(), "username", user.get().getUsername());
            String accessToken = jwtUtil.generateAccessToken(claims, user.get().getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(claims, user.get().getUsername());

            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 60);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setHttpOnly(true);

            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setHttpOnly(true);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
        }
        catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

    }


    public void userLogout(HttpServletResponse response) {
        try {
            logger.info("User logging out");
            Cookie accessTokenCookie = new Cookie("accessToken", null);
            accessTokenCookie.setMaxAge(0);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);

            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
        } catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    public User updateUser(UserUpdateRequestDTO userUpdateRequestDTO, User user) {
        try {
            logger.info("Update user for the user: {}", user.getUsername());
            Optional<User> existingUserOptional = userRepository.findByUsername(user.getUsername());
            if (existingUserOptional.isEmpty()) {
                throw new CustomException(CommonErrorCodes.USER_NOT_FOUND_ERROR_CODE, "User not found");
            }
            User existingUser = existingUserOptional.get();
            existingUser.setName(userUpdateRequestDTO.getName());
            userRepository.save(existingUser);
            updateUserCache(existingUser);
            return existingUser;
        }
        catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    public void deleteUser(String username, HttpServletResponse response) {
        try {
            logger.info("Delete user for the user: {}", username);
            Optional<User> existingUserOptional = userRepository.findByUsername(username);
            if (existingUserOptional.isEmpty()) {
                throw new CustomException(CommonErrorCodes.USER_NOT_FOUND_ERROR_CODE, "User not found");
            }
            User existingUser = existingUserOptional.get();
            userRepository.delete(existingUser);
            deleteUserCache(username);

            Cookie accessTokenCookie = new Cookie("accessToken", null);
            accessTokenCookie.setMaxAge(0);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);

            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
        }
        catch (CustomException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error("Error deleting user: {}", username, e);
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }


    @Cacheable(value = "userCache", key = "#username")
    public User getUserByUsername(String username) {
        logger.info("Set cache for the user: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(CommonErrorCodes.USER_NOT_FOUND_ERROR_CODE, "User not found"));
    }

    @CachePut(value = "userCache", key = "#updatedUser.username")
    public User updateUserCache(User updatedUser) {
        logger.info("Update cache for the user: {}", updatedUser.getUsername());
        return updatedUser;
    }

    @CacheEvict(value = "userCache", key = "#username")
    public void deleteUserCache(String username) {
        logger.info("Delete cache for the user: {}", username);
    }



    public Page<User> getAllUsers(int page, int size, String sortDirection) {
        try {
            logger.info("Get all users");
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by("username").descending()
                    : Sort.by("username").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return userRepository.findAll(pageable);
        }
        catch (Exception e) {
            throw new CustomException(CommonErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

}
