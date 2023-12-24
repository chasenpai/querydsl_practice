package ex.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import ex.querydsl.dto.QMemberTeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static ex.querydsl.entity.QMember.member;
import static ex.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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
