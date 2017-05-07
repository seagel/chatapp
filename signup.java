import javax.swing.*;
import org.jivesoftware.smack.XMPPConnection;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class signup 
{
	private JFrame mainframe;
	private JLabel header;
	private JPanel namep;
	private JPanel userp;
	private JPanel emailp,pass1p,pass2p,phnop;
	usermanager userm=new usermanager();
	private XMPPConnection connection=userm.getconnection();
	signup()
	{
		mainframe=new JFrame("Sign Up");
		mainframe.setSize(400, 400);
		mainframe.setVisible(true);
		mainframe.setLayout(new GridLayout(8,1));
		initheader();
		filldetails();
	}
	void initheader()
	{
		header=new JLabel("Enter your details");
		header.setLayout(new FlowLayout());
		mainframe.add(header);
	}
	void filldetails()
	{
		
		final JTextField user=new JTextField(10);
		final JTextField name=new JTextField(10);
		final JPasswordField pass=new JPasswordField(10);
		final JTextField email=new JTextField(10);
		final JTextField phno=new JTextField(10);
		final JPasswordField pass2=new JPasswordField(10);
		JLabel userlabel=new JLabel("Username",JLabel.LEFT);
		JLabel namelabel=new JLabel("Name",JLabel.LEFT);
		JLabel passlabel=new JLabel("Password",JLabel.LEFT);
		JLabel emaillabel=new JLabel("Email",JLabel.LEFT);
		JLabel phnolabel=new JLabel("Phone No.",JLabel.LEFT);
		JLabel pass2label=new JLabel("Password",JLabel.LEFT);
		//Panel Starts
		namep=new JPanel();
		namep.setLayout(new FlowLayout());
		namep.add(namelabel);
		namep.add(name);
		userp=new JPanel();
		userp.setLayout(new FlowLayout());
		userp.add(userlabel);
		userp.add(user);
		pass1p=new JPanel();
		pass1p.setLayout(new FlowLayout());
		pass1p.add(passlabel);
		pass1p.add(pass);
		pass2p=new JPanel();
		pass2p.setLayout(new FlowLayout());
		pass2p.add(pass2label);
		pass2p.add(pass2);
		emailp=new JPanel();
		emailp.setLayout(new FlowLayout());
		emailp.add(emaillabel);
		emailp.add(email);
		phnop=new JPanel();
		phnop.setLayout(new FlowLayout());
		phnop.add(phnolabel);
		phnop.add(phno);
		//adding to layout panel 
		mainframe.add(userp);
		mainframe.add(namep);
		mainframe.add(emailp);
		mainframe.add(pass1p);
		mainframe.add(pass2p);
		mainframe.add(phnop);
		mainframe.setVisible(true);
		JButton submit=new JButton("Signup");
		submit.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String username=user.getText();
				String password=new String(pass.getPassword());
				Map<String,String> attri=new HashMap<String,String>();
				attri.put("name", name.getText());
				attri.put("email", email.getText());
				boolean flag=userm.registeruser(username, password, attri);
				if(flag)
				JOptionPane.showMessageDialog(mainframe, "Account successfully created");
				else
				{
					JOptionPane.showMessageDialog(mainframe, "Error creating account");
				}
				mainframe.setVisible(false);
				userm.login( username, password);
				new userui(userm.connection,username);
			}
		});
		mainframe.add(submit);
	}

}
