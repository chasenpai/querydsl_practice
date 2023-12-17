package ex.querydsl.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberSearch {

    private String username;

    private String teamName;

    private Integer ageGoe;

    private Integer ageLoe;

    @Builder
    public MemberSearch(String username, String teamName, Integer ageGoe, Integer ageLoe) {
        this.username = username;
        this.teamName = teamName;
        this.ageGoe = ageGoe;
        this.ageLoe = ageLoe;
    }
}
