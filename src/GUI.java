/*	Created by: Tyler Wong
 * 	Date: August 10th, 2014
 * 	Purpose: Main class which initializes the GUI and accepts user input for
 * 	email and password. Also, encrypts saved login information.
 */

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class GUI extends JFrame {
	
    // declare class level gui componenets           
    private JPanel accountPanel;
    private JTextField email1;
    private JTextField email2;
    private JTextField email3;
    private JTextField email4;
    private JTextField email5;
    private JLabel emailLabel;
    private JPasswordField password1;
    private JPasswordField password2;
    private JPasswordField password3;
    private JPasswordField password4;
    private JPasswordField password5;
    private JLabel passwordLabel;
    private JButton saveButton;
    private JButton startButton;   
    
    AnswerWorker worker = new AnswerWorker();
    
	Bing startBing;
	
    public static void main(String args[]) {
        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }    
    
    public GUI() {
        initComponents();
    }
     
    // Load GUI                        
    private void initComponents() {

        accountPanel = new JPanel();
        
        // initialize variables that open the config file and read the username nad passwords
        Properties prop = new Properties();
        InputStream input = null;
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("}@d[HZPn{;$&1c81oPui");
        OutputStream output = null;
        
        // initialize all swing componenets
        email1 = new JTextField();
        email2 = new JTextField();
        email3 = new JTextField();
        email4 = new JTextField();
        email5 = new JTextField();
        password1 = new JPasswordField();
        password2 = new JPasswordField();
        password3 = new JPasswordField();
        password4 = new JPasswordField();
        password5 = new JPasswordField();
        emailLabel = new JLabel();
        passwordLabel = new JLabel();
        saveButton = new JButton();
        startButton = new JButton();

        // set window details
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("SearchBot");
        accountPanel.setBorder(BorderFactory.createTitledBorder(null, "Account Information", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));

        // load logins
        try {
            String appdata = System.getenv("APPDATA");      // gets the path to the local roaming profile
            input = new FileInputStream(appdata + "/BingBot/config.properties");
            prop.load(input);   // load the file

            // sets the account information in the UI
            email1.setText(prop.getProperty("1.email"));
            password1.setText(encryptor.decrypt(prop.getProperty("1.password")));

            email2.setText(prop.getProperty("2.email"));
            password2.setText(encryptor.decrypt(prop.getProperty("2.password")));

            email3.setText(prop.getProperty("3.email"));
            password3.setText(encryptor.decrypt(prop.getProperty("3.password")));

            email4.setText(prop.getProperty("4.email"));
            password4.setText(encryptor.decrypt(prop.getProperty("4.password")));

            email5.setText(prop.getProperty("5.email"));
            password5.setText(encryptor.decrypt(prop.getProperty("5.password")));
        }
        catch(IOException e) {
            String appdata = System.getenv("APPDATA");      // gets the path to the local roaming profile
            new File(appdata + "/BingBot").mkdir();   // creates a folder to put the properties file in
            try {
                output = new FileOutputStream(appdata + "/BingBot/config.properties");
            }
            catch(Exception e2) { }
        }
        finally {   // clean up
            if(input != null) {
                try {
                    input.close();
                }
                catch(IOException e) {}
            }
        }

        // set text for the JLabels and JButtons
        emailLabel.setText("Email");
        passwordLabel.setText("Password");
        saveButton.setText("Save");
        startButton.setText("Start");
        
        // calls the action performed methods below when save or start is clicked
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        // CLeanup if program closed. Force close phantomjs if open.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	try {
            		Runtime.getRuntime().exec("taskkill /F /IM phantomjs.exe");
            	}
            	catch(Exception e) {}
            }
        });
        
        // group and organize all the componenets
        GroupLayout accountPanelLayout = new GroupLayout(accountPanel);
        accountPanel.setLayout(accountPanelLayout);
        accountPanelLayout.setHorizontalGroup(
            accountPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(accountPanelLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(emailLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(passwordLabel)
                .addGap(66, 66, 66))
            .addGroup(accountPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(saveButton)
                    .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(email2)
                        .addComponent(email3)
                        .addComponent(email4)
                        .addComponent(email5, GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                        .addComponent(email1)))
                .addGap(18, 18, 18)
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(password1, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password2, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password3, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password4, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password5, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                    .addComponent(startButton))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        accountPanelLayout.setVerticalGroup(
            accountPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, accountPanelLayout.createSequentialGroup()
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel)
                    .addComponent(passwordLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(email1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(email2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(email3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(email4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(email5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(password5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(accountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(startButton)))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(accountPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(accountPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    // save the usernames and encrypted passwords here
    private void saveButtonActionPerformed(ActionEvent evt) {                                           
        Properties prop = new Properties();
        OutputStream output = null;
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("}@d[HZPn{;$&1c81oPui");
        
        try {
            String appdata = System.getenv("APPDATA");      // gets the path to the local roaming profile
            new File(appdata + "/BingBot").mkdir();   // creates a folder to put the properties file in
            output = new FileOutputStream(appdata + "/BingBot/config.properties");
            
            prop.setProperty("1.email", email1.getText().toLowerCase().trim());
            prop.setProperty("2.email", email2.getText().toLowerCase().trim());
            prop.setProperty("3.email", email3.getText().toLowerCase().trim());
            prop.setProperty("4.email", email4.getText().toLowerCase().trim());
            prop.setProperty("5.email", email5.getText().toLowerCase().trim());

            prop.setProperty("1.password", encryptor.encrypt(new String(password1.getPassword())));
            prop.setProperty("2.password", encryptor.encrypt(new String(password2.getPassword())));
            prop.setProperty("3.password", encryptor.encrypt(new String(password3.getPassword())));
            prop.setProperty("4.password", encryptor.encrypt(new String(password4.getPassword())));
            prop.setProperty("5.password", encryptor.encrypt(new String(password5.getPassword())));
            
            prop.store(output, null);
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(null, "File could not be saved.", "File could not be saved.", JOptionPane.ERROR_MESSAGE);}
        finally {   // clean up
            if(output != null) {
                try {
                    output.close();
                }
                catch(IOException e) {}
            }
        }
    }                                          

    // starts execution of the selenium script which does the searches
    private void startButtonActionPerformed(ActionEvent evt) {                                            
    	Map<String, char[]> accounts = new LinkedHashMap<String, char[]>();	// holds username and passwords
    	
    	// sets the account details in the hashmap
    	if(!email1.getText().equals("") && new String(password1.getPassword()) != "") {
    		accounts.put(email1.getText().toLowerCase().trim(), password1.getPassword());
    	}
    	if(!email2.getText().equals("") && new String(password2.getPassword()) != "") {
			accounts.put(email2.getText().toLowerCase().trim(), password2.getPassword());
    	}
    	if(!email3.getText().equals("") && new String(password3.getPassword()) != "") {
			accounts.put(email3.getText().toLowerCase().trim(), password3.getPassword());
    	}
    	if(!email4.getText().equals("") && new String(password4.getPassword()) != "") {
			accounts.put(email4.getText().toLowerCase().trim(), password4.getPassword());
    	}
    	if(!email5.getText().equals("") && new String(password5.getPassword()) != "") {
    		accounts.put(email5.getText().toLowerCase().trim(), password5.getPassword());
    	}

    	
    	// Show error box if no account is inputted
    	if(accounts.size() == 0) {
    		JOptionPane.showMessageDialog(null, "No accounts inputted.", "No accounts inputted.", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
        // load current word list
    	SortedSet<String> wordSet = new TreeSet<String>();
    	
        try {
        	InputStream in = getClass().getResourceAsStream("WordList.txt");
        	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        	
        	String line;
        	while ((line = br.readLine()) != null) {
        		wordSet.add(line.trim());
        	}
        	br.close();
        	in.close();
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
    	startBing = new Bing(accounts, wordSet.toArray());   	
    	worker = new AnswerWorker();
    	worker.execute();
    	startButton.setEnabled(false);
    }       
    
    // A worker so that script execution isn't hanging on the eventdispatch thread
    class AnswerWorker extends SwingWorker<Integer, Integer> {
        @Override
        protected Integer doInBackground() {
        	startBing.execute();
            return 32;          // not used  
        }
        @Override
        protected void done() {
        	startButton.setEnabled(true);
        }
    }
}
