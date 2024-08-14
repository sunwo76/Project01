package com.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
	
	@Autowired
	private SqlSession sqlSession;

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		Object id = session.getAttribute("id");
		if(id == null) {
			return "redirect:/login";
		} else {
			session.invalidate();
			return "redirect:/";
		}
	}

	@ResponseBody
	@PostMapping("/login") 
	public boolean login(@RequestParam("user_id") String user_id, @RequestParam("user_pwd") String user_pwd, HttpServletRequest req) {
		// 1. 사용자 id, pwd 가지고 데이터베이스에 Select 하여 존재 여부 확인 후 session 저장 또는 로그인 실패 처리 할것!
//		System.out.println(user_id);
//		System.out.println(user_pwd);
		Map<String, Object> map = new HashMap<>();
		map.put("user_id", user_id);
		map.put("user_pwd", user_pwd);
		map = sqlSession.selectOne("sql.login", map);
		if(map != null) {
			req.getSession().setAttribute("id", map.get("user_no"));
			return true;
		}
		return false;
//		return "redirect:/";
	}
	
	@PostMapping("/sign")
	public String sign(RedirectAttributes ra, HttpServletRequest req) {
		String user_nm = req.getParameter("user_nm");
		String user_id = req.getParameter("user_id");
		String user_pwd = req.getParameter("user_pwd");
//		System.out.println(user_nm);
//		System.out.println(user_id);
//		System.out.println(user_pwd);
		// 3개의 변수를 이용하여 데이터베이스에 User 테이블에 Insert 하기
		Map<String, String> map = new HashMap<>();
		map.put("user_nm", user_nm);
		map.put("user_id", user_id);
		map.put("user_pwd", user_pwd);
		int state = sqlSession.insert("sql.saveUser", map);
		// Insert 후 상태값에 따라서 type의 값을 0 또는 1로 전달하기
		ra.addFlashAttribute("type", state);
		return "redirect:/complete";
	}
	
	@GetMapping("/complete")
	public String complete(Model model) {
		return "complete";
	}
}
