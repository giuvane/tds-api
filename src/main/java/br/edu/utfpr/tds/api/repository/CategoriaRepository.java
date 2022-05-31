package br.edu.utfpr.tds.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.tds.api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	
}
