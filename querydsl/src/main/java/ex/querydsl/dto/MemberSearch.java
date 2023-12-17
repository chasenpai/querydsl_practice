package ex.querydsl.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberSearch {

    private String username;

    private String teamName;

    private Integer ageGoe;

    private Integer ageLoe;

}
