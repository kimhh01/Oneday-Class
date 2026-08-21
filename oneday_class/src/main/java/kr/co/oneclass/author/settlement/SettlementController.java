package kr.co.oneclass.author.settlement;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.AuthorSessionUtils;

@Controller
public class SettlementController {

    private static final int PAGE_SIZE = 20;

    private final SettlementService sService;

    public SettlementController(SettlementService sService) {
        this.sService = sService;
    }

    // 검색 조건에 맞는 매출 요약과 결제 목록을 조회하여 매출 현황 화면을 보여준다
    @GetMapping("/author/sales")
    public String salesList(SalesSearchDTO searchDTO,
            @RequestParam(value = "period", required = false, defaultValue = "all") String period,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model, HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        configureSalesSearch(searchDTO, period, status, keyword, authorCode);
        int salesCount = sService.getSalesCount(searchDTO);

        model.addAttribute("salesSummary", sService.getSalesSummary(authorCode));
        model.addAttribute("sales", sService.getSalesList(searchDTO));
        model.addAttribute("salesCount", salesCount);
        model.addAttribute("period", period);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", searchDTO.getPage());
        model.addAttribute("totalPages", totalPages(salesCount));
        return "author/sales";
    }

    // 선택한 결제의 주문정보, 구매자정보, 환불금액과 정산 예정금액을 조회한다
    @GetMapping("/author/sales/{paymentCode}")
    public String salesDetail(
            @PathVariable("paymentCode") int paymentCode,
            Model model,
            HttpSession session) {

        var payment = sService.getSalesDetail(AuthorSessionUtils.getAuthorCode(session), paymentCode);
        if (payment == null) {
            return "redirect:/author/sales";
        }
        model.addAttribute("payment", payment);
        return "author/sales-detail";
    }

    // 정산 가능금액, 정산 대기금액, 정산계좌와 신청 가능한 매출 목록을 보여준다
    @GetMapping("/author/settlements/new")
    public String settlementApplyForm(Model model, HttpSession session) {
        addSettlementRequestModel(model, AuthorSessionUtils.getAuthorCode(session));
        return "author/settlement-request";
    }

    // 선택한 매출의 정산금액을 DB 값으로 다시 계산한다
    @PostMapping("/author/settlements/confirm")
    public String settlementConfirm(SettlementApplyDTO applyDTO, Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        applyDTO.setAuthorCode(authorCode);
        try {
            model.addAttribute("calculation", sService.calculateSettlement(applyDTO));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("settlementError", exception.getMessage());
        }
        addSettlementRequestModel(model, authorCode);
        return "author/settlement-request";
    }

    // CREATOR 의 계좌번호와 통장사본을 등록하거나 수정한다
    @PostMapping("/author/settlements/account")
    public String modifySettlementAccount(
            SettlementAccountDTO accountDTO,
            @RequestParam(value = "bankbookFile", required = false) MultipartFile bankbookFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        accountDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            boolean modified = sService.modifySettlementAccount(accountDTO, bankbookFile);
            redirectAttributes.addFlashAttribute(modified ? "profileMessage" : "profileError",
                    modified ? "정산 계좌 정보가 저장되었습니다." : "정산 계좌 정보를 저장하지 못했습니다.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("profileError", exception.getMessage());
        }
        return "redirect:/author/profile";
    }

    // 선택한 매출과 계좌정보를 검증한 후 정산 신청을 등록한다
    @PostMapping("/author/settlements")
    public String submitSettlement(SettlementApplyDTO applyDTO, HttpSession session,
            RedirectAttributes redirectAttributes) {
        applyDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            int settlementCode = sService.submitSettlement(applyDTO);
            return "redirect:/author/settlements/complete?settlementCode=" + settlementCode;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("settlementError", exception.getMessage());
            return "redirect:/author/settlements/new";
        }
    }

    // 접수된 정산번호와 신청금액을 조회하여 정산 신청 완료 화면을 보여준다
    @GetMapping("/author/settlements/complete")
    public String settlementComplete(
            @RequestParam("settlementCode") int settlementCode,
            Model model,
            HttpSession session) {

        SettlementDetailDTO detail = sService.getSettlementDetail(
                AuthorSessionUtils.getAuthorCode(session), settlementCode);
        if (detail == null) {
            return "redirect:/author/settlements";
        }
        model.addAttribute("detail", detail);
        return "author/settlement-detail";
    }

    // 기간과 정산상태에 맞는 누적 정산 내역을 조회한다
    @GetMapping("/author/settlements")
    public String settlementList(SettlementSearchDTO searchDTO,
            @RequestParam(value = "period", required = false, defaultValue = "all") String period,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model, HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        configureSettlementSearch(searchDTO, period, status, keyword, authorCode);
        int settlementCount = sService.getSettlementCount(searchDTO);
        List<SettlementListDTO> settlements = sService.getSettlementList(searchDTO);
        int visibleAmount = settlements.stream().mapToInt(SettlementListDTO::getSettlementAmount).sum();

        model.addAttribute("settlementDashboard", sService.getSettlementDashboard(authorCode));
        model.addAttribute("settlements", settlements);
        model.addAttribute("settlementCount", settlementCount);
        model.addAttribute("visibleAmount", visibleAmount);
        model.addAttribute("period", period);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", searchDTO.getPage());
        model.addAttribute("totalPages", totalPages(settlementCount));
        return "author/settlement-history";
    }

    // 선택한 정산의 진행상태, 금액, 지급계좌와 포함된 매출 목록을 조회한다
    @GetMapping("/author/settlements/{settlementCode}")
    public String settlementDetail(
            @PathVariable("settlementCode") int settlementCode,
            Model model,
            HttpSession session) {

        SettlementDetailDTO detail = sService.getSettlementDetail(
                AuthorSessionUtils.getAuthorCode(session), settlementCode);
        if (detail == null) {
            return "redirect:/author/settlements";
        }
        model.addAttribute("detail", detail);
        return "author/settlement-detail";
    }

    @GetMapping("/author/sales/excel")
    public void downloadSalesExcel(SalesSearchDTO searchDTO,
            @RequestParam(value = "period", required = false, defaultValue = "all") String period,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session, HttpServletResponse response) throws IOException {

        configureSalesSearch(searchDTO, period, status, keyword,
                AuthorSessionUtils.getAuthorCode(session));
        searchDTO.setStartRow(1);
        searchDTO.setEndRow(Integer.MAX_VALUE);
        List<SalesListDTO> sales = sService.getSalesList(searchDTO);

        List<List<Object>> rows = new ArrayList<>();
        for (SalesListDTO sale : sales) {
            rows.add(Arrays.asList(
                    "P-" + sale.getPaymentCode(),
                    formatDate(sale.getPaymentDate()),
                    sale.getClassTitle(),
                    sale.getMemberName(),
                    sale.getPaymentAmount(),
                    sale.getRefundAmount(),
                    sale.getSettlementAmount(),
                    sale.getPaymentStatus()));
        }
        prepareExcelResponse(response, "author-sales.xlsx");
        ExcelExportWriter.write(response.getOutputStream(), "매출 내역",
                List.of("결제번호", "결제일", "클래스", "구매자", "결제금액", "환불금액", "정산액", "결제상태"), rows);
    }

    @GetMapping("/author/settlements/excel")
    public void downloadSettlementExcel(SettlementSearchDTO searchDTO,
            @RequestParam(value = "period", required = false, defaultValue = "all") String period,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session, HttpServletResponse response) throws IOException {

        configureSettlementSearch(searchDTO, period, status, keyword,
                AuthorSessionUtils.getAuthorCode(session));
        searchDTO.setStartRow(1);
        searchDTO.setEndRow(Integer.MAX_VALUE);
        List<SettlementListDTO> settlements = sService.getSettlementList(searchDTO);

        List<List<Object>> rows = new ArrayList<>();
        for (SettlementListDTO settlement : settlements) {
            rows.add(Arrays.asList(
                    "S-" + settlement.getSettlementCode(),
                    formatDate(settlement.getAppliedAt()),
                    formatDate(settlement.getPeriodStartDate()),
                    formatDate(settlement.getPeriodEndDate()),
                    settlement.getPaymentCount(),
                    settlement.getTotalPaymentAmount(),
                    settlement.getTotalFeeAmount(),
                    settlement.getSettlementAmount(),
                    settlement.getSettlementStatus()));
        }
        prepareExcelResponse(response, "author-settlements.xlsx");
        ExcelExportWriter.write(response.getOutputStream(), "정산 내역",
                List.of("정산번호", "신청일", "대상시작일", "대상종료일", "결제건수", "매출액", "수수료(10%)", "정산금액", "상태"), rows);
    }

    // 정산 신청 화면이 요구하는 Model 값을 담는다
    private void addSettlementRequestModel(Model model, long authorCode) {
        SettlementDashboardDTO dashboard = sService.getSettlementDashboard(authorCode);
        model.addAttribute("account", sService.getSettlementAccount(authorCode));
        model.addAttribute("availablePayments", sService.getSettlementTargetList(authorCode));
        model.addAttribute("availableAmount", dashboard.getAvailableAmount());
        model.addAttribute("pendingAmount", dashboard.getWaitingAmount());
    }

    private void configureSalesSearch(SalesSearchDTO searchDTO, String period, String status, String keyword,
            long authorCode) {
        searchDTO.setAuthorCode(authorCode);
        searchDTO.setStartDate(startDate(period));
        searchDTO.setEndDate(new Date());
        searchDTO.setPaymentStatus(salesStatus(status));
        searchDTO.setKeyword(trimToNull(keyword));
        configurePage(searchDTO);
    }

    private void configureSettlementSearch(SettlementSearchDTO searchDTO,
            String period, String status, String keyword, long authorCode) {
        searchDTO.setAuthorCode(authorCode);
        searchDTO.setStartDate(startDate(period));
        searchDTO.setEndDate(new Date());
        searchDTO.setSettlementStatus(settlementStatus(status));
        searchDTO.setKeyword(trimToNull(keyword));
        int page = Math.max(searchDTO.getPage(), 1);
        searchDTO.setPage(page);
        searchDTO.setStartRow((page - 1) * PAGE_SIZE + 1);
        searchDTO.setEndRow(page * PAGE_SIZE);
    }

    private void configurePage(SalesSearchDTO searchDTO) {
        int page = Math.max(searchDTO.getPage(), 1);
        searchDTO.setPage(page);
        searchDTO.setStartRow((page - 1) * PAGE_SIZE + 1);
        searchDTO.setEndRow(page * PAGE_SIZE);
    }

    private Date startDate(String period) {
        int days;
        switch (period == null ? "all" : period) {
            case "week", "7" -> days = 7;
            case "month", "30" -> days = 30;
            case "quarter", "90" -> days = 90;
            case "180" -> days = 180;
            default -> {
                return null;
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    private String salesStatus(String status) {
        if ("paid".equals(status)) {
            return "결제완료";
        }
        if ("refunded".equals(status)) {
            return "환불완료";
        }
        return null;
    }

    private String settlementStatus(String status) {
        if ("waiting".equals(status)) {
            return "정산대기";
        }
        if ("hold".equals(status)) {
            return "정산보류";
        }
        if ("completed".equals(status)) {
            return "정산완료";
        }
        return null;
    }

    private int totalPages(int count) {
        return Math.max(1, (count + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    private void prepareExcelResponse(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    }

    private String formatDate(Date date) {
        return date == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
