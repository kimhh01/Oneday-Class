package kr.co.oneclass.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseDAO rvd; // PurchaseDAO (다이어그램상 rvd/pd)

    @Override
    public List<Purchase> getPurchaseList(String memberCode, String status) {
        return rvd.selectListByMember(memberCode, status);
    }

    @Override
    public Purchase getPurchaseDetail(String reservationCode) {
        return rvd.selectDetail(reservationCode);
    }

    @Override
    public boolean writeReview(Object rdto) {
        // 리뷰 DAO 및 서비스 연동 로직
        return true;
    }

    @Override
    public Object reviewView(int memberCode, int classCode) {
        // 리뷰 DAO 및 서비스 연동 로직
        return null;
    }
}