package index;
import global.*;
import intervalTree.ConstructPageException;
import intervalTree.IntervalKey;
import intervalTree.IntervalT;
import intervalTree.IntervalTreeFile;
import intervalTree.IteratorException;
import intervalTree.KeyNotMatchException;
import intervalTree.PinPageException;
import intervalTree.UnpinPageException;
import btree.*;
import iterator.*;
import java.io.*;


/**
 * IndexUtils class opens an index scan based on selection conditions.
 * Currently only BTree_scan is supported
 */
public class IndexUtils {

  /**
   * BTree_scan opens a BTree scan based on selection conditions
   * @param selects conditions to apply
   * @param indFile the index (BTree) file
   * @return an instance of IndexFileScan (BTreeFileScan)
   * @exception IOException from lower layer
   * @exception UnknownKeyTypeException only int and string keys are supported
   * @exception InvalidSelectionException selection conditions (selects) not valid
   * @exception KeyNotMatchException Keys do not match
   * @exception UnpinPageException unpin page failed
   * @exception PinPageException pin page failed
   * @exception IteratorException iterator exception
   * @exception ConstructPageException failed to construct a header page
   */
  public static IndexFileScan BTree_scan(CondExpr[] selects, IndexFile indFile)
    throws IOException,
	   UnknownKeyTypeException,
	   InvalidSelectionException,
	   KeyNotMatchException,
	   UnpinPageException,
	   PinPageException,
	   IteratorException,
	   ConstructPageException, btree.KeyNotMatchException, btree.IteratorException, btree.ConstructPageException, btree.PinPageException, btree.UnpinPageException
	{
		IndexFileScan indScan;

		if (selects == null || selects[0] == null) {
			indScan = ((BTreeFile) indFile).new_scan(null, null);
			return indScan;
		}

		if (selects[1] == null) {
			if (selects[0].type1.attrType != AttrType.attrSymbol && selects[0].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}

			KeyClass key;

			// symbol = value
			if (selects[0].op.attrOperator == AttrOperator.aopEQ) {
				if (selects[0].type1.attrType != AttrType.attrSymbol) {
					key = getValue(selects[0], selects[0].type1, 1);
					indScan = ((BTreeFile) indFile).new_scan(key, key);
				} else {
					key = getValue(selects[0], selects[0].type2, 2);
					indScan = ((BTreeFile) indFile).new_scan(key, key);
				}
				return indScan;
			}

			// symbol < value or symbol <= value
			if (selects[0].op.attrOperator == AttrOperator.aopLT || selects[0].op.attrOperator == AttrOperator.aopLE) {
				if (selects[0].type1.attrType != AttrType.attrSymbol) {
					key = getValue(selects[0], selects[0].type1, 1);
					indScan = ((BTreeFile) indFile).new_scan(null, key);
				} else {
					key = getValue(selects[0], selects[0].type2, 2);
					indScan = ((BTreeFile) indFile).new_scan(null, key);
				}
				return indScan;
			}

			// symbol > value or symbol >= value
			if (selects[0].op.attrOperator == AttrOperator.aopGT || selects[0].op.attrOperator == AttrOperator.aopGE) {
				if (selects[0].type1.attrType != AttrType.attrSymbol) {
					key = getValue(selects[0], selects[0].type1, 1);
					indScan = ((BTreeFile) indFile).new_scan(key, null);
				} else {
					key = getValue(selects[0], selects[0].type2, 2);
					indScan = ((BTreeFile) indFile).new_scan(key, null);
				}
				return indScan;
			}

			// error if reached here
			System.err.println("Error -- in IndexUtils.BTree_scan()");
			return null;
		} else {
			// selects[1] != null, must be a range query
			if (selects[0].type1.attrType != AttrType.attrSymbol && selects[0].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}
			if (selects[1].type1.attrType != AttrType.attrSymbol && selects[1].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}

			// which symbol is higher??
			KeyClass key1, key2;
			AttrType type;

			if (selects[0].type1.attrType != AttrType.attrSymbol) {
				key1 = getValue(selects[0], selects[0].type1, 1);
				type = selects[0].type1;
			} else {
				key1 = getValue(selects[0], selects[0].type2, 2);
				type = selects[0].type2;
			}
			if (selects[1].type1.attrType != AttrType.attrSymbol) {
				key2 = getValue(selects[1], selects[1].type1, 1);
			} else {
				key2 = getValue(selects[1], selects[1].type2, 2);
			}

			switch (type.attrType) {
			case AttrType.attrString:
				if (((StringKey) key1).getKey().compareTo(((StringKey) key2).getKey()) < 0) {
					indScan = ((BTreeFile) indFile).new_scan(key1, key2);
				} else {
					indScan = ((BTreeFile) indFile).new_scan(key2, key1);
				}
				return indScan;

			case AttrType.attrInteger:
				if (((IntegerKey) key1).getKey().intValue() < ((IntegerKey) key2).getKey().intValue()) {
					indScan = ((BTreeFile) indFile).new_scan(key1, key2);
				} else {
					indScan = ((BTreeFile) indFile).new_scan(key2, key1);
				}
				return indScan;

			case AttrType.attrReal:
				/*
				 * if ((FloatKey)key1.getKey().floatValue() <
				 * (FloatKey)key2.getKey().floatValue()) { indScan =
				 * ((BTreeFile)indFile).new_scan(key1, key2); } else { indScan =
				 * ((BTreeFile)indFile).new_scan(key2, key1); } return indScan;
				 */
			default:
				// error condition
				throw new UnknownKeyTypeException("IndexUtils.java: Only Integer and String keys are supported so far");
			}
		} // end of else

	}

  /**
   * getValue returns the key value extracted from the selection condition.
   * @param cd the selection condition
   * @param type attribute type of the selection field
   * @param choice first (1) or second (2) operand is the value
   * @return an instance of the KeyClass (IntegerKey or StringKey)
   * @exception UnknownKeyTypeException only int and string keys are supported
   */
  private static KeyClass getValue(CondExpr cd, AttrType type, int choice)
       throws UnknownKeyTypeException
  {
    // error checking
    if (cd == null) {
      return null;
    }
    if (choice < 1 || choice > 2) {
      return null;
    }

    switch (type.attrType) {
    case AttrType.attrString:
      if (choice == 1) return new StringKey(cd.operand1.string);
      else return new StringKey(cd.operand2.string);
    case AttrType.attrInteger:
      if (choice == 1) return new IntegerKey(new Integer(cd.operand1.integer));
      else return new IntegerKey(new Integer(cd.operand2.integer));
    case AttrType.attrReal:
      /*
      // need FloatKey class in bt.java
      if (choice == 1) return new FloatKey(new Float(cd.operand.real));
      else return new FloatKey(new Float(cd.operand.real));
      */
    default:
	throw new UnknownKeyTypeException("IndexUtils.java: Only Integer and String keys are supported so far");
    }

  }

  public static intervalTree.IndexFileScan intervalTree_scan(CondExpr[] selects, intervalTree.IndexFile indFile)
		  throws IOException,
		   UnknownKeyTypeException,
		   InvalidSelectionException,
		   KeyNotMatchException,
		   UnpinPageException,
		   PinPageException,
		   IteratorException,
		   ConstructPageException, KeyNotMatchException, IteratorException, ConstructPageException, PinPageException, UnpinPageException, intervalTree.KeyNotMatchException, intervalTree.IteratorException, intervalTree.ConstructPageException, intervalTree.PinPageException, intervalTree.UnpinPageException
	{
		intervalTree.IndexFileScan indScan;

		if (selects == null || selects[0] == null) {
			indScan = ((IntervalTreeFile) indFile).new_scan(null, null, 7);
			return indScan;
		}

		if (selects[1] == null) {
			if (selects[0].type1.attrType != AttrType.attrSymbol && selects[0].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}

			intervalTree.KeyClass key;

			// symbol = value
			if (selects[0].op.attrOperator == AttrOperator.aopEQ) {
				if (selects[0].type1.attrType != AttrType.attrSymbol) {
					key = getValueITree(selects[0], selects[0].type1, 1);
					indScan = ((IntervalTreeFile) indFile).new_scan(key, null, 7); // 7 - means contains
				} else {
					key = getValueITree(selects[0], selects[0].type2, 2);
					indScan = ((IntervalTreeFile) indFile).new_scan(key, null, 7);
				}
				return indScan;
			}

			// symbol > value or symbol >= value
			if (selects[0].op.attrOperator == AttrOperator.aopGT || selects[0].op.attrOperator == AttrOperator.aopGE) {
				if (selects[0].type1.attrType != AttrType.attrSymbol) {
					key = getValueITree(selects[0], selects[0].type1, 1);
					indScan = ((IntervalTreeFile) indFile).new_scan(key, null, 7);
				} else {
					key = getValueITree(selects[0], selects[0].type2, 2);
					indScan = ((IntervalTreeFile) indFile).new_scan(key, null, 7);
				}
				return indScan;
			}

			// error if reached here
			System.err.println("Error -- in IndexUtils.BTree_scan()");
			return null;
		} else {
			// selects[1] != null, must be a range query
			if (selects[0].type1.attrType != AttrType.attrSymbol && selects[0].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}
			if (selects[1].type1.attrType != AttrType.attrSymbol && selects[1].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}

			// which symbol is higher??
			intervalTree.KeyClass key1, key2;
			AttrType type;

			if (selects[0].type1.attrType != AttrType.attrSymbol) {
				key1 = getValueITree(selects[0], selects[0].type1, 1);
				type = selects[0].type1;
			} else {
				key1 = getValueITree(selects[0], selects[0].type2, 2);
				type = selects[0].type2;
			}
			if (selects[1].type1.attrType != AttrType.attrSymbol) {
				key2 = getValueITree(selects[1], selects[1].type1, 1);
			} else {
				key2 = getValueITree(selects[1], selects[1].type2, 2);
			}

			switch (type.attrType) {
			case AttrType.attrInterval:
			case AttrType.attrComposite:
				if (IntervalT.keyCompare(((IntervalKey) key1), ((IntervalKey) key2)) < 0) {
					indScan = ((IntervalTreeFile) indFile).new_scan(key1, key2, 7);
				} else {
					indScan = ((IntervalTreeFile) indFile).new_scan(key2, key1, 7);
				}
				return indScan;

			default:
				// error condition
				throw new UnknownKeyTypeException("IndexUtils.java: Only Integer and String keys are supported so far");
			}
		} // end of else

	}
  
  public static compositeTree.IndexFileScan compositeTree_scan(CondExpr[] selects, compositeTree.IndexFile indFile)
		  throws IOException,
		   UnknownKeyTypeException,
		   InvalidSelectionException,
		   KeyNotMatchException,
		   UnpinPageException,
		   PinPageException,
		   IteratorException,
		   ConstructPageException, KeyNotMatchException, IteratorException, ConstructPageException, PinPageException, UnpinPageException, compositeTree.KeyNotMatchException, compositeTree.IteratorException, compositeTree.ConstructPageException, compositeTree.PinPageException, compositeTree.UnpinPageException
	{
	  compositeTree.IndexFileScan indScan;

		if (selects == null || selects[0] == null) {
			indScan = ((compositeTree.IntervalTreeFile) indFile).new_scan(null, null, 7);
			return indScan;
		}

		if (selects[1] == null) {
			if (selects[0].type1.attrType != AttrType.attrSymbol && selects[0].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}

			compositeTree.KeyClass key;

			// symbol = value
			if (selects[0].op.attrOperator == AttrOperator.aopEQ) {
				if (selects[0].type1.attrType != AttrType.attrSymbol) {
					key = getValueCTree(selects[0], selects[0].type1, 1);
					indScan = ((compositeTree.IntervalTreeFile) indFile).new_scan(key, null, 7); // 7 - means contains
				} else {
					key = getValueCTree(selects[0], selects[0].type2, 2);
					indScan = ((compositeTree.IntervalTreeFile) indFile).new_scan(key, null, 7);
				}
				return indScan;
			}

			// symbol > value or symbol >= value
			if (selects[0].op.attrOperator == AttrOperator.aopGT || selects[0].op.attrOperator == AttrOperator.aopGE) {
				if (selects[0].type1.attrType != AttrType.attrSymbol) {
					key = getValueCTree(selects[0], selects[0].type1, 1);
					indScan = ((compositeTree.IntervalTreeFile) indFile).new_scan(key, null, 7);
				} else {
					key = getValueCTree(selects[0], selects[0].type2, 2);
					indScan = ((compositeTree.IntervalTreeFile) indFile).new_scan(key, null, 7);
				}
				return indScan;
			}

			// error if reached here
			System.err.println("Error -- in IndexUtils.BTree_scan()");
			return null;
		} else {
			// selects[1] != null, must be a range query
			if (selects[0].type1.attrType != AttrType.attrSymbol && selects[0].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}
			if (selects[1].type1.attrType != AttrType.attrSymbol && selects[1].type2.attrType != AttrType.attrSymbol) {
				throw new InvalidSelectionException("IndexUtils.java: Invalid selection condition");
			}

			// which symbol is higher??
			compositeTree.KeyClass key1, key2;
			AttrType type;

			if (selects[0].type1.attrType != AttrType.attrSymbol) {
				key1 = getValueCTree(selects[0], selects[0].type1, 1);
				type = selects[0].type1;
			} else {
				key1 = getValueCTree(selects[0], selects[0].type2, 2);
				type = selects[0].type2;
			}
			if (selects[1].type1.attrType != AttrType.attrSymbol) {
				key2 = getValueCTree(selects[1], selects[1].type1, 1);
			} else {
				key2 = getValueCTree(selects[1], selects[1].type2, 2);
			}

			switch (type.attrType) {
			case AttrType.attrInterval:
			case AttrType.attrComposite:
				if (compositeTree.IntervalT.keyCompare(((compositeTree.IntervalKey) key1), ((compositeTree.IntervalKey) key2)) < 0) {
					indScan = ((compositeTree.IntervalTreeFile) indFile).new_scan(key1, key2, 7);
				} else {
					indScan = ((compositeTree.IntervalTreeFile) indFile).new_scan(key2, key1, 7);
				}
				return indScan;

			default:
				// error condition
				throw new UnknownKeyTypeException("IndexUtils.java: Only Integer and String keys are supported so far");
			}
		} // end of else

	}

  private static intervalTree.KeyClass getValueITree(CondExpr cd, AttrType type, int choice)
	       throws UnknownKeyTypeException
	  {
	    // error checking
	    if (cd == null) {
	      return null;
	    }
	    if (choice < 1 || choice > 2) {
	      return null;
	    }

	    switch (type.attrType) {
	    case AttrType.attrInterval:
	      if (choice == 1) return new IntervalKey(cd.operand1.interval);
	      else return new IntervalKey(cd.operand2.interval);
	    case AttrType.attrComposite:
	      if (choice == 1) return new IntervalKey(cd.operand1.composite.interval, cd.operand1.composite.nodeVal);
	      else return new IntervalKey(cd.operand2.composite.interval, cd.operand2.composite.nodeVal);
	    default:
		throw new UnknownKeyTypeException("IndexUtils.java: Only Integer and String keys are supported so far");
	    }

	  }
  
  private static compositeTree.KeyClass getValueCTree(CondExpr cd, AttrType type, int choice)
	       throws UnknownKeyTypeException
	  {
	    // error checking
	    if (cd == null) {
	      return null;
	    }
	    if (choice < 1 || choice > 2) {
	      return null;
	    }

	    switch (type.attrType) {
	    case AttrType.attrInterval:
	      if (choice == 1) return new compositeTree.IntervalKey(cd.operand1.interval);
	      else return new compositeTree.IntervalKey(cd.operand2.interval);
	    case AttrType.attrComposite:
	      if (choice == 1) return new compositeTree.IntervalKey(cd.operand1.composite.interval, cd.operand1.composite.nodeVal);
	      else return new compositeTree.IntervalKey(cd.operand2.composite.interval, cd.operand2.composite.nodeVal);
	    default:
		throw new UnknownKeyTypeException("IndexUtils.java: Only Integer and String keys are supported so far");
	    }

	  }

}
