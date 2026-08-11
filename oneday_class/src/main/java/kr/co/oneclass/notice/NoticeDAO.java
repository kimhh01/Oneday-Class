package kr.co.oneclass.notice;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NoticeDAO {

    /**
     * 1. 공지사항 전체 건수 조회 (페이지네이션 및 필터용)
     */
    int selectTotalCnt(Object rDTO);

    /**
     * 2. 공지사항 목록 조회 (페이지네이션 및 카테고리 필터링)
     */
    List<NoticeDTO> selectList(Object rDTO);

    /**
     * 3. 공지사항 상세 조회
     */
    NoticeDTO selectDetail(String noticeCode);
}