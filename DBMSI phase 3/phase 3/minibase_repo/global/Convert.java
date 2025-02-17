/* file Convert.java */

package global;

import java.io.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;

import intervalTree.IntervalKey;

public class Convert{
 
 /**
 * read 4 bytes from given byte array at the specified position
 * convert it to an integer
 * @param  	data 		a byte array 
 * @param       position  	in data[]
 * @exception   java.io.IOException I/O errors
 * @return      the integer 
 */
  public static int getIntValue (int position, byte []data)
   throws java.io.IOException
    {
      InputStream in;
      DataInputStream instr;
      int value;
      byte tmp[] = new byte[4];
      
      // copy the value from data array out to a tmp byte array
      System.arraycopy (data, position, tmp, 0, 4);
      
      /* creates a new data input stream to read data from the
       * specified input stream
       */
      in = new ByteArrayInputStream(tmp);
      instr = new DataInputStream(in);
      value = instr.readInt();  
      
      return value;
    }
  
  /**
   * read 4 bytes from given byte array at the specified position
   * convert it to a float value
   * @param  	data 		a byte array 
   * @param       position  	in data[]
   * @exception   java.io.IOException I/O errors
   * @return      the float value
   */
  public static float getFloValue (int position, byte []data)
    throws java.io.IOException
    {
      InputStream in;
      DataInputStream instr;
      float value;
      byte tmp[] = new byte[4];
      
      // copy the value from data array out to a tmp byte array
      System.arraycopy (data, position, tmp, 0, 4);
      
      /* creates a new data input stream to read data from the
       * specified input stream
       */
      in = new ByteArrayInputStream(tmp);
      instr = new DataInputStream(in);
      value = instr.readFloat();  
      
      return value;
    }
  
  
  /**
   * read 2 bytes from given byte array at the specified position
   * convert it to a short integer
   * @param  	data 		a byte array
   * @param       position  	the position in data[]
   * @exception   java.io.IOException I/O errors
   * @return      the short integer
   */
  public static short getShortValue (int position, byte []data)
    throws java.io.IOException
    {
      InputStream in;
      DataInputStream instr;
      short value;
      byte tmp[] = new byte[2];
      
      // copy the value from data array out to a tmp byte array
      System.arraycopy (data, position, tmp, 0, 2);
      
      /* creates a new data input stream to read data from the
       * specified input stream
       */
      in = new ByteArrayInputStream(tmp);
      instr = new DataInputStream(in);
      value = instr.readShort();
      
      return value;
    }
  
  /**
   * reads a string that has been encoded using a modified UTF-8 format from
   * the given byte array at the specified position
   * @param       data            a byte array
   * @param       position        the position in data[]
   * @param 	length 		the length of the string in bytes
   *			         (=strlength +2)
   * @exception   java.io.IOException I/O errors
   * @return      the string
   */
  public static String getStrValue (int position, byte []data, int length)
    throws java.io.IOException
    {
      InputStream in;
      DataInputStream instr;
      String value;
      byte tmp[] = new byte[length];  
      int a = data.length;
      
      // copy the value from data array out to a tmp byte array
      System.arraycopy (data, position, tmp, 0, length);
      
      /* creates a new data input stream to read data from the
       * specified input stream
       */
      in = new ByteArrayInputStream(tmp);
      instr = new DataInputStream(in);
      value = instr.readUTF();
      return value;
    }
  
  /**
   * reads 2 bytes from the given byte array at the specified position
   * convert it to a character
   * @param       data            a byte array
   * @param       position        the position in data[]
   * @exception   java.io.IOException I/O errors
   * @return      the character
   */
  public static char getCharValue (int position, byte []data)
    throws java.io.IOException
    {
      InputStream in;
      DataInputStream instr;
      char value;
      byte tmp[] = new byte[2];
      // copy the value from data array out to a tmp byte array  
      System.arraycopy (data, position, tmp, 0, 2);
      
      /* creates a new data input stream to read data from the
       * specified input stream
       */
      in = new ByteArrayInputStream(tmp);
      instr = new DataInputStream(in);
      value = instr.readChar();
      return value;
    }

  /**
   * reads 2 bytes from the given byte array at the specified position
   * convert it to a character Interval
   * @param       data            a byte array
   * @param       position        the position in data[]
   * @exception   java.io.IOException I/O errors
   * @return      the Interval
   */
  public static IntervalType getIntervalValue (int position, byte []data)
			throws java.io.IOException {
		// InputStream in;
		// ObjectInputStream objectInputStream;
		// IntervalType value;
		// byte tmp[] = new byte[2];
		// // copy the value from data array out to a tmp byte array
		// System.arraycopy (data, position, tmp, 0, 2);
		//
		// /* creates a new data input stream to read data from the
		// * specified input stream
		// */
		// in = new ByteArrayInputStream(tmp);
		// objectInputStream = new ObjectInputStream(in);
		// value = (IntervalType) objectInputStream.readObject();
		//
		//
		InputStream in;
		DataInputStream instr;
		int startInterval;
		int endInterval;
		int level;
		byte tmp[] = new byte[12];

		// copy the value from data array out to a tmp byte array
		System.arraycopy(data, position, tmp, 0, 12);

		/*
		 * creates a new data input stream to read data from the specified input stream
		 */
		in = new ByteArrayInputStream(tmp);
		instr = new DataInputStream(in);
		startInterval = instr.readInt();
		endInterval = instr.readInt();
		level = instr.readInt();
		IntervalType interval = new IntervalType(startInterval, endInterval, level);
		return interval;
	}
  
  public static NodeTable getNodeValue (int position, byte []data, int length)
			throws java.io.IOException {
		InputStream in;
		DataInputStream instr;
		int startInterval;
		int endInterval;
		int level;
		byte tmp[] = new byte[GlobalConst.COMPOSITE_KEY_LEN];
		int c = data.length;

		// copy the value from data array out to a tmp byte array
		System.arraycopy(data, position, tmp, 0, GlobalConst.COMPOSITE_KEY_LEN);

		in = new ByteArrayInputStream(tmp);
		instr = new DataInputStream(in);
		startInterval = instr.readInt();
		endInterval = instr.readInt();
		level = instr.readInt();
		
		tmp = new byte[length];  
	      // copy the value from data array out to a tmp byte array
	      System.arraycopy (data, position+12, tmp, 0, 5+2);
	      in = new ByteArrayInputStream(tmp);
	      instr = new DataInputStream(in);
	      String name = instr.readUTF();
		
		IntervalType interval = new IntervalType(startInterval, endInterval, level);
		NodeTable tb = new NodeTable(name, interval);
		return tb;
	}
  
  public static void setNodelValue (intervalTree.IntervalKey value, int position, byte []data)
          throws java.io.IOException
  {
      /* creates a new data output stream to write data to
       * underlying output stream
       */

      OutputStream out = new ByteArrayOutputStream();
      DataOutputStream outstr = new DataOutputStream (out);
       
      outstr.writeInt(value.key.s);
      outstr.writeInt(value.key.e);
      outstr.writeInt(value.key.l);
      outstr.writeUTF(value.name);
      
      // write the value to the output stream
     // outstr.writeObject(value);
      // creates a byte array with this output stream size and the
      // valid contents of the buffer have been copied into it
      byte []B = ((ByteArrayOutputStream) out).toByteArray();
      int a = B.length;
      
    //  int sz =((ByteArrayOutputStream) out).toByteArray().length;
      // copies the contents of this byte array into data[]
try {
    System.arraycopy (B, 0, data, position, a);

}catch(Exception e) {
	System.out.println();
}
  }
  
  public static void setNodelValue (compositeTree.IntervalKey value, int position, byte []data)
          throws java.io.IOException
  {
      /* creates a new data output stream to write data to
       * underlying output stream
       */

      OutputStream out = new ByteArrayOutputStream();
      DataOutputStream outstr = new DataOutputStream (out);
       
      outstr.writeInt(value.key.s);
      outstr.writeInt(value.key.e);
      outstr.writeInt(value.key.l);
      outstr.writeUTF(value.name);
      
      // write the value to the output stream
     // outstr.writeObject(value);
      // creates a byte array with this output stream size and the
      // valid contents of the buffer have been copied into it
      byte []B = ((ByteArrayOutputStream) out).toByteArray();
      int a = B.length;
      
    //  int sz =((ByteArrayOutputStream) out).toByteArray().length;
      // copies the contents of this byte array into data[]
try {
    System.arraycopy (B, 0, data, position, a);

}catch(Exception e) {
	System.out.println();
}
  }
  
  
  /**
   * update an integer value in the given byte array at the specified position
   * @param  	data 		a byte array
   * @param	value   	the value to be copied into the data[]
   * @param	position  	the position of tht value in data[]
   * @exception   java.io.IOException I/O errors
   */
  public static void setIntValue (int value, int position, byte []data) 
    throws java.io.IOException
    {
      /* creates a new data output stream to write data to 
       * underlying output stream
       */
      
      OutputStream out = new ByteArrayOutputStream();
      DataOutputStream outstr = new DataOutputStream (out);
      
      // write the value to the output stream
      
      outstr.writeInt(value);
      
      // creates a byte array with this output stream size and the
      // valid contents of the buffer have been copied into it
      byte []B = ((ByteArrayOutputStream) out).toByteArray();
      
      // copies the first 4 bytes of this byte array into data[] 
      System.arraycopy (B, 0, data, position, 4);
      
    }
  
  /**
   * update a float value in the given byte array at the specified position
   * @param  	data 		a byte array
   * @param	value   	the value to be copied into the data[]
   * @param	position  	the position of tht value in data[]
   * @exception   java.io.IOException I/O errors
   */
  public static void setFloValue (float value, int position, byte []data) 
    throws java.io.IOException
    {
      /* creates a new data output stream to write data to 
       * underlying output stream
       */
      
      OutputStream out = new ByteArrayOutputStream();
      DataOutputStream outstr = new DataOutputStream (out);
      
      // write the value to the output stream
      
      outstr.writeFloat(value);
      
      // creates a byte array with this output stream size and the
      // valid contents of the buffer have been copied into it
      byte []B = ((ByteArrayOutputStream) out).toByteArray();
      
      // copies the first 4 bytes of this byte array into data[] 
      System.arraycopy (B, 0, data, position, 4);
      
    }
  
  /**
   * update a short integer in the given byte array at the specified position
   * @param  	data 		a byte array
   * @param	value   	the value to be copied into data[]
   * @param	position  	the position of tht value in data[]
   * @exception   java.io.IOException I/O errors
   */
  public static void setShortValue (short value, int position, byte []data) 
    throws java.io.IOException
    {
      /* creates a new data output stream to write data to 
       * underlying output stream
       */
      
      OutputStream out = new ByteArrayOutputStream();
      DataOutputStream outstr = new DataOutputStream (out);
      
      // write the value to the output stream
      
      outstr.writeShort(value);
      
      // creates a byte array with this output stream size and the
      // valid contents of the buffer have been copied into it
      byte []B = ((ByteArrayOutputStream) out).toByteArray();
      
      // copies the first 2 bytes of this byte array into data[] 
      System.arraycopy (B, 0, data, position, 2);
      
    }
  
  /**
   * Insert or update a string in the given byte array at the specified 
   * position.
   * @param       data            a byte array
   * @param       value           the value to be copied into data[]
   * @param       position        the position of tht value in data[]
   * @exception   java.io.IOException I/O errors
   */
 public static void setStrValue (String value, int position, byte []data)
        throws java.io.IOException
 {
  /* creates a new data output stream to write data to
   * underlying output stream
   */
   OutputStream out = new ByteArrayOutputStream();
   DataOutputStream outstr = new DataOutputStream (out);
   
   // write the value to the output stream
   outstr.writeUTF(value);
   // creates a byte array with this output stream size and the 
   // valid contents of the buffer have been copied into it
   byte []B = ((ByteArrayOutputStream) out).toByteArray();
   int sz =outstr.size(); 
   
   // copies the contents of this byte array into data[]
   System.arraycopy (B, 0, data, position, sz);
   
 }
  
  /**
   * Update a character in the given byte array at the specified position.
   * @param       data            a byte array
   * @param       value           the value to be copied into data[]
   * @param       position        the position of tht value in data[]
   * @exception   java.io.IOException I/O errors
   */
  public static void setCharValue (char value, int position, byte []data)
    throws java.io.IOException
    {
      /* creates a new data output stream to write data to
       * underlying output stream
       */

      OutputStream out = new ByteArrayOutputStream();
      DataOutputStream outstr = new DataOutputStream (out);

      // write the value to the output stream
      outstr.writeChar(value);

      // creates a byte array with this output stream size and the
      // valid contents of the buffer have been copied into it
      byte []B = ((ByteArrayOutputStream) out).toByteArray();

      // copies contents of this byte array into data[]
      System.arraycopy (B, 0, data, position, 2);

    }

    /**
     * Insert or update a string in the given byte array at the specified
     * position.
     * @param       data            a byte array
     * @param       value           the value to be copied into data[]
     * @param       position        the position of tht value in data[]
     * @exception   java.io.IOException I/O errors
     */
    public static void setIntervalValue (IntervalType value, int position, byte []data)
            throws java.io.IOException
    {
        /* creates a new data output stream to write data to
         * underlying output stream
         */

        OutputStream out = new ByteArrayOutputStream();
        DataOutputStream outstr = new DataOutputStream (out);
         
        outstr.writeInt(value.s);
        outstr.writeInt(value.e);
        outstr.writeInt(value.l);
        
        // write the value to the output stream
       // outstr.writeObject(value);
        // creates a byte array with this output stream size and the
        // valid contents of the buffer have been copied into it
        byte []B = ((ByteArrayOutputStream) out).toByteArray();
        
      //  int sz =((ByteArrayOutputStream) out).toByteArray().length;
        // copies the contents of this byte array into data[]
        System.arraycopy (B, 0, data, position, 12);

    }
}
