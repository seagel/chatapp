import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class Friend 
{
	JFrame mainframe;
	JLabel header;
	JPanel jid,name,group;
	XMPPConnection connection;
	singleusermanager suserm;
	static String request;
	Friend(XMPPConnection c)
	{
		
		connection=c;
		suserm=new singleusermanager(c);
		mainframe=new JFrame();
		mainframe.setSize(400, 400);
		mainframe.setVisible(true);
		mainframe.setLayout(new GridLayout(5,1));
		initheader();
		filldetails_Friend();
	
		
	}
	void initheader()
	{
		header=new JLabel("Enter your details");
		header.setLayout(new FlowLayout());
		mainframe.add(header);
	}
		void filldetails_Friend()
	{
		jid =new JPanel();
		jid.setLayout(new FlowLayout());
		name=new JPanel();
		name.setLayout(new FlowLayout());
		group=new JPanel();
		group.setLayout(new FlowLayout());
		JTextField jid_text=new JTextField(10);
		JTextField name_text=new JTextField(10);
		JTextField group_text=new JTextField(10);
		JLabel jid_label=new JLabel("Enter the jid",JLabel.LEFT);
		JLabel name_label=new JLabel("Enter the name",JLabel.LEFT);
		JLabel group_label=new JLabel("Enter the group you want to add friend",JLabel.LEFT);
		jid.add(jid_label);
		jid.add(jid_text);
		name.add(name_label);
		name.add(name_text);
		group.add(group_label);
		group.add(group_text);
		mainframe.add(name);
		mainframe.add(jid);
		mainframe.add(group);
		JButton send=new JButton("Send Request");
		send.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				suserm.sendfriendrequest(jid_text.getText()+'@'+connection.getServiceName(),name_text.getText());
				System.out.println(connection.getServiceName());
				mainframe.setVisible(false);
			}
			
		});
		mainframe.add(send);
		mainframe.setVisible(true);

	}
	
}
