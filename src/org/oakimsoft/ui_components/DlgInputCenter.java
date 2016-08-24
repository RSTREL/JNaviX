package org.oakimsoft.ui_components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.bbn.openmap.LatLonPoint;

public class DlgInputCenter extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	public Double Lat;
	public Double Lon ;
	public LatLonPoint center= new LatLonPoint(0d,0d);
	public LatLonPoint prevcenter= new LatLonPoint(0d,0d);
	private JTextField tfLatitude;
	private JTextField tfLongitude;

	/**
	 * Create the dialog.
	 */
	public DlgInputCenter(LatLonPoint pLLP) {
		setTitle("Center");
		setBounds(100, 100, 419, 129);
		this.center =  pLLP;
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
		    JPanel panel = new JPanel();
			panel.setMaximumSize(new Dimension(32767, 30));
			contentPanel.add(panel);
			panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			{
				JLabel lblcm = new JLabel("Latitude");
				lblcm.setPreferredSize(new Dimension(50, 14));
				lblcm.setMinimumSize(new Dimension(50, 14));
				lblcm.setMaximumSize(new Dimension(50, 14));
				panel.add(lblcm);
			}
			{
				tfLatitude = new JTextField(String.format("%6.6f",this.center.getLatitude()));
				panel.add(tfLatitude);
				tfLatitude.setColumns(10);
			}
			{
				JLabel lblKm = new JLabel("degrees");
				panel.add(lblKm);
			}
		}
		{
			JPanel panel = new JPanel();
			panel.setMaximumSize(new Dimension(32767, 30));
			contentPanel.add(panel);
			panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			{
				JLabel lblLongitude = new JLabel("Longitude");
				lblLongitude.setPreferredSize(new Dimension(50, 14));
				lblLongitude.setMinimumSize(new Dimension(50, 14));
				lblLongitude.setMaximumSize(new Dimension(50, 14));
				panel.add(lblLongitude);
			}
			{
				tfLongitude = new JTextField(String.format("%6.6f",this.center.getLongitude()));
				tfLongitude.setColumns(10);
				panel.add(tfLongitude);
			}
			{
				JLabel lblDegrees = new JLabel("degrees");
				panel.add(lblDegrees);
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
			   prevcenter.setLatLon(center);	 
               center.setLatitude(Float.valueOf(tfLatitude.getText().replaceAll(",", ".")));
               center.setLongitude(Float.valueOf(tfLongitude.getText().replaceAll(",", ".")));
			}catch(java.lang.NumberFormatException ex){
     			center.setLatLon(prevcenter);
			}

				
			
			
			this.setVisible(false);
		}

		if (e.getActionCommand().equals("Cancel")){
			this.setVisible(false);
		}
		
	}

}
