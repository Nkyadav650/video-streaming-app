package com.stream.video.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.video.service.VideoService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/video")
@AllArgsConstructor
public class VideoController {

	private final VideoService videoService;
	
	@PostMapping("/add")
	public void upload() {
		
	}
}
