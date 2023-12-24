package ex.querydsl.controller;

import com.querydsl.core.types.Predicate;
import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import ex.querydsl.entity.Member;
import ex.querydsl.repository.MemberJpaRepository;
import ex.querydsl.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    //Querydsl Web 지원
    //@QuerydslPredicate 를 사용하여 컨트롤러에서 간단하게 querydsl 조건문을 생성할 수 있다
    //ex) /v4/members?username=Member1
    //단순한 조건만 가능하고 컨트롤러가 querydsl 에 의존하게 된다
    //조건을 커스텀할 수 있지만 복잡하고 명시적이지 않다
    //복잡한 실무환경에서 사용하기엔 제약이 많이 따른다
    @GetMapping("/v4/members")
    public Page<MemberTeamDto> getMembersV4(@QuerydslPredicate(root = Member.class) Predicate predicate, Pageable pageable) {
        Page<Member> result = memberRepository.findAll(predicate, pageable);
        return result.map(MemberTeamDto::new);
    }

}
