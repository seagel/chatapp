import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.*;
import org.jivesoftware.smackx.provider.*;
import org.jivesoftware.smackx.search.UserSearch;

import java.sql.Timestamp;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.Date;
public class usermanager 
{
	XMPPConnection connection=null;
	private static ConnectionConfiguration config;
	public boolean registeruser(String username,String pass,Map<String,String> attri)
	{
		try{connection.connect();}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		AccountManager acm=connection.getAccountManager();
		/*System.out.println("Enter username");
		String username=sc.next();
		System.out.println("Enter Password");
		String pass=sc.next();
		Map<String,String> attri=new HashMap<String,String>();
		System.out.println("Enter Name of user");
		attri.put("name", sc.next());
		System.out.println("Enter your email");
		attri.put("email", sc.next());*/
		try{acm.createAccount(username, pass, attri);}
		catch(Exception e)
		{
			System.out.println("Error in creating account");
			return false;
		}
		return true;
	}
	
	public XMPPConnection getconnection()
	{
		if(config==null)
		{
			config=new ConnectionConfiguration("som-Lenovo-G50-70",5222);
			config.setSASLAuthenticationEnabled(true);
			SASLAuthentication.supportSASLMechanism("PLAIN", 0); 
			//config.setDebuggerEnabled(true);
			config.setRosterLoadedAtLogin(true);
			config.setCompressionEnabled(true);
			config.setReconnectionAllowed(true);
			config.setSendPresence(true);
		}
		if(connection==null)
		{
			connection=new XMPPConnection(config);
		}
		return connection;
	}
	
	public boolean login(String username,String pass)
	{
		
		try{ 
			if(connection==null)
				connection=this.getconnection();
			if(!connection.isConnected())
				connection.connect();
			connection.login(username, pass,resource());
			connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			System.out.println("Invalid username or password");
			connection.disconnect();
			//connection=null;
			return false;
			//System.exit(1);
		}
		return true;
	}
	public void changepassword(String npass1)
	{
		AccountManager acm=connection.getAccountManager();
		try
		{
			acm.changePassword(npass1);
		}
		catch(Exception e)
		{
			System.out.println("Password change failed");
		}
	}
	public String resource()
	{
		return new Timestamp(new Date().getTime()).toString();
	}
	public void deleteuser()
	{
		AccountManager acm=connection.getAccountManager();
		try{acm.deleteAccount();
			System.out.println("Account deleted successfully");
		}
		catch(Exception e)
		{
			System.out.println("Request cannot be processed");
		}
		
	}
	public boolean logout(XMPPConnection c)
	{
		if(c.isConnected())
		{
			try{c.disconnect();
			System.out.println("Successfully log out");}
			catch(Exception e)
			{
				System.out.println("Error performing logout");
				return false;
			}
		}
		return true;
	}
	public String checkpresence(String jid)
	{
		String url="http://localhost:9090/plugins/presence/status?jid="+jid+"&req_jid="+jid+"&type=xml";
		try
		{
			System.out.println(jid);
			URL curl=new URL(url);
			URLConnection uconn=curl.openConnection();
			if(uconn!=null)
			{
				Scanner sc=new Scanner(new InputStreamReader(uconn.getInputStream()));
				if(sc!=null)
				{
					String flag=sc.nextLine();
					if(flag.indexOf("type=\"unavailable\"")>=0)
					{
						return "User available";
					}
					else if(flag.indexOf("type=\"error\"")>=0)
					{
						return "error";
					}
					else
					{
						return "User available";
					}
				}
				sc.close();
			}
			
		}
		catch(Exception e)
		{
			System.out.println("error opening connection");
		}
		return "error";
	}
	
}
