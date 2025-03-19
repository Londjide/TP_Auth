package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

/**
 * Classe gérant la première étape de réinitialisation de mot de passe.
 * Permet à l'utilisateur de demander un mot de passe temporaire
 * en fournissant son adresse email.
 * 
 * @author Equipe de développement
 * @version 1.0
 */
public class Reset  {

	private JFrame frame;
	private JTextField textField;

	
	/**
	 * Lance l'application de réinitialisation de mot de passe.
	 * 
	 * @param args Arguments de ligne de commande (non utilisés)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Reset window = new Reset();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * URL de connexion à la base de données SQLite.
	 */
	private static final String DB_URL = "jdbc:sqlite:users.db";
	
	
	/**
	 * Établit une connexion à la base de données.
	 * 
	 * @return Objet Connection pour interagir avec la base de données
	 */
	public static Connection connect( ) {
		 
		 Connection conn = null;
		 
		 try {
			 conn = DriverManager.getConnection(DB_URL);
			 System.out.println ("Connexion a SQLite etablie.");
		 }
		 catch (SQLException e) {
			 System.out.println(e.getMessage());
			 
			 
		 }
		 return conn;
	 }
	
	/**
	 * Constructeur de la classe Reset.
	 * Initialise l'interface utilisateur de réinitialisation.
	 */
	public Reset() {
		initialize();
	}

	/**
	 * Initialise les composants de l'interface utilisateur.
	 * Configure la fenêtre pour la saisie de l'email.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 554, 359);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Reinitialiser votre mot de passe");
		lblNewLabel.setBounds(166, 19, 202, 16);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Veuillez entrer votre email");
		lblNewLabel_1.setBounds(28, 126, 171, 16);
		frame.getContentPane().add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(213, 121, 179, 26);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Valider");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                resetPassword();
                
                
            }
        });
		
		btnNewButton.setBounds(412, 247, 117, 29);
		frame.getContentPane().add(btnNewButton);
	}
	
	/**
	 * Réinitialise le mot de passe de l'utilisateur.
	 * Génère un mot de passe temporaire et l'enregistre dans la base de données.
	 */
	private void resetPassword() {
        String email = textField.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Veuillez entrer votre email.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tempPassword = generateTempPassword();
        String hashedTempPassword = hashPassword(tempPassword);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE email = ?")) {

        	pstmt.setString(1, hashedTempPassword);
            pstmt.setString(2, email);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Votre mot de passe temporaire est: " + tempPassword, "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                Reset2 reset2 = new Reset2();
				frame.dispose(); // Fermer la fenêtre de reinitialisation
				reset2.afficher(); // Utiliser la nouvelle méthode
			
            } else {
                JOptionPane.showMessageDialog(frame, "Email non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erreur lors de la mise à jour du mot de passe.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Génère un mot de passe temporaire aléatoire.
     * Crée une chaîne de 8 caractères incluant des lettres majuscules et minuscules,
     * des chiffres et des caractères spéciaux.
     * 
     * @return Un mot de passe temporaire aléatoire
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder tempPassword = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }

        return tempPassword.toString();
    }
    
    /**
     * Crée un hachage SHA-256 du mot de passe fourni.
     * Sécurise le stockage des mots de passe dans la base de données.
     * 
     * @param password Le mot de passe à hacher
     * @return Le mot de passe haché sous forme de chaîne hexadécimale
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hachage du mot de passe", e);
        }
}
    
    /**
     * Rend la fenêtre de réinitialisation visible.
     * Méthode utilisée pour afficher la fenêtre après son initialisation.
     */
    public void afficher() {

		frame.setVisible(true);
}
}
