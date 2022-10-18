package com.springboot.blog.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
	private UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		System.out.println("usernameOrEmail: "+usernameOrEmail);
		User enUser= userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
					.orElseThrow(()-> new UsernameNotFoundException("User not found with username or email: "+ usernameOrEmail));

		// 시큐리티로 감싸서 반환
		return new org.springframework.security.core.userdetails.User(enUser.getUsername(), enUser.getPassword(), mapRolesToAuthorizities(enUser.getRoles()));
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorizities(Set<Role> roles){
		return roles.stream().map(role-> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

}
