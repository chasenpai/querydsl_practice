package ex.querydsl.repository;

import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import ex.querydsl.entity.Member;
import ex.querydsl.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void repositoryTest() {

        Member member = Member.builder().username("MemberA").build();
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());

        List<Member> allMembers = memberRepository.findAll();
        assertThat(allMembers).containsExactly(member);

        List<Member> allMembersByUsername = memberRepository.findByUsername("MemberA");
        assertThat(allMembersByUsername).containsExactly(member);
    }
    @BeforeEach
    void before() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        em.persist(teamA);
        em.persist(teamB);

        Member memberA = Member.builder().username("MemberA").age(20).team(teamA).build();
        Member memberB = Member.builder().username("MemberB").age(30).team(teamA).build();
        Member memberC = Member.builder().username("MemberC").age(40).team(teamB).build();
        Member memberD = Member.builder().username("MemberD").age(50).team(teamB).build();

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);
    }

    @Test
    void conditionTest() {

        MemberSearch search = MemberSearch.builder()
                .ageGoe(30)
                .ageLoe(40)
                .teamName("TeamB")
                .build();

        List<MemberTeamDto> searchV1Result = memberRepository.search(search);
        assertThat(searchV1Result)
                .extracting("username")
                .containsExactly("MemberC");

        List<MemberTeamDto> searchV2Result = memberRepository.search(search);
        assertThat(searchV2Result)
                .extracting("username")
                .containsExactly("MemberC");
    }

    @Test
    void simplePaging() {

        MemberSearch search = new MemberSearch();
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(search, pageRequest);
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username")
                .containsExactly("MemberA", "MemberB", "MemberC");
    }

    @Test
    void simpleComplex() {

        MemberSearch search = new MemberSearch();
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageComplex(search, pageRequest);
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username")
                .containsExactly("MemberA", "MemberB", "MemberC");
    }

}
