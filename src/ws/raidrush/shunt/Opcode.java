package ws.raidrush.shunt;

public class Opcode
{
  public final static short
    OP_ADD         = 1,  // %2 = + %0, %1
    OP_SUB         = 2,  // %2 = - %0, %1
    OP_MUL         = 3,  // %2 = * %0, %1
    OP_DIV         = 4,  // %2 = / %0, %1
    OP_POW         = 5,  // %2 = ^ %0, %1
    OP_MOD         = 6,  // %2 = % %0, &1
    OP_NOT         = 7,  // %1 = ! %0
    OP_UNARY_MINUS = 8,  // %1 = - %0
    OP_UNARY_PLUS  = 9,  // %1 = + %0
    OP_PUSH        = 10,  // push %0
    OP_POP         = 11, // pop
    OP_CALL        = 12, // call %0
    OP_FETCH_FN    = 13, // fetch_fn %0
    OP_FETCH_VL    = 14, // fetch_vl %0
    OP_RETURN      = 15; // return %0
}
