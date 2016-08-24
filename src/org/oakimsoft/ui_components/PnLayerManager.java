package org.oakimsoft.ui_components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.oakimsoft.CJNavixGlobalManager;
import org.oakimsoft.common.ActionObserver;
import org.oakimsoft.common.CLog;
import org.oakimsoft.common.JNavixActions;

public class PnLayerManager extends JPanel implements ActionListener, ActionObserver {

	private static final long serialVersionUID = 20111027L;
	private CJNavixGlobalManager globManager;
	private JTable table;
	public DefaultTableModel dtmLayers = new DefaultTableModel() {

		private static final long serialVersionUID = 20111027L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private BufferedImage biLayer01 = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biLayer02 = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biLayer03 = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biLayer04 = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biEmpty = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biVisible = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biInvisible = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biUseCommon = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biUseOwn = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
	private BufferedImage biFocused = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Create the panel.
	 */
	public PnLayerManager(CJNavixGlobalManager eGlobManager) {

		if (eGlobManager != null) {
			globManager = eGlobManager;
			globManager.registerObserver(this);

		}

		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JButton btnLayerAdd = new JButton("");
		btnLayerAdd.setPreferredSize(new Dimension(30, 30));
		btnLayerAdd.setMinimumSize(new Dimension(30, 30));
		btnLayerAdd.setMaximumSize(new Dimension(30, 30));
		btnLayerAdd.setIcon(new ImageIcon(PnLayerManager.class.getResource("/imgs24x24/gtk-add.png")));
		// btnLayerAdd.setActionCommand(JNavixActions.APP_LayerAdd);
		// btnLayerAdd.addActionListener(globManager);
		panel.add(btnLayerAdd);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(btnLayerAdd, popupMenu);

		JMenuItem mntmMap = new JMenuItem("Map");
		mntmMap.setActionCommand(JNavixActions.APP_LayerAddMap);
		mntmMap.setIcon(new ImageIcon(PnLayerManager.class.getResource("/imgs24x24/gnome-globe.png")));
		mntmMap.addActionListener(globManager);
		popupMenu.add(mntmMap);

		JMenuItem mntmSketch = new JMenuItem("Sketch");
		mntmSketch.setActionCommand(JNavixActions.APP_LayerAddSketch);
		mntmSketch.setIcon(new ImageIcon(PnLayerManager.class.getResource("/imgs24x24/sketch.png")));
		mntmSketch.addActionListener(globManager);
		popupMenu.add(mntmSketch);

		JMenuItem mntmGpsTrack = new JMenuItem("GPS Track");
		mntmGpsTrack.setActionCommand(JNavixActions.APP_LayerAddGPSTrack);
		mntmGpsTrack.setIcon(new ImageIcon(PnLayerManager.class.getResource("/imgs24x24/stock_draw-curved-connector-starts-with-circle.png")));
		mntmGpsTrack.addActionListener(globManager);
		popupMenu.add(mntmGpsTrack);

		JButton btnLayerRemove = new JButton("");
		btnLayerRemove.setPreferredSize(new Dimension(30, 30));
		btnLayerRemove.setMinimumSize(new Dimension(30, 30));
		btnLayerRemove.setMaximumSize(new Dimension(30, 30));
		btnLayerRemove.setIcon(new ImageIcon(PnLayerManager.class.getResource("/imgs24x24/gtk-delete.png")));
		btnLayerRemove.setActionCommand(JNavixActions.APP_LayerRemoveFocused);
		btnLayerRemove.addActionListener(globManager);
		panel.add(btnLayerRemove);

		JButton btnMoveUp = new JButton("");
		btnMoveUp.setMaximumSize(new Dimension(30, 30));
		btnMoveUp.setMinimumSize(new Dimension(30, 30));
		btnMoveUp.setPreferredSize(new Dimension(30, 30));
		btnMoveUp.setIcon(new ImageIcon(PnLayerManager.class.getResource("/imgs24x24/gtk-go-up.png")));
		btnMoveUp.setActionCommand(JNavixActions.APP_LayerUpFocused);
		btnMoveUp.addActionListener(globManager);
		panel.add(btnMoveUp);

		JButton btnMoveDown = new JButton("");
		btnMoveDown.setPreferredSize(new Dimension(30, 30));
		btnMoveDown.setMinimumSize(new Dimension(30, 30));
		btnMoveDown.setMaximumSize(new Dimension(30, 30));
		btnMoveDown.setIcon(new ImageIcon(PnLayerManager.class.getResource("/imgs24x24/gtk-go-down.png")));
		btnMoveDown.setActionCommand(JNavixActions.APP_LayerDnFocused);
		btnMoveDown.addActionListener(globManager);

		panel.add(btnMoveDown);

		String[] headings = { "", "Тип", "Виден", "Независим", "Описание" };
		this.dtmLayers.setColumnIdentifiers(headings);

		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {

				StringBuilder sb = new StringBuilder();
				sb.append(JNavixActions.APP_LayerFocused).append("|");
				int[] selRows = table.getSelectedRows();
				if (selRows.length != 0) {
					sb.append(selRows[0]);
					sb.append("|");
					sb.append(selRows[selRows.length - 1]);
					ActionEvent lActEvent = new ActionEvent(this, 1001, sb.toString());
					globManager.actionPerformed(lActEvent);
				}

				if (me.getID() == MouseEvent.MOUSE_PRESSED) {
					int rowIndx = table.rowAtPoint(me.getPoint());
					int colIndx = table.columnAtPoint(me.getPoint());
					StringBuilder sb2 = new StringBuilder();
					sb2.append(JNavixActions.UI_CallCellClicked).append("|");
					sb2.append(colIndx);
					sb2.append("|");
					sb2.append(rowIndx);
					ActionEvent lActEvent = new ActionEvent(this, MouseEvent.MOUSE_CLICKED, sb2.toString());
					globManager.actionPerformed(lActEvent);
					return;
				}

			}
		});

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(false);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.table.setRowHeight(28);
		// this.table.setAutoCreateRowSorter(true);
		this.table.setModel(this.dtmLayers);
		table.getTableHeader().setReorderingAllowed(false);

		TableCellRenderer tcr = new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel l = new JLabel();
				if (value != null) {
					if (value instanceof BufferedImage) {
						l.setIcon(new ImageIcon((BufferedImage) value));
					} else {
						l.setText((String) value);
					}
					return l;
				} else {
					l.setText("nulla");
					return null;
				}
			}
		};

		TableColumnModel tcm = this.table.getColumnModel();
		TableColumn tc;
		tc = tcm.getColumn(0);
		tc.setPreferredWidth(28);
		tc.setCellRenderer(tcr);

		tc = tcm.getColumn(1);
		tc.setPreferredWidth(28);
		tc.setCellRenderer(tcr);

		tc = tcm.getColumn(2);
		tc.setPreferredWidth(28);
		tc.setCellRenderer(tcr);

		tc = tcm.getColumn(3);
		tc.setPreferredWidth(28);
		tc.setCellRenderer(tcr);

		tc = tcm.getColumn(4);
		tc.setPreferredWidth(250);
		tc.setCellRenderer(tcr);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(table);

		// Загрузить изображения
		InputStream input_stream;
		try {
			input_stream = this.getClass().getResourceAsStream("/imgs24x24/gnome-globe.png");
			biLayer01 = ImageIO.read(input_stream);
			input_stream = null;
			input_stream = this.getClass().getResourceAsStream("/imgs24x24/sketch.png");
			biLayer02 = ImageIO.read(input_stream);
			input_stream = null;
			input_stream = this.getClass().getResourceAsStream("/imgs24x24/stock_draw-curved-connector-starts-with-circle.png");
			biLayer03 = ImageIO.read(input_stream);
			input_stream = null;
			input_stream = this.getClass().getResourceAsStream("/imgs24x24/dialog-warning.png");
			biLayer04 = ImageIO.read(input_stream);
			this.biEmpty = ImageIO.read(this.getClass().getResourceAsStream("/imgs24x24/empty.png"));
			this.biInvisible = ImageIO.read(this.getClass().getResourceAsStream("/imgs24x24/empty.png"));
			this.biVisible = ImageIO.read(this.getClass().getResourceAsStream("/imgs24x24/eye.png"));
			this.biUseCommon = ImageIO.read(this.getClass().getResourceAsStream("/imgs24x24/empty.png"));
			this.biUseOwn = ImageIO.read(this.getClass().getResourceAsStream("/imgs24x24/stock_disconnect.png"));
			this.biFocused = ImageIO.read(this.getClass().getResourceAsStream("/imgs24x24/gtk-go-back-rtl.png"));

		} catch (IOException e) {
			logger.info("[ee] Problem with picture loading " + e.toString());
		} catch (RuntimeException re) {
			logger.info("[ee] Problem with picture loading " + re.toString());
		}

		this.updateTable();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	private void updateTable() {

		ArrayList<String> layerList = new ArrayList<String>(1);
		globManager.LayerManager.getListOfLayes(layerList);

		this.dtmLayers.setRowCount(0);
		Iterator<String> it = layerList.iterator();
		while (it.hasNext()) {
			String lv = (String) it.next();
			String[] ls = lv.split("\\|");
			Object[] values = new Object[5];

			if (ls[4].equals("+")) {
				values[0] = this.biFocused;
			} else {
				values[0] = this.biEmpty;
			}

			if (ls[0].equals("1")) {
				values[1] = this.biLayer01;
			}
			if (ls[0].equals("2")) {
				values[1] = this.biLayer02;
			}
			if (ls[0].equals("3")) {
				values[1] = this.biLayer03;
			}
			if (ls[0].equals("4")) {
				values[1] = this.biLayer04;
			}

			if (ls[1].equals("true")) {
				values[2] = this.biVisible;
			} else {
				values[2] = this.biInvisible;
			}

			if (ls[2].equals("true")) {
				values[3] = this.biUseOwn;
			} else {
				values[3] = this.biUseCommon;
			}

			values[4] = ls[3];

			this.dtmLayers.addRow(values);

		}

	}

	@Override
	public void subscribedAction(ActionEvent ae) {
		if (ae.getActionCommand().equals(JNavixActions.APP_LayerAddGPSTrack) || ae.getActionCommand().equals(JNavixActions.APP_LayerAddMap)
				|| ae.getActionCommand().equals(JNavixActions.APP_LayerAddSketch) || ae.getActionCommand().equals(JNavixActions.APP_LayerDnFocused)
				|| ae.getActionCommand().equals(JNavixActions.APP_LayerUpFocused) || ae.getActionCommand().startsWith(JNavixActions.APP_LayerFocused)
				|| ae.getActionCommand().equals(JNavixActions.APP_LayerRemoveFocused) || ae.getActionCommand().startsWith(JNavixActions.UI_CallCellClicked)
				|| ae.getActionCommand().startsWith(JNavixActions.UI_UpdateLayersList)) {
			this.updateTable();
			CLog.info(this,"Table updated.");
		}

	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// if (e.isPopupTrigger()) {
				showMenu(e);
				// }
			}

			// public void mouseReleased(MouseEvent e) {
			// if (e.isPopupTrigger()) {
			// showMenu(e);
			// }
			// }
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
