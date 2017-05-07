import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
public class chatdialog 
{
	 JFrame mainframe;
	private JPanel prevchat;
	private JPanel chat;
	private String user;
	private String me;
	private String sn;
	private JPanel options,buttons;
	DefaultListModel<String> model;
	private JList<String> list;
	singleusermanager suserm=null;
	XMPPConnection connection=null;
	public chatdialog(XMPPConnection c,String text,String u,JList<String> rl)
	{	
		connection=c;
		user=text+"@"+c.getServiceName();
		me=u+"@"+c.getServiceName();
		sn=c.getServiceName();
		suserm=new singleusermanager(c);
		mainframe =new JFrame("Chat Box");
		mainframe.setSize(600, 600);
		mainframe.setLayout(new BorderLayout());
		mainframe.setVisible(true);
		mainframe.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent w)
			{
				//rl.clearSelection();
				System.out.println("Bye");
			}
		});
		getprevchat();
		//startchat();
		initchatpanel();
		addpacketlistener();
	}
	void addpacketlistener()
	{
		connection.addPacketListener(new PacketListener(){

			@Override
			public void processPacket(Packet arg0) 
			{
				if(arg0 instanceof Message)
				{
					String sender[]=arg0.getFrom().split("@");
					if(((Message) arg0).getBody()!=null)
					model.addElement(sender[0]+" :"+((Message) arg0).getBody());
				}
			}
			
		},null);
	}
	void getprevchat()
	{
		prevchat=new JPanel();
		prevchat.setLayout(new GridLayout(2,1));
		options=new JPanel();
		options.setLayout(new BorderLayout());
		JLabel header=new JLabel("Previous Chats");
		buttons=new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		JButton filetransferbutton=new JButton("Send File");
		JButton showfilesbutton=new JButton("Show recieved files");
		showfilesbutton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				File file=new File("/home/som/chat");
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) 
				{
					System.out.println("Not able to open file");
				}
			}
		});
		filetransferbutton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser filechooser=new JFileChooser();
				filechooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result=filechooser.showOpenDialog(buttons);
				File file=filechooser.getSelectedFile();
				if(result==JFileChooser.APPROVE_OPTION)
				{
					suserm.sendFile(user,file);
					model.addElement("File send"+filechooser.getSelectedFile().getName()+"Size: "+filechooser.getSize());
					System.out.println(user+filechooser.getSelectedFile().toString());
				}
			}
			
		});
		buttons.add(filetransferbutton);
		buttons.add(showfilesbutton);
		model=new DefaultListModel<String>();
		list=new JList<String>(model);
		ArrayList<Pair<String, String>> chatrecap=userui.chathistory.get(me+user);
//		System.out.println(me+user);
		ArrayList<Pair<String, String>> chatrecap2=userui.chathistory.get(user+me);
		int i=0,j=0;
		Pair<String,String> temp1,temp2;
		if(chatrecap!=null&&chatrecap2!=null)
		{
			while(i<chatrecap.size()&&j<chatrecap2.size())
			{
				temp1=chatrecap.get(i);
				temp2=chatrecap2.get(j);
				if(temp1.getLeft().compareTo(temp2.getLeft())<0)
				{
					if(temp1.getRight()!=null)
					model.addElement(me.split("@")[0]+":"+temp1.getRight());
					i++;
				}
				else
				{
					if(temp2.getRight()!=null)
					model.addElement(user.split("@")[0]+":"+temp2.getRight());
					j++;
				}
			}
			while(i<chatrecap.size())
			{
				model.addElement(me.split("@")[0]+":"+chatrecap.get(i).getRight());
				i++;
			}
			while(j<chatrecap2.size())
			{
				model.addElement(user.split("@")[0]+":"+chatrecap2.get(j).getRight());
				j++;
			}
		}
		
		options.add(header,BorderLayout.CENTER);
		options.add(buttons,BorderLayout.EAST);
		prevchat.add(options);
		prevchat.add(list);
		mainframe.add(prevchat,BorderLayout.CENTER);
	}
	void initchatpanel()
	{
		chat=new JPanel(new FlowLayout());
		
		final JTextArea messagebox=new JTextArea(10,20);
		JLabel messagelabel =new JLabel("Enter Message");
		JButton send=new JButton("Send");
		chat.add(messagelabel);
		chat.add(messagebox);
		chat.add(send);
		mainframe.add(chat,BorderLayout.SOUTH);
		send.addActionListener(new ActionListener()
		{

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
				Date date=new Date();
				ArrayList<Pair<String, String>> obj=userui.chathistory.get(me+user);
				if(obj==null)
				{
					obj=new ArrayList<Pair<String,String>>();
					obj.add(new Pair<String, String>(sdf.format(date),messagebox.getText().toString()));
					userui.chathistory.put(me+user, obj);
				}
				else
				{
					obj.add(new Pair<String, String>(sdf.format(date),messagebox.getText().toString()));
					userui.chathistory.put(me+user, obj);
				}
				suserm.sendmessage(user,messagebox.getText().toString());
				messagebox.setText(null);
				getprevchat();
			}
			
		});
	}
	
	
}
