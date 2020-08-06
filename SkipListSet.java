import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;

/*
 * 
 * Name: Kensal J Ramos Andino
 * 
 * Description: This program will implement a skip list as a genericized SkipListSet<T> collection in Java. The class must implement SortedSet<T>
 * 				(and hence Set<T>, Collection<T>, and Iterable<T>)
 * 				
 * 				Skip-List behaviors are as follow:
 * 					* Randomized BST
 * 					* Height needs to be adjusted when number of "nodes" encompasses a range of another power of 2
 * 					* Make sure to set minimum height so no silly thrashing of small lists
 * 
 * Required Methods/Classes:
 * 				* class SkipListSet<T>
 * 				* 
 * 
 * Notes:
 * 				* Extends - Creating a subclass of the base class you are extending
 * 				* Implements - means you are using the elements of an interface in your class
 * 
 */


public class SkipListSet<T  extends Comparable<T>> implements SortedSet<T> {
	
	private int lvl;
	private int maxLvl;
	private Random randomNum; // random
	private int size;
	private SkipListSetItem<T> head;
	private SkipListSetItem<T> tail;
	
	private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

		/*
		 * In order to iterate over all the items we must traverse all the way down and then go right.
		 */
		
		SkipListSetItem<T> iterNode = new SkipListSetItem<T>();
		 
		public SkipListSetIterator(SkipListSetItem<T> itHead) {
			
			iterNode = itHead;
			
			// Go all the way down
			while (iterNode.down != null) {
				iterNode = iterNode.down;
			}
			
		}
		
		@Override
		public boolean hasNext() {
			
			if (iterNode.right.key != SkipListSetItem.posInf)
				return true;
			else
				return false;	
		}

		@Override
		public T next() {
			
			iterNode = iterNode.right;
			
			return iterNode.getData();
		}
		 
		@Override
		public void remove() {
				
			SkipListSetItem<T> temp = iterNode;
			iterNode = iterNode.left;
					
			// Cut connections
			temp.left.right = temp.right;
			temp.right.left = temp.left;
			temp.right = null;
			temp.left = null;
			
			while (temp.up != null) {
				temp.setData(null);;
				temp = temp.up;
				temp.down = null;
				temp.left.right = temp.right;
				temp.right.left = temp.left;
				temp.left = null;
				temp.right = null;
			} 
			
		}
		
	}
	
	class SkipListSetItem<E> implements Comparable<E> {

		E data;
		String key;
		SkipListSetItem<E> left, right, up, down;
		static final String posInf = "+oo";
		static final String negInf = "-oo";  
		
		// No data given
		public SkipListSetItem() {
			
		}

		// Data given
		public SkipListSetItem(E dataParam) {

			setData(dataParam);			
			
		}

		public E getData() {
			return data;
		}

		public void setData(E data) {
			this.data = data;
		}

		public SkipListSetItem<E> getLeft() {
			return left;
		}

		public void setLeft(SkipListSetItem<E> left) {
			this.left = left;
		}

		public SkipListSetItem<E> getRight() {
			return right;
		}

		public void setRight(SkipListSetItem<E> right) {
			this.right = right;
		}

		public SkipListSetItem<E> getUp() {
			return up;
		}

		public void setUp(SkipListSetItem<E> up) {
			this.up = up;
		}

		public SkipListSetItem<E> getDown() {
			return down;
		}

		public void setDown(SkipListSetItem<E> down) {
			this.down = down;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@Override
		public int compareTo(Object arg0) {
				if (this.equals(arg0))
					return 1;
				else
					return 0;
		}
	}
	
	public SkipListSet() {

		head = new SkipListSetItem<T>();
		tail = new SkipListSetItem<T>();
		lvl = 0;
		size = 0;
		randomNum = new Random();
		head.right = tail;
		tail.left = head;
		head.key = SkipListSetItem.negInf;
		tail.key = SkipListSetItem.posInf;
	}
	
	public SkipListSetItem<T> getHead() {
		return head;
	}

	public void setHead(SkipListSetItem<T> head) {
		this.head = head;
	}

	public int getLvl() {
		return lvl;
	}
	public void setLvl(int lvl) {
		this.lvl = lvl;
	}
	public int getMaxLvl() {
		return maxLvl;
	}
	public void setMaxLvl(int maxlvl) {
		this.maxLvl = maxlvl;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	/*
	 * Very similar to contains but different return type
	 */
	public SkipListSetItem<T> findNode(T e) {
		SkipListSetItem<T> temp = head;
		SkipListSetItem<T> nodeToFind = new SkipListSetItem<T>(e);
		
		while (true) {
			
			while (temp.right.key != SkipListSetItem.posInf && temp.right.getData().compareTo(nodeToFind.getData()) <= 0) {
				temp = temp.right;
			}
			
			if (temp.down != null) {
				temp = temp.down;
			}
			else
				break;
			
		}
		
		// At this point we have reached the bottom
		return(temp);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#add(java.lang.Object)
	 * This will add a new element to the skiplist. It will look for its correct placement.
	 * Height must be checked afterwards.
	 * If height needs to be re-adjusted, a randomized rebalance is necessary.
	 */
	@Override
	public boolean add(T e) {
		
		SkipListSetItem<T> findP = new SkipListSetItem<T>();
		SkipListSetItem<T> temp = new SkipListSetItem<T>(e);
		
		// Find entry first and return false if found
		findP = findNode(e);
		
		if (findP != head && findP.data != null && findP.getData().compareTo(temp.getData()) == 0) 
			return false;
		
		temp.left = findP;
		temp.right = findP.right;
		findP.right.left = temp;
		findP.right = temp;
		
		int i = 0;
		
		// i = 0
		while (randomNum.nextDouble() < 0.5 && i <= getMaxLvl()) {
			
			if (i >= lvl) 
				alterHead(i + 1);
			
			// Go left until we find a node with an up
			while (findP.up == null) 
				findP = findP.left;
			
			findP = findP.up;
			
			// Now we need to create a new node and make it the next layer of the added data
			SkipListSetItem<T> newLayer = new SkipListSetItem<T>();
			
			// Insert the newLayer node into its correct position
			newLayer.down = temp;
			newLayer.right = findP.right;
			newLayer.left = findP;
			
			// newLayer node is inserted but neighboring nodes are not connected to it.
			findP.right.left = newLayer;
			findP.right = newLayer;
			temp.up = newLayer;
			
			// Now we make our temp go up 1 and restart the while loop if needed
			temp = temp.up;
			temp.setData(e);
			i++;
		}
		
		size++;
		
		if (size > Math.pow(2, lvl)) 
			maxLvl++;
		
		return true;
	}
	
	/*
	 * If we get a newLvl higher than the current lvl we need to alter the head.
	 */
	private void alterHead(int newLvl) {
		
		lvl = newLvl;
		SkipListSetItem<T> temp1 = new SkipListSetItem<T>();
		SkipListSetItem<T> temp2 = new SkipListSetItem<T>();
		
		temp1.right = temp2; // temp1 --- temp2
							// head  --- tail
		temp1.down = head;
		temp2.left = temp1;
		temp2.down = tail;
		
		head.up = temp1;
		tail.up = temp2;
		head = temp1;
		tail = temp2;
		
		head.key = SkipListSetItem.negInf;
		tail.key = SkipListSetItem.posInf;
		
	}
	
	private void incTowerLvl(SkipListSetItem<T> nodeToInc) {
		
		SkipListSetItem<T> current = nodeToInc;
		SkipListSetItem<T> newNode = new SkipListSetItem<T>();
		
		while (current.up == null && current.left != null) 
			current = current.left;
		
		current = current.up;
		
		newNode.left = current;
		newNode.right = current.right;
		newNode.down = nodeToInc;
		
		newNode.setData(nodeToInc.getData());
		
		current.right.left = newNode;
		current.right = newNode;
		
		nodeToInc.up = newNode;
		
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {

		Boolean[] flag = {false};
		
		c.forEach(temp -> {
			flag[0] = flag[0] | add(temp);
		});	
		
		return flag[0];
		
	}

	@Override
	public void clear() {
		
		while (head.down != null) {
			head.right = tail;
			tail.left = head;
			head.up = null;
			tail.up = null;
			head = head.down;
			tail = tail.down;
		}
		
		head.right = tail;
		tail.left = head;
		head.up = null;
		tail.up = null;
		size = 0;
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		
		SkipListSetItem<T> current = new SkipListSetItem<T>((T) o);
		SkipListSetItem<T> nodeFound = new SkipListSetItem<T>();
		
		if (o != null)
			nodeFound = findNode((T) o);
		
		if (nodeFound.getData() != null && nodeFound.getData().equals(current.data)) 
			return true;
		
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		
		Boolean[] flag = {true};

		if (c.size() > 1 && c != null) {
			
			c.forEach(temp -> {
				System.out.println("forEach");
				flag[0] = flag[0] & contains(temp);
			});
			
			return flag[0];
		}
		else if (c.size() != 0 && c != null) {
			flag[0] = flag[0] & contains(c.iterator().next());
			return flag[0];
		}
		
		return false;
	}

	@Override
	public boolean isEmpty() {
		
		if (head.right == tail)
			return true;
		
		return false;
	}

	// it = iterator();
	// it.next();
	
	@Override 
	public Iterator<T> iterator() {
		
		SkipListSetIterator<T> iterator = new SkipListSetIterator<T>(head);
		
		return iterator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		SkipListSetItem<T> temp = findNode((T) o);
		
		if (temp.getData().compareTo((T) o) != 0)
			return false;
		
		// Cut connections
		temp.left.right = temp.right;
		temp.right.left = temp.left;
		temp.right = null;
		temp.left = null;
		
		while (temp.up != null) {
			temp.setData(null);;
			temp = temp.up;
			temp.down = null;
			temp.left.right = temp.right;
			temp.right.left = temp.left;
			temp.left = null;
			temp.right = null;
		} 
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Boolean[] flag = {true};
		
		c.forEach(temp -> {
			flag[0] = flag[0] & remove(temp);
		});
		
		return flag[0];
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// Get the intersection of c and our skiplist and remove everything else
		
		SkipListSetItem<T> headNode =  head;
		Object tempObj;
		boolean[] flag = {false};
		
		while (headNode.down != null)
			headNode = headNode.down;
		
		//nodeArr[0] = headNode;
		if (c.size() > 1 && c != null) {
			for (Object temp : c) {			
				if (!headNode.right.getData().equals(temp)) {
					
					remove(headNode.right.getData());
					flag[0] = true;
					
				}
				else
					headNode = headNode.right;
				
			}
		}
		else if (c.size() != 0 && c != null) {
			
			tempObj = c.iterator().next();
			
			while (headNode.right.getData() != null) {
				if(!headNode.right.getData().equals(tempObj)) {
					remove(headNode.right.getData());
					flag[0] = true;
				}
				else 
					headNode = headNode.right;
				
			}
			
		}
		
		return flag[0];
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Object[] toArray() {
		
		SkipListSetItem<T> temp = head;
		int max = size();
		Object[] skipListArr = new Object[size()];
		
		// Go down...
		while (temp.down != null) 
			temp = temp.down;
		
		for (int i = 0; i < max; i++) {
			
			if (temp.right.getKey() != SkipListSetItem.posInf) {
				skipListArr[i] = temp.right.getData();
				temp = temp.right;
			}
			else 
				break;

		}
		
		return skipListArr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		
		// If it is less, we must create a new array of size, size()
		if (a.length < size()) {
			
			Object[] arr = new Object[size()];
			arr = toArray();
			a = (T[]) arr;
		}
		else if (a.length > size()) {
			
			// Pass in data
			a = (T[]) toArray();
			// Set the index after the last element to null. This will tell the user/program where the array actually ends
			// This must be done since arrays are of fixed size
			a[size()] = null;
		}
		else
			a = (T[]) toArray();
		
		return a;
	}

	@Override
	public Comparator<? super T> comparator() {
		// Natural ordering used
		return null;
	}

	@Override
	public T first() {

		SkipListSetItem<T> temp = head;
		
		while (temp.down != null)
			temp = temp.down;
		
		if (temp.right.getData() != SkipListSetItem.posInf)
			return temp.right.getData();
		else
			return null;
	}

	@Override
	public T last() {
	
		SkipListSetItem<T> temp = tail;
		
		while (temp.down != null)
			temp = temp.down;
		
		if (temp.left.getData() != SkipListSetItem.negInf)
			return temp.left.getData();
		else
			return null;

	}

	public void reBalance() {
		
		SkipListSetItem<T> temp = head;
		SkipListSetItem<T> newLayer = new SkipListSetItem<T>();
		
		// Take advantage of java's automatic trash 
		while (temp.down != null) {
			temp.right = tail;
			tail.left = temp;
			temp = temp.down;
		}
		
		while (temp.right.key != SkipListSetItem.posInf) {
			temp = temp.right;
			
			if (temp.up != null) {
				temp.up.down = null;
				temp.up = null;
			}
			
			int i = 0;
			newLayer = temp;
			while (randomNum.nextDouble() < 0.5 && i <= getMaxLvl()) {

				if (i >= lvl) 
					break;
				
				//System.out.println("In first loop " + i);
				incTowerLvl(newLayer); // increase towerLvl int as well
				
				newLayer = newLayer.up;
				i++;
				
			}
			
		}
		
	}
	
	@Override
	public boolean equals(Object o) {

		if (this == o) 
			return true;
		
		if (o == null)
			return false;
		
		if (o.getClass() != this.getClass())
			return false;
		
		return (this.containsAll((Collection<?>) o));
	}

	// Unsupported Operations
	@Override
	public SortedSet<T> headSet(T toElement) {
		throw new java.lang.UnsupportedOperationException();
	}
	@Override
	public SortedSet<T> subSet(T fromElement, T toElement) {
		throw new java.lang.UnsupportedOperationException();
	}
	@Override
	public SortedSet<T> tailSet(T fromElement) {
		throw new java.lang.UnsupportedOperationException();
	}
}
