package objects;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

public class LoanServer 
{
		
	public static void main(String[] args)
	{
		
		try 
		{
			ServerSocket service = new ServerSocket(253);//creates a new TCP socket connection at the specified port number
			Socket client;
			while (true)
			{
				Socket clients= service.accept(); //until a client connects, the server will  listen for a connection
				 
				 Thread t = new Thread(new AverageTask(clients));
				 t.start();
				 //System.out.println("Connected to " +client.getInetAddress());
				 //DataInputStream in = new DataInputStream(client.getInputStream());
				 //DataOutputStream send = new DataOutputStream(client.getOutputStream());//buffering on a network is important for performance as well as power efficiency 
				 //System.out.println(in.readUTF());
				 //x=in.readInt();
				 //y=in.readInt();
				 //send.writeUTF("averaging " +x +", " +y);
				 //send.writeInt((int)((x+y)/2));
			}
		} 
			catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class AverageTask implements Runnable
{	
	private Socket client;
	double principal, rate;
	int time;
	
	public AverageTask(Socket s)
	{
		this.client=s;
	}
	
	@Override
	public void run() 
	{
		try
		{
			System.out.println("Connected to " +client.getInetAddress());
			DataInputStream in = new DataInputStream(client.getInputStream());
			DataOutputStream send = new DataOutputStream(client.getOutputStream());//buffering on a network is important for performance as well as power efficiency 
			//System.out.println(in.readUTF());
			
			principal=in.readDouble();
			rate=in.readDouble();
			time = in.readInt();
			
			send.writeDouble(getMonthlyPayment(principal, rate, time));
			send.writeDouble(getTotalPayment(principal, rate, time));
			//in.close();
			//send.close();
			//client.close();
		}
		catch(IOException ioe)
		{
			System.exit(-1);
		}
	}
	
	public double getMonthlyPayment(double principal, double rate, int time) 
	  {
	      DecimalFormat money = new DecimalFormat(".##");
	    double monthlyInterestRate = rate / 12;
	    double monthlyPayment = principal * monthlyInterestRate / (1 -
	      (1 / Math.pow(1 + monthlyInterestRate, time * 1200)));
	    return Double.valueOf(money.format(monthlyPayment));
	  }
	
	public double getTotalPayment(double principal, double rate, int time)
	{
		DecimalFormat money = new DecimalFormat(".##");
		double ans = getMonthlyPayment(principal, rate, time) * time * 12;
		return Double.valueOf(money.format(ans));
	}
	
	
}
