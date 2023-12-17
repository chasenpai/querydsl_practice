package ex.querydsl.repository;

import ex.querydsl.entity.Member;
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

}