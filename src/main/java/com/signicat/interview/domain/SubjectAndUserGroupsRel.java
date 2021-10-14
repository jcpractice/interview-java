package com.signicat.interview.domain;
/**
 * This additional relation table is kept to be used in future for any new fields.
 */

import lombok.*;

import javax.persistence.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subject_user_group")
public class SubjectAndUserGroupsRel {
    @EmbeddedId
    private SubjectAndUserGroupsRelKey id;
    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    Subject subject;

    @ManyToOne
    @MapsId("userGroupId")
    @JoinColumn(name = "user_group_id")
    UserGroup userGroup;
}
