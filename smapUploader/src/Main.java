/*
 * Copyright (C) 2013 Smap Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import com.apple.eawt.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String APP_NAME = "Smap Uploader";
    private static final String SAMPLE_SERVER = "xxx.smap.com.au";
    private JFrame frame;
    private JTextArea statusLog;
    private JTextField folderField;
    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JTextField identField;
    private JCheckBox encryptField;
    private String filePath = null;
    private String serverName = null;
    private String userName = null;
    private String password = null;
    private String ident = null;
    private boolean encrypted = true;

    public Main() {
        initialize();
    }

    public static void main(String[] args) {

        if (System.getProperty("os.name").contains("Mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name", APP_NAME);
            System.setProperty("com.apple.macos.use-file-dialog-packages", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");

        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize() {

        frame = new JFrame(APP_NAME);
        frame.setResizable(false);
        //frame.setBounds(100, 100, 450, 380);
        frame.setBounds(100, 100, 500, 410);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(
                new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setLocationRelativeTo(null);

        ImageIcon mainLogo = new javax.swing.ImageIcon(getClass().getResource("main_logo.jpg"));
        Image appLogo = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("su_logo.png"));

        if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
            Application app = Application.getApplication();
            app.setDockIconImage(appLogo);
        }

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel logo = new JLabel(mainLogo);
        logo.setBounds(380, 225, 100, 130);
        panel.add(logo);

        statusLog = new JTextArea();
        statusLog.setEditable(false);
        statusLog.setFont(new Font("Dialog", Font.PLAIN, 11));
        statusLog.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(21, 26, 458, 172);
        scrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(statusLog);
        panel.add(scrollPane);

        JLabel folderLabel = new JLabel("Survey Folder: ");
        folderLabel.setBounds(22, 200, 150, 20);
        folderField = new JTextField(100);
        folderField.setEditable(false);
        folderField.setBounds(160, 200, 150, 20);
        folderField.setText("");	// Set from saved settings
        panel.add(folderLabel);
        panel.add(folderField);
        
        JLabel urlLabel = new JLabel("Server Name: ");
        urlLabel.setBounds(22, 225, 150, 20);
        urlField = new JTextField(100);
        urlField.setBounds(160, 225, 150, 20);
        urlField.setText(SAMPLE_SERVER);
        panel.add(urlLabel);
        panel.add(urlField);
        
        JLabel userLabel = new JLabel("User Id: ");
        userLabel.setBounds(22, 250, 150, 20);
        userField = new JTextField(100);
        userField.setBounds(160, 250, 150, 20);
        panel.add(userLabel);
        panel.add(userField);
        
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setBounds(22, 275, 150, 20);
        passwordField = new JPasswordField(100);
        passwordField.setBounds(160, 275, 150, 20);
        panel.add(passwordLabel);
        panel.add(passwordField);
        
        JLabel identLabel = new JLabel("New Ident(Optional): ");
        identLabel.setBounds(22, 300, 150, 20);
        identField = new JTextField(100);
        identField.setBounds(160, 300, 150, 20);
        panel.add(identLabel);
        panel.add(identField);
        
        JLabel encryptLabel = new JLabel("Encrypt: ");
        encryptLabel.setBounds(22, 325, 150, 20);
        encryptField = new JCheckBox("", true);
        encryptField.setBounds(160, 325, 150, 20);
        panel.add(encryptLabel);
        panel.add(encryptField);
        
        encryptField.addItemListener( new ItemListener() {
        		public void itemStateChanged(ItemEvent e) {
        			if (e.getStateChange() == ItemEvent.DESELECTED) {
        				encrypted = false;
        			} else {
        				encrypted = true;
        			}
        		}
        });
        
        JButton selectForm = new JButton("Choose");
        selectForm.setBounds(310, 195, 100, 29);
        selectForm.addActionListener(new FileChooser());
        panel.add(selectForm);


        JButton generateCodebook = new JButton("Submit");
        generateCodebook.setBounds(130, 345, 150, 29);
        generateCodebook.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	filePath = folderField.getText();
            	serverName = urlField.getText();
            	userName = userField.getText();

            	password = passwordField.getText();
            	ident = identField.getText();
            	
                if (filePath == null || filePath.length() == 0) {
                    appendToStatus("Please select a folder first.");
                    return;
                } else if (serverName == null || serverName.length() == 0 || serverName.equals(SAMPLE_SERVER)) {
                    appendToStatus("Please enter a valid server name first.");
                    return;
                } else if (serverName.startsWith("http")) {
                    appendToStatus("Only enter the server name, not the protocol.");
                    return;
                } else if (userName == null || userName.length() == 0) {
                    appendToStatus("Please enter user identifier first.");
                    return;
                } else if (password == null || password.length() == 0) {
                    appendToStatus("Please enter password first.");
                    return;
                } else {
                    MyTask process = new MyTask(filePath);
                    process.execute();
                }
            }
        });
        panel.add(generateCodebook);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

    }

    private int processFolder(String instanceFolderPath) throws Exception {

    	SubmitResults sr = new SubmitResults();
		String response = null;
		int count = 0;
		
		// If the ident is blank set it to null
		if(ident != null && ident.trim().length() == 0) {
			ident = null;
		}
		
		try {
			
		    File instanceFolder = new File(instanceFolderPath);		// get folder containing survey instances
		    
		    if (!instanceFolder.exists()) {
		    	appendToStatus("Error: Instance folder " + instanceFolderPath + " does not exist");
		    } else {
		    	
		    	File[] allFiles = instanceFolder.listFiles();
		    	
		    	// Get the folders and the names of any folders that have been sent
		    	List<File> folders = new ArrayList<File>();
		    	List<String> sentFolderNames = new ArrayList<String>();
			    for (File f : allFiles) {
			    	if(f.isDirectory()) {
			    		folders.add(f);
			    	} else {
			    		String fileName = f.getName();
			    		if(fileName.endsWith(".sent")) {
			    			int idx = fileName.lastIndexOf(".");
			    			String folderName = "";
				            if (idx != -1) {
				                folderName = fileName.substring(0, idx);
				            }
			    			sentFolderNames.add(folderName);
			    		}
			    	}
			    }
			    
			    // Remove sent folders from the list of folders to send
			    for(int i = 0; i < sentFolderNames.size(); i++) {
			    	String folderName = sentFolderNames.get(i);
			    	for(int j = folders.size() - 1; j >= 0; j--) {
			    		File f = folders.get(j);
			    		if(folderName.equals(f.getName())) {
			    			appendToStatus("Not Sent: " + folderName + " has already been sent to a server");
			    			folders.remove(j);
			    		}
			    	}
			    }
			    
			    // Submit any remaining instance folders
			    for(int i = 0; i < folders.size(); i++) {
			    	File f = folders.get(i);
			    	// Assume instance file has same name root as its folder plus ".xml"
			    	String filepath = f.getPath() + "/" + f.getName() + ".xml";
			    	appendToStatus("Sending: " + f.getName());
			    	if(sr.sendFile(this, serverName, filepath, "unknown", userName, password, encrypted, ident) == true) {
			    		appendToStatus("	Success: sent file: " + f.getName());
			    		count++;
		    			
		                String sentFilePath = instanceFolderPath + "/" + f.getName() + ".sent";
		                File sentFile = new File(sentFilePath);
			    		sentFile.createNewFile();
			            
			    	} else {
			    		appendToStatus("	Error: Failed to send file: " + filepath);
			    	}
			    }
		    }

		
		} catch (Exception e) {
			response = "Error: " + e.getMessage();
			e.printStackTrace();
			appendToStatus("        " + response);
			
			
		} finally {

		}		
		
		return count;

    }

    public void appendToStatus(String text) {
        statusLog.setText(statusLog.getText() + text + "\n");
    }
    
    private class FileChooser implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rVal = fileChooser.showOpenDialog(frame);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                statusLog.setText("");
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
                folderField.setText(filePath);            }
        }
    }

    class MyTask extends SwingWorker {
        private final String selected;

        public MyTask(String f) {
            super();
            selected = f;
        }

        protected Object doInBackground() {
            try {
            	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                int count = processFolder(selected);
                frame.setCursor(Cursor.getDefaultCursor());
                appendToStatus("Upload Complete " + count + " files sent. ---------------------");
            } catch (Exception e) {
                appendToStatus("Error: Failed to submit files because " + e.getMessage() +".");
            }
            return null;
        }
    }

}
