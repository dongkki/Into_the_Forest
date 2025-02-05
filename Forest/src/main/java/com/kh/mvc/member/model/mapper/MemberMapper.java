package com.kh.mvc.member.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.mvc.member.model.vo.Member;

@Mapper
public interface MemberMapper {
	int selectCount();
	List<Member> selectAll();
	Member selectMember(@Param("id") String id);
	int insertMember(Member Member);
	int updateMember(Member Member);
	int deleteMember(@Param("no") int no);
	int updatePwd(Map<String, String> map);
}
