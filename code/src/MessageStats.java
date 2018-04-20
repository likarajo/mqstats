package com.mypack;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import com.jcraft.jsch.*;


public class MessageStats {
	
	protected static Shell shell;
	protected static Text textUser;
	protected static Text textPassword;
	protected static Text textTimeout;
	protected static Text textDate;
	protected static Text textMessage;
	protected Properties prop;
	Session session = null;
	
	
	public static void main(String[] args) {
		try {
			GCSSGQStats window = new GCSSGQStats();
			window.open();
		} 
		catch (Exception e) {
			textMessage.setText(""+e);
			e.printStackTrace();
		}
	}

	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createContents() {	    
	    		
		shell = new Shell();
		shell.setSize(750, 600);
		shell.setText("Message Stats");
		
		loadProperties();		
		
		textMessage = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textMessage.setEditable(false);
		textMessage.setBounds(300, 10, 400, 500);
		
		Label lblUser = new Label(shell, SWT.NONE);
		lblUser.setBounds(15, 10, 115, 33);
		lblUser.setText("User Name: ");
		
		textUser = new Text(shell, SWT.BORDER);
		textUser.setBounds(130, 10, 120, 33);
		
		Label lblPassword = new Label(shell, SWT.NONE);
		lblPassword.setBounds(15, 45, 115, 33);
		lblPassword.setText("Password:");
		
		textPassword = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		textPassword.setBounds(130, 45, 120, 33);
		
		Label lblSender = new Label(shell, SWT.NONE);
		lblSender.setBounds(15, 80, 115, 33);
		lblSender.setText("Message Sender:");

		final Combo comboSender = new Combo(shell, SWT.NONE);
		comboSender.setBounds(130, 80, 120, 35);
		String[] s = prop.getProperty("senders").split(";");
		for(int i=0; i<s.length; i++) 
		comboSender.add(s[i]);
		comboSender.setText("-- Sender --");

		Label lblTimeout = new Label(shell, SWT.NONE);
		lblTimeout.setBounds(15, 115, 115, 33);
		lblTimeout.setText("Timeout:");

		textTimeout = new Text(shell, SWT.BORDER);
		textTimeout.setBounds(130, 115, 120, 33);

		Label lblDate = new Label(shell, SWT.NONE);
		lblDate.setBounds(15, 150, 115, 33);
		lblDate.setText("Date yymmdd");

		textDate = new Text(shell, SWT.BORDER);
		textDate.setBounds(130, 150, 120, 33);

		Button btnGqStats = new Button(shell, SWT.NONE);
		btnGqStats.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    
				//String host = "10.255.111.152";
				String host = prop.getProperty("host");
				System.out.println(""+host);
			    String script = prop.getProperty("script");
			    
			    String user = textUser.getText();
			    String password = textUser.getText();
			    
			    String date = textDate.getText();
			    String timeout = textTimeout.getText();
			    String sender = comboSender.getText();
			    
			    String command = ""+script+" "+date+" "+timeout+" "+sender+"";
			    System.out.println(""+command);
			   
			    java.util.Properties config = new java.util.Properties(); 
		    	config.put("StrictHostKeyChecking", "no");
		    	
		    	Session session = null;
				JSch jsch = new JSch();
		    	
				try {
					session = jsch.getSession(user, host, 22);
					session.setPassword(password);
					System.out.println("Session created");
					
				   	session.setConfig(config);
				   	System.out.println("Session configured");
				   	
		    		session.connect();
			    	System.out.println("session connected");
			    	
			    	Channel channel=session.openChannel("exec");
			    	System.out.println("channel opened");
			    	
			    	((ChannelExec)channel).setCommand(command);
			    	System.out.println("command set");
			    	
			        channel.setInputStream(null);
			        System.out.println("input stream set");
			        
			        ((ChannelExec)channel).setErrStream(System.err);
			        System.out.println("error stream set");
			        
			        InputStream in=channel.getInputStream();
			        System.out.println("input stream get");
			        
			        channel.connect();
			        System.out.println("channel connected");
			        
			        byte[] tmp=new byte[1024];
			        
			        System.out.println("Fetching GQ stats...");
			        
			        while(true){
			          while(in.available()>0){
			            int i=in.read(tmp, 0, 1024);
			            if(i<0)break;
			            System.out.print(new String(tmp, 0, i));
			            textMessage.setText(new String(tmp, 0, i));
			          }
			          if(channel.isClosed()){
			        	System.out.println("channel closed");
			            break;			            
			          }
			          try{
			        	  Thread.sleep(1000);
			        	  }
			          catch(Exception ee)
			          {
			        	  System.out.println("can't sleep");
			          }
			        }
			        
			        channel.disconnect();
			        System.out.println("channel disconnected");
			        
			        session.disconnect();
			        System.out.println("session disconnected");
			    }
			    catch(Exception e1){
			    	e1.printStackTrace();
			    } 
			}
		});
		btnGqStats.setBounds(130, 185, 120, 33);
		btnGqStats.setText("Message Stats");
				
	}

	protected void loadProperties(){
		try {
			prop = new Properties();
			//System.out.println("prop object");
			prop.load(new FileInputStream("prop_file.properties"));
			System.out.println("Properties loaded");
		}
		catch(Exception e){
			textMessage.setText("" + e);
		}		
	}
	
	
		
		
}
	

