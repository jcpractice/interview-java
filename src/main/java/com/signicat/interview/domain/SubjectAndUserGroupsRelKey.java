package com.signicat.interview.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Embeddable
@Data
@Getter
@Setter
public class SubjectAndUserGroupsRelKey implements Serializable {
    @Column(name="subject_id")
    private long subjectId;

    @Column(name="user_group_id")
    private long userGroupId;
}
