import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

//import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;  
public class conferenceparticipant
{
	XMPPConnection connection;
	String roomjid;
	singleusermanager suserm;
	MultiUserChat muc_u;
	JFrame mainframe;
	JLabel header;
	JPanel chat;
	JButton useradd;
	JPanel rosterpanel;
	DefaultListModel<String> model,model1;
	String me;
	JTextField messagebox;
	JList<String> list,list1;
	conferenceparticipant(XMPPConnection c,String room)
	{
		connection=c;
		suserm=new singleusermanager(c);
		muc_u=new MultiUserChat(connection,room);
		//muc_o=new MultiUserChat(owner,room);
		me=JOptionPane.showInputDialog(null, "Enter your nickname for the room");
		System.out.println(muc_u.getRoom());
		roomjid=muc_u.getRoom();
		//System.out.println(roomjid);
		try 
		{
			//muc.create(me);
			//muc.sendConfigurationForm(null);
			if(!muc_u.isJoined())
				muc_u.join(me);
			System.out.println(connection.getUser());
			//muc.grantAdmin(admin);
		} 
		catch (XMPPException e) 
		{
			e.printStackTrace();
		}
		mainframe=new JFrame();
		mainframe.setLayout(new GridLayout(3,1));
		mainframe.setSize(400, 400);
		mainframe.setVisible(true);
		initheader();
		initmessagebox();
		inithistory();
		//initpanel2();
		//System.out.println(muc.getOccupantsCount());
		
		//initroster();
		muc_u.addInvitationRejectionListener(new InvitationRejectionListener(){

			@Override
			public void invitationDeclined(String invitee, String reason) 
			{
				JOptionPane.showMessageDialog(null,"invitee "+invitee+" rejects your invitation", null,JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		muc_u.addMessageListener(new PacketListener(){

			@Override
			public void processPacket(Packet arg0) 
			{
				if(arg0 instanceof Message&&((Message) arg0).getType()==Message.Type.groupchat)
				{
					String user[]=arg0.getFrom().split("/");
					if(user[1]!=me)
					{
						model1.addElement(user[1]+" :"+((Message) arg0).getBody());
						System.out.println(((Message) arg0).getBody()+" "+arg0.getFrom()+"  "+arg0.getTo());
					}
				}
			}
			
		});
		//inithistory();
		//initpanel2();
	}
	public void initheader()
	{
		String[] s=roomjid.split("@");
		header=new JLabel("Welcome to conference room"+"     "+s[0]);
		mainframe.add(header);
	}
	public void initmessagebox()
	{
		chat=new JPanel();
		chat.setLayout(new GridLayout(3,1));
		messagebox=new JTextField(10);
		JLabel label=new JLabel("Enter the message");
		JButton submit=new JButton("Send");
		chat.add(label);
		chat.add(messagebox);
		chat.add(submit);
		submit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
				Date date=new Date();
				ArrayList<Pair<String, String>> obj=userui.chathistory.get(roomjid);
				if(obj==null)
				{
					obj=new ArrayList<Pair<String,String>>();
					obj.add(new Pair<String, String>(sdf.format(date),messagebox.getText().toString()));
					userui.chathistory.put(roomjid, obj);
				}
				else
				{
					obj.add(new Pair<String, String>(sdf.format(date),messagebox.getText().toString()));
					userui.chathistory.put(roomjid, obj);
				}
				try 
				{
					muc_u.sendMessage(messagebox.getText().toString());
				} 
				catch (XMPPException e1) 
				{
					e1.printStackTrace();
				}
				messagebox.setText(null);
				
				//inithistory();
			}
			
		});
		mainframe.add(chat,BorderLayout.SOUTH);
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
			roster = muc_u.getParticipants();
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
			roster=muc_u.getModerators();
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
		if(list!=null)
			rosterpanel.add(list);
		mainframe.add(rosterpanel);
	}
	public void inithistory()
	{
		model1=new DefaultListModel<String>();
		list1=new JList<String>(model1);		
		mainframe.add(list1);
	}
	
}
