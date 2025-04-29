package com.stream.video.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.stream.video.entity.Video;

public interface VideoService {

//	save video
	Video saveVideo(Video video, MultipartFile file) throws IOException;
	
//	get video by id
	Video getVideo(String videoId);
	
	
//	get video by title
	Video getVideoByTitle(String title);
	
//	get all video
	List<Video> getAllVideo();
}
