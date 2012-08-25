/*!
 * Java Shunting-yard Implementierung
 * Copyright 2012 - droptable <murdoc@raidrush.org>
 *
 * Referenz: <http://en.wikipedia.org/wiki/Shunting-yard_algorithm>
 *
 * ---------------------------------------------------------------- 
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * <http://opensource.org/licenses/mit-license.php>
 */

package ws.raidrush.shunt;

import java.util.Stack;
import java.util.Vector;

public class Parser 
{
	protected Scanner scanner;
	protected Stack<TokenStack> queue, stack;		
	
	public Parser(Scanner s) throws ParseError
	{
		Token t;
		
		scanner = s;
		
		// alloc
		queue = new Stack<TokenStack>();
		stack = new Stack<TokenStack>();
		
		queue.add(new TokenStack());
		stack.add(new TokenStack());
		
		int idx = 0, tln = 0;
		
		// queue erzeugen
		while ((t = scanner.next()) != null) {
		  if (t.type == Token.T_SEMI && tln > 0) {
		    // When there are no more tokens to read:
		    // While there are still operator tokens in the stack:
		    while ((t = stack.get(idx).pop()) != null) {
		      if (t.type == Token.T_POPEN || t.type == Token.T_PCLOSE)
		        throw new ParseError("fehlerhafte verschachtelung von `(` und `)`");
		      
		      queue.get(idx).push(t);
		    }
		    
		    ++idx;
        tln = 0;
        
        queue.add(new TokenStack());
        stack.add(new TokenStack());
        
        continue;
		  }
		  
		  ++tln;
			handle(t, idx);
		}
		
		// When there are no more tokens to read:
    // While there are still operator tokens in the stack:
    while ((t = stack.get(idx).pop()) != null) {
      if (t.type == Token.T_POPEN || t.type == Token.T_PCLOSE)
        throw new ParseError("fehlerhafte verschachtelung von `(` und `)`");
      
      queue.get(idx).push(t);
    }
    
    // clear stack
    stack.clear();
	}
	
	public String opcode() throws RuntimeError
	{
	  StringBuilder opcode  = new StringBuilder();
	  int sidx = 0;
	  
		for (TokenStack ts : queue) {
		  if (ts.size() == 0)
		    continue;
		  
		  opcode.append(":segment").append(sidx++).append("\n");
		  
	    Token t;
	    Vector<String> stack = new Vector<String>();
		  int opn = 0, idx = 0, len = 0;
		  
		  while ((t = ts.shift()) != null) {
  		  opcode.append(idx).append(": ");
  		  
        switch (t.type) {
          case Token.T_NUMBER:
          case Token.T_IDENT:
          case Token.T_RIDENT:
          case Token.T_STRING:
            // If the token is a value or identifier
            // Push it onto the stack.
            if (t.type == Token.T_IDENT) {
              opcode.append("%").append(opn).append(" = FETCH_ID \"").append(((Ident) t).value).append("\"\n");
              stack.add("%" + opn);
              ++opn;
            } else if (t.type == Token.T_STRING) {
              String value = ((Ident) t ).value;
              opcode.append("%").append(opn).append(" = STR ").append(value.length()).append(", ").append(quote(value)).append("\n");
              stack.add("%" + opn);
              ++opn;
            } else if (t.type == Token.T_RIDENT) {
              opcode.append("%").append(opn).append(" = FETCH_RD \"").append(((Ident) t).value).append("\"\n");
              stack.add("%" + opn);
              ++opn;
            } else {
              opcode.append("NOOP\n");
              stack.add("" + ((Number) t).value);
            }
              
            ++len;
            break;
            
          case Token.T_PLUS:
          case Token.T_MINUS:
          case Token.T_UNARY_PLUS:
          case Token.T_UNARY_MINUS:
          case Token.T_TIMES:
          case Token.T_DIV:
          case Token.T_MOD:
          case Token.T_POW:
          case Token.T_NOT: 
          case Token.T_ASSIGN: {
            opcode.append("%").append(opn).append(" = ");
            
            // It is known a priori that the operator takes n arguments.
            int na = this.argc(t);
            
            // If there are fewer than n values on the stack
            if (len < na)
              throw new RuntimeError("zu wenig token für operator `" + ((Operator) t).value + "`");
            
            String rhs = stack.lastElement();
            String lhs = null;
            
            stack.removeElementAt(stack.size() - 1);
                            
            if (na > 1) {
              lhs = stack.lastElement();
              stack.removeElementAt(stack.size() - 1);
            }
                             
            len -= (na - 1);
            
            // Push the returned results, if any, back onto the stack.
            //this.stack.push(new Number(this.op(t.type, lhs, rhs), Token.T_NUMBER));
            
            String opc = "";
            
            switch (t.type) {
              case Token.T_ASSIGN:
                opc = "SET";
                break;
                
              case Token.T_PLUS:
                opc = "ADD";
                break;
                
              case Token.T_MINUS:
                opc = "SUB";
                break;
                
              case Token.T_UNARY_PLUS:
                opc = "UNARY_PLUS";
                break;
                
              case Token.T_UNARY_MINUS:
                opc = "UNARY_MINUS";
                break;
                
              case Token.T_TIMES:
                opc = "MUL";
                break;
                
              case Token.T_DIV:
                opc = "DIV";
                break;
                
              case Token.T_MOD:
                opc = "MOD";
                break;
                
              case Token.T_POW:
                opc = "POW";
                break;
                
              case Token.T_NOT:
                opc = "NOT";
                break;
            }
            
            opcode.append(opc).append(" ");
            
            if (lhs != null)
              opcode.append(lhs).append(", ");
            
            opcode.append(rhs).append("\n");
            stack.add("%" + opn);
            opn++;
            break;
          }
          
          case Token.T_FUNCTION: {
            int fnop = opn++;
            opcode.append("%").append(fnop).append(" = FETCH_FN \"").append(((Ident) t).value).append("\"\n"); 
            
            int      argc = ((Ident) t).argc;
            //double[] argv = new double[argc];
            
            len -= (argc - 1);
            
            opcode.append(++idx).append(": ");
            opcode.append("%").append(opn).append(" = CALL %").append(fnop).append(" (")
              .append(argc).append(") ");
            
            for (int i = 0; i < argc; ++i) {
              opcode.append(stack.firstElement());
              stack.removeElementAt(0);
              
              if (stack.size() > 0)
                opcode.append(", ");
            }
            
            opcode.append("\n");
            stack.add("%" + opn);
            opn++;
            
            // Push the returned results, if any, back onto the stack.
            //this.stack.push(new Number(ctx.fn(((Ident) t).value, argv), Token.T_NUMBER));
            break;
          }
          
          /*case Token.T_SEMI:
            opcodes.append(opcode.toString()).append("\n");
            opcode.setLength(0);
            break;*/
            
          default:
            throw new RuntimeError("unerwarteter token #" + t.type);
        }
        
        ++idx;
  		}
  		
  		// If there is only one value in the stack
      // That value is the result of the calculation.
      if (stack.size() == 1) {
        opcode.append(idx).append(": RETURN ").append(stack.lastElement()).append("\n\n");
        stack.removeElementAt(stack.size() - 1);
        continue;
      }
      
      throw new RuntimeError("zu viele werte auf dem stack");
		}
		
		// clear queue
		queue.clear();
		
		return opcode.toString();
	}
	
	protected String quote(String str)
	{
	  return new StringBuilder().append("\"")
	      .append(str.replace("\"", "\\\""))
	      .append("\"").toString();
	}
	
	public Symbol reduce(Context ctx) throws RuntimeError
	{
	  Symbol res = null;
	  
	  for (TokenStack ts : queue) {
	    if (ts.size() == 0)
	      continue;
	    
  	  int len = 0;
      
      Token t;
      Stack<Symbol> stack = new Stack<Symbol>();
      
  		// While there are input tokens left
  		// Read the next token from input.
  		while ((t = ts.shift()) != null) {
  			switch (t.type) {
  				case Token.T_NUMBER:
  				case Token.T_IDENT:
  				case Token.T_RIDENT:
  				case Token.T_STRING: {
  				  Symbol sym = null;
  				  
  				  switch (t.type) {
  				    case Token.T_NUMBER:
  				      sym = new Symbol(((Number) t).value);
  				      break;
  				      
  				    case Token.T_RIDENT:
  				      // write-access
  				      String name = ((Ident) t).value;
  				      
  				      try {
    				      sym = ctx.getSymbol(name);
  				      } catch (RuntimeError e) {
  				        // create symbol
  				        ctx.setSymbol(name, sym = new Symbol("", true));
  				      }
  				      
  				      if (sym.readonly == true)
                  throw new RuntimeError("symbol \"" + name + "\" kann nicht überschrieben werden");
  				      
  				      break;
  				      
  				    case Token.T_IDENT:
  				      // read-access
  				      sym = ctx.getSymbol(((Ident) t).value);
  				      break;
  				      
  				    case Token.T_STRING:
  				      sym = new Symbol(((Ident) t).value, false);
  				      break;
  				  }
  				  
  					stack.push(sym);
  					++len;
  					break;
  				}
  				
  				case Token.T_PLUS:
  				case Token.T_MINUS:
  				case Token.T_UNARY_PLUS:
  				case Token.T_UNARY_MINUS:
  				case Token.T_TIMES:
  				case Token.T_DIV:
  				case Token.T_MOD:
  				case Token.T_POW:
  				case Token.T_NOT:
  				case Token.T_ASSIGN: {
  					// It is known a priori that the operator takes n arguments.
  					int na = argc(t);
  					
  					// If there are fewer than n values on the stack
  					if (len < na)
  						throw new RuntimeError("zu wenig token für operator `" + ((Operator) t).value + "`");
  					
  					Symbol rhs = stack.pop();
  					Symbol lhs = null;
  								          
  					if (na > 1) lhs = stack.pop();
  								           
  					len -= (na - 1);
  					
  					// Push the returned results, if any, back onto the stack.
  					stack.push(doOp(t.type, lhs, rhs));
  					break;
  				}
  				
  				case Token.T_FUNCTION: {
  					int      argc = ((Ident) t).argc;
  					Symbol[] argv = new Symbol[argc];
  					
  					len -= (argc - 1);
  					
  					for (; argc > 0; --argc)
  						argv[argc - 1] = stack.pop();
  					
  					// Push the returned results, if any, back onto the stack.
  					stack.push(ctx.getFunction(((Ident) t).value).call(argv));
  					break;
  				}
  				
  				default:
  					throw new RuntimeError("unerwarteter token #" + t.type);
  			}
  		}
  		
  		// If there is only one value in the stack
  		// That value is the result of the calculation.
  		if (stack.size() == 1) {
  		  res = stack.pop();
  		  continue;
  		}
  		
  		throw new RuntimeError("zu viele werte auf dem stack");
	  }
	  
	  // clear queue
	  queue.clear();
	  
	  return res;
	}
	
	protected Symbol doOp(short type, Symbol lhs, Symbol rhs) throws RuntimeError
	{
		if (lhs != null) {
		  if (type == Token.T_ASSIGN) {
		    if (lhs.ident != true)
		      throw new RuntimeError("nur identifer können werte speichern");
		    
		    if (rhs.type == Symbol.IS_NUMBER) {
		      lhs.num = rhs.num;
		      lhs.type = Symbol.IS_NUMBER;
		    } else {
		      lhs.str = rhs.str;
		      lhs.type = Symbol.IS_STRING;
		    }
		    
		    return lhs;
		  }
		  
		  boolean lin = lhs.type == Symbol.IS_NUMBER,
		          rin = rhs.type == Symbol.IS_NUMBER;
		  
			switch (type) {
				case Token.T_PLUS: {
				  if (lin && rin)
				    return new Symbol(lhs.num + rhs.num);
				  
				  StringBuilder sb = new StringBuilder();
				  
				  sb.append(lin ? lhs.num : lhs.str)
				    .append(rin ? rhs.num : rhs.str);
				  
				  return new Symbol(sb.toString(), false);
				}
				
				case Token.T_MINUS: {
				  if (!lin) {
				    String val = lhs.str;
				    
				    if (rin) 
				      val = val.substring(0, (int) rhs.num);
				    else 
				      val = val.replace(rhs.str, "");
				    
				    return new Symbol(val, false);
				  }
				  
				  if (!rin)
				    throw new RuntimeError("undefinierter operator `-` für NUMBER und STRING");
				  
					return new Symbol(lhs.num - rhs.num);
				}
				
				case Token.T_TIMES: {
				  if (!lin || (lin && !rin)) { // XOR
				    String val = lin ? rhs.str : lhs.str;
				    StringBuilder sb = new StringBuilder().append(val);
				    
				    for (int i = 0, l = (int) (lin ? lhs.num : rhs.num); i < l; ++i)
				      sb.append(val);
				    
				    return new Symbol(sb.toString(), false);
				  }
				  
					return new Symbol(lhs.num * rhs.num);
				}
				
				case Token.T_DIV:
				  if (!lin || !rin)
				    throw new RuntimeError("undefinierter operator `/` für STRING");
				  
					if (rhs.num == 0.)
						throw new RuntimeError("teilung durch 0");
					
					return new Symbol(lhs.num / rhs.num);
				
				case Token.T_MOD:
				  if (!lin || rin)
				    throw new RuntimeError("undefinierter operator `%` für STRING");
				  
					if (rhs.num == 0.)
						throw new RuntimeError("rest-teilung durch 0");
					
					return new Symbol((double) (lhs.num % rhs.num));

				case Token.T_POW:
				  if (!lin || rin)
            throw new RuntimeError("undefinierter operator `%` für STRING");
				  
					return new Symbol(Math.pow(lhs.num, rhs.num));
			}

			// throw?
			assert false : "Undefinierter operator?";
			return null;
		}

		boolean rin = rhs.type == Symbol.IS_NUMBER;
		
		switch (type) {
			case Token.T_NOT:
			  if (rin) return new Symbol(rhs.num > 0. ? 0. : 1.);
			  return new Symbol(rhs.str.isEmpty() ? 1. : 0.);

			case Token.T_UNARY_MINUS: {
			  if (rin) return new Symbol(-rhs.num);
			  
			  try {
			    return new Symbol(-Double.parseDouble(rhs.str));
			  } catch (NumberFormatException e) {
			    return new Symbol(0);
			  }
			}
			
			case Token.T_UNARY_PLUS: {
			  if (rin) return new Symbol(+rhs.num);
			  
			  try {
			    return new Symbol(+Double.parseDouble(rhs.str));
			  } catch (NumberFormatException e) {
          return new Symbol(0);
        }
			}
		}
		
		// throw?
		assert false : "Undefinierter operator?";
		return null;
	}
	
	protected int argc(Token t)
	{
		switch (t.type) {
			case Token.T_PLUS:
			case Token.T_MINUS:
			case Token.T_TIMES:
			case Token.T_DIV:
			case Token.T_MOD:
			case Token.T_POW:
			case Token.T_ASSIGN:
				return 2;
		}
	    
		return 1;
	}
	
	protected void fargs(Token fn, int idx) throws ParseError
	{
		handle(scanner.next(), idx); // '('

		int argc = 0;
		Token next = scanner.peek();

		if (next != null && next.type != Token.T_PCLOSE) {
			argc = 1;

			while ((next = scanner.next()) != null) {
				handle(next, idx);
		
				if (next.type == Token.T_PCLOSE)
					break;
		
				if (next.type == Token.T_COMMA)
					++argc;
			}
		}
	
		((Ident) fn).argc = argc;
	}
	
	protected void handle(Token t, int idx) throws ParseError
	{
	  TokenStack cstack = stack.get(idx),
	             cqueue = queue.get(idx);
	  
		switch (t.type) {
			case Token.T_NUMBER:
			case Token.T_IDENT:
			case Token.T_RIDENT:
			case Token.T_STRING:
				// If the token is a number (identifier), then add it to the output queue.        
				cqueue.push(t);
				break;

			case Token.T_FUNCTION:
				// If the token is a function token, then push it onto the stack.
				cstack.push(t);
				fargs(t, idx);
				break;


			case Token.T_COMMA: {
				// If the token is a function argument separator (e.g., a comma):

				boolean pe = false;
				
				while ((t = cstack.last()) != null) {
					if (t.type == Token.T_POPEN) {
						pe = true;
						break;
					}

					// Until the token at the top of the stack is a left parenthesis,
					// pop operators off the stack onto the output queue.
					cqueue.push(cstack.pop());
				}

				// If no left parentheses are encountered, either the separator was misplaced
				// or parentheses were mismatched.
				if (pe != true)
					throw new ParseError("vermisster token `(` oder fehlplazierter token `,`");

				break;
			}

			// If the token is an operator, op1, then:
			case Token.T_PLUS:
			case Token.T_MINUS:
			case Token.T_UNARY_PLUS:
			case Token.T_UNARY_MINUS:
			case Token.T_TIMES:
			case Token.T_DIV:
			case Token.T_MOD:
			case Token.T_POW:
			case Token.T_NOT:
			case Token.T_ASSIGN: {
				parent_while: while (cstack.size() > 0) {
					Token s = cstack.last();

					// While there is an operator token, o2, at the top of the stack
					// op1 is left-associative and its precedence is less than or equal to that of op2,
					// or op1 has precedence less than that of op2,
					// Let + and ^ be right associative.
					// Correct transformation from 1^2+3 is 12^3+
					// The differing operator priority decides pop / push
					// If 2 operators have equal priority then associativity decides.
					switch (s.type) {
						default: break parent_while;

						case Token.T_PLUS:
						case Token.T_MINUS:
						case Token.T_UNARY_PLUS:
						case Token.T_UNARY_MINUS:
						case Token.T_TIMES:
						case Token.T_DIV:
						case Token.T_MOD:
						case Token.T_POW:
						case Token.T_NOT: 
						case Token.T_ASSIGN: {
							int p1 = preced(t);
							int p2 = preced(s);

							if (!((assoc(t) == 1 && (p1 <= p2)) || (p1 < p2)))
								break parent_while;

							// Pop o2 off the stack, onto the output queue;
							cqueue.push(cstack.pop());
						}
					}
				}

				// push op1 onto the stack.
				cstack.push(t);
				break;
			}
		
			case Token.T_POPEN:
				// If the token is a left parenthesis, then push it onto the stack.
				cstack.push(t);
				break;

			// If the token is a right parenthesis:  
			case Token.T_PCLOSE: {
				boolean pe = false;

				// Until the token at the top of the stack is a left parenthesis,
				// pop operators off the stack onto the output queue
				while ((t = cstack.pop()) != null) {
					if (t.type == Token.T_POPEN) {
						// Pop the left parenthesis from the stack, but not onto the output queue.
						pe = true;
						break;
					}

					cqueue.push(t);
				}

				// If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
				if (pe != true)
					throw new ParseError("unerwarteter token `)`");

				// If the token at the top of the stack is a function token, pop it onto the output queue.
				if ((t = cstack.last()) != null && t.type == Token.T_FUNCTION)
					cqueue.push(cstack.pop());
 
				break;
			}
			
			default:
				throw new ParseError("unbekannter token #" + t.type);          
		}
	}
	
	protected int assoc(Token t)
	{
		switch (t.type) {
			case Token.T_TIMES:
			case Token.T_DIV:
			case Token.T_MOD:

			case Token.T_PLUS:
			case Token.T_MINUS:
				return 1; // ltr

			case Token.T_NOT:  
			case Token.T_UNARY_PLUS:
			case Token.T_UNARY_MINUS:

			case Token.T_POW: 
			case Token.T_ASSIGN:
				return 2; // rtl
		}

		return 0; // nassoc
	}
	
	protected int preced(Token t)
	{
		switch (t.type) {
			case Token.T_NOT:
			case Token.T_UNARY_PLUS:
			case Token.T_UNARY_MINUS:
				return 5;
		
			case Token.T_POW:
				return 4;
		
			case Token.T_TIMES:
			case Token.T_DIV:
			case Token.T_MOD:
				return 3;
		
			case Token.T_PLUS:
			case Token.T_MINUS:
				return 2;
				
			case Token.T_ASSIGN:
			  return 1;
		}
	
		return 0;
	}
	
	public static Symbol parse(String term) throws SyntaxError, ParseError, RuntimeError
	{
		return Parser.parse(term, new Context());
	}
	
	public static Symbol parse(String term, Context ctx) throws SyntaxError, ParseError, RuntimeError
	{
	  return new Parser(new Scanner(term)).reduce(ctx);
	}
	
	public static String generateOpcode(String term) throws RuntimeError, ParseError, SyntaxError
	{
	  return new Parser(new Scanner(term)).opcode();
	}
}
