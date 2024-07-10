package com.grinder.domain.entity;

import com.grinder.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "member", indexes = {
        @Index(name = "idx_email", columnList = "email")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity{

    @Id
    @Column(name = "member_id", updatable = false, length = 36)
    private String memberId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 16)
    private Role role;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "phone_num", nullable = false, length = 11)
    private String phoneNum;

    @PrePersist
    public void prePersist() {
        memberId = memberId == null ? UUID.randomUUID().toString() : memberId;
        role = role == null ? Role.MEMBER : role;
        isDeleted = isDeleted == null ? false : isDeleted;
    }

    public void verify() {
        role = role == Role.MEMBER ? Role.VERIFIED_MEMBER : role;
    }

    public void cancelVerify() {
        role = role == Role.VERIFIED_MEMBER ? Role.MEMBER : role;
    }

    public boolean delete() {
        isDeleted = true;
        return true;
    }

    public boolean recover() {
        isDeleted = false;
        return true;
    }

    public void setPassword(String password){ this.password = password;}

    public void toSeller() {
        this.role = Role.SELLER;
    }
}