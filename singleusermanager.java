import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.search.UserSearchManager;

public class singleusermanager
{
	usermanager userm=new usermanager();
	static XMPPConnection connection=null;
	static private Map<String,Chat> chatrecord=new HashMap<String,Chat>();
	FileTransferManager ftm;
	singleusermanager(XMPPConnection c)
	{
		userm.connection=c;
		connection=c;
		ftm=new FileTransferManager(c);
	}
	public void addgroup(String groupname )
	{
		if(checkgroup(groupname)==true)
		{
			System.out.println("Same groupname exists");
		}
		else
		{
			connection.getRoster().createGroup(groupname);
			String user=connection.getUser();
			String name=connection.getAccountManager().getAccountAttribute("name");
			try{connection.getRoster().createEntry(user, name,new String[]{groupname});
			System.out.println("Successfully done");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public boolean checkgroup(String groupname)
	{
        Collection<RosterGroup> rostergroup = connection.getRoster().getGroups();
        Iterator<RosterGroup> it= rostergroup.iterator();
        while(it.hasNext())
        {
        	if(it.next().getName()==groupname)
        	{
        		return true;
        	}
        }
        return false;
	}
	public RosterEntry getuser(String username)
	{
		return connection.getRoster().getEntry(username);
	}
	
	public void sendFile(String user,File file)
	{
		try
		{
		OutgoingFileTransfer transfer=ftm.createOutgoingFileTransfer(user);
		transfer.sendFile(file, null);
		}
		catch(Exception e)
		{
			System.out.println("Error in sending file");
		}
	}
	
	public void display()
	{
        Collection<RosterGroup> rostergroup = connection.getRoster().getGroups();
        Iterator<RosterGroup> it= rostergroup.iterator();
        while(it.hasNext())
        {
        	RosterGroup rs=it.next();
        	System.out.println(rs.getName());
        	Collection<RosterEntry> rosterentry =rs.getEntries();
        	Iterator<RosterEntry> it1=rosterentry.iterator();
    		while(it1.hasNext())
        	{
        		System.out.println(it1.next().getUser());
        	}
        }
	}
	public String[] getgroup()
	{
        Collection<RosterGroup> rostergroup = connection.getRoster().getGroups();
        if(rostergroup==null)
        {
        	return null;
        }
        String a[]=new String[connection.getRoster().getGroupCount()];
        Iterator<RosterGroup> it= rostergroup.iterator();
        int i=0;
        while(it.hasNext())
        {
        	a[i++]=it.next().getName();
        }
        return a;
	}
	public Collection<RosterEntry> getallusers()
	{
		return connection.getRoster().getEntries();
	}
	public void sendfriendrequest(String jid,String name)
	{
		
		try
		{
			Presence presence =new Presence(Presence.Type.subscribed);
			presence.setTo(jid);
			connection.sendPacket(presence);
			connection.getRoster().createEntry(jid, name,null);
			System.out.println("Successfully done");
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void unfrienduser(String jid)
	{
		RosterEntry rosterentry=connection.getRoster().getEntry(jid);
		if(rosterentry==null)
		{
			System.out.println("no user exist with this name");
		}
		else
		{
			try
			{
				Presence presence =new Presence(Presence.Type.unsubscribed);
				presence.setTo(jid);
				connection.sendPacket(presence);
				connection.getRoster().removeEntry(getuser(jid));
				System.out.println("Successfully done");
				//sc.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void changeStatus(int state)
	{
		Presence presence=null;
		switch(state)
		{
			case 1:
				presence=new Presence(Presence.Type.available);
				presence.setMode(Presence.Mode.available);
				connection.sendPacket(presence);
				break;
			case 2:
				presence=new Presence(Presence.Type.available);
				presence.setMode(Presence.Mode.dnd);
				connection.sendPacket(presence);
				break;
			case 3:
				presence=new Presence(Presence.Type.unavailable);
				presence.setMode(Presence.Mode.away);
				connection.sendPacket(presence);
				break;
		}
	}
	public void removegroup()
	{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter name of group");
		String groupname=sc.next();
		try
		{
			RosterGroup rostergroup =connection.getRoster().getGroup(groupname);
			if(rostergroup==null)
			{
				System.out.println("No group exists under your ownership with this name");
			}
			else
			{
				Collection<RosterEntry> entry=rostergroup.getEntries();
				Iterator<RosterEntry> it=entry.iterator();
				while(it.hasNext())
				{
					rostergroup.removeEntry(it.next());
				}
			}
			sc.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void addusertoagroup()
	{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter name of group");
		String groupname=sc.next();
		System.out.println("Enter username of user");
		String jid=sc.next();
		try
		{
			RosterGroup rostergroup=connection.getRoster().getGroup(groupname);
			rostergroup.addEntry(connection.getRoster().getEntry(jid));
			System.out.println("Successfully done");
			sc.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/*public void addListener()
	{
        connection.getChatManager().addChatListener(new ChatManagerListener() {
            @Override //check at compile time this method is correct
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) 
                    {
                    	StringUtils.parseName(userm.getconnection().getUser());
                        String from = message.getFrom();
                        String content = message.getBody();
                        System.out.println("From " + from + ": "+ content);
                    }
                });
            }
        });
    }*/
	public ReportedData usersearch(String user)
	{
		
		UserSearchManager usersearchmanager =new UserSearchManager(connection);
		ReportedData  results=null;
		try
		{
			String serviceDomain = "search."+connection.getServiceName();
			//System.out.println(serviceDomain);
			
			Form form1 =usersearchmanager.getSearchForm(serviceDomain);
			Form form2=form1.createAnswerForm();
			form2.setAnswer("Username", true);
			form2.setAnswer("search", user);
			results=usersearchmanager.getSearchResults(form2, serviceDomain);
			if(results==null)
			{
				System.out.println("No user found");
			}
			else
			{	
				Iterator<ReportedData.Row> it=results.getRows();
				ReportedData.Row row=null;
				int i=0;
				while(it.hasNext())
				{
					row=it.next();
					i++;
					System.out.println("Entry no : "+i+"");
					System.out.println("Username:"+row.getValues("Username").next().toString());
					System.out.println("Email:"+row.getValues("Email").next().toString());
					System.out.println("Name:"+row.getValues("Name").next().toString());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return results;
	}
	public Chat chatwithfriend(String user)
	{
		Chat chat=null;
		if(chatrecord.get(user)!=null)
		{
			chat=chatrecord.get(user);
		}
		else
		{chat=connection.getChatManager().createChat(user, new MessageListener(){
			public void processMessage(Chat chat,Message msg)
			{
				if(msg.getBody()!=null)
				{
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
					Date date=new Date();
					String from=msg.getFrom().split("/")[0];
					String to=msg.getTo().split("/")[0];
					//System.out.println(from+to);
					ArrayList<Pair<String,String>> obj=userui.chathistory.get(from+to);
					if(obj==null)
					{
						obj=new ArrayList<Pair<String,String>>();
						obj.add(new Pair<String, String>(sdf.format(date),msg.getBody()));
						userui.chathistory.put(from+to, obj);
					}
					else
					{
						obj.add(new Pair<String, String>(sdf.format(date),msg.getBody()));
						userui.chathistory.put(from+to, obj);
					}
					//System.out.println("From "+  msg.getFrom()+""+msg.getPacketID()+" "+ msg.getBody());
				}
			}
		});
		chatrecord.put(user, chat);
		
		}
		return chat;
	}
	public void sendmessage(String user,String message)
	{
		try
		{
			Chat chat=chatwithfriend(user);
				chat.sendMessage(message);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
