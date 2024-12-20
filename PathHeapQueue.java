package edu.uwm.cs351;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import edu.uwm.cs351.util.WeightedPath;

/**
 * A priority queue of weighted paths in a graph,
 * where the minimum weight path is the first to come out.
 */
public class PathHeapQueue<V> extends AbstractQueue<WeightedPath<V>> {
	private final List<WeightedPath<V>> heap = new ArrayList<>(); // The only field!

	/**
	 * Return the parent index of this index, or -1 if there is no parent
	 * @param x index in the heap
	 * @return index of parent in heap
	 */
	private static int parent(int x) {
		if (x == 0) return -1;
		return (x - 1)/2;
	}

	/**
	 * Return the index of the first (left) child of this node in the heap.
	 * The second (right) child index will be one greater.
	 * @param x index of node
	 * @return index of left child.
	 */
	private static int child(int x) {
		return (2*x) + 1;
	}

	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
	
	/**
	 * Used to report an error found when checking the invariant.
	 * By providing a string, this will help debugging the class if the invariant should fail.
	 * @param error string to print to report the exact error found
	 * @return false always
	 */
	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}
	
	private boolean wellFormed() {
		// (1) The heap cannot contain any nulls
		for (int i = 0; i < heap.size(); i++)
			if (heap.get(i) == null) return report("heap cannot contain any nulls");
		
		// (2) Every path in the heap must have weight no less than
		//     the weight of the path as its parent (if any)
		for (int in = 1; in < heap.size(); in++) {
			double thisElement = WeightedPath.weight(heap.get(in));
			double parent = WeightedPath.weight(heap.get(parent(in)));
			if (thisElement < parent) return report("this element's weight cannot be less than parent's weight");
		}
		
		return true;
	}
	
	private PathHeapQueue(boolean ignored) {} // do not change this construct -- used by Spy
	
	public PathHeapQueue() {
		assert wellFormed() : "invariant failed at end of constructor";
	}
	
	@Override
	public int size() {
		assert wellFormed() : "invariant failed in size()";
		return heap.size();
	}
	
	/*
	 * 	swap the elements at two different indicies
	 *  @param x index of element to swap
	 *  @param y index of element to swap (focus element)
	 *  @return index of focus element y
	 */
	private int swap(int x, int y) {
		WeightedPath<V> save = heap.get(x);
		heap.set(x, heap.get(y));
		heap.set(y, save);
		return y;
	}

	@Override
	public boolean offer(WeightedPath<V> e) {
		assert wellFormed() : "invariant failed in offer()";
		
		heap.add(e);
		if (heap.size() == 1) return true;
		boolean spotFound = false;
		int thisIndex = heap.size() - 1;
		int parentIndex;
		
		while (!spotFound) {
			if (thisIndex == 0) break;
			parentIndex = parent(thisIndex);
			if (WeightedPath.weight(e) < WeightedPath.weight(heap.get(parentIndex))) 
				thisIndex = swap(thisIndex, parentIndex);
			else spotFound = true;
		}
		
		assert wellFormed() : "invariant broken by offer()";
		return true;
	}

	@Override
	public WeightedPath<V> poll() {
		assert wellFormed() : "invariant failed in poll()";
		if (heap.isEmpty()) return null;
		if (heap.size() == 1) return heap.remove(0);
		
		WeightedPath<V> removed = heap.get(0);
		heap.set(0, heap.get(heap.size() - 1));
		heap.remove(heap.size() - 1);
		if (heap.size() == 1) return removed;
		
		boolean spotFound = false;
		int focusIndex = 0;	
		int left, right, moreAngry;
		
		while(!spotFound) {
			left = child(focusIndex);
			right = child(focusIndex) + 1;
			
			// both children exist
			if (left < heap.size() && right < heap.size()) {
				if (WeightedPath.weight(heap.get(left)) < WeightedPath.weight(heap.get(right))) moreAngry = left;
				else moreAngry = right;
			}
			// only left child or neither of the children exist
			else if (left >= heap.size() && right >= heap.size()) break;
			else moreAngry = left;
			
			// swap if necessary or end loop
			if (WeightedPath.weight(heap.get(focusIndex)) > WeightedPath.weight(heap.get(moreAngry)))
				focusIndex = swap(focusIndex, moreAngry);
			else spotFound = true;
		}
		
		assert wellFormed() : "invariant broken by poll()";
		return removed;
	}

	@Override
	public WeightedPath<V> peek() {
		assert wellFormed() : "invariant failed in peek()";
		if (heap.isEmpty()) return null;
		return heap.get(0);
	}

	@Override
	public Iterator<WeightedPath<V>> iterator() {
		return Collections.unmodifiableCollection(heap).iterator();
	}

}
