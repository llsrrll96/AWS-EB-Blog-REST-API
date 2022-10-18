package com.springboot.blog;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.springboot.blog.entity.Role;
import com.springboot.blog.repository.RoleRepository;

@SpringBootApplication
public class BlogRestApiApplication implements CommandLineRunner {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BlogRestApiApplication.class, args);
		
		
		
	}
	/* 메타 데이터 테이블 작성 */
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	public void run(String... args) throws Exception {
		
		final String roleAdmin  ="ROLE_ADMIN";
		final String roleUser ="ROLE_USER";
		
		if(!roleRepository.existsRoleByName(roleAdmin))
		{
			Role adminRole =new Role();
			adminRole.setName(roleAdmin);
			roleRepository.save(adminRole);
		}
		if(!roleRepository.existsRoleByName(roleUser))
		{
			Role userRole =new Role();
			userRole.setName(roleUser);
			roleRepository.save(userRole);
		}
	}
}
