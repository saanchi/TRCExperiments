package edu.ufl.bme.application;


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Application extends JPanel
                             implements ActionListener {

	private static final long serialVersionUID = 1L;
	JButton openButton, runButton;
	JRadioButton fileButton, textButton;
	ButtonGroup buttonGroup;
    JTextArea sentenceArea;
    JFileChooser fc;
    File file;
	public static Process process;

    public Application() {
        super(new BorderLayout());

        sentenceArea = new JTextArea(15,20 );
        sentenceArea.setEditable(true);
        sentenceArea.setText("Marvin : I think you ought to know I'm feeling very depressed.");
        sentenceArea.setText("Marvin: Life, loathe it or ignore it, you can't like it.");
        sentenceArea.setLineWrap(true);
        JScrollPane sentenceAreaScrollPane = new JScrollPane(sentenceArea);
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.add(sentenceAreaScrollPane);
        // Create Buttons
        runButton = new JButton ("Run");
        runButton.addActionListener(this);
        
        //Create a file chooser
        fc = new JFileChooser();
        openButton = new JButton("Open a File");
        openButton.addActionListener(this);
        openButton.setVisible(false);

        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); 
        buttonPanel.add(openButton);
        buttonPanel.add(runButton);
        
        // Radio button to select whether file or text mode
        fileButton = new JRadioButton("File");
        //birdButton.setActionCommand();
        fileButton.addActionListener(this);
        
     // Radio button to select whether file or text mode
        textButton = new JRadioButton("Text" );
        //birdButton.setActionCommand();
        textButton.setSelected(true);
        textButton.addActionListener(this);
        
        buttonGroup = new ButtonGroup();
        buttonGroup.add(textButton);
        buttonGroup.add(fileButton);
        
        JPanel radioPanel = new JPanel();
        radioPanel.add(fileButton);
        radioPanel.add(textButton);
        
        //Add the buttons and the sentenceArea to this panel.
        add(radioPanel, BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH );
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if ( e.getSource() == fileButton ){
        	sentenceArea.setEditable(false);
        	openButton.setVisible(true);
        }
        else if ( e.getSource() == textButton ){
        	openButton.setVisible(false);
        	sentenceArea.setEditable(true);
        }
        else if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(Application.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
            }
        }
        // if the run button is pressed
        else if (e.getSource() == runButton) {
        	if (fileButton.isSelected()) {// if in file mode
        		if( file != null ){
        			try {
						process.runFileMode(file.getAbsolutePath(), file.getName());
        			} catch (Exception e1) {
						e1.printStackTrace();
					}
        		}
        	}
        	else if (textButton.isSelected()) {
        		//System.out.println( "Text mode is active ");
        		// Get the results
        		String sentence = sentenceArea.getText();
        		String output = process.runTextMode(sentence);
        		sentenceArea.setText(output);
        	}
        }
        
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame ("TRCExperiment");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        frame.add(new Application());
        frame.setLocationRelativeTo(null);
        //Display the window.
        frame.setVisible(true);
    }
    
    /**
     * To initialize the parser/tagger and compile the regex patterns 
     */
    public static void init(){
    	process = new Process();
    }

    public static void main(String[] args) {
    	
    	// Initialize the Parser/tagger and the Regex Engine
    	Application.init();
    	
    	// Bring out the GUI
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                createAndShowGUI();
            }
        });
    }
}
