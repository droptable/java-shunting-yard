package ws.raidrush.shunt;

public class Symbol
{
  public static final short
    IS_STRING = 1,
    IS_NUMBER = 2;
  
  public String str;
  public double num;
  
  public short type;
  public boolean readonly = false, ident = false;
  
  public Symbol() {}
  
  public Symbol(String str)
  {
    this(str, false);
  }
  
  public Symbol(String str, boolean ident)
  {
    this.str = str;
    this.type = IS_STRING;
    this.ident = ident;
  }
  
  public Symbol(String str, boolean ident, boolean readonly)
  {
    this(str, ident);
    this.readonly = readonly;
  }
  
  public Symbol(double num)
  {
    this(num, false);
  }
  
  public Symbol(double num, boolean ident)
  {
    this.num = num;
    this.type = IS_NUMBER;
    this.ident = ident;
  }
  
  public Symbol(double num, boolean ident, boolean readonly)
  {
    this(num, ident);
    this.readonly = readonly;
  }
}
