/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;
import java.io.*;

/**
 *
 * @author kireh
 */
public class LoanArrayGenerator 
{
        public static void main(String[] args)
        {
            LoanArrayGenerator test = new LoanArrayGenerator();
            test.LoanWriter();
            System.out.println("\n" +(test.LoanReader()));
        }
        
        /**
         * Creates an array of five Loan objects and writes them to a file
         */
        public void LoanWriter()
        {
             Loan[] printables = new Loan[5];
            for(int i=0; i<printables.length; i++)
            {
                double rate = Math.random()*10;
                int years = (int)(Math.random()*5)+1;
                double amount = Math.random()*1000;
                printables[i]=new Loan(rate, years, amount);
            }
            try
            {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("target.dat"));
                ObjectInputStream in = new ObjectInputStream(new FileInputStream("target.dat"));
                out.writeObject(printables);
                Loan[] m = (Loan[])in.readObject();
                for( Loan l : m)
                {
                     System.out.println("\n" +l.toString());
                }
            }
            catch (IOException io)
            {
                System.out.println(io.getMessage());
            } catch (ClassNotFoundException cnf) {
                System.out.println(cnf.getMessage());
            }
            finally
            {
                System.out.println("Loan writing completed.");
                //System.out.println("" +test.LoanReader("target.dat"));
            }
        }
        
        /**
         * Reads Loan arrays from a file and calculates the total loan principal amount
         * @return Loan principal amount as a double
         */
        public double LoanReader()
        {
            double totalAmount=0.0;
            try
            {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream("target.dat"));
                while (true)
                {
                    Loan[] objs = (Loan[])in.readObject();
                    for (Loan l : objs)
                    {
                        totalAmount+=l.getLoanAmount();
                    }
                }
               
            }
            catch (ClassNotFoundException cnf)
            {
                System.out.println("Error: " +cnf.getMessage());
            }
            catch (EOFException eof)
            {
                System.out.println("Total loan amount: " +totalAmount);
            }
            catch (IOException io)
            {
                System.out.println("Error: " +io.getMessage());
            }
            catch (Exception ex)
            {
                System.out.println("Error: " +ex.getMessage());
            }
            finally
            {
                System.out.println("\nLoan reading completed.");
                return (totalAmount);
            }
        }
}
