package kr.co.oneclass.author.classbasic.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.classbasic.dto.ClassImageDTO;

@Repository
public class ClassImageDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classbasic.dao.ClassImageDAO.";

    private final SqlSessionTemplate sqlSession;

    public ClassImageDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 클래스의 전체 이미지 목록을 조회한다
    public List<ClassImageDTO> selectClassImageList(int classCode) {
        return sqlSession.selectList(NAMESPACE + "selectClassImageList", classCode);
    }

    // 대표·완성작·갤러리 등 유형별 이미지 목록을 조회한다
    public List<ClassImageDTO> selectClassImageListByType(int classCode, String imageType) {
        Map<String, Object> param = new HashMap<>();
        param.put("classCode", classCode);
        param.put("imageType", imageType);
        return sqlSession.selectList(NAMESPACE + "selectClassImageListByType", param);
    }

    // 유형별 이미지를 일괄 삭제한다
    public int deleteClassImageListByType(int classCode, String imageType) {
        Map<String, Object> param = new HashMap<>();
        param.put("classCode", classCode);
        param.put("imageType", imageType);
        return sqlSession.delete(NAMESPACE + "deleteClassImageListByType", param);
    }

    // 클래스 이미지를 등록한다
    public int insertClassImage(ClassImageDTO ciDTO) {
        return sqlSession.insert(NAMESPACE + "insertClassImage", ciDTO);
    }

    // 클래스 이미지를 단건 삭제한다
    public int deleteClassImage(int imageCode) {
        return sqlSession.delete(NAMESPACE + "deleteClassImage", imageCode);
    }

    // 클래스 이미지의 출력 순서를 변경한다
    public int updateClassImageOrder(ClassImageDTO ciDTO) {
        return 0;
    }
}
