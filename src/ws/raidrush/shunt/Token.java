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

public class Token 
{
	public static final short 
		T_NUMBER       = 1,   // eine nummer (integer / double)
		T_IDENT        = 2,   // konstante
		T_FUNCTION     = 3,   // funktion
		T_POPEN        = 4,   // (
		T_PCLOSE       = 5,   // )
		T_RIDENT       = 8,   // ident vor = (R -> right)
		T_STRING       = 9,   // string
		T_OPERATOR     = 128, // operator
		T_PLUS         = 129, // +
		T_MINUS        = 130, // -
		T_TIMES        = 131, // * 
		T_DIV          = 132, // /
		T_MOD          = 133, // %
		T_POW          = 134, // ^
		T_UNARY_PLUS   = 135, // + als vorzeichen (zur übersetzungszeit ermittelt)
		T_UNARY_MINUS  = 136, // - als vorzeichen (zur übersetzungszeit ermittelt)
		T_NOT          = 137, // ! als vorzeichen
		T_SEMI         = 138, // ;
		T_COMMA        = 139, // ,
		T_ASSIGN       = 140; // =
	
	public short type;
}
