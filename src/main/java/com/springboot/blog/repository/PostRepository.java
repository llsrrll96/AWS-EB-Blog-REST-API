package com.springboot.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.blog.entity.Post;

//We don't need to @Repository annotation to this interface 
//because the JpaRepository interface has an implementation
//SimpleJpaRepository internally annotated with @Repository annotation and @Transactional
public interface PostRepository extends JpaRepository<Post, Long>
{
	

}
