package compositeTree;

import chainexception.ChainException;

public class KeyNotMatchException extends ChainException
{
  public KeyNotMatchException() {super();}
  public KeyNotMatchException(String s) {super(null,s);}
  public KeyNotMatchException(Exception e, String s) {super(e,s);}

}
