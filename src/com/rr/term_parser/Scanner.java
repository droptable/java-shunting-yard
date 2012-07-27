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

package com.rr.term_parser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.rr.term_parser.Stack;
import com.rr.term_parser.Token;
import com.rr.term_parser.Ident;
import com.rr.term_parser.Number;
import com.rr.term_parser.Operator;

import com.rr.term_parser.SyntaxError;

public class Scanner 
{	
	public final String 
		// pattern
		RE_PATTERN = "^([!,\\+\\-\\*\\/\\^%\\(\\)]|\\d*\\.\\d+|\\d+\\.\\d*|\\d+|[a-z_A-Zπ]+[a-z_A-Z0-9]*|[ \\t]+).*",
		
		// nummer
		RE_NUMBER  = "^\\d*\\.\\d+|\\d+\\.\\d*|\\d+$",
		
		// ident
		RE_IDENT   = "^[a-z_A-Zπ]+[a-z_A-Z0-9]*$";
	
	protected Stack tokens;
	
	public Scanner(String term) throws SyntaxError
	{		
		this.tokens = new Stack();
		
		Pattern pattern = Pattern.compile(this.RE_PATTERN);
		Pattern number  = Pattern.compile(this.RE_NUMBER);
		Pattern ident   = Pattern.compile(this.RE_IDENT);
		
		while (!term.isEmpty()) {
			Matcher match = pattern.matcher(term);
			
			if (!match.matches()) {
				// syntax fehler (unmatched token)
				
				int e = term.length();
				if (e > 10) e = 10;
				
				throw new SyntaxError("syntax fehler in der nähe von `" + term.substring(0, e) + "`");
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
			
			// nummer
			if (number.matcher(token).matches()) {
				this.tokens.push(new Number(Double.parseDouble(token), Token.T_NUMBER));
				continue;
			}
			
			// ident
			if (ident.matcher(token).matches()) {
				this.tokens.push(new Ident(token, Token.T_IDENT));
				continue;
			}
			
			short type = 0;
			
			switch (token.charAt(0)) {
				case '!':
					type = Token.T_NOT;
					break;
		          
				case '+':
					type = Token.T_PLUS;
					break;

				case '-':
					type = Token.T_MINUS;
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
					
					Token prev = this.tokens.last();
					if (prev != null) {						
						switch (prev.type) {
							case Token.T_IDENT:
								prev.type = Token.T_FUNCTION;
								break;
								
							case Token.T_NUMBER:
							case Token.T_PCLOSE:
								this.tokens.push(new Operator("*", Token.T_TIMES));
								break;
						}
					}
					
					break;
				}
				
				case ')':
					type = Token.T_PCLOSE;
					break;

				case ',':
					type = Token.T_COMMA;
					break;
			}
			
			this.tokens.push(new Operator(token, type));
		}
	}
	
	public Token next() { return this.tokens.next(); }
	public Token prev() { return this.tokens.prev(); }
	public Token peek( ){ return this.tokens.peek(); }
}

