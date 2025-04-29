package com.stream.video.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stream.video.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {

	
	Optional<Video> findByVideoTitle(String title);
	
//	query methods
	
//	native
	
//	criteria api
}
