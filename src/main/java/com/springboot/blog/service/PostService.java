package com.springboot.blog.service;

import java.util.List;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;

public interface PostService 
{
	public PostDto createPost(PostDto postDto);
	
//	public List<PostDto>getAllPosts();
	public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
	
	public PostDto getPostById(Long id);
	
	public PostDto updatePost(PostDto postDto, Long id);
	
	public void deletePostById(Long id);
}
