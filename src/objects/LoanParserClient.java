package objects;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import counting.CountingClient;


public class LoanParserClient extends JFrame
{
	public JLabel l1;
	public static JLabel l2;
	public static JLabel l3;
	public static JLabel l4;
	public static JLabel l5;
	public static JTextField t1;
	public static JTextField t2;
	public static JTextField t3;
	public static JButton b;
	public double loanAmount;
	public double loanRate;
	public int loanPeriod;
	
	public LoanParserClient()
	{
		super("Loan Window");
		this.setLayout(new GridLayout(7,1));
		l1 = new JLabel("Enter the Loan Information below:");
		
		t1 = new JTextField(6);
		t1.setBorder(new TitledBorder("Loan Amount"));
		
		t2 = new JTextField(6);
		t2.setBorder(new TitledBorder("Loan Period (years)"));
		
		t3 = new JTextField(6);
		t3.setBorder(new TitledBorder("Annual Interest Rate"));
		
		l2 = new JLabel("");
		l3 = new JLabel("");
		
		l4 = new JLabel("");
		l4.setBorder(new TitledBorder("Monthly Payment"));
		
		l5 = new JLabel("");
		l5.setBorder(new TitledBorder("Total Payment"));
		
		b = new JButton("Calculate");
		b.addActionListener(new ButtonListener());
		this.add(l1);
		this.add(t1);
		this.add(t2);
		this.add(t3);
		this.add(l4);
		this.add(l5);
		this.add(b);
		this.add(l2);
		this.add(l3);
	}
	
	public static void main(String[] args)
	{
		LoanParserClient a = new LoanParserClient();
		a.setSize(400,400);
		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		a.setVisible(true);
	}
	
	public double getMonthlyPayment(double principal, double rate, int time) 
	  {
	      DecimalFormat money = new DecimalFormat(".##");
	    double monthlyInterestRate = rate / 12;
	    double monthlyPayment = principal * monthlyInterestRate / (1 -
	      (1 / Math.pow(1 + monthlyInterestRate, time * 12)));
	    return Double.valueOf(money.format(monthlyPayment));
	  }
	
	public double getTotalPayment(double principal, double rate, int time)
	{
		DecimalFormat money = new DecimalFormat(".##");
		double ans = getMonthlyPayment(principal, rate, time) * time * 12;
		return Double.valueOf(money.format(ans));
	}
	
	class ButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			Socket sock = null;
			try 
			{
				l1.setText("Connecting to server...");
				sock = new Socket("localhost", 253);
				DataOutputStream send = new DataOutputStream(sock.getOutputStream());
				DataInputStream receive = new DataInputStream(sock.getInputStream());
				
				loanAmount = Double.valueOf(t1.getText());
				loanRate = Double.valueOf(t2.getText());
				loanPeriod = Integer.valueOf(t3.getText());
				
				send.writeDouble(loanAmount);
				send.writeDouble(loanRate);
				send.writeInt(loanPeriod);
				
				l1.setText("Press the button to calculate another loan.");
				l4.setText("$" +receive.readDouble());
				l5.setText("$" +receive.readDouble());
				
			} 
			catch (NumberFormatException nfe)
			{
				System.out.println("Error: " +nfe.getMessage());
				System.out.println("Please chack all values to make sure they are properly formatted.");
				
				try 
				{
					sock.close();
				} 
				catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			catch (UnknownHostException uhe) 
			{
				uhe.printStackTrace();
				System.exit(-1);
			} 
			catch (IOException ioe) 
			{
				ioe.printStackTrace();
				System.exit(-1);
			}
		}			
	}

}
