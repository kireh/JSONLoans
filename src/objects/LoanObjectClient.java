package objects;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


public class LoanObjectClient extends JFrame
{
	public JLabel instructions;
	public JLabel errorLabel;
	public static JLabel l3;
	public static JLabel monthly;
	public static JLabel l5;
	public JTextField principalField;
	public JTextField timeField;
	public JTextField rateField;
	public static JButton b;
	public double loanAmount;
	public double loanRate;
	public int loanPeriod;
	
	public LoanObjectClient()
	{
		super("Loan Object Client");
		this.setLayout(new GridLayout(7,1));
		instructions = new JLabel("Enter the Loan Information below:");
		
		principalField = new JTextField(6);
		principalField.setBorder(new TitledBorder("Loan Amount"));
		
		timeField = new JTextField(6);
		timeField.setBorder(new TitledBorder("Loan Period (years)"));
		
		rateField = new JTextField(6);
		rateField.setBorder(new TitledBorder("Annual Interest Rate"));
		
		errorLabel = new JLabel("");
		l3 = new JLabel("");
		
		monthly = new JLabel("");
		monthly.setBorder(new TitledBorder("Monthly Payment"));
		
		l5 = new JLabel("");
		l5.setBorder(new TitledBorder("Total Payment"));
		
		b = new JButton("Calculate");
		//b.addActionListener(new ButtonListener());
		this.add(instructions);
		this.add(principalField);
		this.add(timeField);
		this.add(rateField);
		this.add(monthly);
		this.add(l5);
		this.add(b);
		this.add(errorLabel);
		this.add(l3);
	}
	
	public static void main(String[] args)
	{
		LoanObjectClient a = new LoanObjectClient();
		a.setSize(400,400);
		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		a.setVisible(true);
	}
		
	class ButtonListener implements ActionListener
	{

		@Override
	    public void actionPerformed(ActionEvent e) 
		{
			Socket sock = null;
			try 
			{
				instructions.setText("Connecting to server...");
				sock = new Socket("localhost", 2563);
				ObjectOutputStream send = new ObjectOutputStream(sock.getOutputStream());
				ObjectInputStream receive = new ObjectInputStream(sock.getInputStream());
				
				//The code below executes once the socket has been connected and properly attached
				instructions.setText("Sending information to server...");
				JSONLoanWriter loanWriter = new JSONLoanWriter(send);
				loanWriter.writeLoan();
				
				JSONLoanReader loanReader = new JSONLoanReader(receive);
				Loan result = loanReader.readLoan();
				
				instructions.setText("Press the button to calculate another loan.");
				monthly.setText("$" +result.getMonthlyPay());//monthly payment
				l5.setText("$" +result.getTotalPay());//totalPayment
				
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
	
	/**
	 * This class is responsible for passing the Loan object from 
	 * the Client to the server and uses the OBJECT(WRITER) model
	 * @author kireh
	 *
	 */
	class JSONLoanWriter 
	{
		JsonObjectBuilder loan; //handles the object building
		JsonWriter writer; //writes the object once it is built
		OutputStream out; //output stream to which the JSON object is written
		double principal; //holder variable for the loan principal
		double rate; //holder for the loan percentage rate variable
		double time; //holder for the loan period variable
		
		/**
		 * Creates a JSONLoanWriter instance that creates 
		 * a Loan from the information input to the GUI
		 * @param stream The ObjectOutputStream to which 
		 *        the created Loan object will be written
		 */
		public JSONLoanWriter(ObjectOutputStream stream)
		{
			out = stream;
			Loan source = createLoan();
			loan =Json.createObjectBuilder();
			
			loan.add("Principal", "" +source.getLoanAmount())
				.add("Rate", "" +source.getAnnualInterestRate())
				.add("Time", "" +source.getNumberOfYears());
		}

		/**
		 * This class is responsible for creating the Loan object that is represented in the JSON message
		 * @return Loan object to be sent to the server
		 */
		public Loan createLoan()
		{
			/*
			 * This try block attempts to read numbers from each of the text fields and
			 * gives an appropriate error if a number cannot be read from a field 
			 */
			try
			{
				principal = Double.valueOf(principalField.getText());
				time = Double.valueOf(timeField.getText());
				rate = Double.valueOf(rateField.getText());
			}
			//This catch block gives the error and makes it red for visibility
			catch (NumberFormatException nfe)
			{
				errorLabel.setForeground(Color.RED);
				errorLabel.setText("Number Format Error: Check all values to make sure they are formatted properly");
			}
			Loan out = new Loan(rate, time, principal);
			return out;
		}
		
		public void writeLoan()
		{
			JsonObject loanBuild = loan.build();
			writer = Json.createWriter(out);
			
			writer.writeObject(loanBuild);
			writer.close();
		}
	}
	
	class JSONLoanReader
	{
		private ObjectInputStream in;
		private JsonReader loanReader;
		
		public JSONLoanReader(ObjectInputStream inStream)
		{
			in = inStream;
			loanReader = Json.createReader(inStream);
			
		}
		
		public Loan readLoan()
		{
			JsonObject obj = loanReader.readObject();
			
			Loan  ans = new Loan();
			
			Double principal = Double.valueOf(obj.getString("Principal"));
			Double time = Double.valueOf(obj.getString("Time"));
			Double rate = Double.valueOf(obj.getString("Rate"));
			Double monthly = Double.valueOf(obj.getString("Monthly"));
			Double total = Double.valueOf(obj.getString("Total"));
			
			ans.setAnnualInterestRate(rate);
			ans.setLoanAmount(principal);
			ans.setNumberOfYears(time);
			
			return ans;
			
		}
	}

}
	