package uz.pdp.appjwtdbemailaudit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import uz.pdp.appjwtdbemailaudit.entity.enums.RoleName;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role implements GrantedAuthority { // role  ni avtorizatsiyalash

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING) // DB ga string holatda yozish [ default = ordinal numbers ]
    private RoleName roleName; // Enum calling type

    @Override
    public String getAuthority() {
        return roleName.name(); // ADMIN ni "ADMIN" holatida qaytaradi
    }
}
