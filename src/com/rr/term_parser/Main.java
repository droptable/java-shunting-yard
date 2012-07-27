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

import com.rr.term_parser.Parser;
import com.rr.term_parser.Context;
import com.rr.term_parser.Function;

public class Main 
{
	public static void main(String[] args)
	{
		if (args.length < 1)
			return;
		
		Context ctx = new Context();
		
		ctx.def("foo", 1.);
		ctx.def("bar", new Function() {
			public double call(double[] args) {
				return args[0] + args[1];
			}
		});
		
		try {
			System.out.println(Parser.parse(args[0], ctx));
		} catch (ParseError e) {
			System.out.println("parse error: " + e.getMessage());
		} catch (SyntaxError e) {
			System.out.println("syntax error: " + e.getMessage());
		} catch (RuntimeError e) {
			System.out.println("runtime error: " + e.getMessage());
		}
	}
}
