package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.dto.request.CreateMemberDTO;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.response.MyPageInfoDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.dto.request.EditRequest;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.response.EditResponse;
import com.fasttime.domain.member.dto.response.MemberResponse;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.global.exception.ErrorCode;
import com.fasttime.global.util.ResponseDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;


    @PostMapping("/api/v1/join")
    public ResponseEntity<ResponseDTO<?>> join(@Valid @RequestBody CreateMemberDTO createMemberDTO) {
        ResponseDTO<Object> response = memberService.registerOrRecoverMember(createMemberDTO);
        return ResponseEntity.status(HttpStatus.valueOf(response.getCode())).body(response);
    }

    @PutMapping("/api/v1/retouch-member")
    public ResponseEntity<ResponseDTO<EditResponse>> updateMember(
        @Valid @RequestBody EditRequest editRequest,
        HttpSession session) {

        return memberService.updateMemberInfo(editRequest, session)
            .map(updatedMember -> ResponseEntity.ok(
                ResponseDTO.<EditResponse>res(HttpStatus.OK, "회원 정보가 업데이트되었습니다.",
                    new EditResponse(updatedMember.getEmail(),
                        updatedMember.getNickname(),
                        updatedMember.getImage()))
            ))
            .orElseGet(() -> ResponseEntity
                .status(ErrorCode.MEMBER_NOT_FOUND.getHttpStatus())
                .body(ResponseDTO.<EditResponse>res(ErrorCode.MEMBER_NOT_FOUND.getHttpStatus(),
                    ErrorCode.MEMBER_NOT_FOUND.getMessage(),
                    null))
            );
    }


    @DeleteMapping("/api/v1/delete")
    public ResponseEntity<ResponseDTO<Object>> deleteMember(HttpSession httpSession) {
        Long memberId = (Long) httpSession.getAttribute("MEMBER");
        if (memberId != null) {
            Member member = memberService.getMember(memberId);
            memberService.softDeleteMember(member);
            httpSession.invalidate();
            return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, "탈퇴가 완료되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDTO.res(HttpStatus.UNAUTHORIZED,
                    "세션에 유효한 회원 ID가 없습니다. 로그인 상태를 확인하세요."));
        }
    }


    @GetMapping("/api/v1/mypages")
    public ResponseEntity<ResponseDTO> getMyPageInfo(HttpSession session) {

        Long memberId = (Long) session.getAttribute("MEMBER");
        // 프론트 측에서 요청한 예외 처리 ->403
        if (memberId == null) {

            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ResponseDTO.res(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."));
        }

        MyPageInfoDTO myPageInfoDto = memberService.getMyPageInfoById(memberId);
        return ResponseEntity.ok(
            ResponseDTO.res(HttpStatus.OK, "사용자 정보를 성공적으로 조회하였습니다.",
                myPageInfoDto));
    }


    @PostMapping("/api/v1/login")
    public ResponseEntity<ResponseDTO> logIn(@Validated @RequestBody LoginRequestDTO dto
        , HttpSession session) {

        MemberResponse response = memberService.loginMember(dto);
        session.setAttribute("MEMBER", response.getId());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "로그인이 완료되었습니다.", response));
    }

    @GetMapping("/api/v1/logout")
    public ResponseEntity<ResponseDTO> logOut(HttpSession session) {
        if (session.getAttribute("ADMIN") != null) {
            session.removeAttribute("ADMIN");
        }
        if (session.getAttribute("MEMBER") != null) {
            session.removeAttribute("MEMBER");
        }
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "로그아웃이 완료되었습니다."));
    }

    @PostMapping("/api/v1/RePassword")
    public ResponseEntity<ResponseDTO> rePassword
        (@Validated @RequestBody RePasswordRequest request, HttpSession session) {

        MemberResponse response =
            memberService.rePassword(request, (Long) session.getAttribute("MEMBER"));
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.res
            (HttpStatus.OK, "패스워드 재설정이 완료되었습니다", response));
    }
}

