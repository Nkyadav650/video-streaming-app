package com.stream.video.serviceimpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.stream.video.entity.Video;
import com.stream.video.service.VideoService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

	@Value("${files.video}")
	String DIR;
	
	@Override
	public Video saveVideo(Video video, MultipartFile file) throws IOException {
		// TODO Auto-generated method stub
		String fileName = file.getOriginalFilename();
		String contentType= file.getContentType();
		InputStream inputStream = file.getInputStream();
		
		String cleanFileName = StringUtils.cleanPath(fileName);
		String cleanFolder = StringUtils.cleanPath(DIR);
		
		Path path = Paths.get(cleanFolder,cleanFileName);
		
		log.info("file path : {}",path);
		
		return null;
	}

	@Override
	public Video getVideo(String videoId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Video getVideoByTitle(String Title) {
		// TODO Auto-generated method stub
	
		return null;
	}

	@Override
	public List<Video> getAllVideo() {
		// TODO Auto-generated method stub
		return null;
	}

}
