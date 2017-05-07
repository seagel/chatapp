import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
public class userui
{
	private JFrame mainframe;
	private JMenu menu;
	private JLabel header;
	private JPanel rosterpanel;
	private JPanel searchpanel;
	private JPanel presencepanel;
	private  String me;
	private usermanager userm=new usermanager();
	private DefaultListModel<String> model;
	private XMPPConnection connection=null;
	private singleusermanager suserm=null;
	private Map<String,chatdialog> chatboxes=new HashMap<String,chatdialog>();
	static public Map<String,ArrayList<Pair<String,String>>> chathistory=new HashMap<String,ArrayList<Pair<String,String>>>();
	userui(XMPPConnection c,String text)
	{
		connection=c;
		userm.connection=c;
		suserm=new singleusermanager(c);
		//suserm.addListener();
		mainframe=new JFrame("Hello "+text+"");
		me=text;
		mainframe.setSize(400, 400);
		mainframe.setVisible(true);
		mainframe.setLayout(new BorderLayout());
		initmenu();
		initpresencepanel();
		initroster();
		initsearch();
		addPresenceListener();
		addFileTransferListener();
		new conferenceinvitation(c,"1.@conference."+connection.getServiceName());
	}
	void initpresencepanel()
	{
		presencepanel=new JPanel();
		presencepanel.setLayout(new GridLayout(2,1));
		String[] presence={"Online","Dnd","Invisible"};
		JComboBox<String> jcb=new JComboBox<String>(presence);
		jcb.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String s=(String) jcb.getSelectedItem();
				if(s=="Online")
				{
					suserm.changeStatus(1);
				}
				else if(s=="Dnd")
				{
					suserm.changeStatus(2);
				}
				else
				{
					suserm.changeStatus(3);
				}
				
			}
			
		});
		header=new JLabel("Active Users");
		header.setFont(new Font("Times New Roman", Font.BOLD, 12));
		presencepanel.add(jcb);
		presencepanel.add(header);
		mainframe.add(presencepanel,BorderLayout.NORTH);
	}
	void initmenu()
	{
		JMenuBar menubar=new JMenuBar();
		JMenu filemenu=new JMenu("File");
		JMenu contacts=new JMenu("Contacts");
		JMenu setting=new JMenu("setting");
		JMenu Help=new JMenu("Help");
		//file menu items starts
		
		// logout option
		
		JMenuItem logout=new JMenuItem("Log out");
		logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if(userm.logout(connection))
				{
					JOptionPane.showMessageDialog(mainframe, "Logout successfully");
					mainframe.setVisible(false);
					new index();
				}
				else
				{
					JOptionPane.showMessageDialog(mainframe, "forcefully Logging out ");
					mainframe.setVisible(false);
					new index();
				}
			}
		});
		//logout ends
		JMenuItem addFriend=new JMenuItem("Add Friend");
		addFriend.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				new Friend(connection);
				
			}
			
		});
		JMenuItem unfriend=new JMenuItem("unfriend");
		unfriend.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				new Defriend(connection);
			}
			
		});
		JMenuItem pass=new JMenuItem("Change Password");
		pass.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String npass1=JOptionPane.showInputDialog(null, "Enter the new Password");
				if(npass1!=null)
				{
					userm.changepassword(npass1);
				}
			}
			
		});
		JMenuItem userpresence=new JMenuItem("Check Presence");
		userpresence.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String user=JOptionPane.showInputDialog(null,"Enter the user name");
				user=user+"@"+connection.getServiceName();
				String status=userm.checkpresence(user);
				if(status=="error")
				{
					JOptionPane.showMessageDialog(null, "User not found");
				}
				else
				{
					JOptionPane.showMessageDialog(null, "User found");
				}
			}
			
		});
		JMenuItem conferenceroom=new JMenuItem("Start a conference");
		conferenceroom.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String room=JOptionPane.showInputDialog("Enter the room name");
				room=room+"@conference."+connection.getServiceName();
				new conference(connection,room);
			}
			
		});
		filemenu.add(logout);
		contacts.add(addFriend);
		contacts.add(unfriend);
		setting.add(pass);
		setting.add(userpresence);
		setting.add(conferenceroom);
		menubar.add(filemenu);
		menubar.add(contacts);
		menubar.add(setting);
		menubar.add(Help);
		mainframe.setJMenuBar(menubar);
	}
	void addFileTransferListener()
	{
		FileTransferManager ftm=suserm.ftm;
		ftm.addFileTransferListener(new FileTransferListener(){

			@Override
			public void fileTransferRequest(FileTransferRequest arg0) 
			{
				int result=JOptionPane.showConfirmDialog(null,"Do you want to accept the file ", null, JOptionPane.YES_NO_CANCEL_OPTION);
				if(result==JOptionPane.YES_OPTION)
				{
					IncomingFileTransfer transfer=arg0.accept();
					File file=new File("/home/som/chat/"+arg0.getFileName());
					System.out.println(file.getAbsolutePath());
					String user=arg0.getRequestor();
					try 
					{
						transfer.recieveFile(file);
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
						Date date=new Date();
						ArrayList<Pair<String, String>> obj=userui.chathistory.get(me+"@"+connection.getServiceName()+user);
						if(obj==null)
						{
							obj=new ArrayList<Pair<String,String>>();
							obj.add(new Pair<String, String>(sdf.format(date),"File Transfer :"+arg0.getFileName()));
							userui.chathistory.put(me+user, obj);
						}
						else
						{
							obj.add(new Pair<String, String>(sdf.format(date),"File Transfer :"+arg0.getFileName()));
							userui.chathistory.put(me+user, obj);
						}
					} 
					catch (XMPPException e) 
					{
							System.out.println("File transfer failed");
					}
				}
				else
				{
					arg0.reject();
				}
			}
		});
	}
	void addPresenceListener() 
	{
		
		connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
		connection.addPacketListener(new PacketListener(){

			@Override
			public void processPacket(Packet arg0) 
			{
				
				if(arg0 instanceof Presence)
				{
					
					System.out.println("HI");
					if(((Presence) arg0).getType().equals(Presence.Type.subscribe))
					{
						int choice=JOptionPane.YES_NO_OPTION;
						int result=JOptionPane.showConfirmDialog(null, "Do you want to accept the friend request","Request from "+arg0.getFrom(), choice);
						if(result==JOptionPane.YES_OPTION)
						{
							Presence newp = new Presence(Presence.Type.subscribed);
	                        newp.setMode(Presence.Mode.available);
	                        newp.setTo(arg0.getFrom());
	                        connection.sendPacket(newp);
	                        System.out.println(arg0.getFrom());
	                        String[] a=arg0.getFrom().split("@");
	                        String[] b=new String[1];
	                        b[0]="friends";
	                        try {
								connection.getRoster().createEntry(arg0.getFrom(),a[0],b);
							} 
	                        catch (XMPPException e) 
	                        {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else if(((Presence) arg0).getType().equals(Presence.Type.unsubscribe))
					{
						if(connection.getRoster().getEntry(arg0.getFrom())!=null)
						{
							int choice=JOptionPane.YES_NO_OPTION;
							int result=JOptionPane.showConfirmDialog(null, "Do you want to delete the friend "," Request from "+arg0.getFrom(), choice);
							if(result==JOptionPane.YES_OPTION)
							{
								Presence newp = new Presence(Presence.Type.unsubscribed);
		                        newp.setMode(Presence.Mode.available);
		                        newp.setTo(arg0.getFrom());
		                        connection.sendPacket(newp);
		                        try {
		                        	if(connection.getRoster().getEntry(arg0.getFrom())!=null)
		                        		connection.getRoster().removeEntry(connection.getRoster().getEntry(arg0.getFrom()));
								} 
		                        catch (XMPPException e) 
		                        {
									//e.printStackTrace();
								}
		                        //System.out.println(arg0.getFrom());
							}
						}
					}
				}
					
			}
			
		}, new PacketFilter(){

			@Override
			public boolean accept(Packet packet) 
			{

	                if (packet instanceof Presence) {
	                    Presence presence = (Presence) packet;
	                    if (presence.getType().equals(Presence.Type.subscribed)
	                            || presence.getType().equals(
	                                    Presence.Type.subscribe)
	                            || presence.getType().equals(
	                                    Presence.Type.unsubscribed)
	                            || presence.getType().equals(
	                                    Presence.Type.unsubscribe)) {
	                        return true;
	                    }
	                }
	                return false;
			}
		});
	
	}

	void addRosterListener(DefaultListModel<String> model)
	{
		Collection<RosterEntry> list=suserm.getallusers();
		connection.getRoster().addRosterListener(new RosterListener(){

			@Override
			public void entriesAdded(Collection<String> arg0) 
			{
				model.clear();
				model.addElement(me);
				for(RosterEntry i:list)
				{
					Presence presence=connection.getRoster().getPresence(i.getUser());
					if(presence.isAvailable())
					{
						model.addElement(i.getName());
					}
				}
				
			}

			@Override
			public void entriesDeleted(Collection<String> arg0) 
			{
				model.clear();
				model.addElement(me);
				for(RosterEntry i:list)
				{
					Presence presence=connection.getRoster().getPresence(i.getUser());
					System.out.println(i.getUser());
					if(presence.isAvailable())
					{
						model.addElement(i.getName());
					}
				}
				
			}

			@Override
			public void entriesUpdated(Collection<String> arg0) 
			{
				model.clear();
				model.addElement(me);
				for(RosterEntry i:list)
				{
					Presence presence=connection.getRoster().getPresence(i.getUser());
					if(presence.isAvailable())
					{
						model.addElement(i.getName());
					}
				}
				
			}

			@Override
			public void presenceChanged(Presence arg0) {
				//rosterlist.clearSelection();
				model.clear();
				model.addElement(me);
				for(RosterEntry i:list)
				{
					Presence presence=connection.getRoster().getPresence(i.getUser());
					if(presence.isAvailable())
					{
						model.addElement(i.getName());
					}
				}
				
			}
			
		});

	}
	void initroster()
	{
		rosterpanel =new JPanel();
			model=new DefaultListModel<String>();
			final JList<String> rosterlist=new JList<String>(model);
			model.addElement(me);
			Collection<RosterEntry> list=suserm.getallusers();
			for(RosterEntry i:list)
			{
				Presence presence=connection.getRoster().getPresence(i.getUser());
				if(presence.isAvailable())
				{
					System.out.println("presence: "+i.getUser());
					model.addElement(i.getName());
				}
			}
			addRosterListener(model);
						rosterlist.addListSelectionListener(new ListSelectionListener() {

	            @Override
	            public void valueChanged(ListSelectionEvent arg0) {
	                if (!arg0.getValueIsAdjusting())
	                {
	                	try
	                		{
	                		if(chatboxes.get(rosterlist.getSelectedValue().toString())==null)
	                		chatboxes.put(rosterlist.getSelectedValue().toString(),new chatdialog(connection,rosterlist.getSelectedValue().toString(),me,rosterlist));
	                	else
	                	{	
	                		chatdialog chatd=chatboxes.get(rosterlist.getSelectedValue().toString());
	                		chatd.mainframe.setVisible(true);
	                	}
	                	rosterlist.clearSelection();
	                		        
	                }
	                	catch(Exception e)
	                	{
	                		System.out.println("Bye");
	                	}
	            }
	            }
	        });			
			rosterpanel.add(rosterlist);
			mainframe.add(rosterpanel,BorderLayout.WEST);
		}
		
	void initsearch()
	{
		searchpanel=new JPanel();
		searchpanel.setLayout(new FlowLayout());
		final JTextField searchfield=new JTextField(10);
		JLabel searchlabel=new JLabel("Enter Username");
		JButton search=new JButton("Search");
		search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				org.jivesoftware.smackx.ReportedData result=suserm.usersearch(searchfield.getText());
				new searchdialog(result);
			}
		});
		searchpanel.add(searchlabel);
		searchpanel.add(searchfield);
		searchpanel.add(search);
		mainframe.add(searchpanel,BorderLayout.SOUTH);
	}
	
}

