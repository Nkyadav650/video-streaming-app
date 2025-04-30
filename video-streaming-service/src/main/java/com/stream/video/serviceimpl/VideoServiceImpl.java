package com.stream.video.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.stream.video.entity.Video;
import com.stream.video.repository.VideoRepository;
import com.stream.video.service.VideoService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class VideoServiceImpl implements VideoService {

	@Value("${files.video}")
	String DIR;

	private final VideoRepository videoRepo;

	public VideoServiceImpl(VideoRepository videoRepo) {
		this.videoRepo = videoRepo;
	}

	@PostConstruct
	public void init() {
		File file = new File(DIR);
		if (!file.exists()) {
			file.mkdir();
			log.info("folder created!");
		} else {
			log.info("Folder already exist!");
		}
	}

	@Override
	public Video saveVideo(Video video, MultipartFile file) {
		try {
			String fileName = file.getOriginalFilename();
			String contentType = file.getContentType();
			log.info("Content type : {}", contentType);
			InputStream inputStream = file.getInputStream();

			String cleanFileName = StringUtils.cleanPath(fileName);
			String cleanFolder = StringUtils.cleanPath(DIR);

			Path path = Paths.get(cleanFolder, cleanFileName);

			log.info("file path : {}", path);

//		copy file to the folder
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

//		video metadata
			video.setContentType(contentType);
			video.setVideoPath(path.toString());

			return videoRepo.save(video);

		} catch (IOException e) {
			// TODO: handle exception
			return null;
		}

	}

	@Override
	public Video getVideo(String videoId) {
		// TODO Auto-generated method stub
		return videoRepo.findById(videoId).orElseThrow(()->new RuntimeException("Video is not available!"));
	}

	@Override
	public Video getVideoByTitle(String Title) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public List<Video> getAllVideo() {
		// TODO Auto-generated method stub
		return videoRepo.findAll();
	}

}
