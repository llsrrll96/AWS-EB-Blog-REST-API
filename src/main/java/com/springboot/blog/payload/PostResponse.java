package com.springboot.blog.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse 
{
	private List<PostDto> content;
	private int pageNo;
	private int pageSize;
	private long  totalElements;
	private int totalPages;
	private boolean last;
}
