package com.stream.video.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stream.video.entity.Video;
import com.stream.video.payload.CustomMessage;
import com.stream.video.service.VideoService;
import com.stream.video.serviceimpl.AppConstants;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/videos")
@AllArgsConstructor
@CrossOrigin("*")
@Slf4j
public class VideoController {

	private final VideoService videoService;

//	video upload
	@PostMapping("/add")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
			@RequestParam("videoTitle") String videoTitle, @RequestParam("description") String description)
			throws IOException {

		Video video = Video.builder().videoTitle(videoTitle).description(description)
				.videoId(UUID.randomUUID().toString()).build();

		Video savedVideo = videoService.saveVideo(video, file);
		if (savedVideo != null) {
			return ResponseEntity.status(HttpStatus.OK).body(video);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(CustomMessage.builder().message("Video not uploaded!").success(false).build());
		}

	}

//	video stream
	@GetMapping("/stream/{videoId}")
	public ResponseEntity<Resource> stream(@PathVariable String videoId) {
		Video video = videoService.getVideo(videoId);

		String contentType = video.getContentType();
		String path = video.getVideoPath();

		Resource resource = new FileSystemResource(path);
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
	}

//	video stream
	@GetMapping("/stream/range/{videoId}")
	public ResponseEntity<Resource> streamVideoRange(@PathVariable String videoId,
			@RequestHeader(value = "Range", required = false) String range) throws IOException {

		log.info("range :{}", range);
		Video video = videoService.getVideo(videoId);
		Path videoPath = Paths.get(video.getVideoPath());

		Resource resource = new FileSystemResource(videoPath);
//	    file length
		long fileLength = videoPath.toFile().length();

		String contentType = video.getContentType() != null ? video.getContentType() : "application/octet-stream";

		if (range == null) {
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
		}

		long rangeStart;
		long rangeEnd;

		String[] ranges = range.replace("bytes=", "").split("-");
		rangeStart = Long.parseLong(ranges[0]);
		log.info("rangeStart :{}", rangeStart);

		rangeEnd = rangeStart + AppConstants.CHUNK_SIZE - 1;

//		if (ranges.length > 1) {
//			rangeEnd = Long.parseLong(ranges[1]);
//		} else {
//			rangeEnd = fileLength - 1;
//			
//		}
//
		if (rangeEnd > fileLength - 1) {
			rangeEnd = fileLength - 1;
		}
		log.info("rangeEnd :{}", rangeEnd);
		InputStream inputStream;
		try {
			inputStream = Files.newInputStream(videoPath);
			inputStream.skip(rangeStart);

			long contentLength = rangeEnd - rangeStart + 1;
			log.info("the length of content :{}", contentLength);

			byte[] data = new byte[(int) contentLength];
			int read = inputStream.read(data, 0, data.length);
			log.info("read (number of bytes) :{}", read);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
			headers.add("Cache-Control", "no-cache, no-store, must revalidate");
			headers.add("pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("X-Content-Type-Options", "nosniff");
			headers.setContentLength(contentLength);
			inputStream.close();
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers)
					.contentType(MediaType.parseMediaType(contentType)).body(new ByteArrayResource(data));

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}

	}

//	 get all Videos
	@GetMapping("/get-all")
	public List<Video> getAll() {
		return videoService.getAllVideo();
	}
}