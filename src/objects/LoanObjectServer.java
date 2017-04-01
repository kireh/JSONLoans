package objects;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LoanObjectServer extends JFrame
{
	private JTextArea output;
	private JScrollPane scroll;
	private Date d;
	private Calendar c;
	
	public LoanObjectServer()
	{
		super("Loan Object Server");
		output = new JTextArea(400, 600);
		scroll = new JScrollPane(output);
		scroll.setAutoscrolls(true);
		this.add(scroll);
	}
	
	public void output(String s)
	{
		c = Calendar.getInstance();
		this.output.setText(output.getText() 
				+"\n\n" +c.getTime() +s);
	}
	
	public static void main(String[] args)
	{
		LoanObjectServer server = new LoanObjectServer();
		server.setSize(400,600);
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.setVisible(true);
		
		try 
		{
			ServerSocket service = new ServerSocket(2563);//creates a new TCP socket connection at the specified port number
			Socket client;
			while (true)
			{
				client = service.accept(); //until a client connects, the server will  listen for a connection
				 
				 Thread t = new Thread(new CalculateTask(server, client));
				 t.start();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

class CalculateTask implements Runnable
{	
	private Socket client;
	double principal, rate, time;
	LoanObjectServer server;
	
	
	public CalculateTask(LoanObjectServer los, Socket s)
	{
		this.client=s;
	}
	
	@Override
	public void run() 
	{
		try
		{
			server.output("Connected to " +client.getInetAddress());
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			DataOutputStream send = new DataOutputStream(client.getOutputStream());//buffering on a network is important for performance as well as power efficiency 
			
			JSONLoanReader loanReader = new JSONLoanReader(in);
			JsonObject obj =  loanReader.
			
			principal=obj.getLoanAmount();
			rate=obj.getAnnualInterestRate();
			time = obj.getNumberOfYears();
			
			send.writeDouble(getMonthlyPayment(principal, rate, time));
			send.writeDouble(getTotalPayment(principal, rate, time));
			//in.close();
			//send.close();
			//client.close();
		}
		catch(IOException ioe)
		{
			System.exit(-1);
		} catch (ClassNotFoundException cfe) {
			// TODO Auto-generated catch block
			cfe.printStackTrace();
		}
	}
	
	public double getMonthlyPayment(double principal, double rate, double time2) 
	  {
	      DecimalFormat money = new DecimalFormat(".##");
	    double monthlyInterestRate = rate / 12;
	    double monthlyPayment = principal * monthlyInterestRate / (1 -
	      (1 / Math.pow(1 + monthlyInterestRate, time2 * 1200)));
	    return Double.valueOf(money.format(monthlyPayment));
	  }
	
	public double getTotalPayment(double principal, double rate, double time)
	{
		DecimalFormat money = new DecimalFormat(".##");
		double ans = getMonthlyPayment(principal, rate, time) * time * 12;
		return Double.valueOf(money.format(ans));
	}
	
	/**
	 * This class will encapsulate the Loan writer that sends the 
	 * Loan information from server to client in a JSON object format
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
				.add("Time", "" +source.getNumberOfYears())
				.add("Monthly", source.getMonthlyPayment())
				.add("Total", source.getTotalPayment());
				
		}

		/**
		 * This class is responsible for creating the Loan object that is represented in the JSON message
		 * @return Loan object to be sent to the server
		 */
		public Loan createLoan()
		{
			return new Loan();
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
		
		/**
		 * Read in a Loan from the 
		 * @return
		 */
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
			ans.setMonthlyPay(ans.getMonthlyPayment());
			ans.setTotalPay(ans.getTotalPayment());
			
			return ans;
			
		}
	}
	
}
