package ex.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ex.querydsl.entity.Member;
import ex.querydsl.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ex.querydsl.entity.QMember.*;
import static ex.querydsl.entity.QTeam.*;
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
        Member memberB = Member.builder().username("MemberB").age(30).team(teamA).build();
        Member memberC = Member.builder().username("MemberC").age(40).team(teamB).build();
        Member memberD = Member.builder().username("MemberD").age(50).team(teamB).build();

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);
    }

    //JPQL 과 QueryDsl 비교
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

    //검색 조건
    @Test
    void search() {

        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("MemberA")
                        .and(member.age.eq(20)) //검색 조건은 and(콤마로도 가능), or 메서드로 체인 가능
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("MemberA");
        assertThat(findMember.getAge()).isEqualTo(20);

        /**
           member.username.eq("memberA") // username = 'memberA'
           member.username.ne("memberA") //username != 'memberA'
           member.username.eq("memberA").not() // username != 'memberA'
           member.username.isNotNull() //이름이 is not null
           member.age.in(10, 20) // age in (10,20)
           member.age.notIn(10, 20) // age not in (10, 20)
           member.age.between(10,30) //between 10, 30
           member.age.goe(30) // age >= 30
           member.age.gt(30) // age > 30
           member.age.loe(30) // age <= 30
           member.age.lt(30) // age < 30
           member.username.like("member%") //like 검색
           member.username.contains("member") // like ‘%member%’ 검색
           member.username.startsWith("member") //like ‘member%’ 검색
         */
    }

    //결과 조회
    @Test
    void result() {

        //List
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        //단 건
        Member findMember1 = queryFactory
                .selectFrom(member)
                .fetchOne(); //NonUniqueResultException

        //처음 한 건
        Member findMember2 = queryFactory
                .selectFrom(member)
                .fetchFirst();

        //fetchResults - 페이징 정보 포함
        QueryResults<Member> results1 = queryFactory
                .selectFrom(member)
                .fetchResults(); //Deprecated - count 쿼리 추가로 발생, 복잡한 쿼리에서 제대로 동작하지 않을 수 있음 > count 쿼리를 따로 날리는 방식을 권장

        //count
        long count1 = queryFactory
                .selectFrom(member)
                .fetchCount(); //Deprecated

        Long count2 = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        Long count3 = queryFactory
                .select(Wildcard.count) //count(*)
                .from(member)
                .fetchOne();
    }

    //정렬
    @Test
    void sort() {

        em.persist(Member.builder().username("MemberE").age(100).build());
        em.persist(Member.builder().username("MemberF").age(90).build());
        em.persist(Member.builder().age(90).build());

        //나이 내림차순, 이름 오름차순, 이름이 없으면 마지막
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.between(90, 100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        for (Member member : result) {
            System.out.println("member age = " + member.getAge() + " name = " + member.getUsername());
        }

        Member memberE = result.get(0);
        Member memberF = result.get(1);
        Member memberNull = result.get(2);
        assertThat(memberE.getUsername()).isEqualTo("MemberE");
        assertThat(memberF.getUsername()).isEqualTo("MemberF");
        assertThat(memberNull.getUsername()).isNull();
    }

    //페이징
    @Test
    void paging() {

        List<Member> result1 = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(result1.size()).isEqualTo(2);

        //total count 가 필요하다면 fetchResults - count 쿼리를 추가로 실행
        //fetchResults Deprecated
        //추가로 실행되는 count 쿼리는 단순히 select 구문을 count 처리 용도로 바꾼 것으로
        //단순한 쿼리에서는 잘 동작하지만 복잡한 쿼리에서는 제대로 동작하지 않을 수 있다
        //추가로 //원본 쿼리와 똑같이 조인을 해버리기 때문에 성능이 안나올 수 있다
        //따라서 count 쿼리가 필요하다면 별도로 작성해야 한다
        QueryResults<Member> result2 = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(result2.getTotal()).isEqualTo(4);

        Long count1 = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        assertThat(count1).isEqualTo(4);

        Long count2 = queryFactory
                .select(Wildcard.count) //count(*)
                .from(member)
                .fetchOne();

        assertThat(count1).isEqualTo(4);
    }

    //집합 & 집계
    @Test
    void aggregation() {

        List<Tuple> result1 = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result1.get(0);

        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(140);
        assertThat(tuple.get(member.age.avg())).isEqualTo(35);
        assertThat(tuple.get(member.age.max())).isEqualTo(50);
        assertThat(tuple.get(member.age.min())).isEqualTo(20);

        //groupBy & having 팀의 이름과 각 팀의 평균 나이
        List<Tuple> result2 = queryFactory
                .select(
                        team.name,
                        member.age.avg()
                )
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
//                .having(member.age.avg().gt(30))
                .fetch();

        Tuple teamA = result2.get(0);
        System.out.println("teamA = " + teamA);
        Tuple teamB = result2.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("TeamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(25);
        assertThat(teamB.get(team.name)).isEqualTo("TeamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(45);
    }

}