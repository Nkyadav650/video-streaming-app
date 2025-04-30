package com.stream.video.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
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
	        @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

	    Video video = videoService.getVideo(videoId);
	    Path videoPath = Paths.get(video.getVideoPath());

	    long fileSize = Files.size(videoPath);
	    String contentType = video.getContentType() != null ? video.getContentType() : "application/octet-stream";

	    long start = 0;
	    long end = fileSize - 1;

	    if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
	        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
	        start = Long.parseLong(ranges[0]);
	        if (ranges.length > 1 && !ranges[1].isEmpty()) {
	            end = Long.parseLong(ranges[1]);
	        }
	    }

	    if (end >= fileSize) end = fileSize - 1;

	    long contentLength = end - start + 1;

	    InputStream inputStream = Files.newInputStream(videoPath);
	    inputStream.skip(start);

	    HttpHeaders headers = new HttpHeaders();
	    headers.set(HttpHeaders.CONTENT_TYPE, contentType);
	    headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
	    headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
	    headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);

	    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
	            .headers(headers)
	            .body(new InputStreamResource(inputStream));
	}


//	 get all Videos
	@GetMapping("/get-all")
	public List<Video> getAll() {
		return videoService.getAllVideo();
	}
}