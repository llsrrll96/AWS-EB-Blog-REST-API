package com.springboot.blog.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostDtoV2;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.security.JwtAuthenticationFilter;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.PostService;
import com.springboot.blog.utils.AppConstants;

@RestController
@RequestMapping()
public class PostController 
{
	private PostService postService;
	
	@Autowired
	private JwtTokenProvider tokenProvider;

	// if you are configuring a class as a spring bean and it has only one constructor,
	// then we can omit @Autowired annotation
	public PostController(PostService postService) {
		this.postService = postService;
	}
	
//	@GetMapping
//	public List<PostDto> getAllPosts() {
//		return postService.getAllPosts();
//	}
	
	// apply pagenation
	@GetMapping("/api/v1/posts")
	public PostResponse getAllPosts(
			@RequestParam(defaultValue=AppConstants.DEFAULT_PAGE_NUMBER, required=false)int pageNo,
			@RequestParam(defaultValue=AppConstants.DEFAULT_PAGE_SIZE, required=false)int pageSize,
			@RequestParam(defaultValue=AppConstants.DEFAULT_SORT_BY, required=false)String sortBy,
			@RequestParam(defaultValue=AppConstants.DEFAULT_SORT_DIRECTION, required =false) String sortDir,
			@RequestHeader("Authorization") String jwtToken
			) {
		String username= tokenProvider.getUsernameFromJWT( jwtToken.substring(7, jwtToken.length()));
		System.out.println(":: "+ username);
	
		return postService.getAllPosts(pageNo,pageSize, sortBy,sortDir);
	}
	
	@PostMapping("/api/v1/posts")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto){
		return new ResponseEntity<>(postService.createPost(postDto),HttpStatus.CREATED);
	}
	
	// get post by id
	@GetMapping(value= "/api/v1/posts/{id}")
	public ResponseEntity<PostDto> getPostByIdV1(@PathVariable(name= "id") long id){
		return ResponseEntity.ok(postService.getPostById(id));
	}
	
	// get post by id
	@GetMapping(value= "/api/v2/posts/{id}",produces="application/vnd.springcom.v1+json")
	public ResponseEntity<PostDtoV2> getPostByIdV2(@PathVariable(name= "id") long id){
		PostDto postDto = postService.getPostById(id);
		PostDtoV2 postDtoV2= new PostDtoV2();
		postDtoV2.setId(postDto.getId());
		postDtoV2.setTitle(postDto.getTitle());
		postDtoV2.setDescription(postDto.getDescription());
		postDtoV2.setContent(postDto.getContent());
		
		List<String> tags =new ArrayList<String>();
		tags.add("java");
		tags.add("SpringBoot");
		tags.add("aws");
		postDtoV2.setTags(tags);
		
		return ResponseEntity.ok(postDtoV2);
	}
	
	// update post by id rest api
	@PutMapping("/api/v1/posts/{id}")
	public ResponseEntity<PostDto> updatePost(@Valid @RequestBody PostDto postDto, @PathVariable(name="id") long id){
		PostDto postResponse = postService.updatePost(postDto, id);
		return new ResponseEntity<>(postResponse,HttpStatus.OK);
		
	}
	
	// delete post by id rest api
	@DeleteMapping("/api/v1/posts/{id}")
	public ResponseEntity<String> deletePost(@PathVariable Long id)
	{
		postService.deletePostById(id);
		return new ResponseEntity<>("삭제 완료",HttpStatus.OK);
	}

}
