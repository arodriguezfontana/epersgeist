package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;;

@Repository
public interface MediumDAO extends JpaRepository<Medium, Long> {

    @Query(
            "from Espiritu i where i.medium.id = :idMedium"
    )
    List<Espiritu> espiritus(@Param("idMedium") Long mediumId);

    @Query(
            "select i from Espiritu i where i.tipo = :tipo and " +
                    "i.medium.id = :idMedium"
    )
    List<Espiritu> espiritusTipo(@Param("idMedium") Long mediumId,@Param("tipo") TipoEspiritu tipo);


    @Query("SELECT EXISTS (SELECT 1 FROM Espiritu e WHERE e.medium.id = :idMedium)")
    boolean tieneEspiritus(@Param("idMedium") Long mediumId);
}
