package com.springboot.blog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService
{
	private CommentRepository commentRepository;
	private PostRepository postRepository;
	
	private ModelMapper modelMapper;
	
	@Autowired
	public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper modelMapper) {
		super();
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
		this.modelMapper = modelMapper;
	}


	@Override
	public CommentDto createComment(long postId, CommentDto commentDto) {
		
		// retrieve post entity by id
		Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		
		// set Post to comment entity
		Comment comment = mapToEntity(commentDto);
		comment.setPost(post);

		// comment entity to DB
		Comment newComment = commentRepository.save(comment);
		
		return mapToDTO(newComment);
	}
	
	private CommentDto mapToDTO(Comment comment)
	{
		CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
//		CommentDto commentDto = new CommentDto();
//		commentDto.setId(comment.getId());
//		commentDto.setName(comment.getName());
//		commentDto.setEmail(comment.getEmail());
//		commentDto.setBody(comment.getBody());
		return commentDto;
	}
	private Comment mapToEntity(CommentDto commentDto)
	{
		Comment comment = modelMapper.map(commentDto, Comment.class);

//		Comment comment = new Comment();
//		comment.setId(commentDto.getId());
//		comment.setName(commentDto.getName());
//		comment.setEmail(commentDto.getEmail());
//		comment.setBody(commentDto.getBody());
		return comment;
	}


	@Override
	public List<CommentDto> getCommentsByPostId(long postId) 
	{
		List<Comment> comments = commentRepository.findByPostId(postId);
		
		return comments.stream().map(comment -> mapToDTO(comment)).collect(Collectors.toList());
	}


	@Override
	public CommentDto getCommentById(Long postId, Long commentId) {
		Post post = postRepository.findById(postId).orElseThrow(
				()-> new ResourceNotFoundException("Post", "id", postId));
		
		Comment comment =commentRepository.findById(commentId).orElseThrow(
				()-> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(!post.getId().equals(comment.getPost().getId())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST	, "Comment does not belong to post");
		}
		
		return mapToDTO(comment);
	}


	@Override
	@Transactional
	public CommentDto updateComment(Long postId, long commentId, CommentDto commentRequest) {
		
		Post post = postRepository.findById(postId).orElseThrow(
				()-> new ResourceNotFoundException("Post", "id", postId));
		
		Comment comment =commentRepository.findById(commentId).orElseThrow(
				()-> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(!post.getId().equals(comment.getPost().getId())){
			throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Comment does not belongs to post");
		}
		
		comment.setName(commentRequest.getName());
		comment.setEmail(commentRequest.getEmail());
		comment.setBody(commentRequest.getBody());
		
		return mapToDTO(comment);
	}


	@Override
	public void deleteComment(Long postId, long commentId) {
		Post post =  postRepository.findById(postId).orElseThrow(
				()-> new ResourceNotFoundException("Post", "id",postId	));
		
		Comment comment =commentRepository.findById(commentId).orElseThrow(
				()-> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(!post.getId().equals(comment.getPost().getId())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belongs to post");
		}
		
		commentRepository.delete(comment);
	}
}
