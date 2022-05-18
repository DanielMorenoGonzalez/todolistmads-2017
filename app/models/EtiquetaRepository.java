package models;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(JPAEtiquetaRepository.class)
public interface EtiquetaRepository {
    Etiqueta add(Etiqueta etiqueta);

    Etiqueta update(Etiqueta etiqueta);

    void delete(Long idEtiqueta);

    Etiqueta findById(Long idEtiqueta);

    Etiqueta findByTituloConUsuario(String tituloEtiqueta, Long idUsuario);

    Etiqueta findByTituloConTablero(String tituloEtiqueta, Long idTablero);

    List<Etiqueta> findAll(Long idUsuario);
}
