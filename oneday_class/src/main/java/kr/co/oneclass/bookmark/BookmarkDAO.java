package kr.co.oneclass.bookmark;

import kr.co.oneclass.board.RangeDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookmarkDAO {

    /**
     * 1. 관심 클래스 전체 개수 조회 (CATEGORY 조인 적용)
     */
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM bookmark b " +
            "JOIN class c ON b.class_code = c.class_code " +
            "JOIN category cat ON c.category_code = cat.category_code " +
            "LEFT JOIN category pcat ON cat.parents_category = pcat.category_code " +
            "WHERE b.member_code = TO_NUMBER(#{memberCode}) " +
            "<if test='rDTO != null and rDTO.filed != null and rDTO.filed != \"\" and rDTO.filed != \"ALL\"'>" +
            "  AND (cat.name = #{rDTO.filed} OR pcat.name = #{rDTO.filed}) " +
            "</if>" +
            "</script>")
    int selectTotalCnt(@Param("memberCode") String memberCode, @Param("rDTO") RangeDTO rDTO);

    /**
     * 2. 관심 클래스 목록 조회 (ROWNUM 페이징 + CLASS_IMG 조인 + CATEGORY 조인 + 주소 정제)
     */
    @Select("<script>" +
            "SELECT * FROM ( " +
            "    SELECT ROWNUM rnum, temp.* FROM ( " +
            "        SELECT c.class_code AS classCode, " +
            "               c.name AS className, " +
            "               CASE " +
            "                   WHEN c.old_address IS NULL OR c.old_address = '-' THEN '' " +
            "                   ELSE NVL(REGEXP_SUBSTR(c.old_address, '^.+?[동|구|면|리]'), c.old_address) " +
            "               END AS classRegion, " +
            "               ci.image AS classImg, " +
            "               TO_CHAR(c.price, 'FM999,999,999') || '원' AS classPrice " +
            "        FROM bookmark b " +
            "        JOIN class c ON b.class_code = c.class_code " +
            "        JOIN category cat ON c.category_code = cat.category_code " +
            "        LEFT JOIN category pcat ON cat.parents_category = pcat.category_code " +
            "        LEFT JOIN class_img ci ON c.class_code = ci.class_code " +
            "                              AND ci.type = '상세' " +
            "                              AND ci.sort_order = 1 " +
            "        WHERE b.member_code = TO_NUMBER(#{memberCode}) " +
            "        <if test='rDTO != null and rDTO.filed != null and rDTO.filed != \"\" and rDTO.filed != \"ALL\"'>" +
            "          AND (cat.name = #{rDTO.filed} OR pcat.name = #{rDTO.filed}) " +
            "        </if>" +
            "        ORDER BY b.bookmark_date DESC " +
            "    ) temp " +
            ") WHERE rnum BETWEEN #{rDTO.startNum} AND #{rDTO.endNum}" +
            "</script>")
    List<Bookmark> selectBookmark(@Param("memberCode") String memberCode, @Param("rDTO") RangeDTO rDTO);

    /**
     * 3. 관심 클래스 추가
     */
    @Insert("INSERT INTO bookmark (bookmark_code, member_code, class_code, bookmark_date) " +
            "VALUES (seq_bookmark_code.NEXTVAL, TO_NUMBER(#{memberCode}), TO_NUMBER(#{classCode}), SYSDATE)")
    int insertBookmark(@Param("memberCode") String memberCode, @Param("classCode") String classCode);

    /**
     * 4. 관심 클래스 삭제
     */
    @Delete("DELETE FROM bookmark " +
            "WHERE member_code = TO_NUMBER(#{memberCode}) " +
            "  AND class_code = TO_NUMBER(#{classCode})")
    int deleteBookmark(@Param("memberCode") String memberCode, @Param("classCode") String classCode);
    
    
    
    /**
     * 5. 관심 존재 여부 확인
     */
    @Select("SELECT COUNT(*)"
    		+ "    FROM bookmark"
    		+ "    WHERE member_code = #{memberCode} AND class_code = #{classCode}")
    int checkBookmark(@Param("memberCode") String memberCode, @Param("classCode") String classCode);
}