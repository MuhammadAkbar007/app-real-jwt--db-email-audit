package uz.pdp.appjwtdbemailaudit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;



    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;



    @Column(nullable = false, updatable = false) // tashqi tomondan o'zgartirish ruxsati
    @CreationTimestamp // when created
    private Timestamp createdAt;

    @UpdateTimestamp // when updated [ @LastModified ]
    private Timestamp updatedAt;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled;

    private String emailCode;

    // <--------------- override userDetails methods ---------------> //

    /**
     * user Roles and / or Permissions
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    /**
     * unique defining field to use
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * account eskirmaganmi
     */
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    /**
     * account bloklanmaganmi
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    /**
     * ishonchliligi eskirmaganmi
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    /**
     * account activemi
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
