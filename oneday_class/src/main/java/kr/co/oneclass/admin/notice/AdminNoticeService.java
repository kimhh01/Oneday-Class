package kr.co.oneclass.admin.notice;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.oneclass.admin.common.PageDomain;

@Service
public class AdminNoticeService {
	private final AdminNoticeDAO dao;

	public AdminNoticeService(AdminNoticeDAO dao) {
		this.dao = dao;
	}

	public List<AdminNoticeSummaryDomain> getNoticeList(AdminNoticeSearchDTO s) {
		setPage(s);
		return dao.selectNoticeList(s).stream()
				.map(d -> new AdminNoticeSummaryDomain(d.getNoticeCode(), d.getManagerCode(), d.getNoticeTitle(),
						d.getNoticeType(), d.getWriterId(), d.getStatus(), d.getRegisteredDate()))
				.toList();
	}

	public PageDomain getNoticePage(AdminNoticeSearchDTO s) {
		int cp = Math.max(s.getPage(), 1), ps = Math.max(s.getPageSize(), 1);
		int total = dao.selectNoticeTotalCount(s);
		int tp = total == 0 ? 1 : (int) Math.ceil((double) total / ps);
		int sp = ((cp - 1) / 5) * 5 + 1, ep = Math.min(sp + 4, tp);
		return new PageDomain(total, cp, ps, tp, sp, ep);
	}

	public AdminNoticeDetailDomain getNoticeDetail(int code) {
		AdminNoticeDetailDTO d = dao.selectNoticeDetail(code);
		return d == null ? null
				: new AdminNoticeDetailDomain(d.getNoticeCode(), d.getManagerCode(), d.getNoticeTitle(),
						d.getNoticeType(), d.getNoticeContent(), d.getWriterId(), d.getStatus(), d.getRegisteredDate());
	}

	@Transactional
	public boolean registerNotice(AdminNoticeCreateDTO d) {
		validate(d.getNoticeType());
		if (!"공개".equals(d.getStatus()))
			d.setStatus("비공개");
		return dao.insertNotice(d) > 0;
	}

	@Transactional
	public boolean updateNotice(AdminNoticeUpdateDTO d) {
		validate(d.getNoticeType());
		if (!"공개".equals(d.getStatus()))
			d.setStatus("비공개");
		return dao.updateNotice(d) > 0;
	}

	private void setPage(AdminNoticeSearchDTO s) {
		int p = Math.max(s.getPage(), 1), z = Math.max(s.getPageSize(), 1);
		s.setStartRow((p - 1) * z + 1);
		s.setEndRow(p * z);
	}

	private void validate(String t) {
		if (!"서비스".equals(t) && !"이용안내".equals(t))
			throw new IllegalArgumentException("공지 구분은 서비스 또는 이용안내만 사용할 수 있습니다.");
	}
}
