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

   public List<Payment> amortizationSchedule()
   {
	   List<Payment> schedule = new ArrayList<>();

	   BigDecimal balance = thePresentValue;
	   BigDecimal monthlyInterestRate = theApr.divide(new BigDecimal(12), 12, BigDecimal.ROUND_HALF_DOWN);

	   while (balance.compareTo(BigDecimal.ZERO) < 0)
	   {
	      BigDecimal monthlyInterest = balance.multiply(monthlyInterestRate);
	      BigDecimal monthlyPrinciple = theMonthlyPayment.subtract(monthlyInterest);
	      if (balance.abs().compareTo(monthlyPrinciple) < 0)
	      {
	    	  monthlyPrinciple = balance.abs();
	      }
	      schedule.add(new Payment(monthlyPrinciple.setScale(2, BigDecimal.ROUND_HALF_DOWN), monthlyInterest.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
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
	*  n = - (LN(1-(B/m)*(r/q)))/LN(1+(r/q))
    *  # years = - 1/q * (LN(1-(B/m)*(r/q)))/LN(1+(r/q))
    *
    * Where:
    *
    * q = amount of annual payment periods
    * r = interest rate
    * B = principal
    * m = payment amount
    * n = amount payment periods
    * LN = natural logarithm 
    *
	* @param presentValue
	* @param annualInterestRate
	* @param paymentsInMonths
	* @return
	*/
   public BigDecimal futureValue(Integer paymentsInMonths)
   {
      BigDecimal monthlyInterestRate = theApr.divide(new BigDecimal(12), 12, BigDecimal.ROUND_HALF_DOWN);
      BigDecimal fv = thePresentValue.multiply(monthlyInterestRate.add(new BigDecimal(1)).pow(paymentsInMonths));

      System.out.println("Annual Rate: " + theApr);
      System.out.println("Monthly Rate: " + monthlyInterestRate);
      System.out.println("Future Value: " + fv);

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
   }
}
