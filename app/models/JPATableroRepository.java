package models;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;
import javax.persistence.NoResultException;

public class JPATableroRepository implements TableroRepository {
    private JPAApi jpaApi;

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    public JPATableroRepository(JPAApi api) {
        this.jpaApi = api;
    }

    @Override
    public Tablero add(Tablero tablero) {
        return jpaApi.withTransaction(entityManager -> {
            entityManager.persist(tablero);
            entityManager.flush();
            entityManager.refresh(tablero);
            return tablero;
        });
    }

    @Override
    public Tablero update(Tablero tablero) {
        return jpaApi.withTransaction(entityManager -> entityManager.merge(tablero));
    }

    @Override
    public Tablero findById(Long idTablero) {
        return jpaApi.withTransaction(entityManager -> entityManager.find(Tablero.class, idTablero));
    }

    @Override
    public Tablero findByName(String nombre) {
        return jpaApi.withTransaction(entityManager -> {
            TypedQuery<Tablero> query = entityManager.createQuery("select t from Tablero t where t.nombre = :nombre", Tablero.class);
            try {
              Tablero tablero = query.setParameter("nombre", nombre).getSingleResult();
              return tablero;
            } catch(NoResultException e) {
              return null;
            }
        });
    }

    @Override
    public List<Tablero> findRestByUserId(Long userId, boolean incluirPrivados) {

        return jpaApi.withTransaction(entityManager -> {
            String  privados="0";
            if(incluirPrivados==true){
                privados="1";
            }
            final TypedQuery<Tablero> typedQuery = entityManager.createQuery("select t from Tablero t " +
                    "where t.administrador.id != :userId " +
                    "and :userId not in (select p.id from t.participantes p) and t.privado ="+privados, Tablero.class);
            typedQuery.setParameter("userId", userId);
            return typedQuery.getResultList();
        });
    }
}
