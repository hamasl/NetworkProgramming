import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Equation implements Serializable {
   private final double NUM1;
   private final double NUM2;
   private final char OPERAND;
   private Double answer;

   public Equation(double num1, double num2, char operand) {
      this.NUM1 = num1;
      this.NUM2 = num2;
      this.OPERAND = operand;
   }

   public double getNum1() {
      return NUM1;

   }

   public double getNum2() {
      return NUM2;
   }

   public double getAnswer() {
      return answer;
   }

   public boolean updateAnswer() {
      if (OPERAND == '+') {
         answer = NUM1 + NUM2;
         return true;
      } else if (OPERAND == '-') {
         answer = NUM1 - NUM2;
         return true;
      }
      return false;
   }

   public char getOperand() {
      return OPERAND;
   }

   public boolean isValidOperand() {
      if (OPERAND != '+' || OPERAND != '-')
         return false;
      return true;
   }

   public static Equation deSerialize(byte[] obj) {
      ByteArrayInputStream byteStream = new ByteArrayInputStream(obj);
      try {
         ObjectInputStream objectStream = new ObjectInputStream(byteStream);
         Object o = objectStream.readObject();
         if (o instanceof Equation)
            return (Equation) o;
      } catch (IOException | ClassNotFoundException e) {
         return null;
      }

      return null;
   }

   public byte[] serialize(int bufferSize) {
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream(bufferSize);
      try {
         ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
         objectStream.writeObject(this);
         objectStream.flush();
         objectStream.close();
      } catch (IOException e) {
         return new byte[0];
      }
      byte[] obj = byteStream.toByteArray();
      return obj;
   }

   @Override
   public String toString() {
      return NUM1 + " " + OPERAND + " " + NUM2 + " = "
            + ((answer == null) ? "unknown\nOperand may be incorrect (either + or -)" : answer);
   }

}