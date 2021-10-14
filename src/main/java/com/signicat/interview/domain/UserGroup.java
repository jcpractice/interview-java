package com.signicat.interview.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_group")
public class UserGroup {
    @Id
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToMany(mappedBy = "groups")
    private Set<Subject> users = new HashSet<>();
    @OneToMany(mappedBy = "userGroup")
    Set<SubjectAndUserGroupsRel> relationship;
}
