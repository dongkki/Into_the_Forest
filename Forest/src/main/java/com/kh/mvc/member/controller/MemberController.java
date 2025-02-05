package com.kh.mvc.member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.kh.mvc.member.model.service.MemberService;
import com.kh.mvc.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SessionAttributes("loginMember") //loginMember를 model에서 취급할때 Session으로 처리하는 파라미터!
@Controller
public class MemberController {

	@Autowired
	private MemberService service;
	
	@RequestMapping(value = "/member/login", method = {RequestMethod.GET})
	public String loginPage() {
		log.info("로그인 페이지 요청");
		return "member/login";
	}
	
	@RequestMapping(value = "/login", method = {RequestMethod.POST})
	public ModelAndView login(ModelAndView model, String loginUserId, String loginPassword) {
		log.info("{},{}",loginUserId,loginPassword);
		
		Member loginMember = service.login(loginUserId, loginPassword);
		
		if(loginMember != null) {
			model.addObject("loginMember",loginMember); // 어노테이션을 통해 Session으로 처리되는 코드 
			model.setViewName("redirect:/");
		}else {
			model.addObject("msg","아이디나 패스워드가 일치하지 않습니다!");
			model.addObject("location", "/");
			model.setViewName("common/msg");
		}
		
		return model;
	}
	
	@RequestMapping("/logout")
	public String logout(SessionStatus status) {

		log.info("status : " + status.isComplete());
		status.setComplete(); // 세션을 종료하는 코드!
		log.info("status : " + status.isComplete());
		
		return "redirect:/";
	}
	
	@GetMapping("/member/signup")
	public String enrollPage() {
		log.info("가입 페이지 요청");
		return "member/signup";
	}
	
	@PostMapping("/member/signup")
	public ModelAndView enroll(ModelAndView model, @ModelAttribute Member member) {
		log.info("회원가입 member : " + member);
		
		int result = service.save(member);

		if(result > 0) {
			model.addObject("msg", "회원 가입에 성공하였습니다.");
			model.addObject("location", "/");
		}else {
			model.addObject("msg", "회원 가입에 실패하였습니다. 다시 한번 확인해주세요.");
			model.addObject("location", "/");
		}
		
		model.setViewName("common/msg");
		
		return model;
	}
	
	
	@GetMapping("/member/idCheck")
	public ResponseEntity<Map<String, Object>> idCheck(String id){
		log.info("userId : " + id );
		
		boolean result = service.validate(id);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("validate", result);
		
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
	}
	
	@GetMapping("/member/view")
	public String view() {
		log.info("회원정보 페이지 요청");
		return "member/view";
	}
	
	@PostMapping("/member/update")
	public ModelAndView update(ModelAndView model,
				@ModelAttribute Member member, // @ModelAttribute 생략 가능! 
				@SessionAttribute(name ="loginMember", required = false) Member loginMember
			) {
		if(loginMember == null || loginMember.getId().equals(member.getId()) == false) {
			model.addObject("msg", "잘못된 접근입니다.");
			model.addObject("location", "/");
			model.setViewName("common/msg");
			return model;
		}
		
		member.setNo(loginMember.getNo());
		int result = service.save(member);
		
		if(result > 0) {
			model.addObject("loginMember", service.findById(member.getId())); // 세션에 넣는 코드
			model.addObject("msg","회원정보를 수정하였습니다.");
			model.addObject("location", "/member/view");
		}else{
			model.addObject("msg", "회원정보를 수정에 실패하였습니다!");
			model.addObject("location", "/member/view");
		}
		
		model.setViewName("common/msg");
		return model;
	}
	
	
	
	@GetMapping("/member/delete")
	public ModelAndView delete(ModelAndView model, 
			@SessionAttribute(name ="loginMember", required = false) Member loginMember) {
		int result = service.delete(loginMember.getNo());
		
		if(result > 0) {
			model.addObject("msg", "정상적으로 탈퇴되었습니다.");
			model.addObject("location", "/logout");
		} else {
			model.addObject("msg", "회원 탈퇴를 실패하였습니다.");
			model.addObject("location", "/member/view");
		}	
		
		model.setViewName("common/msg");
		return model;
	}
	
	@GetMapping("/member/updatePwd")
	public String updatePwd() {
		return "/member/updatePwd";
	}
	
	@PostMapping("/member/updatePwd")
	public ModelAndView updatePwd(ModelAndView model, 
			@SessionAttribute(name ="loginMember", required = false) Member loginMember, 
			String userPwd) {
		
		System.out.println(loginMember);
		int result = service.updatePwd(loginMember, userPwd);
		
		if(result > 0) {
			model.addObject("msg", "정상적으로 변경되었습니다.");
		} else {
			model.addObject("msg", "정상적으로 변경되지 않았습니다!");
		}	
		model.addObject("script", "self.close()");
		model.setViewName("common/msg");
		return model;
	}
}
