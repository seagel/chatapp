import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jivesoftware.smackx.ReportedData;
public class searchdialog 
{
	private JFrame mainframe;
	private JPanel resultpanel;
	private ReportedData searchresult;
	searchdialog(ReportedData result)
	{
		searchresult=result;
		mainframe=new JFrame("Search Results");
		mainframe.setSize(600, 600);
		mainframe.setLayout(new BorderLayout());
		mainframe.setVisible(true);
		mainframe.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent w)
			{
				System.out.println("Bye");
				mainframe.setVisible(false);
			}
		});

		result();
	}
	void result()
	{
		resultpanel=new JPanel();
//		DefaultListModel<String> model=new DefaultListModel<String>();
//		JList<String> searchlist=new JList<String>(model);
		DefaultTableModel model=new DefaultTableModel(new String[]{"Name","Username","Email"},0);
		JTable searchtable=new JTable(model);
		searchtable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchtable.setFont(new Font("Times New Roman", Font.BOLD, 12));
		searchtable.setDefaultEditor(getClass(), null);
		Iterator<ReportedData.Row> it=searchresult.getRows();
		ReportedData.Row row=null;
		while(it.hasNext())
		{
			row=it.next();
			String rowData[]={row.getValues("Username").next().toString(),row.getValues("Name").next().toString(),row.getValues("Email").next().toString()};
			model.addRow(rowData);
		}
		resultpanel.add(new JScrollPane(searchtable));

		mainframe.add(resultpanel,BorderLayout.CENTER);
		
	}
}
