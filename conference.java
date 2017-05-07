import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;  
public class conference 
{
	XMPPConnection connection;
	String roomjid;
	static MultiUserChat muc;
	JFrame mainframe;
	JLabel header;
	JButton useradd;
	JPanel rosterpanel,messagepanel,panel2;
	DefaultListModel<String> model,model1;
	String me;
	JList<String> list,list1;
	@SuppressWarnings("static-access")
	conference(XMPPConnection c,String room)
	{
		connection=c;
		muc=new MultiUserChat(connection,room);
		me=JOptionPane.showInputDialog(null, "Enter your nickname for the room");
		System.out.println(muc.getRoom());
		roomjid=muc.getRoom();
		System.out.println(roomjid);
		try 
		{
			muc.create(me);
			//muc.sendConfigurationForm(null);
			if(!muc.isJoined())
				muc.join(me);
			System.out.println(connection.getUser());
			//muc.grantAdmin(admin);
		} 
		catch (XMPPException e) 
		{
			e.printStackTrace();
		}
		mainframe=new JFrame();
		mainframe.setLayout(new BorderLayout());
		mainframe.setSize(400, 400);
		mainframe.setVisible(true);
		initheader();
		//System.out.println(muc.getOccupantsCount());
		inithistory();
		initroster();
		adduser();
		muc.addInvitationRejectionListener(new InvitationRejectionListener(){

			@Override
			public void invitationDeclined(String invitee, String reason) 
			{
				JOptionPane.showMessageDialog(null,"invitee "+invitee+" rejects your invitation", null,JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		muc.addMessageListener(new PacketListener(){

			@Override
			public void processPacket(Packet arg0) 
			{
				if(arg0 instanceof Message)
				{
					String user[]=arg0.getFrom().split("/");
					model1.addElement(user[1]+": "+((Message) arg0).getBody());
				}
			}
			
		});
		
	}
	public void initheader()
	{
		String[] s=roomjid.split("@");
		header=new JLabel("Welcome to conference room"+"     "+s[0]);
		mainframe.add(header,BorderLayout.NORTH);
	}
	public void adduser()
	{
		useradd=new JButton("Add User");
		useradd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String user=JOptionPane.showInputDialog(null, "Enter the name of user to add");
				if(user!=null)
				{
					user=user+"@"+connection.getServiceName();
					muc.invite(user,null);
				}
				
			}
			
		});
		mainframe.add(useradd,BorderLayout.SOUTH);
	}
	public void initroster()
	{
		rosterpanel =new JPanel();
		model=new DefaultListModel<String>();
		list=new JList<String>(model);
		model.addElement(me);
		Collection<Occupant> roster = null;
		try 
		{
			roster = muc.getParticipants();
		} 
		catch (XMPPException e) 
		{
			e.printStackTrace();
		}
		if(roster!=null)
		{
			for(Occupant i:roster)
			{
				Presence presence=connection.getRoster().getPresence(i.getJid());
				if(presence.isAvailable())
				{
					model.addElement(i.getNick());
					//System.out.println(i.getNick());
				}
			}
		}
//		try {
//			System.out.println(muc.getModerators().size()+muc.getParticipants().size());
//		} catch (XMPPException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try
		{
			roster=muc.getModerators();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(roster!=null)
		{
			for(Occupant i:roster)
			{
				Presence presence=connection.getRoster().getPresenceResource(i.getJid());
				System.out.println(i.getJid()+"presence: "+presence.getStatus());
				if(presence.isAvailable())
				{
					model.addElement(i.getNick());
					//System.out.println(i.getNick());
				}
			}
		}
		rosterpanel.add(list);
		mainframe.add(rosterpanel,BorderLayout.WEST);
	}
	public void inithistory()
	{
		messagepanel=new JPanel();
		model1=new DefaultListModel<String>();
		list1=new JList<String>(model1);
		messagepanel.add(list1);
		mainframe.add(messagepanel,BorderLayout.EAST);
	}
	
}
