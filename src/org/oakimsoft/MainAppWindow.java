package org.oakimsoft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.oakimsoft.common.JNavixActions;
import org.oakimsoft.ui_components.PnMain;

public class MainAppWindow {

	private JFrame frame;
	public CJNaviXLayerManager LayerManager;
	public CJNavixGlobalManager globManager;
	private JToolBar globalToolbar;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainAppWindow window = new MainAppWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainAppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		LayerManager = new CJNaviXLayerManager();
		globManager = new CJNavixGlobalManager();
		globManager.LayerManager = LayerManager;

		LayerManager.globManager = globManager; 

		
		frame = new JFrame();
		frame.setExtendedState(6);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("JNaviX");

		PnMain panel = new PnMain(globManager);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		
		this.globalToolbar = new JToolBar();
		JLabel jlbl = new JLabel();
     	jlbl.setText("Main ");
	    jlbl.setForeground(Color.blue);
		this.globalToolbar.add(jlbl);
		

		JButton jbtnOpen = new JButton();
		jbtnOpen.setText("Open");
		jbtnOpen.setActionCommand(JNavixActions.UI_MainBtnOpen);
		jbtnOpen.addActionListener(globManager);
		jbtnOpen.setIcon(new ImageIcon(this.getClass().getResource("/imgs16x16/fileopen.png")));
		this.globalToolbar.add(jbtnOpen);		

		
		
		frame.getContentPane().add(globalToolbar, BorderLayout.NORTH);

		

	}

	public void processButtonCommand() {

	}

}
