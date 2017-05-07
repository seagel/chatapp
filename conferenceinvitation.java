import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class conferenceinvitation
{

	XMPPConnection connection;
	MultiUserChat muc;
	@SuppressWarnings("static-access")
	conferenceinvitation(XMPPConnection c,String room)
	{
		connection=c;
		muc=new MultiUserChat(connection,room);
		muc.addInvitationListener(connection, new InvitationListener(){

			@Override
			public void invitationReceived(XMPPConnection conn, String room, String inviter, String reason, String pass,Message msg) 
			{
				int ans=JOptionPane.showConfirmDialog(null,"Invitation to join room from "+inviter+" to join room "+room+" ", null, JOptionPane.YES_NO_OPTION);
			    if(ans==JOptionPane.YES_OPTION)
			    {
			    	new conferenceparticipant(connection,room);
			    }
			    else
			    {
			    	MultiUserChat muc1=new MultiUserChat(connection,room);
			    	muc1.decline(connection, room, inviter, "not interested");
			    }
			}
			
		});
		
	}
	
}
