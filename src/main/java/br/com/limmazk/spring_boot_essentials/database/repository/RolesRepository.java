package br.com.limmazk.spring_boot_essentials.database.repository;

import br.com.limmazk.spring_boot_essentials.database.model.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<RolesEntity, Integer> {
}
