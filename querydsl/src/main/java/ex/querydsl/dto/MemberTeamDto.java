package ex.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import ex.querydsl.entity.Member;
import lombok.Data;

@Data
public class MemberTeamDto {

    private Long memberId;

    private String username;

    private int age;

    private Long teamId;

    private String teamName;

    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public MemberTeamDto(Member member) {
        this.memberId = member.getId();
        this.username = member.getUsername();
        this.age = member.getAge();
    }

}
