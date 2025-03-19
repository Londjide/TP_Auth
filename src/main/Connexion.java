package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

import javax.swing.*;
import java.util.Properties;
import com.jtattoo.plaf.smart.SmartLookAndFeel; // Choose any theme

//import Design.Inscription;

/**
 * Classe de gestion de l'authentification des utilisateurs.
 * Permet aux utilisateurs de se connecter à l'application avec leur email et mot de passe.
 * Vérifie les identifiants par rapport à la base de données SQLite.
 * 
 * @author Equipe de développement
 * @version 1.0
 */
@SuppressWarnings("unused")
public class Connexion extends JFrame {

	/** Fenêtre principale de l'interface de connexion */
	private JFrame frame;
	/** Champ de saisie pour l'adresse email de l'utilisateur */
	private JTextField usernameField;
	/** Champ de saisie sécurisé pour le mot de passe de l'utilisateur */
	private JPasswordField passwordField;
	
	
	
	/**
	 * Validates an email address using a regex pattern.
	 * 
	 * @param email The email address to validate.
	 * @return true if the email is valid, false otherwise.
	 */
    static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
	/**
	 *  Validates an password using regex pattern.
	 *  
	 * @param Password The Password to validate.
	 * @return true if the Password is valid, false otherwise. 
	 */
    static boolean isValidPassword(String password) {
    	
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+<>?]).{12,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    
    /**
	 * Hashes a password using SHA-256.
	 * 
	 * @param password The password to hash.
	 * @return The hashed password as a hexadecimal string.
	 */
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    
    
    
    /**
	 * Main method to launch the application.
	 * Creates and displays the login window.
	 * 
	 * @param args Command line arguments (not used)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

		            // Set JTattoo Look and Feel
//		            UIManager.setLookAndFeel(new SmartLookAndFeel());
					Connexion window = new Connexion();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor for the Connexion class.
	 * Initializes the UI components.
	 */
	public Connexion() {
		initialize();
	}
	
	
	 /**
	 * Establishes a connection to the SQLite database.
	 * 
	 * @return A Connection object to the database
	 */
	 private static final String DB_URL = "jdbc:sqlite:users.db";

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
	 * Initializes the UI components and sets up event listeners.
	 * Creates the login form with fields for email and password.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 603, 405);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		usernameField = new JTextField();
		usernameField.setBounds(317, 73, 130, 26);
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		JButton btnNewButton = new JButton("Valider");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/**
				 * Handles the login button action.
				 * 
				 * @param e The action event.
				 */
				 String email = usernameField.getText();
			        String password = new String(passwordField.getPassword());
			        
			        if (email.isEmpty() || password.isEmpty()) {
			            JOptionPane.showMessageDialog(frame, "Email and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
			            return;
			        }

			        // Validate email format
			        if (!isValidEmail(email)) {
			            JOptionPane.showMessageDialog(frame, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
			            return;
			        }

			        // Validate password strength
			        if (!isValidPassword(password)) {
			            JOptionPane.showMessageDialog(frame, "Password must be at least 12 characters long, contain a mix of uppercase, lowercase, digits, and special characters.", "Error", JOptionPane.ERROR_MESSAGE);
			            return;
			        }
			        
			        String hashedPassword = hashPassword(password);
			        
			        try (Connection conn = DriverManager.getConnection(DB_URL);
			             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");) {
			            
			            pstmt.setString(1, email);
			            pstmt.setString(2, hashedPassword);
			            
			            ResultSet rs = pstmt.executeQuery();
			            if (rs.next()) {
			                JOptionPane.showMessageDialog(frame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
			                
			                Admin admin = new Admin();
			                frame.dispose();
			                admin.afficher();
			                
			            } else {
			                JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
			            }
			        } catch (SQLException ex) {
			            ex.printStackTrace();
			            JOptionPane.showMessageDialog(frame, "An error occurred while authenticating the user.", "Error", JOptionPane.ERROR_MESSAGE);
			        }
			    }
			
			
			
			
			
			
		});
		btnNewButton.setBounds(457, 274, 117, 29);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Login");
		lblNewLabel.setBounds(202, 73, 61, 16);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Mot de passe");
		lblNewLabel_1.setBounds(159, 138, 93, 16);
		frame.getContentPane().add(lblNewLabel_1);
		
		JButton btnAnnuler = new JButton("Inscription");
		btnAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Inscription inscription = new Inscription();
				frame.dispose(); // Fermer la fenêtre de connexion
				inscription.afficher(); // Utiliser la nouvelle méthode
			
				
				
				
			}
			
			
			
		});
		btnAnnuler.setBounds(16, 274, 117, 29);
		frame.getContentPane().add(btnAnnuler);
		
		JLabel lblNewLabel_2 = new JLabel("Connectez-vous");
		lblNewLabel_2.setBounds(251, 16, 210, 16);
		frame.getContentPane().add(lblNewLabel_2);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(317, 138, 130, 26);
		frame.getContentPane().add(passwordField);
		
		JLabel lblNewLabel_3 = new JLabel("Vous n'avez pas d'identifiant ? Inscrivez-vous ");
		lblNewLabel_3.setBounds(6, 246, 304, 16);
		frame.getContentPane().add(lblNewLabel_3);
		
		JLabel lblNewLabel_3_1 = new JLabel("Vous avez oubliez votre mot de passe? Reinitialiser le!");
		lblNewLabel_3_1.setBounds(6, 315, 351, 16);
		frame.getContentPane().add(lblNewLabel_3_1);
		
		JButton btnReinitialisation = new JButton("Reinitialisation");
		btnReinitialisation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Reset reset = new Reset();
				frame.dispose(); // Fermer la fenêtre de reinitialisation
				reset.afficher(); // Utiliser la nouvelle méthode
			
				
			}
		});
		btnReinitialisation.setBounds(6, 342, 138, 29);
		frame.getContentPane().add(btnReinitialisation);
	}
	
	/**
	 * Makes the login window visible.
	 * Used to display the window after initialization.
	 */
	public void afficher() {

		frame.setVisible(true);
}
}
