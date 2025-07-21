package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EspirituDAO extends JpaRepository<Espiritu, Long> {

    Page<Espiritu> findByTipo(TipoEspiritu tipo, Pageable pageable);

    @Query(value = "SELECT NOT EXISTS (SELECT 1 FROM espiritu_amo WHERE espiritu_id = :idDominado AND amo_id = :idDominante)", nativeQuery = true)
    boolean noTieneComoAmoA(@Param("idDominante") Long idDominante, @Param("idDominado") Long idDominado);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM espiritu_amo WHERE espiritu_id = :idEspiritu)", nativeQuery = true)
    boolean estaSiendoDominado(@Param("idEspiritu") Long idEspiritu);
}
