package tn.esprit.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.entities.Classe;
import tn.esprit.entities.CoursClassroom;
import tn.esprit.entities.Niveau;
import tn.esprit.entities.Specialite;
import tn.esprit.entities.Utilisateur;
import tn.esprit.repositories.ClasseRepository;
import tn.esprit.repositories.CoursClassroomRepository;
import tn.esprit.repositories.UtilisateurRepository;

@Slf4j
@RestController
@RequestMapping("rest")
public class ExamRestController {

	@Autowired
	ClasseRepository classeRepository;
	@Autowired
	CoursClassroomRepository coursClassroomRepository;
	@Autowired
	UtilisateurRepository utilisateurRepository;




	//http://localhost:9090/rest/add-Utilisateur
	@PostMapping("/add-Utilisateur")
	public Utilisateur ajouter(@RequestBody Utilisateur entity) {
		return	utilisateurRepository.save(entity);
	}

	//http://localhost:9090/rest/add-Classe
	@PostMapping("/add-Classe")
	public Classe ajouterClasse(@RequestBody Classe entity) {
		return	classeRepository.save(entity);
	}


	//http://localhost:9090/rest/add-CoursClassroom/{codeClasse}
	@PostMapping("/add-CoursClassroom/{codeClasse}")
	public CoursClassroom ajouterCoursClassroom(@RequestBody CoursClassroom entity,@PathVariable int codeClasse) {
		Classe classe =classeRepository.findById(codeClasse).orElse(null);
		entity.setClasse(classe);
		return	coursClassroomRepository.save(entity);
	}


	//http://localhost:9090/rest/affecterUtilisateurClasse/{idUtilisateur}/{codeClasse}
	@GetMapping("/affecterUtilisateurClasse/{idUtilisateur}/{codeClasse}") 
	public void affecterUtilisateurClasse(@PathVariable int idUtilisateur, @PathVariable Integer codeClasse ) {

		Utilisateur utilisateur =utilisateurRepository.findById(idUtilisateur).orElse(null);
		Classe classe =classeRepository.findById(codeClasse).orElse(null);

		//n'oubliez pas que c'est une relation unidirectionnel coté user 
		utilisateur.setClasse(classe);
		utilisateurRepository.save(utilisateur);


	}
	
	
	//http://localhost:9090/rest/desaffecterCoursClassroomClasse/{idCours}
	@GetMapping("/desaffecterCoursClassroomClasse/{idCours}") 
	public void desaffecterCoursClassroomClasse(@PathVariable Integer idCours) {

		CoursClassroom coursClassroom =coursClassroomRepository.findById(idCours).orElse(null);

		coursClassroom.setClasse(null);
		coursClassroomRepository.save(coursClassroom);


	}



	//http://localhost:9090/rest/nbUtilisateursParNiveau/{niveau}
	@GetMapping("/nbUtilisateursParNiveau/{niveau}")
	public Integer nbUtilisateursParNiveau(@PathVariable Niveau niveau ) {

		Integer nbr = 0;

		// first methode
		/*
		List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
		for (Utilisateur user : utilisateurs) {
			if(user.getClasse().getNiveau().equals(niveau)) {
				nbr ++;
			}
		}

		return nbr;

		 */

		//Query Method (Jpa keyWords)
		return	utilisateurRepository.countByClasseNiveau(niveau);
		//Query Method (Jpql)
		//		return	utilisateurRepository.countByClasseNiveauJPQL(niveau);
		//		Query Method (Sql)
		//		return	utilisateurRepository.countByClasseNiveauSQL(niveau);

	}
	
	
	@Scheduled(fixedRate = 60000)
	public void archiverCoursClassrooms() {
		List<CoursClassroom> classrooms = coursClassroomRepository.findAll();
		for (CoursClassroom coursClassroom : classrooms) {
			coursClassroom.setArchive(false);
			coursClassroomRepository.save(coursClassroom);
		}
	}



	//http://localhost:9090/rest/nbHeuresParSpecEtNiv/{specialite}/{niveau}
	@GetMapping("/nbHeuresParSpecEtNiv/{specialite}/{niveau}")
	public Integer nbHeuresParSpecEtNiv(@PathVariable Specialite specialite, @PathVariable Niveau niveau) {

		//normale
//		Integer nbrHeures = 0;
//		List<CoursClassroom> classrooms = coursClassroomRepository.findAll();
//		for (CoursClassroom coursClassroom : classrooms) {
//			if(coursClassroom.getSpecialite().equals(specialite)&& coursClassroom.getClasse().getNiveau().equals(niveau))
//				nbrHeures = nbrHeures + coursClassroom.getNrHeures();
//		}


		//stream
		Integer nbrHeures = 0;
		nbrHeures=	coursClassroomRepository.findAll()
				.stream()
				.filter(c -> c.getSpecialite().equals(specialite) && c.getClasse().getNiveau().equals(niveau))
				.mapToInt(CoursClassroom::getNrHeures)
				.sum();



		return nbrHeures;

	}



}
