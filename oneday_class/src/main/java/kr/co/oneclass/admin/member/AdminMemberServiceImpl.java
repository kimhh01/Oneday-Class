package kr.co.oneclass.admin.member;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.admin.common.PageDomain;

@Service
public class AdminMemberServiceImpl implements AdminMemberService {

	private static final int PAGE_SIZE = 10;
	private static final int RESERVATION_PAGE_SIZE = 5;
	private static final int PAGE_BLOCK_SIZE = 5;

	private final AdminMemberDAO adminMemberDAO;

	public AdminMemberServiceImpl(AdminMemberDAO adminMemberDAO) {
		this.adminMemberDAO = adminMemberDAO;
	}

	@Override
	public List<AdminMemberDomain> getMemberList(AdminMemberSearchDTO searchDTO) {

		int page = searchDTO.getPage() < 1 ? 1 : searchDTO.getPage();

		int startRow = (page - 1) * PAGE_SIZE + 1;
		int endRow = page * PAGE_SIZE;

		return adminMemberDAO.selectMemberList(searchDTO, startRow, endRow).stream().map(AdminMemberDTO::toDomain)
				.toList();
	}

	@Override
	public PageDomain getPage(AdminMemberSearchDTO searchDTO) {

		int page = searchDTO.getPage() < 1 ? 1 : searchDTO.getPage();

		int totalCount = adminMemberDAO.selectMemberCount(searchDTO);

		int totalPage = (int) Math.ceil((double) totalCount / PAGE_SIZE);

		int startPage = ((page - 1) / PAGE_BLOCK_SIZE) * PAGE_BLOCK_SIZE + 1;

		int endPage = startPage + PAGE_BLOCK_SIZE - 1;

		if (endPage > totalPage) {
			endPage = totalPage;
		}

		return new PageDomain(totalCount, page, PAGE_SIZE, totalPage, startPage, endPage);
	}

	@Override
	public AdminMemberDomain getMemberDetail(int memberCode) {

		AdminMemberDTO dto = adminMemberDAO.selectMemberByCode(memberCode);

		return dto == null ? null : dto.toDomain();
	}

	@Override
	public List<AdminMemberReservationDomain> getMemberReservationList(int memberCode, int page) {

		if (page < 1) {
			page = 1;
		}

		int startRow = (page - 1) * RESERVATION_PAGE_SIZE + 1;

		int endRow = page * RESERVATION_PAGE_SIZE;

		return adminMemberDAO.selectMemberReservationList(memberCode, startRow, endRow).stream()
				.map(AdminMemberReservationDTO::toDomain).toList();
	}

	@Override
	public PageDomain getMemberReservationPage(int memberCode, int page) {

		if (page < 1) {
			page = 1;
		}

		int totalCount = adminMemberDAO.selectMemberReservationCount(memberCode);

		int totalPage = (int) Math.ceil((double) totalCount / RESERVATION_PAGE_SIZE);

		int startPage = ((page - 1) / PAGE_BLOCK_SIZE) * PAGE_BLOCK_SIZE + 1;

		int endPage = startPage + PAGE_BLOCK_SIZE - 1;

		if (endPage > totalPage) {
			endPage = totalPage;
		}

		return new PageDomain(totalCount, page, RESERVATION_PAGE_SIZE, totalPage, startPage, endPage);
	}

	@Transactional
	@Override
	public boolean updateMemberStatus(AdminMemberStatusUpdateDTO statusUpdateDTO) {

		return adminMemberDAO.updateMemberStatus(statusUpdateDTO) > 0;
	}

}