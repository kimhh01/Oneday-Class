package kr.co.oneclass.author.classbasic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class ClassDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classbasic.ClassDAO.";

    private final SqlSessionTemplate sqlSession;

    public ClassDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 등록 시작 시 초안 클래스 행을 생성한다
    public int insertDraftClass(ClassBasicDTO cbDTO) {
        return sqlSession.insert(NAMESPACE + "insertDraftClass", cbDTO);
    }

    // 작가가 가장 최근에 저장한 작성중 초안을 조회한다
    public ClassBasicDTO selectLatestDraftClass(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectLatestDraftClass", authorCode);
    }

    // 기본정보를 조회한다
    public ClassBasicDTO selectClassBasic(long authorCode, int classCode) {
        return sqlSession.selectOne(NAMESPACE + "selectClassBasic", ownerParam(authorCode, classCode));
    }

    // 기본정보를 수정한다
    public int updateClassBasic(ClassBasicDTO cbDTO) {
        return sqlSession.update(NAMESPACE + "updateClassBasic", cbDTO);
    }

    // 승인 상태를 유지한 채 운영 클래스의 기본정보를 수정한다
    public int updateApprovedClassBasic(ClassBasicDTO cbDTO) {
        return sqlSession.update(NAMESPACE + "updateApprovedClassBasic", cbDTO);
    }

    // 위치 정보를 조회한다
    public ClassLocationDTO selectClassLocation(long authorCode, int classCode) {
        return sqlSession.selectOne(NAMESPACE + "selectClassLocation", ownerParam(authorCode, classCode));
    }

    // 위치 정보를 수정한다
    public int updateClassLocation(ClassLocationDTO clDTO) {
        return sqlSession.update(NAMESPACE + "updateClassLocation", clDTO);
    }

    // 승인 상태를 유지한 채 운영 클래스의 장소정보를 수정한다
    public int updateApprovedClassLocation(ClassLocationDTO clDTO) {
        return sqlSession.update(NAMESPACE + "updateApprovedClassLocation", clDTO);
    }

    // 클래스 등록 선택창에 사용할 하위 카테고리 목록을 조회한다
    public List<CategoryDTO> selectCategoryList() {
        return sqlSession.selectList(NAMESPACE + "selectCategoryList");
    }

    // 일정·가격 정보를 조회한다
    public ClassScheduleDTO selectClassSchedule(long authorCode, int classCode) {
        return sqlSession.selectOne(NAMESPACE + "selectClassSchedule", ownerParam(authorCode, classCode));
    }

    // 일정·가격 정보를 수정한다
    public int updateClassSchedule(ClassScheduleDTO csDTO) {
        return sqlSession.update(NAMESPACE + "updateClassSchedule", csDTO);
    }

    // 기존 일정 행은 유지하고 승인 클래스의 가격·재료비 정보만 수정한다
    public int updateApprovedClassPricing(ClassScheduleDTO csDTO) {
        return sqlSession.update(NAMESPACE + "updateApprovedClassPricing", csDTO);
    }

    // 상세정보를 조회한다
    public ClassDetailDTO selectClassDetail(long authorCode, int classCode) {
        return sqlSession.selectOne(NAMESPACE + "selectClassDetail", ownerParam(authorCode, classCode));
    }

    // 상세정보를 수정한다
    public int updateClassDetail(ClassDetailDTO cdDTO) {
        return sqlSession.update(NAMESPACE + "updateClassDetail", cdDTO);
    }

    // 승인 상태를 유지한 채 운영 클래스의 상세 본문을 수정한다
    public int updateApprovedClassDetail(ClassDetailDTO cdDTO) {
        return sqlSession.update(NAMESPACE + "updateApprovedClassDetail", cdDTO);
    }

    // 기존 '이런점이 좋아요' 목록을 삭제한다
    public int deleteClassAdvantageList(int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("classCode", classCode);
        param.put("detailType", "장점");
        return sqlSession.delete(NAMESPACE + "deleteDetailInfoList", param);
    }

    // '이런점이 좋아요' 목록을 등록한다
    public int insertClassAdvantageList(ClassDetailDTO cdDTO) {
        return insertDetailInfoList(cdDTO.getClassCode(), "장점", cdDTO.getAdvantageList());
    }

    // 기존 '이런분께 추천해요' 목록을 삭제한다
    public int deleteClassRecommendList(int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("classCode", classCode);
        param.put("detailType", "추천");
        return sqlSession.delete(NAMESPACE + "deleteDetailInfoList", param);
    }

    // '이런분께 추천해요' 목록을 등록한다
    public int insertClassRecommendList(ClassDetailDTO cdDTO) {
        return insertDetailInfoList(cdDTO.getClassCode(), "추천", cdDTO.getRecommendList());
    }

    // 기존 제공 항목 목록을 삭제한다
    public int deleteClassOptionList(int classCode) {
        return sqlSession.delete(NAMESPACE + "deleteClassOptionList", classCode);
    }

    // 제공 항목 목록을 등록한다
    public int insertClassOptionList(ClassDetailDTO cdDTO) {
        int inserted = 0;
        for (Integer optionCode : cdDTO.getOptionCodeList()) {
            Map<String, Object> param = new HashMap<>();
            param.put("classCode", cdDTO.getClassCode());
            param.put("optionCode", optionCode);
            inserted += sqlSession.insert(NAMESPACE + "insertClassOption", param);
        }
        return inserted;
    }

    // 기존 주의사항 목록을 삭제한다
    public int deleteClassNoticeList(int classCode) {
        return sqlSession.delete(NAMESPACE + "deleteClassNoticeList", classCode);
    }

    // 주의사항 목록을 등록한다
    public int insertClassNoticeList(ClassDetailDTO cdDTO) {
        int inserted = 0;
        for (String notice : cdDTO.getNoticeList()) {
            Map<String, Object> param = new HashMap<>();
            param.put("classCode", cdDTO.getClassCode());
            param.put("content", notice);
            inserted += sqlSession.insert(NAMESPACE + "insertClassNotice", param);
        }
        return inserted;
    }

    // 기존 태그 목록을 삭제한다
    public int deleteClassTagList(int classCode) {
        return sqlSession.delete(NAMESPACE + "deleteClassTagList", classCode);
    }

    // 태그 목록을 등록한다
    public int insertClassTagList(ClassDetailDTO cdDTO) {
        int inserted = 0;
        for (String tag : cdDTO.getTagList()) {
            Map<String, Object> param = new HashMap<>();
            param.put("classCode", cdDTO.getClassCode());
            param.put("content", tag);
            inserted += sqlSession.insert(NAMESPACE + "insertClassTag", param);
        }
        return inserted;
    }

    // 상세정보 화면의 활성 제공 항목 선택지를 조회한다
    public List<OfferingDTO> selectOfferingList() {
        return sqlSession.selectList(NAMESPACE + "selectOfferingList");
    }

    // 등록 위저드의 현재 진행 단계를 갱신한다
    public int updateRegisterStep(long authorCode, int classCode, String step) {
        Map<String, Object> param = ownerParam(authorCode, classCode);
        param.put("step", step);
        return sqlSession.update(NAMESPACE + "updateRegisterStep", param);
    }

    // 반려된 클래스를 같은 클래스 코드의 작성중 초안으로 되돌린다
    public int reopenRejectedClass(long authorCode, int classCode) {
        return sqlSession.update(NAMESPACE + "reopenRejectedClass", ownerParam(authorCode, classCode));
    }

    // 클래스 상태를 변경한다
    public int updateClassStatus(long authorCode, int classCode, String status) {
        Map<String, Object> param = ownerParam(authorCode, classCode);
        param.put("status", status);
        return sqlSession.update(NAMESPACE + "updateClassStatus", param);
    }

    // 등록 완료 화면에 출력할 결과 정보를 조회한다
    public ClassRegisterResultDTO selectRegisterResult(long authorCode, int classCode) {
        return sqlSession.selectOne(NAMESPACE + "selectRegisterResult", ownerParam(authorCode, classCode));
    }

    // 클래스의 현재 상태값을 조회한다
    public String selectClassStatus(long authorCode, int classCode) {
        return sqlSession.selectOne(NAMESPACE + "selectClassStatus", ownerParam(authorCode, classCode));
    }

    // 승인 완료이면서 폐강하지 않은 본인 클래스인지 확인한다
    public boolean existsApprovedEditableClass(long authorCode, int classCode) {
        Integer count = sqlSession.selectOne(
                NAMESPACE + "countApprovedEditableClass", ownerParam(authorCode, classCode));
        return count != null && count == 1;
    }

    // 커리큘럼처럼 CLASS 본문을 수정하지 않는 단계의 최근 저장 시각을 갱신한다
    public int touchDraftClass(long authorCode, int classCode) {
        return sqlSession.update(NAMESPACE + "touchDraftClass", ownerParam(authorCode, classCode));
    }

    // 커리큘럼 교체 트랜잭션 끝에서 승인 상태와 소유권을 다시 확인한다
    public int guardApprovedClassEdit(long authorCode, int classCode) {
        return sqlSession.update(NAMESPACE + "guardApprovedClassEdit", ownerParam(authorCode, classCode));
    }

    private Map<String, Object> ownerParam(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return param;
    }

    private int insertDetailInfoList(int classCode, String detailType, List<String> contents) {
        int inserted = 0;
        for (String content : contents) {
            Map<String, Object> param = new HashMap<>();
            param.put("classCode", classCode);
            param.put("detailType", detailType);
            param.put("content", content);
            inserted += sqlSession.insert(NAMESPACE + "insertDetailInfo", param);
        }
        return inserted;
    }
}
