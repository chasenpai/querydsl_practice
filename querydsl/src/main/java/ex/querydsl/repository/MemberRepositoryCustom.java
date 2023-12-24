package ex.querydsl.repository;

import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearch search);

}
