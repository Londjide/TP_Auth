package main;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Classe de test pour la fonctionnalité de connexion.
 * Teste les méthodes de validation d'email et de mot de passe.
 * Utilise JUnit pour automatiser les tests.
 * 
 * @author Equipe de développement
 * @version 1.0
 */
public class LoginTest {

	 /**
     * Teste la validation d'email avec différents formats.
     * Vérifie que les formats valides sont acceptés et 
     * que les formats invalides sont rejetés.
     */
	 @Test
	    public void testValidEmail() {
	        assertTrue(Inscription.isValidEmail("test@example.com"));
	        assertTrue(Inscription.isValidEmail("username@domain.co"));
	        assertFalse(Inscription.isValidEmail("invalid-email"));
	        assertFalse(Inscription.isValidEmail("user@domain"));
	        assertFalse(Inscription.isValidEmail("user@.com"));
	        assertTrue(Connexion.isValidEmail("toto@example.com"));
	        assertTrue(Connexion.isValidEmail("username@domain.co"));
	        assertFalse(Connexion.isValidEmail("invalid-email"));
	        assertFalse(Connexion.isValidEmail("user@domain"));
	        assertFalse(Connexion.isValidEmail("user@.com"));
	    
	    }

	    /**
	     * Teste la validation de mot de passe selon les critères de sécurité.
	     * Vérifie qu'un mot de passe respectant tous les critères est accepté
	     * et qu'un mot de passe ne respectant pas tous les critères est rejeté.
	     */
	    @Test
	   public void testValidPassword() {
	        assertTrue(Inscription.isValidPassword("StrongP@ss1"));
	        assertFalse(Inscription.isValidPassword("weakpass"));
	        assertFalse(Inscription.isValidPassword("Short1!"));
	        assertFalse(Inscription.isValidPassword("NoSpecialChar1"));
	        assertFalse(Inscription.isValidPassword("nouppercase1!"));
	    }

	    
	    
	   
	    
	  

}
