package ex.querydsl.repository;

import ex.querydsl.dto.MemberSearch;
import ex.querydsl.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearch search);

    Page<MemberTeamDto> searchPageSimple(MemberSearch search, Pageable pageable);

    Page<MemberTeamDto> searchPageComplex(MemberSearch search, Pageable pageable);

}
