package edu.uwm.cs351;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import edu.uwm.cs351.util.DirectedGraph;
import edu.uwm.cs351.util.WeightedEdge;
import edu.uwm.cs351.util.WeightedPath;

/**
 * A class to find shortest paths in a graph.
 * @param T graph element type
 */
public class FindPath<T> {
	private DirectedGraph<T,? extends WeightedEdge<T>> graph;
	
	/**
	 * Set up the algorithm to work on the given graph.
	 * @param g graph to use, must not be null
	 */
	public FindPath(DirectedGraph<T,? extends WeightedEdge<T>> g) {
		graph = g;
	}

	/**
	 * Return a shortest instance path between two vertices in the graph.
	 * This algorithm works if there are no negative distances in the graph.
	 * @param v1 starting vertex
	 * @param v2 ending vertex
	 * @return path from the first to the second as a weighted path, or null
	 * if the two vertices are the same (the empty path)
	 * @throws NoSuchElementException if no path can be found
	 * @throws IllegalArgumentException if v1 or v2 is not in the graph
	 */
	public WeightedPath<T> shortestPath(T v1, T v2) throws NoSuchElementException {		
		if (!graph.containsVertex(v2) || !graph.containsVertex(v1)) throw new IllegalArgumentException("not in graph");
		if (v1 == null || v2 == null || v1.equals(v2)) return null;
		
		PathHeapQueue<T> pq = new PathHeapQueue<>();
		Set<T> notYetVisited = new HashSet<>(graph.vertexSet());
		
		addToPQ(graph.getConnected(v1), notYetVisited, pq, null);		
		notYetVisited.remove(v1);
		T currentLocation = null;
		WeightedPath<T> currentPath = null,
						done = null;
		
		while (!notYetVisited.isEmpty()) {
			if (pq.isEmpty()) break;
			currentPath = pq.peek();
			currentLocation = currentPath.getEnd();
			if (v2.equals(currentLocation)) {
				done = currentPath;
				break;
			}
			addToPQ(graph.getConnected(currentLocation), notYetVisited, pq, currentPath);
			notYetVisited.remove(currentLocation);
			pq.remove();
		}
		if (done != null) return done;
		throw new NoSuchElementException("no path found"); // no path found
	}
	
	/*
	 *  add all edges going from current location to the priority queue
	 *  @param edges - the Set of WeightedEdges going from given current location
	 *  @param notYetVisited - a set of locations(verticies) not yet visited
	 *  @param pq - the priority queue in use
	 *  @param currentPath - the path to be extended
	 */
	private void addToPQ(Set<? extends WeightedEdge<T>> edges, Set<T> notYetVisited, PathHeapQueue<T> pq, WeightedPath<T> currentPath) {
		for (WeightedEdge<T> toDestination : edges) {
			if (notYetVisited.contains(toDestination.getSink())) {
				pq.offer(new WeightedPath<T>(currentPath, toDestination));
			}
		}
	}
}
