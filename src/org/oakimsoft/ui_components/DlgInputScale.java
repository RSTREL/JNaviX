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

public class DlgInputScale extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	public Double scale ;
	private Integer currentScale ;
	private Double currentMinScale ;
	private double currentMaxScale ;
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
	public DlgInputScale(Double scaleValue, Double minValue, Double maxValue) {
		setTitle("Scale");
		setBounds(100, 100, 419, 170);
		currentScale =  (int) Math.round(scaleValue);
		currentMinScale =  minValue;
		currentMaxScale =  maxValue;
		
		scale = scaleValue;
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			scale = scaleValue;
		}
		{
			lblNewLabel = new JLabel("<html>1:".concat(currentScale.toString()));
			lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
			contentPanel.add(lblNewLabel, BorderLayout.NORTH);
		}
		{
			JPanel panel_1 = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			contentPanel.add(panel_1, BorderLayout.CENTER);
			slider = new JSlider();
			panel_1.add(slider);
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					currentScale  = slider.getValue();
					if (lblNewLabel != null)
					  lblNewLabel.setText("<html>1:".concat(currentScale.toString()));
					if (txtDist != null)
						txtDist.setText(String.format("%6.3f",(double)(currentScale/100000.0)));
					
				}
			});
			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			
			slider.setMinimum(minValue.intValue());
			slider.setMaximum(maxValue.intValue());
			slider.setValue(scaleValue.intValue());
			{
				JPanel panel = new JPanel();
				panel_1.add(panel);
				panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				{
					JLabel lblcm = new JLabel("1cm=");
					panel.add(lblcm);
				}
				{
					txtDist = new JTextField(String.format("%6.3f",(double)(currentScale/100000.0)));
					panel.add(txtDist);
					txtDist.setColumns(10);
				}
				{
					JLabel lblKm = new JLabel("km");
					panel.add(lblKm);
				}
			}
			{
				JButton btn1to0100 = new JButton("1cm=0.1km");
				btn1to0100.setActionCommand("scale1to0100");
				btn1to0100.addActionListener(this);
				panel_1.add(btn1to0100);
			}
			{
				JButton btn1to0500 = new JButton("1cm=0.5km");
				btn1to0500.setActionCommand("scale1to0500");
				btn1to0500.addActionListener(this);
				panel_1.add(btn1to0500);
			}
			{
				JButton btn1to1000 = new JButton("1cm=1km");
				btn1to1000.setActionCommand("scale1to1000");
				btn1to1000.addActionListener(this);
				panel_1.add(btn1to1000);
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
		if (e.getActionCommand().equals("scale1to0100")){
			scale = 10000.0;
			this.setVisible(false);
		}
		if (e.getActionCommand().equals("scale1to0500")){
			scale = 50000.0;
			this.setVisible(false);
		}
		if (e.getActionCommand().equals("scale1to1000")){
			scale = 100000.0;
			this.setVisible(false);
		}
		

		if (e.getActionCommand().equals("OK")){
			try{
			scale = Double.valueOf(txtDist.getText().replaceAll(",", "."))*100000.0;
			}catch(java.lang.NumberFormatException ex){
     			scale = (double)currentScale;
			}
			if (scale > currentMaxScale)
				scale = currentMaxScale;
			if (scale < currentMinScale)
				scale = currentMinScale;
			this.setVisible(false);
		}
		if (e.getActionCommand().equals("Cancel")){
			this.setVisible(false);
		}
		
	}

}
