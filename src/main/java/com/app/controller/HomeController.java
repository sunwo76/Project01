package com.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {
	
	@Autowired
	private SqlSession sqlSession;
	
	@GetMapping("/")
	public String home(Model model) {
		// Board 테이블에서 전체 목록 Select 후 List를 화면쪽으로 전달하기
		model.addAttribute("list", sqlSession.selectList("sql.findAll"));
		return "home";
	}
	
	@GetMapping("/sign")
	public String sign() {
		return "sign";
	}
		
	@GetMapping("/detail/{no:[0-9]+}")
	public String detail(@PathVariable("no") int no, Model model) {
		// Board 테이블에서 해당 번호(no)를 이용하여 하나의 게시글를 Select 후 화면쪽으로 전달하기
		model.addAttribute("board", sqlSession.selectOne("sql.findOne", no));
		return "detail";
	}
	
	@ResponseBody
	@PostMapping("/detail/{no:[0-9]+}")
	public Map<String, Object> detail(@PathVariable("no") int no, HttpServletRequest req) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("status", false);
		Object id = req.getSession().getAttribute("id");
		if(id != null) {
			resultMap.put("id", id);
		
			// 1. 로그인 된 사용자가 해당 게시글의 작성자인지 확인 후 동일하면 수정 그렇지 않으면 디테일 화면으로 이동
			int user_no = sqlSession.selectOne("sql.findUserID", no);
			int sUser_no = Integer.parseInt(id.toString());
			if(user_no == sUser_no) {
				String board_title = req.getParameter("board_title");
				String board_content = req.getParameter("board_content");
				
				// 2. Board 테이블에 해당 게시판번호(no)를 이용하여  게시판제목(board_title), 게시판내용(board_content) 두개의 변수의 값을 전달하여 수정하기
				HashMap<String, Object> map = new HashMap<>();
				map.put("board_title", board_title);
				map.put("board_content", board_content);
				map.put("board_no", no);
				
				if(sqlSession.update("sql.editBoard", map) == 1) {
					resultMap.put("status", true); // 성공한 경우
				}
			}
		}
		return resultMap;
	}
	
	@ResponseBody
	@PostMapping("/delete/{no:[0-9]+}")
	public Map<String, Object> delete(@PathVariable("no") int no, HttpServletRequest req) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("status", false);
		Object id = req.getSession().getAttribute("id");
		if(id != null) {
			resultMap.put("id", id);
			
			// 1. 로그인 된 사용자가 해당 게시글의 작성자인지 확인 후 동일하면 삭제 그렇지 않으면 디테일 화면으로 이동
			int user_no = sqlSession.selectOne("sql.findUserID", no);
			int sUser_no = Integer.parseInt(id.toString());
			if(user_no == sUser_no) {
				int state = sqlSession.update("sql.deleteBoard", no);
				if(state == 1) {
					resultMap.put("status", true); // 성공한 경우
				}
			}
		}
		return resultMap;
	}
	
	@GetMapping("/board")
	public String board() {
		return "board";
	}
	
	@PostMapping("/board")
	public String board(HttpServletRequest req) {
		Object id = req.getSession().getAttribute("id");
		if(id != null) {
			String board_title = req.getParameter("board_title");
			String board_content = req.getParameter("board_content");
			HashMap<String, Object> map = new HashMap<>();
			map.put("board_title", board_title);
			map.put("board_content", board_content);
			map.put("user_no", id);
			log.info("Map : {}", map);
			// 전달 받은 글 내용을 Board에 저장하고 board_no를 주소와 함께 전달하기
			int state = sqlSession.insert("sql.saveBoard", map);
			if(state == 1) {
				try {
					int no = Integer.parseInt(map.get("board_no").toString());
					return "redirect:/detail/" + no;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return "redirect:/board";
				}
			}
		}
		return "redirect:/login";
	}
	
}
