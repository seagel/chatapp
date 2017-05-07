import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.smack.XMPPConnection;

public class Defriend 
{
	JFrame mainframe;
	JLabel header;
	JPanel jid;
	XMPPConnection connection;
	singleusermanager suserm;
	Defriend(XMPPConnection c)
	{
		connection=c;
		suserm=new singleusermanager(c);
		mainframe=new JFrame();
		mainframe.setSize(400, 400);
		mainframe.setVisible(true);
		mainframe.setLayout(new GridLayout(3,1));
		initheader();
		filldetails_Unfriend();
		//sendrequest();
		
	}
	void initheader()
	{
		header=new JLabel("Enter your details");
		header.setLayout(new FlowLayout());
		mainframe.add(header);
	}
	
	void filldetails_Unfriend()
	{
		jid =new JPanel();
		jid.setLayout(new FlowLayout());
		JTextField jid_text=new JTextField(10);
		JLabel jid_label=new JLabel("Enter the jid",JLabel.LEFT);
		jid.add(jid_label);
		jid.add(jid_text);
		mainframe.add(jid);

		JButton send=new JButton("Send Request");
		send.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				suserm.unfrienduser(jid_text.getText()+'@'+connection.getServiceName());
				mainframe.setVisible(false);
			}
			
		});
		mainframe.add(send);
		mainframe.setVisible(true);

	}
}
