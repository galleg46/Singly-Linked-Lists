// This is an assignment for students to complete after reading Chapter 4 of
// "Data Structures and Other Objects Using Java" by Michael Main.

package edu.uwm.cs351;

import java.awt.Color;

import junit.framework.TestCase;


/******************************************************************************
 * This class is a homework assignment;
 * A ParticleSeq is a collection of Particles.
 * The sequence can have a special "current element," which is specified and 
 * accessed through four methods
 * (start, getCurrent, advance and isCurrent).
 *
 ******************************************************************************/
public class ParticleSeq implements Cloneable
{
	// Declare the private static Node class.
	// It should have a constructor but no methods.
	// The fields of Node should have "default" access (neither public, nor private)
	// and should not start with underscores.
	private static class Node
	{
		Particle data;
		Node next;
		
		Node(Particle p, Node n)
		{
			data = p;
			next = n;
		}
	}

	// The data structure:
	private int _manyNodes;
	private Node _head;
	private Node _precursor, _cursor;
	private boolean _atEnd;
	
	
	private static boolean _doReport = true; // used only by invariant checker
	
	/**
	 * Used to report an error found when checking the invariant.
	 * By providing a string, this will help debugging the class if the invariant should fail.
	 * @param error string to print to report the exact error found
	 * @return false always
	 */
	private boolean _report(String error) {
		if (_doReport) System.out.println("Invariant error found: " + error);
		return false;
	}

	/**
	 * Check the invariant. 
	 * Return false if any problem is found.  Returning the result
	 * of {@link #_report(String)} will make it easier to debug invariant problems.
	 * @return whether invariant is currently true
	 */
	private boolean _wellFormed() {
		// Invariant:
		// 1. list must not include a cycle.
		if (_head != null)
		{
			// This check uses an interesting property described by Guy Steele (CLtL 1987)
			Node fast = _head.next;
			for (Node p = _head; fast != null && fast.next != null; p = p.next) 
			{
				if (p == fast) return _report("list is cyclic!");
				fast = fast.next.next;
			}
		}
		
		// 2. manyNodes is number of nodes in list
		int count = 0;
		boolean foundPrecursor = false;
		for(Node c = _head; c != null; c = c.next)
		{
			if(c == _precursor) foundPrecursor = true;
			++count;
		}
		if(count != _manyNodes) return _report("manynodes is: " +_manyNodes +" does not equal the number of nodes: " +count);
		
		// 3. precursor is either null or points to a node in the list.
		if(_precursor != null && !foundPrecursor) return _report("precursor is not in the list");
		
		// 4. if cursor != precursor, then
		//    if precursor is null, cursor is head, otherwise cursor is precursor.next;
		if(_cursor != _precursor)
		{
			if(_precursor == null && _cursor != _head) return _report("cursor is not the head");
			if(_precursor != null && _cursor != _precursor.next) return _report("cursor is not after precursor");
		}
		
		// 5. if _atEnd is true then
		//        cursor must be null
		//        if precursor is null, then so must head
		if(_atEnd == true)
		{
			if(_cursor != null) return _report("we are at the end, cursor should be null");
			if(_precursor == null && _head != null) return _report("precursor is null. Head should also be null and it is not");
		}
		
		// If no problems found, then return true:
		return true;
	}

	private ParticleSeq(boolean doNotUse) {} // only for purposes of testing, do not change
	
	/**
	 * Create an empty sequence of particles.
	 * @param - none
	 * @postcondition
	 *   This sequence of particles is empty 
	 **/   
	public ParticleSeq( )
	{
		_manyNodes = 0;
		_head = _cursor = _precursor = null;
		_atEnd = true;
		
		assert _wellFormed() : "invariant failed at end of constructor";
	}


	/**
	 * Determine the number of elements in this sequence.
	 * @param - none
	 * @return
	 *   the number of elements in this sequence
	 **/ 
	public int size( )
	{
		assert _wellFormed() : "invariant wrong at start of size()";
		return _manyNodes;
		// This method shouldn't modify any fields, hence no assertion at end
	}

	/**
	 * Return true if we are at the end of the sequence.
	 * This can happen in one of three ways:
	 * <ol>
	 * <li> The sequence is empty.
	 * <li> We advanced from the last element.
	 * </ol>
	 * @precondition true
	 * @return true if at the end of the sequence
	 */
	public boolean atEnd() {
		assert _wellFormed() : "Invariant failed at start of atEnd";
		
		return _atEnd;
	}

	/**
	 * Set the current element at the front of this sequence.
	 * @param - none
	 * @postcondition
	 *   The front element of this sequence is now the current element, but 
	 *   if this sequence has no elements at all, then there is no current 
	 *   element and we are at the end.
	 **/ 
	public void start( )
	{
		assert _wellFormed() : "invariant wrong at start of start()";

		_cursor = _head;
		_precursor = null;
		
		if(_head == null)
		{
			_atEnd = true;
		}
		else
		{
			_atEnd = false;
		}
		
		assert _wellFormed() : "invariant wrong at end of start()";
	}

	/**
	 * Accessor method to determine whether this sequence has a specified 
	 * current element that can be retrieved with the 
	 * getCurrent method. 
	 * @param - none
	 * @return
	 *   true (there is a current element) or false (there is no current element at the moment)
	 **/
	public boolean isCurrent( )
	{
		assert _wellFormed() : "invariant wrong at start of getCurrent()";
		
		return _precursor != _cursor && _cursor != null;
		// This method shouldn't modify any fields, hence no assertion at end
	}

	/**
	 * Accessor method to get the current element of this sequence. 
	 * @param - none
	 * @precondition
	 *   isCurrent() returns true.
	 * @return
	 *   the current element of this sequence
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   getCurrent may not be called.
	 **/
	public Particle getCurrent( )
	{
		assert _wellFormed() : "invariant wrong at start of getCurrent()";
		
		if(!isCurrent()) throw new IllegalStateException("no current to remove");
		
		return _cursor.data;
		// This method shouldn't modify any fields, hence no assertion at end
	}

	/**
	 * Move forward, so that the current element is now the next element in
	 * this sequence.
	 * @param - none
	 * @precondition
	 *   atEnd() returns false. 
	 * @postcondition
	 *   If the current element (even if it was removed)
	 *   was already the last element of this sequence,
	 *   then there is no longer any current element. 
	 *   Otherwise, the new current element is the element immediately after the 
	 *   previous current element (whether or not it has been removed).
	 * @exception IllegalStateException
	 *   Indicates that we are at the end so 
	 *   advance may not be called.
	 **/
	public void advance( )
	{
		assert _wellFormed() : "invariant wrong at start of advance()";
		
		if(atEnd() == true) throw new IllegalStateException("advancing past the end");
		
		_precursor = _cursor;
		_cursor = _cursor == null ? _head : _cursor.next;
		
		if(_cursor == null) _atEnd = true;
		
		assert _wellFormed() : "invariant wrong at end of advance()";
	}

	/**
	 * Add a new element to this sequence, before the current element (if any).
	 * @param element
	 *   the new element that is being added, it is allowed to be null
	 * @postcondition
	 *   The element has been added to this sequence. If there was
	 *   a current element, then the new element is placed before the current
	 *   element. If there was no current element, then the new element is placed
	 *   where the removed element was, or at the end. In all cases, the new element becomes the
	 *   new current element of this sequence. 
	 **/
	public void addBefore(Particle element)
	{
		assert _wellFormed() : "invariant wrong at start of addBefore";
		
		if(_precursor == null)
		{
			_cursor = _head = new Node(element, _head);
		}
		else
		{
			_cursor = _precursor.next = new Node(element, _precursor.next);
		}
		
		++_manyNodes;
		_atEnd = false;
		
		assert _wellFormed() : "invariant wrong at end of addBefore";
	}
	
	/**
	 * Add a new element to this sequence, after the current element if any.
	 * @param element
	 *   the new element that is being added, may be null
	 * @postcondition
	 *   The element has been added to this sequence. If there was
	 *   a current element, then the new element is placed after the current
	 *   element. If there was no current element, then the new element is placed
	 *   where the element was, or at the end of the sequence.
	 *   In all cases, the new element becomes the
	 *   new current element of this sequence. 
	 **/
	public void addAfter(Particle element) {
		assert _wellFormed() : "invariant wrong at start of addAfter";
		
		if(isCurrent())
		{
			_precursor = _cursor;
			_cursor = _cursor.next = new Node(element, _cursor.next);
			++_manyNodes;
			_atEnd = false;
		}
		else
		{
			addBefore(element);
		}
		
		assert _wellFormed() : "invariant wrong at end of addAfter";
	}

	/**
	 * Remove the current element from this sequence.
	 * @param - none
	 * @precondition
	 *   isCurrent() returns true.
	 * @postcondition
	 *   The current element has been removed from this sequence. and now
	 *   there is no current element.  The sequence will <em>not</em> be
	 *   at the end, even if the current element had been at the end.
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   removeCurrent may not be called. 
	 **/
	public void removeCurrent( )
	{
		assert _wellFormed() : "invariant wrong at start of removeCurrent()";
		
		if(!isCurrent()) throw new IllegalStateException("no current to remove");
		
		if(_precursor == null)
		{
			_head = _head.next;
		}
		else
		{
			_precursor.next = _precursor.next.next;
		}

		_cursor = _precursor;
		--_manyNodes;
		
		assert _wellFormed() : "invariant wrong at end of removeCurrent()";
	}

	/**
	 * Place the contents of another sequence at the end of this sequence.
	 * @param addend
	 *   a sequence whose contents will be placed at the end of this sequence
	 * @precondition
	 *   The parameter, addend, is not null. 
	 * @postcondition
	 *   The elements from addend have been placed at the end of 
	 *   this sequence. The current element of this sequence if any,
	 *   remains unchanged.   The addend is unchanged.
	 *   Whether we are at the end is unchanged.
	 * @exception NullPointerException
	 *   Indicates that addend is null. 
	 **/
	public void addAll(ParticleSeq addend)
	{
		assert _wellFormed() : "invariant wrong at start of addAll";
		assert addend._wellFormed() : "invariant of parameter wrong at start of addAll";
		
		if(addend.size() == 0) return;
	
		ParticleSeq clone = addend.clone();
		Node last = _head;
		
		if(_head == null)
		{
			_head = clone._head;
		}
		else
		{
			while(last.next != null)
			{
				last = last.next;
			}
			last.next = clone._head;
		}
		
		if(_atEnd)
		{
			last = clone._head;
			while(last.next != null)
			{
				last = last.next;
			}
			_precursor = last;
		}
		else if(_precursor == last && _precursor != _cursor) 
		{
			_cursor = clone._head;
		}
		
		clone._head = clone._precursor = null; //defensive programming
		
		_manyNodes += addend._manyNodes;
		
		assert _wellFormed() : "invariant wrong at end of addAll";
		assert addend._wellFormed() : "invariant of parameter wrong at end of addAll";
	}   


	/**
	 * Generate a copy of this sequence.
	 * @param - none
	 * @return
	 *   The return value is a copy of this sequence. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 *   Whatever was current in the original object is now current in the clone.
	 * @exception OutOfMemoryError
	 *   Indicates insufficient memory for creating the clone.
	 **/ 
	public ParticleSeq clone( )
	{  	 
		assert _wellFormed() : "invariant wrong at start of clone()";

		ParticleSeq result;

		try
		{
			result = (ParticleSeq) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  
			// This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}

		if(_head != null)
		{
			Node newHead = null;
			Node prev = newHead;
			for(Node p = _head; p != null; p = p.next)
			{
				Node copy = new Node(p.data, null);
				if(newHead == null) newHead = copy;
				else prev.next = copy;
				
				prev = copy;
				
				if(p == _precursor) result._precursor = copy;
				if(p == _cursor) result._cursor = copy;
			}
			
			result._head = newHead;
		}

		assert _wellFormed() : "invariant wrong at end of clone()";
		assert result._wellFormed() : "invariant wrong for result of clone()";
		return result;
	}

	
	public static class TestInvariantChecker extends TestCase {
		ParticleSeq hs = new ParticleSeq(false);
		Particle h1 = new Particle(new Point(1,1), new Vector(1,1),1,Color.BLUE);
		Particle h2 = new Particle(new Point(2,2), new Vector(2,2), 2, Color.BLACK);
		
		@Override
		protected void setUp() {
			hs = new ParticleSeq(false);
			hs._atEnd = false;
			_doReport = false;
		}
		
		public void test0() {
			hs._manyNodes = 1;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 0;

			_doReport = true;
			hs._atEnd = false;
			assertTrue(hs._wellFormed()); // it appears only element was removed
			hs._atEnd = true;
			assertTrue(hs._wellFormed()); // actually at end
		}
		
		public void test1() {
			hs._head = new Node(h1,null);
			assertFalse(hs._wellFormed());
			hs._manyNodes = 2;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 1;

			_doReport = true;
			assertTrue(hs._wellFormed());
		}
		
		public void test2() {
			hs._head = new Node(h1,null);
			hs._manyNodes = 1;
			hs._precursor = new Node(h1,null);
			assertFalse(hs._wellFormed());
			hs._precursor = new Node(h2,hs._head);
			hs._cursor = hs._head;
			assertFalse(hs._wellFormed());
			hs._precursor = hs._head;

			_doReport = true;
			assertTrue(hs._wellFormed());
		}
		
		public void test3() {
			hs._head = new Node(h1,null);
			hs._head.next = hs._head;
			hs._manyNodes = 1;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 2;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 3;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 0;
			assertFalse(hs._wellFormed());
			hs._manyNodes = -1;
			assertFalse(hs._wellFormed());
		}
		
		public void test5() {
			hs._head = new Node(h1,null);
			hs._head = new Node(h2,hs._head);
			hs._head = new Node(null,hs._head);
			hs._head = new Node(h2,hs._head);
			hs._head = new Node(h1,hs._head);
			hs._manyNodes = 1;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 2;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 3;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 4;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 0;
			assertFalse(hs._wellFormed());
			hs._manyNodes = -1;
			assertFalse(hs._wellFormed());
			hs._manyNodes = 5;

			_doReport = true;
			assertTrue(hs._wellFormed());
		}
		
		public void test6() {
			Node n1,n2,n3,n4,n5;
			hs._head = n5 = new Node(h1,null);
			hs._head = n4 = new Node(h2,hs._head);
			hs._head = n3 = new Node(null,hs._head);
			hs._head = n2 = new Node(h2,hs._head);
			hs._head = n1 = new Node(h1,hs._head);
			hs._manyNodes = 5;

			hs._precursor = new Node(h1,null);
			assertFalse(hs._wellFormed());
			hs._precursor = new Node(h1,n1);
			hs._cursor = n1;
			assertFalse(hs._wellFormed());
			hs._precursor = new Node(h1,n2);	
			hs._cursor = n2;
			assertFalse(hs._wellFormed());
			hs._precursor = new Node(h2,n3);
			hs._cursor = n3;
			assertFalse(hs._wellFormed());
			hs._precursor = new Node(null,n4);
			hs._cursor = hs._precursor;
			assertFalse(hs._wellFormed());
			hs._precursor = new Node(h2,n5);
			hs._cursor = n5;
			assertFalse(hs._wellFormed());
			
			_doReport = true;
			hs._precursor = n1;
			hs._cursor = n1;
			assertTrue(hs._wellFormed());
			hs._precursor = n2;
			hs._cursor = n3;
			assertTrue(hs._wellFormed());
			hs._precursor = n3;
			assertTrue(hs._wellFormed());
			hs._precursor = n4;
			hs._cursor = n5;
			assertTrue(hs._wellFormed());
			hs._precursor = n5;
			assertTrue(hs._wellFormed());
		}
		
		public void test7() {
			Node n1,n2,n3,n4,n5;
			hs._head = n5 = new Node(h1,null);
			hs._head = n4 = new Node(h2,hs._head);
			hs._head = n3 = new Node(null,hs._head);
			hs._head = n2 = new Node(h2,hs._head);
			hs._head = n1 = new Node(h1,hs._head);
			hs._manyNodes = 5;

			hs._precursor = n1;
			hs._cursor = n3;
			assertFalse(hs._wellFormed());
			hs._cursor = n4;
			assertFalse(hs._wellFormed());
			hs._cursor = n5;
			assertFalse(hs._wellFormed());
			hs._cursor = null;
			assertFalse(hs._wellFormed());
			
			hs._precursor = n3;
			hs._cursor = n1;
			assertFalse(hs._wellFormed());
			hs._cursor = n2;
			assertFalse(hs._wellFormed());
			hs._cursor = n5;
			assertFalse(hs._wellFormed());
			hs._cursor = null;
			assertFalse(hs._wellFormed());
		}
		
		public void test8() {
			Node n2 = new Node(h1,null);
			Node n1 = new Node(h1,n2);
			hs._manyNodes = 2;
			hs._head = n1;
			
			hs._precursor = null;
			hs._cursor = n2;
			assertFalse(hs._wellFormed());
			
			_doReport = true;
			
			hs._cursor = null;
			assertTrue(hs._wellFormed());
			
			hs._cursor = n1;
			assertTrue(hs._wellFormed());
		}
		
		public void test9() {
			Node n2 = new Node(h1,null);
			Node n1 = new Node(h1,n2);
			hs._manyNodes = 2;
			hs._head = n1;

			hs._atEnd = true;
			assertFalse(hs._wellFormed()); // we are at the beginning,not end
			
			hs._precursor = n1;
			hs._cursor = n2;
			assertFalse(hs._wellFormed()); // we have a current value, we can't be at end
			
			hs._cursor = n1;
			assertFalse(hs._wellFormed()); // the current was removed, we're not at end

			hs._precursor = n2;
			hs._cursor = n2;
			assertFalse(hs._wellFormed()); // the current was removed, we're not at end
			
			_doReport = true;
			hs._cursor = null;
			assertTrue(hs._wellFormed()); // we're at the end
		}
	}
}

