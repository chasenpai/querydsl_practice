package ex.querydsl.controller;

import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import ex.querydsl.repository.MemberJpaRepository;
import ex.querydsl.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> getMembersV1(MemberSearch search) {
        return memberJpaRepository.searchV2(search);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> getMembersV2(MemberSearch search, Pageable pageable) {
        return memberRepository.searchPageSimple(search, pageable);
    }

    @GetMapping("/v3/members")
    public Page<MemberTeamDto> getMembersV3(MemberSearch search, Pageable pageable) {
        return memberRepository.searchPageComplex(search, pageable);
    }

}
