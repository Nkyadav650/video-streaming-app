package com.stream.video.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "yt_course")
public class Course {

	@Id
	private String id;
	private String title;
//	@OneToMany(mappedBy = "course")
//	private List<Video> vedioList =new ArrayList<>();
}
