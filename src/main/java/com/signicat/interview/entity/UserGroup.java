package com.signicat.interview.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Generated
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_group")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "groups",
            cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH})
    private Set<Subject> users;

    public UserGroup(String name) {
        this.name = name;
    }

    public UserGroup(Long id, String name) {
        this.id= id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserGroup other = (UserGroup) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }
}
