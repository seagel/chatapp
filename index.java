import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jivesoftware.smack.XMPPConnection;
public class index
{
	usermanager userm=new usermanager();
	XMPPConnection connection=userm.getconnection();
	singleusermanager suserm=new singleusermanager(connection);
	private JFrame mainframe;
	private JLabel headerpanel;
	private JPanel controlpanel1,controlpanel2;
	index()
	{
		mainframe=new JFrame("Welcome to chat box");
		mainframe.setSize(400, 400);
		mainframe.setVisible(true);
		mainframe.setLayout(new GridLayout(3,1));
		mainframe.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent w)
			{
				System.out.println("Bye");
				System.exit(0);
			}
		});
		headerpanel=new JLabel("Enter your username and password",JLabel.CENTER);
		headerpanel.setSize(250,100);
		controlpanel1=new JPanel();
		controlpanel2=new JPanel();
		controlpanel1.setLayout(new FlowLayout());
		controlpanel2.setLayout(new FlowLayout());
		mainframe.add(headerpanel);
		mainframe.add(controlpanel1);
		mainframe.add(controlpanel2);
		regpage();
	}
	public void regpage()
	{
		JLabel userlabel=new JLabel("Username",JLabel.RIGHT);
		JLabel passlabel=new JLabel("Password",JLabel.CENTER);
		final JTextField username=new JTextField(10);
		final JPasswordField pass=new JPasswordField(10);
		controlpanel1.add(userlabel);
		controlpanel1.add(username);
		controlpanel1.add(passlabel);
		controlpanel1.add(pass);
		JButton login=new JButton("Login");
		login.setAlignmentX(JButton.RIGHT_ALIGNMENT);
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				boolean flag=userm.login(username.getText(),new String(pass.getPassword()));
				if(flag)
				{
					JOptionPane.showMessageDialog(mainframe,"logging in");
					mainframe.setVisible(false);
					new userui(connection,username.getText());
				}
				else
				{
					JOptionPane.showMessageDialog(mainframe,"Invalid credentials");
					
				}
			}
		});
		JButton signup=new JButton("Signup");
		signup.setAlignmentX(JButton.LEFT_ALIGNMENT);
		signup.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				mainframe.setVisible(false);
				new signup();
			}
		});
		controlpanel2.add(signup);
		controlpanel2.add(login);
		mainframe.setVisible(true);
	}
	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() 
			{
				new index();
				
			}
			
		});
	}
}