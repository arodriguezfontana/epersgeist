package ar.edu.unq.epersgeist.persistencia.dao.impl;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.EstadisticaDAO;
import jakarta.persistence.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public class EstadisticaDaoImpl implements EstadisticaDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Medium mediumConMasEspiritusDeTipoEn(TipoEspiritu tipoEspiritu, Long idUbicacion) {
            String jpql = """
                    SELECT m
                    FROM Medium m
                    JOIN Espiritu e on e.medium.id = m.id
                    WHERE m.ubicacion.id = :idUbi AND e.tipo = 1
                    group by m
                    ORDER BY count(e.tipo) DESC
                    """;
        TypedQuery<Medium> query = entityManager.createQuery(jpql, Medium.class);
        query.setParameter("idUbi", idUbicacion);


        try {
            return query.setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Override
    public int espiritusDeTipoEnUbicacion(TipoEspiritu tipoEspiritu, Long ubicacionId) {
        String jpql = "select count(*) from Espiritu e where e.ubicacion.id = :idUbi and e.tipo = 1";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("idUbi", ubicacionId);

        return query.getSingleResult().intValue();
    }

    @Override
    public List<Ubicacion> findTopSantuarioWithMoreDemoniosThanAngeles(Pageable pageable) {
        String sql = """
                    SELECT u.*
                    FROM ubicacion u
                    JOIN espiritu e ON u.id = e.ubicacion_id
                    WHERE u.tipo_ubicacion = 0
                    GROUP BY u.id
                    HAVING 
                    SUM(CASE WHEN e.tipo = 1 THEN 1 ELSE 0 END) >
                    SUM(CASE WHEN e.tipo = 0 THEN 1 ELSE 0 END)
                    ORDER BY 
                    SUM(CASE WHEN e.tipo = 1 THEN 1 ELSE 0 END) -
                    SUM(CASE WHEN e.tipo = 0 THEN 1 ELSE 0 END) DESC
                    """;
        Query query = entityManager.createNativeQuery(sql, Ubicacion.class);

        return query.getResultList();
    }

    @Override
    public int espiritusDeTipoLibres(TipoEspiritu tipoEspiritu, Long ubicacionId) {
        String jpql = """
                        select count(*)
                        from Espiritu e 
                        where e.tipo = :tipoEspiritu 
                        and e.medium is null 
                        and e.ubicacion.id = :ubicacionId
                      """;
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("tipoEspiritu", tipoEspiritu);
        query.setParameter("ubicacionId", ubicacionId);

        return query.getSingleResult().intValue();
    }
}
