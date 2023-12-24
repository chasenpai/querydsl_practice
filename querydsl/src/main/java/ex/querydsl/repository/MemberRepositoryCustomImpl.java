package ex.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import ex.querydsl.dto.QMemberTeamDto;
import lombok.RequiredArgsConstructor;

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
