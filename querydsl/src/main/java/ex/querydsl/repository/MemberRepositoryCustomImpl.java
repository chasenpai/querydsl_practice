package ex.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import ex.querydsl.dto.QMemberTeamDto;
import ex.querydsl.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static ex.querydsl.entity.QMember.member;
import static ex.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl
//        extends QuerydslRepositorySupport
        implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //리포지토리 지원 - QuerydslRepositorySupport
    //스프링데이터가 지원하는 페이징을 querydsl 로 편리하게 변환 가능
    //EntityManager 를 제공
    //하지만 querydsl 3.x 버전을 대상으로 만든 것이라 JPQQueryFactory 를 사용할 수 없음
    //select 로 시작할 수 없고 from 부터 시작
    //스프링 데이터 Sort 기능이 정상 동작하지 않음
//    public MemberRepositoryCustomImpl() {
//        super(Member.class);
//    }

    @Override
    public List<MemberTeamDto> search(MemberSearch search) {
        return queryFactory
                .select(
                        new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        searchByUsername(search.getUsername()),
                        searchByTeamName(search.getTeamName()),
                        searchByAgeBetween(search.getAgeGoe(), search.getAgeLoe())
                )
                .fetch();
    }

//    public List<MemberTeamDto> searchV2(MemberSearch search) {
//       return from(member)
//             .leftJoin(member.team, team)
//             .where(
//                     searchByUsername(search.getUsername()),
//                     searchByTeamName(search.getTeamName()),
//                     searchByAgeBetween(search.getAgeGoe(), search.getAgeLoe())
//             )
//             .select(
//                     new QMemberTeamDto(
//                             member.id.as("memberId"),
//                             member.username,
//                             member.age,
//                             team.id.as("teamId"),
//                             team.name.as("teamName")
//                     )
//             )
//             .fetch();
//    }
    
    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearch search, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(
                        new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        searchByUsername(search.getUsername()),
                        searchByTeamName(search.getTeamName()),
                        searchByAgeBetween(search.getAgeGoe(), search.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

//        long count1 = queryFactory
//                .selectFrom(member)
//                .fetchCount(); //Deprecated
//
//        Long count2 = queryFactory
//                .select(member.count())
//                .from(member)
//                .fetchOne();
//
        Long count3 = queryFactory
                .select(Wildcard.count) //count(*)
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        searchByUsername(search.getUsername()),
                        searchByTeamName(search.getTeamName()),
                        searchByAgeBetween(search.getAgeGoe(), search.getAgeLoe())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count3);
    }

//    public Page<MemberTeamDto> searchPageSimpleV2(MemberSearch search, Pageable pageable) {
//
//        JPQLQuery<MemberTeamDto> jpqQuery = from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        searchByUsername(search.getUsername()),
//                        searchByTeamName(search.getTeamName()),
//                        searchByAgeBetween(search.getAgeGoe(), search.getAgeLoe())
//                )
//                .select(
//                        new QMemberTeamDto(
//                                member.id.as("memberId"),
//                                member.username,
//                                member.age,
//                                team.id.as("teamId"),
//                                team.name.as("teamName")
//                        )
//                );
//
//        JPQLQuery<MemberTeamDto> result = getQuerydsl().applyPagination(pageable, jpqQuery);
//        List<MemberTeamDto> content = result.fetch();
//
//        Long count = from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        searchByUsername(search.getUsername()),
//                        searchByTeamName(search.getTeamName()),
//                        searchByAgeBetween(search.getAgeGoe(), search.getAgeLoe())
//                )
//                .select(Wildcard.count)
//                .fetchOne();
//
//        return new PageImpl<>(content, pageable, count);
//    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearch search, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(
                        new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        searchByUsername(search.getUsername()),
                        searchByTeamName(search.getTeamName()),
                        searchByAgeBetween(search.getAgeGoe(), search.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        searchByUsername(search.getUsername()),
                        searchByTeamName(search.getTeamName()),
                        searchByAgeBetween(search.getAgeGoe(), search.getAgeLoe())
                );

        //count 쿼리 생략 LongSupplier - 카운트 쿼리가 필요할 때 날린다
        //페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈 보다 작을 때
        //마지막 페이지 일 때
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private BooleanExpression searchByUsername(String username) {
        return hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression searchByTeamName(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression searchByAgeGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression searchByAgeLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    private BooleanExpression searchByAgeBetween(Integer ageGoe, Integer ageLoe) {
        if(ageGoe == null || ageLoe == null) return null;
        return searchByAgeGoe(ageGoe).and(searchByAgeLoe(ageLoe));
    }


}
