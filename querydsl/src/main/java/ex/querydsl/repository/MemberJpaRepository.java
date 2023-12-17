package ex.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import ex.querydsl.dto.QMemberTeamDto;
import ex.querydsl.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static ex.querydsl.entity.QMember.member;
import static ex.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.*;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findAllUseQueryDsl() {
        return queryFactory.selectFrom(member).fetch();
    }

    public List<Member> findByUsername(String username) {
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findByUsernameUseQueryDsl(String username) {
        return queryFactory.selectFrom(member).where(member.username.eq(username)).fetch();
    }

    public List<MemberTeamDto> searchV1(MemberSearch search) {

        BooleanBuilder builder = new BooleanBuilder();
        if(hasText(search.getUsername())) {
            builder.and(member.username.eq(search.getUsername()));
        }
        if(hasText(search.getTeamName())) {
            builder.and(team.name.eq(search.getTeamName()));
        }
        if(search.getAgeGoe() != null) {
            builder.and(member.age.goe(search.getAgeGoe()));
        }
        if(search.getAgeLoe() != null) {
            builder.and(member.age.loe(search.getAgeLoe()));
        }

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
                .where(builder)
                .fetch();
    }

    public List<MemberTeamDto> searchV2(MemberSearch search) {
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
//                        searchByAgeGoe(search.getAgeGoe()),
//                        searchByAgeLoe(search.getAgeLoe())
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
        return searchByAgeGoe(ageGoe).and(searchByAgeLoe(ageLoe));
    }

}
