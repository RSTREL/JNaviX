package org.oakimsoft.ui_components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DlgInputAzimuth extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	public Double azimuth ;
	private Integer currentAzimuth ;
	public JSlider slider;
	public JLabel lblNewLabel;	
	private JTextField txtDist;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DlgInputScale dialog = new DlgInputScale(null,null,null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
		}
	}

	/**
	 * Create the dialog.
	 */
	public DlgInputAzimuth(Double azimuthValue) {
		setTitle("Azimuth");
		setBounds(100, 100, 419, 170);
		currentAzimuth =  (int) Math.round(azimuthValue);
		
		azimuth = azimuthValue;
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			azimuth = azimuthValue;
			slider = new JSlider();
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					currentAzimuth  = slider.getValue();
					if (lblNewLabel != null)
					  lblNewLabel.setText("<html>".concat(currentAzimuth.toString()));
					if (txtDist != null)
						txtDist.setText(String.format("%6.0f",(double)(currentAzimuth)));
					
				}
			});
			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			
			slider.setMinimum(-360);
			slider.setMaximum(360);
			slider.setValue(azimuthValue.intValue());
			contentPanel.add(slider, BorderLayout.CENTER);
		}
		{
			lblNewLabel = new JLabel("<html>".concat(currentAzimuth.toString()));
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblNewLabel, BorderLayout.NORTH);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JLabel lblcm = new JLabel("Azimuth ");
				panel.add(lblcm);
			}
			{
				txtDist = new JTextField(String.format("%6.0f",(double)(currentAzimuth)));
				panel.add(txtDist);
				txtDist.setColumns(10);
			}
			{
				JLabel lblKm = new JLabel(" degrees");
				panel.add(lblKm);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(this);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")){
			
			
			
			try{
			azimuth = Double.valueOf(txtDist.getText().replaceAll(",", "."));
			}catch(java.lang.NumberFormatException ex){
				azimuth = (double)currentAzimuth;
			}
			this.setVisible(false);
		}

		if (e.getActionCommand().equals("Cancel")){
			this.setVisible(false);
		}
		
	}

}
