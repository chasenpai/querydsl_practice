package ex.querydsl.repository;

import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import ex.querydsl.entity.Member;
import ex.querydsl.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void repositoryTest() {

        Member member = Member.builder().username("MemberA").build();
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());

        List<Member> allMembers = memberJpaRepository.findAll();
        assertThat(allMembers).containsExactly(member);

        List<Member> allMembersByUsername = memberJpaRepository.findByUsername("MemberA");
        assertThat(allMembersByUsername).containsExactly(member);
    }

    @Test
    void querydslTest() {

        Member member = Member.builder().username("MemberA").build();
        memberJpaRepository.save(member);

        List<Member> allMembers = memberJpaRepository.findAllUseQueryDsl();
        assertThat(allMembers).containsExactly(member);

        List<Member> allMembersByUsername = memberJpaRepository.findByUsernameUseQueryDsl("MemberA");
        assertThat(allMembersByUsername).containsExactly(member);
    }

    @Test
    void conditionTest() {

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

        MemberSearch search = MemberSearch.builder()
                .ageGoe(30)
                .ageLoe(40)
                .teamName("TeamB")
                .build();

        List<MemberTeamDto> searchV1Result = memberJpaRepository.searchV1(search);
        assertThat(searchV1Result)
                .extracting("username")
                .containsExactly("MemberC");

        List<MemberTeamDto> searchV2Result = memberJpaRepository.searchV2(search);
        assertThat(searchV2Result)
                .extracting("username")
                .containsExactly("MemberC");
    }

}