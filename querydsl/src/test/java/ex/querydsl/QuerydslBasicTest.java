package ex.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import ex.querydsl.entity.Member;
import ex.querydsl.entity.QMember;
import ex.querydsl.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static ex.querydsl.entity.QMember.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {

        queryFactory = new JPAQueryFactory(em);

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
    }

    @Test
    void jpql_VS_querydsl() {

        //JPQL
        //쿼리를 문자로 작성(런타임 시점에 오류)
        //파라미터 바인딩 직접
        String query = "select m from Member m where m.username = :username";

        Member findByJpql = em.createQuery(query, Member.class)
                .setParameter("username", "MemberA")
                .getSingleResult();

        assertThat(findByJpql.getUsername()).isEqualTo("MemberA");

        //QueryDsl
        //쿼리를 빌더로 작성(컴파일 시점에 오류)
        //파라미터 바인딩 자동 처리
        //QMember member = new QMember("m1"); - 별칭 직접 지정, 같은 테이블을 조인해야 하는 경우 아니면 기본 인스턴스 사용 권장
        Member findByQuerydsl = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("MemberB"))
                .fetchOne();

        assertThat(findByQuerydsl.getUsername()).isEqualTo("MemberB");
    }

}
