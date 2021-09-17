package uz.pdp.appjwtdbemailaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjwtdbemailaudit.entity.Role;
import uz.pdp.appjwtdbemailaudit.entity.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByRoleName(RoleName roleName);
}
