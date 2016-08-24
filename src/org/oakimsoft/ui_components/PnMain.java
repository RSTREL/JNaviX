package org.oakimsoft.ui_components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.oakimsoft.CJNavixGlobalManager;
import org.oakimsoft.CJNavixLayerDrawBoard;
import org.oakimsoft.CJNavixLayerGPSTrack;
import org.oakimsoft.CJNavixLayerUnimap;
import org.oakimsoft.common.ActionObserver;
import org.oakimsoft.common.JNavixActions;

public class PnMain extends JPanel implements ActionObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CJNavixGlobalManager globManager;
	private JPanel LeftPanel;
	private JPanel jpnInfo;
	private JButton jbtnScale;
	private JButton jbtnAzimuth;
	private JButton jbtnDDCenter;
	private JToolBar mainToolbar;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Create the panel.
	 */
	public PnMain(CJNavixGlobalManager eGlobManager) {
		globManager = eGlobManager;

		globManager.registerObserver(this);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		LeftPanel = new JPanel();
		LeftPanel.setLayout(new BorderLayout());

		PnDraw DrawPanel = new PnDraw(globManager);
		LeftPanel.add(DrawPanel, BorderLayout.CENTER);
		this.mainToolbar = globManager.tbMain;
		LeftPanel.add(this.mainToolbar, BorderLayout.NORTH);

		jpnInfo = new JPanel();
		jpnInfo.setLayout(new FlowLayout(FlowLayout.LEADING));
		jpnInfo.setMinimumSize(new Dimension(67, 50));
		jpnInfo.setPreferredSize(new Dimension(67, 50));

		jbtnScale = new JButton("1:1");
		jbtnScale.setMinimumSize(new Dimension(150, 30));
		jbtnScale.setActionCommand(JNavixActions.UI_CallScaleChange);
		jbtnScale.addActionListener(globManager);
		jbtnScale.setToolTipText("Scale");
		jpnInfo.add(jbtnScale);

		jbtnAzimuth = new JButton("0");
		jbtnAzimuth.setMinimumSize(new Dimension(67, 30));
		jbtnAzimuth.setActionCommand(JNavixActions.UI_CallAzimuthChange);
		jbtnAzimuth.addActionListener(globManager);
		jbtnAzimuth.setToolTipText("Azimuth");
		jpnInfo.add(jbtnAzimuth);

		jbtnDDCenter = new JButton("0");
		jbtnDDCenter.setMinimumSize(new Dimension(150, 30));
		jbtnDDCenter.setActionCommand(JNavixActions.UI_CallDDCenterChange);
		jbtnDDCenter.setToolTipText("Center");
		jbtnDDCenter.addActionListener(globManager);

		jpnInfo.add(jbtnDDCenter);

		LeftPanel.add(this.jpnInfo, BorderLayout.SOUTH);

		globManager.tbMain.add(new JLabel("Для начала работы добавьте слой в панели справа"));
		PnLayerManager RightPanel = new PnLayerManager(globManager);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, LeftPanel, RightPanel);
		splitPane.setResizeWeight(0.9);
		splitPane.setOneTouchExpandable(true);

		add(splitPane);

	}

	@Override
	public void subscribedAction(ActionEvent ae) {
		if (ae.getActionCommand().startsWith(JNavixActions.UI_UpdateToolbar)) {
			LeftPanel.remove(this.mainToolbar);
			if (globManager.LayerManager.focusedLayer != null) {
				this.mainToolbar = globManager.LayerManager.focusedLayer.toolbar;
				LeftPanel.add(this.mainToolbar, BorderLayout.NORTH);
			}
			for (int i = 0; i < this.mainToolbar.getComponentCount(); i++) {
				Component c = this.mainToolbar.getComponent(i);
				if (c.getClass().getName().equals("javax.swing.JButton")) {
					((javax.swing.JButton) (c)).removeActionListener(globManager);
					((javax.swing.JButton) (c)).addActionListener(globManager);
				}
			}
			this.mainToolbar.setVisible(true);
			this.mainToolbar.repaint();
			LeftPanel.revalidate();
		}

		if (ae.getActionCommand().startsWith(JNavixActions.UI_UpdateInfoPanel)) {
			this.jbtnScale.setText("1:".concat(Integer.toString((int) Math.round(globManager.LayerManager.getFocusedLayerScale()))));
			this.jbtnAzimuth.setText(globManager.LayerManager.getFocusedLayerAzimuth().toString());
			this.jbtnDDCenter.setText(String.format("%3.7f : %3.7f", globManager.LayerManager.getFocusedLayerCenter().getLatitude(), globManager.LayerManager.getFocusedLayerCenter().getLongitude()));

		}

		if (ae.getActionCommand().startsWith(JNavixActions.UI_CallScaleChange)) {
			DlgInputScale dlgInputScale = new DlgInputScale(globManager.LayerManager.getFocusedLayerScale(), globManager.LayerManager.getFocusedLayerScaleMin(),
					globManager.LayerManager.getFocusedLayerScaleMax());
			dlgInputScale.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

			dlgInputScale.setVisible(true);
			globManager.LayerManager.setFocusedLayerScale(dlgInputScale.scale);
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));

		}
		if (ae.getActionCommand().startsWith(JNavixActions.UI_CallAzimuthChange)) {
			DlgInputAzimuth dlgInputAzimuth = new DlgInputAzimuth(globManager.LayerManager.getFocusedLayerAzimuth());
			dlgInputAzimuth.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

			dlgInputAzimuth.setVisible(true);
			globManager.LayerManager.setFocusedLayerAzimuth(dlgInputAzimuth.azimuth);
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
		}
		

		if (ae.getActionCommand().startsWith(JNavixActions.UI_CallDDCenterChange)) {
			DlgInputCenter dlgInputCenter = new DlgInputCenter(globManager.LayerManager.getFocusedLayerCenter());
			dlgInputCenter.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
			dlgInputCenter.setVisible(true);
			globManager.LayerManager.setFocusedLayerCenter(dlgInputCenter.center);
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));

		}
		
		
		if (ae.getActionCommand().startsWith(JNavixActions.UI_UnimapBtnProperties)) {
			DlgPropsUnimap dlgPropsUnimap = new DlgPropsUnimap((CJNavixLayerUnimap)(globManager.LayerManager.focusedLayer));
			dlgPropsUnimap.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
			dlgPropsUnimap.setVisible(true);
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateLayersList));
		}
		
		if (ae.getActionCommand().startsWith(JNavixActions.UI_DrawBoardBtnProperties)) {
			DlgPropsDrawBoard dlgPropsDrawBoard = new DlgPropsDrawBoard((CJNavixLayerDrawBoard)(globManager.LayerManager.focusedLayer));
			dlgPropsDrawBoard.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
			dlgPropsDrawBoard.setVisible(true);
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateLayersList));
		}

		
		if (ae.getActionCommand().startsWith(JNavixActions.UI_GPSTrackBtnProperties)) {
			DlgPropsGPSTrack dlgPropsGPSTrack = new DlgPropsGPSTrack((CJNavixLayerGPSTrack)(globManager.LayerManager.focusedLayer));
			dlgPropsGPSTrack.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
			dlgPropsGPSTrack.setVisible(true);
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateLayersList));
		}

	}

}
