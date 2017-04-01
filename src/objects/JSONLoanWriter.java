package objects;

import org.json.*;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.json.*;


public class JSONLoanWriter 
{
	JsonObjectBuilder loan;
	JsonWriter writer;
	OutputStream out;
	
	public JSONLoanWriter(ObjectOutputStream stream)
	{
		Loan source = createLoan(principal, rate, time);
		loan =Json.createObjectBuilder();
		
		loan.add("Principal", source.getLoanAmount())
			.add("Rate", source.getAnnualInterestRate())
			.add("Time", source.getNumberOfYears());
		
		JsonObject loanBuild = loan.build();
		
		writer = Json.createWriter(stream);
		
		writer.writeObject(loanBuild);
		writer.close();
	}

	public static Loan createLoan(double rate, int time, double amount)
	{
		Loan out = new Loan(rate, time, amount);
		return out;
	}
}
