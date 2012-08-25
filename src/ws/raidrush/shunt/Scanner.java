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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Scanner 
{	
	public static final String 
		RE_PATTERN = "^([\\+\\-\\*\\/%\\^]=|[\"'!,\\+\\-\\*\\/\\^%\\(\\)=;]|0x[a-fA-f0-9]+|0b[10]+|\\d*\\.\\d+|\\d+\\.\\d*|\\d+|[a-z_A-Zπ]+[a-z_A-Z0-9]*|[ \\t]+).*",
		RE_DECIMAL = "^\\d*\\.\\d+|\\d+\\.\\d*|\\d+$",
		RE_BINARY  = "^0b[10]+$",
		RE_HEX     = "^0x[a-fA-f0-9]+$",
		RE_IDENT   = "^[a-z_A-Zπ]+[a-z_A-Z0-9]*$";
	
	protected TokenStack tokens;
	
	public Scanner(String term) throws SyntaxError
	{		
		tokens = new TokenStack();
		
		Pattern re_pattern = Pattern.compile(RE_PATTERN),
				    re_decimal = Pattern.compile(RE_DECIMAL),
				    re_binary  = Pattern.compile(RE_BINARY),
				    re_hex     = Pattern.compile(RE_HEX),
				    re_ident   = Pattern.compile(RE_IDENT);
		
		// dummy
		Token prev = new Token();
		prev.type = Token.T_OPERATOR;
		
		while_loop: while (!term.isEmpty()) {
			Matcher match = re_pattern.matcher(term);
			
			if (!match.matches()) {
				// syntax fehler (unmatched token)
				
				int e = term.length();
				if (e > 10) e = 10;
				
				throw new SyntaxError("einlese fehler in der nähe von `" + term.substring(0, e) + "`");
			}
			
			String token = match.group(1);
			
			if (token.isEmpty()) {
				// regex fehler (endlosschleife)
				throw new SyntaxError("leerer fund! endlosschleife");
			}
			
			term = term.substring(token.length());
			
			if ((token = token.trim()).isEmpty()) {
				// leerzeichen ignorieren
				continue;
			}
			
			// decimal
			if (re_decimal.matcher(token).matches()) {
				tokens.push(prev = new Number(Double.parseDouble(token), Token.T_NUMBER));
				continue;
			}
			
			// binary
			if (re_binary.matcher(token).matches()) {
			  tokens.push(prev = new Number((double) Long.parseLong(token.substring(2), 2), Token.T_NUMBER));
			  continue;
			}
			
			// hex
			if (re_hex.matcher(token).matches()) {
			  tokens.push(prev = new Number((double) Long.parseLong(token.substring(2), 16), Token.T_NUMBER));
			  continue;
			}
			
			// ident
			if (re_ident.matcher(token).matches()) {
				tokens.push(prev = new Ident(token, Token.T_IDENT));
				continue;
			}
			
			// TODO: add these tokens directly to the parser?
			
			if (token.equals("+=")) {
			  prev = expandShortcut(tokens, prev, "=", "+", Token.T_ASSIGN, Token.T_PLUS);
			  continue;
			}
			
			if (token.equals("-=")) {
        prev = expandShortcut(tokens, prev, "=", "-", Token.T_ASSIGN, Token.T_MINUS);
        continue;
      }
			
			if (token.equals("*=")) {
        prev = expandShortcut(tokens, prev, "=", "*", Token.T_ASSIGN, Token.T_TIMES);
        continue;
      }
			
			if (token.equals("/=")) {
        prev = expandShortcut(tokens, prev, "=", "/", Token.T_ASSIGN, Token.T_DIV);
        continue;
      }
			
			if (token.equals("%=")) {
        prev = expandShortcut(tokens, prev, "=", "%", Token.T_ASSIGN, Token.T_MOD);
        continue;
      }
			
			if (token.equals("^=")) {
        prev = expandShortcut(tokens, prev, "=", "^", Token.T_ASSIGN, Token.T_POW);
        continue;
      }
			
			short type = 0;
			
			switch (token.charAt(0)) {
			  case '"':
			  case '\'': {
			    char          t = token.charAt(0);
			    StringBuilder s = new StringBuilder();
			    int           l = term.length();
			    boolean       e = false;
			    
			    for (int idx = 0; idx < l; ++idx) {
			      char c = term.charAt(idx);
			      
			      switch (c) {
			        case '\\': {
			          char n = term.charAt(idx + 1);
			          
			          switch (n) {
			            case 'n': c = '\n'; break;
			            case 'r': c = '\r'; break;
			            case 't': c = '\t'; break;
			            
			            default:
			              if (n != t)
			                throw new SyntaxError("illegale escape-sequenz \\" + n);
			              
			              c = t;
			          }
			          
			          ++idx;
			          break;
			        }
			        
			        default:
			          if (c == t) {
			            e = true;
			            break;
			          }
			      }
			      
			      if (e == true) break;
			      s.append(c);
			    }
			    
			    if (e == false)
			      throw new SyntaxError("fehlendes anführungszeichen am ende einer string-sequenz");
			    
			    term = term.substring(s.length() + 1);
			    tokens.push(prev = new Ident(s.toString(), Token.T_STRING));
			    
			    continue while_loop;
			  }
			  
				case '!':
					type = Token.T_NOT;
					break;
		          
				case '+':
					type = ((prev.type & Token.T_OPERATOR) > 0 || prev.type == Token.T_POPEN) 
					  ? Token.T_UNARY_PLUS : Token.T_PLUS;
					break;

				case '-':
					type = ((prev.type & Token.T_OPERATOR) > 0 || prev.type == Token.T_POPEN) 
					  ? Token.T_UNARY_MINUS : Token.T_MINUS;
					break;

				case '*':
					type = Token.T_TIMES;
					break;

				case '/':
					type = Token.T_DIV;
					break;

				case '%':
					type = Token.T_MOD;
					break;

				case '^':
					type = Token.T_POW;
					break;

				case '(': {
					type = Token.T_POPEN;
											
					switch (prev.type) {
						case Token.T_IDENT:
							prev.type = Token.T_FUNCTION;
							break;
							
						case Token.T_NUMBER:
						case Token.T_PCLOSE:
							this.tokens.push(new Operator("*", Token.T_TIMES));
							break;
					}
					
					break;
				}
				
				case ')':
					type = Token.T_PCLOSE;
					break;

				case ',':
					type = Token.T_COMMA;
					break;
					
				case ';':
				  type = Token.T_SEMI;
				  break;
				  
				case '=':
				  if (prev.type != Token.T_IDENT)
				    throw new SyntaxError("fehledner ident vor `=`");
				  
				  prev.type = Token.T_RIDENT;
				  type = Token.T_ASSIGN;
				  break;
			}
			
			this.tokens.push(prev = new Operator(token, type));
		}
	}
	
	protected Token expandShortcut(TokenStack tokens, Token prev, String av, String bv, short at, short bt) throws SyntaxError
	{
	  if (prev.type != Token.T_IDENT)
      throw new SyntaxError("ident erwartet");
    
    tokens.push(new Operator(av, at));
    tokens.push(new Ident(((Ident) prev).value, Token.T_IDENT));
    
    prev.type = Token.T_RIDENT;
    
    Token next = new Operator(bv, bt);
    tokens.push(next);
    
    return next;
	}
	
	public Token next() { return this.tokens.next(); }
	public Token prev() { return this.tokens.prev(); }
	public Token peek( ){ return this.tokens.peek(); }
}

