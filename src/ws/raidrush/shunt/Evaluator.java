package ws.raidrush.shunt;

import java.util.Stack;

public class Evaluator
{
  public void evaluate(short op, Stack<Double> stack, Stack<Double> heap) throws RuntimeError
  {
    switch (op) {
      case Opcode.OP_PUSH: {
        double rhs = (Double) stack.peek();
        heap.push(rhs);
        break;
      }
      
      case Opcode.OP_ADD:
      case Opcode.OP_SUB:
      case Opcode.OP_MUL:
      case Opcode.OP_DIV:
      case Opcode.OP_POW:
      case Opcode.OP_MOD: {
        double lhs = (Double) stack.pop(),
               rhs = (Double) stack.pop();
        
        switch (op) {
          case Opcode.OP_ADD:
            stack.push(lhs + rhs);
            break;
            
          case Opcode.OP_SUB:
            stack.push(lhs - rhs);
            break;
            
          case Opcode.OP_MUL:
            stack.push(lhs * rhs);
            break;
            
          case Opcode.OP_DIV:
            if (rhs == 0.) 
              throw new RuntimeError("division durch 0");
              
            stack.push(lhs / rhs);
            break;
            
          case Opcode.OP_POW:
            stack.push(Math.pow(lhs, rhs));
            break;
            
          case Opcode.OP_MOD:
            if (rhs == 0.)
              throw new RuntimeError("rest-division durch 0");
            
            stack.push(lhs % rhs);
            break;
        }
        
        break;
      }
    }
  }
}
