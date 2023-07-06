package tn.esprit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tn.esprit.entities.Niveau;
import tn.esprit.entities.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
	
	//Query Method
	public Integer countByClasseNiveau(Niveau niveau);

	
	// JPQL Query
	@Query("SELECT COUNT(u) FROM Utilisateur u INNER JOIN Classe c on u.classe.codeClasse=c.codeClasse WHERE c.niveau = :niveau")
	public Integer countByClasseNiveauJPQL(@Param("niveau") Niveau niveau); 
	 
	// SQL Query
	@Query(value="SELECT COUNT(*) FROM Utilisateur u INNER JOIN Classe c on u.classe.codeClasse=c.codeClasse WHERE c.niveau = :niveau",nativeQuery =true)
	public Integer countByClasseNiveauSQL(@Param("niveau") Niveau niveau);
	
	
}