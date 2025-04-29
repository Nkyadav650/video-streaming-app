package com.stream.video.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "yt_video")
public class Video {

	@Id
	private String videoId;
	private String discription;
	private String videoTitle;
	private String videoPath;
	private String contentType;
//	@ManyToOne
//	private Course course;

}
