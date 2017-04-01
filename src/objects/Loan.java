/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;
import java.io.*;
import java.text.DecimalFormat;
/**
 * Supplied by E. Sabbah 
 */
public class Loan implements Serializable 
{
  private double annualInterestRate;
  private double numberOfYears;
  private double loanAmount;
  private java.util.Date loanDate;
  private double monthlyPayment;
  private double totalPayment;

  /** Default constructor */
  public Loan() 
  {
    this(2.5, 1, 1000);
  }

  /** Construct a loan with specified annual interest rate,
      number of years, and loan amount
    */
  public Loan(double annualInterestRate, double numberOfYears,
      double loanAmount) 
  {
    this.annualInterestRate = annualInterestRate;
    this.numberOfYears = numberOfYears;
    this.loanAmount = loanAmount;
    loanDate = new java.util.Date();
  }

  public void setMonthlyPay(double m)
  {
	  this.monthlyPayment=m;
  }
  
  public void setTotalPay(double t)
  {
	  this.totalPayment = t;
  }
  
  
  /** Return annualInterestRate */
  public double getAnnualInterestRate() 
  {
      DecimalFormat rate = new DecimalFormat(".###");
    return Double.valueOf(rate.format(annualInterestRate));
  }

  /**
   * Set a new annualInterestRate
   * @param annualInterestRate new rate for the loan
   */
  public void setAnnualInterestRate(double annualInterestRate) 
  {
    this.annualInterestRate = annualInterestRate;
  }

  /** Return numberOfYears */
  public double getNumberOfYears() 
  {
    return numberOfYears;
  }

  /** Set a new numberOfYears */
  public void setNumberOfYears(double numberOfYears) 
  {
	  if (numberOfYears>0)
	  {
		  this.numberOfYears = numberOfYears;
	  }
	  else
	  {
		  this.numberOfYears=3;
	  }
    
  }

  /** Return loanAmount */
  public double getLoanAmount() 
  {
      DecimalFormat money = new DecimalFormat(".##");
    return Double.valueOf(money.format(loanAmount));
  }

  /** Set a newloanAmount */
  public void setLoanAmount(double loanAmount) 
  {
    this.loanAmount = loanAmount;
  }
  
  public double getMonthlyPay()
  {
	  return this.monthlyPayment;
  }
  
  public double getTotalPay()
  {
	  return this.totalPayment;
  }

  /**
   * Calculate the monthly payment of a Loan object
   * @return Monthly payment amount (in dollars) as a double
   */
  public double getMonthlyPayment() 
  {
      DecimalFormat money = new DecimalFormat(".##");
    double monthlyInterestRate = annualInterestRate / 1200;
    double monthlyPayment = loanAmount * monthlyInterestRate / (1 -(1 / Math.pow(1 + monthlyInterestRate, numberOfYears * 12)));
    return Double.valueOf(money.format(monthlyPayment));
//    return monthlyPayment;
  }
  
  public double getTotalPayment()
  {
	  DecimalFormat money = new DecimalFormat(".##");
	  return getMonthlyPayment()*12*this.numberOfYears;
  }
  
  /**
   * Generates a String representation of a Loan object
   * @return Loan object as a String
   */
  public String toString()
          {
              return ("Loan Date: " +this.loanDate
                      +"\n Loan Amount: $" +this.getLoanAmount()
                      +"\n Annual Interest Rate: " +this.getAnnualInterestRate() +"%"
                      +"\n Loan Payment Period: " +this.getNumberOfYears() +" years"
                      +"\n Monthly Payment Amount: $" +this.getMonthlyPayment());
          }
}