package com.signicat.interview.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subject")
public class Subject {
    @Id
    private Long id;
    @Column(unique = true, updatable = false, nullable = false)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    @Column(columnDefinition = "char(1) default 'R'")
    private char profileType;
    @Column(columnDefinition = "int default 1")
    private int status;
    private Timestamp creationDate;
    @ManyToMany
    private Set<UserGroup> groups = new HashSet<>();

    @OneToMany(mappedBy = "subject")
    Set<SubjectAndUserGroupsRel> relationship;
}
