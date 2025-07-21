package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.*;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionDAO extends JpaRepository<Ubicacion, Long> {

    @Query("from Espiritu i where i.ubicacion.id = :ubicacion " +
            "order by i.nombre asc")
    List<Espiritu> espiritusEn(@Param("ubicacion") Long ubicacionId);

    @Query("from Medium i where i.ubicacion.id = :ubicacion and i.espiritus IS EMPTY")
    List<Medium> mediumsSinEspiritusEn(@Param("ubicacion") Long ubicacionId);


    @Query("from Ubicacion i where i.id in :ids order by i.id asc")
    List<Ubicacion> findAllByIdOrdered(@Param("ids") List<Long> ids);

    @Query("SELECT EXISTS (SELECT 1 FROM Espiritu e WHERE e.ubicacion.id = :ubicacionId)")
    boolean existenEspiritusEnUbicacion(@Param("ubicacionId") Long ubicacionId);

    @Query("SELECT EXISTS (SELECT 1 FROM Medium m WHERE m.ubicacion.id = :ubicacionId)")
    boolean existenMediumsEnUbicacion(@Param("ubicacionId") Long ubicacionId);

    boolean existsByNombreAndDeletedAtIsNull(String nombre);
}
