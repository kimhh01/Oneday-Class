package kr.co.oneclass.mypage;

import org.apache.ibatis.annotations.*;

import kr.co.oneclass.notice.NoticeDoamin;

@Mapper
public interface MypageDAO {

    // Oracle 기준: 가장 최근 등록된 공지사항 1건 조회
    @Select("SELECT * FROM (" +
            "  SELECT title, content, input_date, notice_type " +
            "  FROM notice " +
            "  ORDER BY input_date DESC" +
            ") WHERE ROWNUM = 1")
    @Results(id = "mypageNoticeResultMap", value = {
        @Result(property = "title", column = "title"),
        @Result(property = "content", column = "content"),
        @Result(property = "noticeDate", column = "input_date"),
        @Result(property = "noticeType", column = "notice_type")
    })
    NoticeDoamin selectRecentNotice();
}