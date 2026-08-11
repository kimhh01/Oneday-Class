package kr.co.oneclass.author.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.util.AuthorSessionUtils;
import kr.co.oneclass.author.review.dto.ReviewDetailDTO;
import kr.co.oneclass.author.review.dto.ReviewReplyDTO;
import kr.co.oneclass.author.review.dto.ReviewSearchDTO;
import kr.co.oneclass.author.review.service.ReviewService;

@Controller
public class ReviewController {

    private final ReviewService rService;

    public ReviewController(ReviewService rService) {
        this.rService = rService;
    }

    // 리뷰 요약, 클래스 필터, 리뷰 목록을 조회하여 리뷰 관리 화면을 보여준다
    @GetMapping("/author/reviews")
    public String reviewList(ReviewSearchDTO searchDTO, Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        searchDTO.setAuthorCode(authorCode);
        if (searchDTO.getReplyStatus() == null) {
            searchDTO.setReplyStatus("all");
        }
        if (searchDTO.getSortType() == null) {
            searchDTO.setSortType("latest");
        }

        model.addAttribute("summary", rService.getReviewSummary(authorCode));
        model.addAttribute("classes", rService.getReviewClassList(authorCode));
        model.addAttribute("reviews", rService.getReviewList(searchDTO));
        model.addAttribute("reviewCount", rService.getReviewCount(searchDTO));
        model.addAttribute("search", searchDTO);
        return "author/review";
    }

    // 선택한 리뷰의 내용, 리뷰 이미지, 작가 답글을 조회하여 상세 화면을 보여준다
    @GetMapping("/author/reviews/{reviewCode}")
    public String reviewDetail(
            @PathVariable("reviewCode") int reviewCode,
            @RequestParam(value = "mode", required = false, defaultValue = "view") String mode,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);

        ReviewDetailDTO review = rService.getReviewDetail(authorCode, reviewCode);
        // 없는 리뷰이거나 다른 작가의 리뷰면 상세를 보여주지 않고 목록으로 되돌린다
        if (review == null) {
            return "redirect:/author/reviews";
        }

        model.addAttribute("review", review);
        model.addAttribute("mode", mode);
        return "author/review-detail";
    }

    // 답글이 없는 리뷰에 작가 답글을 등록한다
    @PostMapping("/author/reviews/reply")
    public String addReviewReply(ReviewReplyDTO replyDTO, HttpSession session) {
        replyDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        rService.addReviewReply(replyDTO);
        return "redirect:/author/reviews/" + replyDTO.getReviewCode();
    }

    // 이미 등록된 작가 답글의 내용을 수정한다
    @PostMapping("/author/reviews/reply/edit")
    public String modifyReviewReply(ReviewReplyDTO replyDTO, HttpSession session) {
        replyDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        rService.modifyReviewReply(replyDTO);
        return "redirect:/author/reviews/" + replyDTO.getReviewCode();
    }

    // 등록된 작가 답글을 삭제한다
    @PostMapping("/author/reviews/{reviewCode}/reply/delete")
    public String removeReviewReply(
            @PathVariable("reviewCode") int reviewCode,
            HttpSession session) {

        rService.removeReviewReply(AuthorSessionUtils.getAuthorCode(session), reviewCode);
        return "redirect:/author/reviews/" + reviewCode;
    }

    // 리뷰 원문은 수강생의 콘텐츠다. 작가는 자기 답글만 등록·수정·삭제할 수 있다.
    // 리뷰 숨김(/hide)·삭제(/delete) 매핑은 의도적으로 두지 않는다.
}
