//package pa2;

/**
 * 
 * @author Lige Liu, Zhanghao Wen
 *
 */
public class PriorityQ {

	private Node[] queue;
	private int size;
	
	public PriorityQ() {
		queue = new Node[1];
		size = 0;
	}
	
	/**
	 * Adds a string s with priority p to the priority queue.
	 * 
	 * @param s String
	 * @param p Priority
	 */
	public void add(String s, int p) {
		Node node = new Node(p, s);
		if (size >= queue.length-1)
			doubleArray();
		
		queue[++size] = node;
		heapifyUp(size);
		
	}
	
	/**
	 * returns a string whose priority is maximum.
	 * 
	 * @return
	 */
	public String returnMax() {
		if (!isEmpty())
			return queue[1].getValue();
		else
			return null;
	}
	
	/**
	 * returns a string whose priority is maximum and removes it from the priority queue.
	 * 
	 * @return
	 */
	public String extractMax() {
		if (isEmpty()) {
			return null;
		}
		
		Node max = queue[1];
		remove(1);
		return max.getValue(); 
	}
	
	/**
	 * removes the element from the priority queue whose array index is i.
	 * 
	 * @param i index
	 * @return
	 */
	public boolean remove(int i) {
		if (!isEmpty() && i > size || i < 0)
			return false;
		swap(i, size);
		queue[size--] = null;
		
		heapifyDown(i);
		return true;
	}
	
	/**
	 * Decrements the priority of the ith element by k.
	 * 
	 * @param i
	 * @param k
	 * @return
	 */
	public boolean decrementPriority(int i, int k) {
		if (!isEmpty() && i<1 || i>size)
			return false;
		
		int key = queue[i].getKey();
		queue[i].setKey(key - k);
		
		heapifyDown(i);
		return true;
	}
	
	/**
	 * returns an array B with the following property: B[i] = key(A[i]) 
	 * for all i in the array A used to implement the priority queue.
	 * 
	 * @return
	 */
	public int[] priorityArray() {
		if (isEmpty()) {
			return null;
		}
		int[] result = new int[size + 1];
		for (int i=1; i<=size; i++) {
			result[i] = queue[i].getKey();
		}
		
		return result;
	}
	
	/**
	 * Returns key(A[i]), where A is the array used to represent the priority queue
	 * 
	 * @param i
	 * @return
	 */
	public int getKey(int i) {
		if (i<1 || i>size)
			return -1;
		return queue[i].getKey();
	}
	
	/**
	 * Returns value(A[i]), where A is the array used to represent the priority queue
	 * 
	 * @param i
	 * @return
	 */
	public String getValue(int i) {
		if (i<1 || i>size)
			return null;
		return queue[i].getValue();
	}
	
	/**
	 * Return true if and only if the queue is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	
	/**
	 * Helper method for percolating up
	 * 
	 * @param index
	 */
	private void heapifyUp(int index) {
		if (index <= 1)
			return;
		
		int parent = index / 2;
		if(queue[index].getKey() > queue[parent].getKey()) {
			swap(index, parent);
			heapifyUp(parent);
		}
	}
	
	/**
	 * Helper method for percolating down
	 * @param index
	 */
	private void heapifyDown(int index) {
		int child1 = index*2 > size ? -1 : index*2;
		int child2 = index*2+1 > size ? -1 :index*2+1;
		int max = -1;
		if (child1 == -1 && child2 == -1)
			return;
		
		if (child1 != -1 && child2 == -1) {
			max = child1;
		}
		else if(child1 == -1 && child2 != -1) {
			max = child2;
		}
		else {
			if (queue[child1].getKey() < queue[child2].getKey())
				max = child2;
			else
				max = child1;
		}
		if (queue[index].getKey() < queue[max].getKey()) {
			swap(index, max);
			heapifyDown(max);
		}
	}
	
	/**
	 * Doubling the size of array
	 */
	private void doubleArray() {
		Node[] temp = new Node[queue.length*2];
		for (int i=0; i<=size; i++) {
			temp[i] = queue[i];
		}
		
		queue = temp;
	}
	
	/**
	 * Swap the position of two elements
	 * 
	 * @param index1
	 * @param index2
	 */
	private void swap(int index1, int index2) {
		Node temp = queue[index1];
		queue[index1] = queue[index2];
		queue[index2] = temp;
	}
	
	/**
	 * Inner class
	 * Construct a new data structure.
	 * 
	 * @author Lige Liu, Zhanghao Wen
	 *
	 */
	class Node{
		private int key;
		private String value;
		
		public Node(int key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public int getKey() {
			return key;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setKey(int key) {
			this.key = key;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
	}
}
