package models;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(JPATableroRepository.class)
public interface TableroRepository {
    Tablero add(Tablero tablero);

    Tablero update(Tablero tablero);

    Tablero findById(Long idTablero);

    Tablero findByName(String nombreTablero);

    List<Tablero> findRestByUserId(Long userId, boolean incluirPrivados);
}
