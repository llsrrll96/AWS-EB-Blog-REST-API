package com.springboot.blog.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.SignUpDto;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtAuthenticationEntryPoint;
import com.springboot.blog.security.JwtTokenProvider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value= "Auth controller exposes sinin and signup Rest APIs")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController 
{
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder  passwordEncoder;
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	
	@ApiOperation(value="REST API to Register or Signup user to Blog app",
			httpMethod="POST")
	@PostMapping("/signin")
	public ResponseEntity<JWTAuthResponse> authnticateUser(@RequestBody LoginDto loginDto)
	{
		Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				loginDto.getUsernameOrEmail()	, loginDto.getPassword()	));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		// get Token form tokenProvider
		String token= tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JWTAuthResponse(token));
	}
	
	@ApiOperation(value="REST API to Signin or Login user to Blog app")
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto)
	{
		// add check for username exists in a DB
		if(userRepository.existsByUsername(signUpDto.getUsername())) {
			return new ResponseEntity<>("유저 이름이 존재합니다.",HttpStatus.BAD_REQUEST);
		}
		
		// add check for email exists in DB
		if(userRepository.existsByEmail(signUpDto.getEmail())) {
			return new ResponseEntity<>("이메일이 존재합니다.",HttpStatus.BAD_REQUEST);
		}
		
		// create user Entity
		User user= new User();
		user.setName(signUpDto.getName());
		user.setUsername(signUpDto.getUsername());
		user.setEmail(signUpDto.getEmail());
		
		Role role=  null;
		if(signUpDto.getPassword().equals("admin1234!@")) {
			role= roleRepository.findByName("ROLE_ADMIN").get();
		}else {
			role= roleRepository.findByName("ROLE_USER").get();
		}
		user.setRoles(Collections.singleton(role)); // 리스트에 하나의 객체를 set 타입으로 삽입할 경우
		
		user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

		userRepository.save(user);
		
		return new ResponseEntity<String>("회원가입 성공!",HttpStatus.OK);
	}
}
