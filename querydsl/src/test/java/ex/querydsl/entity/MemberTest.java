package ex.querydsl.entity;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    void entityTest() {

        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        em.persist(teamA);
        em.persist(teamB);

        Member memberA = Member.builder().username("MemberA").age(20).team(teamA).build();
        Member memberB = Member.builder().username("MemberB").age(25).team(teamA).build();
        Member memberC = Member.builder().username("MemberC").age(30).team(teamB).build();
        Member memberD = Member.builder().username("MemberD").age(35).team(teamB).build();

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

        em.flush();
        em.clear();

        List<Member> findMembers = em.createQuery("select m from Member m", Member.class).getResultList();
        for (Member member : findMembers) {
            System.out.println("member = " + member.getUsername());
            System.out.println("ㄴteam = " + member.getTeam().getName());
        }
    }

}