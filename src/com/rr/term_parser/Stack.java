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

import java.util.Vector;

import com.rr.term_parser.Token;

// wir brauchen: push(), pop(), shift(), unshift(), peek(), first(), last(), prev() und next()
// wenn es so eine klasse im irrgarten von java geben solle
// bitte mir kurz bescheid geben :-D

public class Stack
{
	protected Vector<Token> stack;
	protected int idx = -1;
	
	public Stack()
	{
		this.stack = new Vector<Token>();
	}
	
	public int size()
	{
		return this.stack.size();
	}
	
	public Token first()
	{
		if (this.stack.size() == 0)
			return null;
		
		return this.stack.firstElement();
	}
	
	public Token last()
	{
		if (this.stack.size() == 0)
			return null;
		
		return this.stack.lastElement();
	}
	
	public Token peek()
	{
		if (this.idx + 1 >= this.stack.size())
			return null;
		
		return this.stack.get(this.idx + 1);
	}
	
	public Token next()
	{
		if (this.idx + 1 >= this.stack.size())
			return null;
		
		return this.stack.get(++this.idx);
	}
	
	public Token prev()
	{
		if (this.idx - 1 < 0)
			return null;
		
		return this.stack.get(--this.idx);
	}
	
	public int push(Token value)
	{
		this.stack.add(value);
		return this.stack.size();
	}
	
	public int unshift(Token value)
	{
		this.stack.add(0, value);
		return this.stack.size();
	}
	
	public Token pop()
	{
		int size = this.stack.size();
		if (size == 0) return null;
		
		return this.stack.remove(size - 1);
	}
	
	public Token shift()
	{
		if (this.stack.size() == 0)
			return null;
		
		return this.stack.remove(0);
	}
}
