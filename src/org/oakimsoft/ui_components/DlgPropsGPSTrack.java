package org.oakimsoft.ui_components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.oakimsoft.CJNavixLayerGPSTrack;
import javax.swing.JTextPane;

public class DlgPropsGPSTrack extends JDialog implements ActionListener {
	private static final long serialVersionUID = 3901952828573279498L;
	public String comment;
	public boolean visible = true; 
	public boolean useOwnParameters = false; 
	
	private CJNavixLayerGPSTrack layerGPSTrack;
	
	
	public Double azimuth;
	private Integer currentAzimuth;
	private JTextField textField;
	
	private JCheckBox chckbxUseOwnParameters;
	private JCheckBox chckbxVisible;
	
	


	/**
	 * Create the dialog.
	 */
	public DlgPropsGPSTrack(CJNavixLayerGPSTrack vLayerGPSTrack) {
		layerGPSTrack = vLayerGPSTrack;
		setTitle("GPSTrack Properties");
		setBounds(100, 100, 419, 355);

		getContentPane().setLayout(new BorderLayout());
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
				{
					JPanel panel = new JPanel();
					getContentPane().add(panel, BorderLayout.CENTER);
					panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					{
						JPanel panel_1 = new JPanel();
						panel.add(panel_1);
						panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
						{
							JPanel panel_2 = new JPanel();
							panel_2.setMaximumSize(new Dimension(32767, 20));
							FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
							flowLayout.setAlignment(FlowLayout.LEFT);
							panel_1.add(panel_2);
							{
								JLabel lblNewLabel = new JLabel("Layer description");
								panel_2.add(lblNewLabel);
							}
						}
						{
							textField = new JTextField();
							textField.setMaximumSize(new Dimension(2147483647, 30));
							textField.setMinimumSize(new Dimension(100, 20));
							textField.setPreferredSize(new Dimension(100, 20));
							panel_1.add(textField);
							textField.setColumns(10);
							textField.setText(vLayerGPSTrack.comment);
							
						}
					}
					{
						JPanel panel_1 = new JPanel();
						FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
						flowLayout.setAlignment(FlowLayout.LEFT);
						panel_1.setMaximumSize(new Dimension(32767, 20));
						panel.add(panel_1);
						{
							chckbxVisible = new JCheckBox("Visible");
							panel_1.add(chckbxVisible);
							chckbxVisible.setSelected(vLayerGPSTrack.visible);
						}
					}
					{
						JPanel panel_1 = new JPanel();
						FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
						flowLayout.setAlignment(FlowLayout.LEFT);
						panel_1.setMaximumSize(new Dimension(32767, 30));
						panel.add(panel_1);
						{
							chckbxUseOwnParameters = new JCheckBox("Use own parameters (Azimuth,Scale, etc)");
							panel_1.add(chckbxUseOwnParameters);
							chckbxUseOwnParameters.setSelected(vLayerGPSTrack.useOwnParameters);
						}
					}
					{
						JScrollPane scrollPane = new JScrollPane();
						panel.add(scrollPane);
						{
							JTextPane textPane = new JTextPane();
							textPane.setContentType("text/html");
							textPane.setText(vLayerGPSTrack.getStatistics());
							scrollPane.setViewportView(textPane);
						}
					}
					{
						JPanel panel_1 = new JPanel();
						FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
						flowLayout.setAlignment(FlowLayout.LEFT);
						panel_1.setMaximumSize(new Dimension(32767, 20));
						panel.add(panel_1);
						{
							JLabel lblNewLabel_1 = new JLabel("Toal: 0 records" + layerGPSTrack.trackManager.alTrackData.size());
							panel_1.add(lblNewLabel_1);
						}
					}
				}
				cancelButton.addActionListener(this);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {

			// try{
			// }catch(java.lang.NumberFormatException ex){
			// }
			layerGPSTrack.comment = this.textField.getText();
			layerGPSTrack.visible = this.chckbxVisible.isSelected();
			layerGPSTrack.useOwnParameters = this.chckbxUseOwnParameters.isSelected();
			
			this.setVisible(false);
		}

		if (e.getActionCommand().equals("Cancel")) {
			this.setVisible(false);
		}

	}

}
