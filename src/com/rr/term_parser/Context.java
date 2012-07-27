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

import java.util.Map;
import java.util.HashMap;

import com.rr.term_parser.Function;

public class Context 
{
	protected Map<String, Function> fnt;
	protected Map<String, Double> cst;
	
	public Context()
	{
		this.fnt = new HashMap<String, Function>();
		this.cst = new HashMap<String, Double>();
	}
	
	public double fn(String name, double[] args) throws RuntimeError
	{
		if (!this.fnt.containsKey(name))
			throw new RuntimeError("undefinierte funktion \"" + name + "\"");
		
		return this.fnt.get(name).call(args);
	}
	
	public double cs(String name) throws RuntimeError
	{
		if (!this.cst.containsKey(name))
			throw new RuntimeError("undefinierte konstante \"" + name + "\"");
		
		return this.cst.get(name);
	}
	
	public Context def(String name, double value)
	{
		this.cst.put(name, value);
		return this;
	}
	
	public Context def(String name, Function value)
	{
		this.fnt.put(name, value);
		return this;
	}
}
