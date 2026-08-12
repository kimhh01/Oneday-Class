package kr.co.oneclass.mypage;

import org.apache.ibatis.annotations.*;

import kr.co.oneclass.notice.NoticeDTO;

@Mapper
public interface MypageDAO {

	// 가장 최근 등록된 공지사항 1건 조회
	NoticeDTO selectRecentNotice();
}