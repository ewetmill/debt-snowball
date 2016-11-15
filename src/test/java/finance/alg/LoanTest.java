package finance.alg;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import finance.alg.Loan.Payment;

public class LoanTest
{
   Loan loan = new Loan(new BigDecimal(-15226.29), new BigDecimal(-0.035), new BigDecimal(102.87+459.21));

   @Test
   public void testNumberOfPayments()
   {
      int payments = loan.amortizationSchedule().size();
      BigDecimal numPayments = loan.numberPayments();
      assertEquals("Number of payments don't match",
            new BigDecimal(payments), numPayments.setScale(0, BigDecimal.ROUND_CEILING));
   }

   @Test
   public void testBalance()
   {
      BigDecimal total = BigDecimal.ZERO;
      for (Payment payment : loan.amortizationSchedule())
      {
         total = total.subtract(payment.getPrinciple());
      }
      
      assertEquals("Balance", total, loan.getBalance());
   }
   
   @Test
   public void testFutureValue()
   {
      int payments = 5;
      BigDecimal futureValueCalculated = loan.futureValue(payments);
      
      List<Payment> schedule = loan.amortizationSchedule();
      BigDecimal totalPayed = BigDecimal.ZERO;
      for (int payment = 0; payment < payments; payment++)
      {
         Payment pmnt = schedule.get(payment);
         totalPayed = totalPayed.add(pmnt.getPrinciple());
      }
      
      BigDecimal futureValueFromSchedule = loan.getBalance().add(totalPayed);
      assertEquals("Future Value", futureValueFromSchedule, futureValueCalculated);
   }
}
