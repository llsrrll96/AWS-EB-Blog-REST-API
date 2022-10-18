package com.springboot.blog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;

@Service
public class PostServiceImpl implements PostService
{
	private PostRepository postRepository;
	private ModelMapper mapper;
	
	// inject
	public PostServiceImpl(PostRepository postRepository, ModelMapper mapper) {
		this.postRepository = postRepository;
		this.mapper  = mapper;
	}

	
	@Override
	public PostDto createPost(PostDto postDto) {
		
		// convert DTO to entity
		Post post = mapToEntity(postDto);
		
		Post newPost  =postRepository.save(post);
		
		// convert entity to DTO
		PostDto postResponse = mapToDTO(newPost);
		
		return postResponse;
	}

//	@Override
//	public List<PostDto> getAllPosts() {
//		List<Post> posts= postRepository.findAll();
//		return posts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
//	}
	@Override
	public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) 
	{
		Sort sort= sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
																															Sort.by(sortBy).descending();
		Pageable pageable =PageRequest.of(pageNo, pageSize, sort);
		
		Page<Post> posts= postRepository.findAll(pageable);
		
		// get content for page object
		List<Post> listOfPosts= posts.getContent();
		
		List<PostDto> contents= listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
		
		PostResponse postResponse= PostResponse.builder()
																.content(contents)
																.pageNo(posts.getNumber())
																.pageSize(posts.getSize())
																.totalElements(posts.getTotalElements())
																.totalPages(posts.getTotalPages())
																.last(posts.isLast())
																.build();
		return postResponse;
	}
	
	
	// convert DTO to entity
	private Post mapToEntity(PostDto postDto)
	{
		Post post = mapper.map(postDto, Post.class);
		
//		Post post = new Post();
//		post.setTitle(postDto.getTitle());
//		post.setDescription(postDto.getDescription());
//		post.setContent(postDto.getContent());
		return post;
	}
	
	// convert entity to DTO
	private PostDto mapToDTO(Post post)
	{
		PostDto postdto =  mapper.map(post, PostDto.class);
		
//		PostDto postdto = new PostDto();
//		postdto.setId(post.getId());
//		postdto.setTitle(post.getTitle());
//		postdto.setDescription(post.getDescription());
//		postdto.setContent(post.getContent());
		return postdto;
	}

	@Override
	public PostDto getPostById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post", "id", id));
		return mapToDTO(post);
	}

	@Override
	public PostDto updatePost(PostDto postDto, Long id) {
		
		Post post = postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post", "id", id));
		post.setTitle(postDto.getTitle());
		post.setDescription(postDto.getDescription());
		post.setContent(postDto.getContent());
		
		return mapToDTO(postRepository.save(post));
	}

	@Override
	public void deletePostById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post", "id", id));
		postRepository.delete(post);
	}

}
