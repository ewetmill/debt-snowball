package finance.alg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Based on formulas from http://hughcalc.org/formula.php
 *
 */
public class Loan
{
   private BigDecimal thePresentValue;
   private BigDecimal theApr;
   private BigDecimal theMonthlyPayment;

   public Loan(BigDecimal presentValue, BigDecimal apr, BigDecimal monthlyPayment)
   {
      thePresentValue = presentValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
      theApr = apr.setScale(6, BigDecimal.ROUND_HALF_DOWN);
      theMonthlyPayment = monthlyPayment.setScale(2, BigDecimal.ROUND_HALF_UP);
   }

   public BigDecimal getBalance()
   {
      return thePresentValue;
   }
   
   public BigDecimal getApr()
   {
      return theApr;
   }
   
   public BigDecimal getPayment()
   {
      return theMonthlyPayment;
   }
   
   public List<Payment> amortizationSchedule()
   {
	   List<Payment> schedule = new ArrayList<>();

	   BigDecimal balance = thePresentValue;
	   BigDecimal monthlyInterestRate = theApr.divide(new BigDecimal(12), 12, BigDecimal.ROUND_HALF_DOWN);

	   while (balance.compareTo(BigDecimal.ZERO) < 0)
	   {
	      BigDecimal monthlyInterest = balance.multiply(monthlyInterestRate).setScale(2, BigDecimal.ROUND_HALF_UP);
	      BigDecimal monthlyPrinciple = theMonthlyPayment.subtract(monthlyInterest);
	      if (balance.abs().compareTo(monthlyPrinciple) < 0)
	      {
	    	  monthlyPrinciple = balance.abs();
	      }
	      schedule.add(new Payment(monthlyPrinciple, monthlyInterest));
	      BigDecimal nextBalance = balance.add(monthlyPrinciple);

	      if (nextBalance.compareTo(BigDecimal.ZERO) < 0)
	      {
	    	  balance = nextBalance;
	      }
	      else
	      {
	    	  balance = BigDecimal.ZERO;
	      }
	   }

	   return schedule;
   }

   /**
    * n = - (LN(1-(B/m)*(r/q)))/LN(1+(r/q))
    * 
    * 
    * q = amount of annual payment periods
    * r = interest rate
    * B = principal
    * m = payment amount
    * n = amount payment periods
    * LN = natural logarithm 
    *
    * @return
    */
   public BigDecimal numberPayments()
   {
      BigDecimal monthlyInterestRate = theApr.divide(new BigDecimal(12), 12, BigDecimal.ROUND_HALF_DOWN);
      BigDecimal pvMonth = thePresentValue.divide(theMonthlyPayment, 2, BigDecimal.ROUND_HALF_DOWN);
      double part1 = Math.log(1.0 -(pvMonth.doubleValue()) * monthlyInterestRate.doubleValue());
      double part2 = Math.log(1.0 + monthlyInterestRate.doubleValue());
      
      return new BigDecimal(part1).divide(new BigDecimal(part2), 2, BigDecimal.ROUND_HALF_UP);
   }

   /**
	*  P = P*(1 -((1 + J)**t - 1)/((1 + J)**N - 1))
   *
   * where:
   *
   * P = principal, the initial amount of the loan
   * I = the annual interest rate (from 1 to 100 percent)
   * L = length, the length (in years) of the loan, or at least the length over which the loan is amortized.
   * J = monthly interest in decimal form = I / (12 x 100)
   * N = number of months over which loan is amortized = L x 12
   * t=number of paid monthly loan payments 
   *
	* @param presentValue
	* @param annualInterestRate
	* @param paymentsInMonths
	* @return
	*/
   public BigDecimal futureValue(Integer paymentsInMonths)
   {
      List<Payment> schedule = amortizationSchedule();
      
      BigDecimal totalPayed = BigDecimal.ZERO;
      for (int paymentNumber = 0; paymentNumber < paymentsInMonths; paymentNumber++)
      {
         Payment payment = schedule.get(paymentNumber);
         totalPayed = totalPayed.add(payment.getPrinciple());
      }
      BigDecimal fv = getBalance().add(totalPayed);
      return fv.setScale(2, BigDecimal.ROUND_HALF_DOWN);
   }

   public String toString()
   {
	   StringBuilder builder = new StringBuilder();
	   
	   builder.append("Principle:").append(thePresentValue).append('\n');
	   builder.append("Interest Rate:").append(theApr).append('\n');
	   builder.append("Payment:").append(theMonthlyPayment).append('\n');
	   for (Payment payment : amortizationSchedule())
	   {
		   builder.append(payment).append('\n');
	   }
	   
	   return builder.toString();
   }
   
   public class Payment
   {
	   private BigDecimal thePrinciple;
	   private BigDecimal theInterest;

	   public Payment(BigDecimal principle, BigDecimal interest)
	   {
		   thePrinciple = principle;
		   theInterest = interest;
	   }
	   
	   public BigDecimal getPrinciple()
	   {
	      return thePrinciple;
	   }
	   
	   public BigDecimal getInterest()
	   {
	      return theInterest;
	   }
	   
	   public String toString()
	   {
		   return "Principle:" + thePrinciple + " Interest:" + theInterest;
	   }
   }

   public static void main(String args[])
   {
	  Loan loan = new Loan(new BigDecimal(-15226.29), new BigDecimal(-0.035), new BigDecimal(102.87+459.21));
      System.out.println("Loan: " + loan);
      int payments = loan.amortizationSchedule().size();
      BigDecimal numPayments = loan.numberPayments();
      
      System.out.println("Count Payments:" + payments + " Calc Payments:" + numPayments.setScale(0, BigDecimal.ROUND_CEILING));
      
      System.out.println("Future Value after 5 payments:" + loan.futureValue(5));
   }
}
