package departamentoDefensa.repositories;

import departamentoDefensa.entities.PersonalFuerzaPublica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;


import java.io.Serializable;
import java.util.Optional;

@Repository
public interface PersonalFuerzaPublicaRepository extends JpaRepository<PersonalFuerzaPublica, Long> {

    Optional<PersonalFuerzaPublica> findByIdentificacion(String identificacion);

    Optional<PersonalFuerzaPublica> findByEmail(String email);

    Optional<PersonalFuerzaPublica> findById(Long id);
    // 🔥 Nuevo método para buscar por idFuerza
    Optional<PersonalFuerzaPublica> findByIdFuerza(String idFuerza);
}
