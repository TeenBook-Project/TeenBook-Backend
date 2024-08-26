package com.contest.rest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contest.rest.domain.dto.Attendance_infoDTO;
import com.contest.rest.domain.dto.BookmarkDTO;
import com.contest.rest.domain.dto.UserDTO;
import com.contest.rest.service.Attendance_infoService;
import com.contest.rest.service.BookmarkService;
import com.contest.rest.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/bookmark")
public class BookmarkController {
	/* 
	Create : 데이터 생성(POST)
	Read : 데이터 조회(GET)
	Update : 데이터 수정(PUT, PATCH)
	Delete : 데이터 삭제(DELETE)
	*/
	
	@Autowired
	private BookmarkService bmservice;
	@Autowired
	private UserService uservice;
	@Autowired
	private Attendance_infoService aiservice;
	
	@PostMapping()
	public ResponseEntity<?> addBookmark(HttpServletRequest request ,@RequestBody BookmarkDTO bookmark) throws Exception {
		// 세션에서 아이디 가져오기
		HttpSession session = request.getSession();
		String loginUser = (String)session.getAttribute("loginUser");
		
		bookmark.setUserId(loginUser);
		
		// user 유무 체크
		if(uservice.getUser(loginUser) != null) {
			// 북마크 추가하기.
			bmservice.addBm(bookmark);
			
			// 이 도서관 방문 횟수를 "ai_count"로 구하기.
			List<Attendance_infoDTO> ai_list = aiservice.getAiList(loginUser, bookmark.getLBRRYSEQNO());
			int ai_count = ai_list.size();
			
			// 모든 정보 합치기
			List<Map<String, Object>> total = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			map.put("userId", loginUser);
			map.put("LBRRY_SEQ_NO", bookmark.getLBRRYSEQNO());
			map.put("LBRRY_NAME", bookmark.getLBRRYNAME());
			map.put("ADRES", bookmark.getADRES());
			map.put("TEL_NO", bookmark.getTELNO());
			map.put("ai_count", ai_count);
		
			total.add(map);
						
			return ResponseEntity.status(200).body(total);
		}
		else {
			return ResponseEntity.badRequest().body("등록된 회원이 아닙니다. 다시 로그인 하세요.");
		}
	}
	
}
