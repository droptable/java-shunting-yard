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

public class Token 
{
	public static final short 
		T_NUMBER      = 1,  // eine nummer (integer / double)
		T_IDENT       = 2,  // konstante
		T_FUNCTION    = 4,  // funktion
		T_POPEN       = 8,  // (
		T_PCLOSE      = 16,  // )
		T_COMMA       = 32, // ,
		T_OPERATOR    = 64, // operator (derzeit ungenutzt)
		T_PLUS        = 65, // +
		T_MINUS       = 66, // -
		T_TIMES       = 67, // * 
		T_DIV         = 68, // /
		T_MOD         = 69, // %
		T_POW         = 70, // ^
		T_UNARY_PLUS  = 71, // + als vorzeichen (zur übersetzungszeit ermittelt)
		T_UNARY_MINUS = 72, // - als vorzeichen (zur übersetzungszeit ermittelt)
		T_NOT         = 73; // ! als vorzeichen
	
	public short type;
}
