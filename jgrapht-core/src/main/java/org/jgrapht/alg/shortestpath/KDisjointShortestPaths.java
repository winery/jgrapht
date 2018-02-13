/*
 * (C) Copyright 2018-2018, by Assaf Mizrachi and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg.shortestpath;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;

/**
 * The algorithm determines the k <em>disjoint</em> shortest simple paths in increasing order of
 * weight. Weights can be negative (but no negative cycle is allowed). Only directed simple graphs
 * are allowed. 
 * <br>
 * An undirected graph can be transformed to a directed simple graph using the following
 * transformation:
 * <br>
 * For each pair of vertices V, U and an undirected edge {@literal E:V<-->U} with weight W:
 * <ol>
 * <li> Create vertices V_in, V_out, U_in and U_out
 * <li> Create edges:
 *     <ol>
 *     <li> {@literal V_out-->U_in}, weight = W;
 *  <li> {@literal U_out-->V_in}, weight = W;
 *  <li> {@literal V_in-->V_out}, weight = 0;
 *  <li> {@literal U_in-->U_out}, weight = 0;
 *  </ol>
 * </ol>
 * <p>
 * The algorithm is based on the Suurballe & Tarjan (later extended by Bhandari) algorithm, so is to find an
 * Edge-disjoint shortest paths. It is using Bhandari approach for negating the edges (and assigning negative cost) 
 * of the last found shortest path in each iteration rather than the removing them and modifying the cost of all the
 * edge used by Suurballe.
 * In order to find a Vertex-disjoint shortest paths you may use the following transformation:
 * <br>
 * For each vertex V and a set of incoming edges E_in(V) and outgoing edges E_out(V):
 * <ol>
 * <li> Create vertex V_in
 * <li> For every edge {@literal e:u-->V}, weight=W in E_in(V), create an edge {@literal e_in:u-->V_in}, weight=W
 * <li> Create vertex V_out
 * <li> For every edge {@literal e:V-->u}, weight=W in E_out(V), create an edge {@literal e_out:V_out-->u}, weight=W
 * <li> Create an edge {@literal e_internal:V_in-->V_out} with weight=0.
 * </ol>
 *
 * <p>
 * The algorithm is running k sequential Bellman-Ford iterations to find the shortest path at each step.
 * Hence, yielding a complexity of k*O(Bellman-Ford). Also, note that the provided graph is to be modified
 * (edges and weights) during the path computation.
 * 
 * <p>
 * For further reference see <a href="https://www.nas.ewi.tudelft.nl/people/Fernando/papers/Wiley.pdf">
 * Disjoint Paths in Networks </a> which was the main reference for the code of this class:
 * <ul>
 * Iqbal, F. and Kuipers, F. A. 2015. Disjoint Paths in Networks. Wiley Encyclopedia of Electrical and Electronics Engineering. 1–11.
 * </ul>
 * 
 * @see BellmanFordShortestPath
 *
 * @author Assaf Mizrachi
 * @since February 12, 2018
 * 
 * @param <V> Vertex
 * @param <E> Edge
 */
public class KDisjointShortestPaths<V, E> implements KShortestPathAlgorithm<V, E> {
    /**
     * Graph on which shortest paths are searched.
     */
    private Graph<V, E> graph;

    private List<List<E>> pathList;

    private int nPaths;

    /**
     * Creates an object to calculate k disjoint shortest paths between the start
     * vertex and others vertices.
     *
     * @param graph
     *            graph on which shortest paths are searched. Note: graph will
     *            be modified (edges and weights) during the path computation.
     * @param nPaths
     *            number of disjoint paths between the start vertex and an end
     *            vertex.
     *
     * @throws IllegalArgumentException
     *             if nPaths is negative or 0.
     * @throws IllegalArgumentException 
     *             if the graph is null.
     * @throws IllegalArgumentException 
     *             if the graph is undirected.
     */
    public KDisjointShortestPaths(Graph<V, E> graph, int nPaths) {
        
        GraphTests.requireDirected(graph);           
        if (nPaths <= 0) {
            throw new IllegalArgumentException("nPaths must be positive value");
        }

        this.graph = graph;
        this.nPaths = nPaths;
    }
    
    /**
     * Returns the k shortest simple paths in increasing order of weight.
     *
     * @param startVertex source vertex of the calculated paths.
     * @param endVertex target vertex of the calculated paths.
     *
     * @return list of disjoint paths between the start vertex and the end vertex
     * 
     * @throws IllegalArgumentException if the graph does not contain the startVertex or the
     *         endVertex
     * @throws IllegalArgumentException if the startVertex and the endVertex are the same vertices
     * @throws IllegalArgumentException if the startVertex or the endVertex is null
     */
    @Override
    public List<GraphPath<V, E>> getPaths(V startVertex, V endVertex)
    {
        if (endVertex == null) {
            throw new IllegalArgumentException("endVertex is null");
        }
        if (startVertex == null) {
            throw new IllegalArgumentException("startVertex is null");
        }
        if (endVertex.equals(startVertex)) {
            throw new IllegalArgumentException("The end vertex is the same as the start vertex!");
        }
        if (! this.graph.vertexSet().contains(startVertex)) {
            throw new IllegalArgumentException("graph must contain the start vertex!");
        }
        if (! this.graph.vertexSet().contains(endVertex)) {
            throw new IllegalArgumentException("graph must contain the end vertex!");
        }
        
        this.graph = new AsWeightedGraph<>(graph, new HashMap<>());

        GraphPath<V, E> currentPath;
        this.pathList = new ArrayList<>();
        BellmanFordShortestPath<V, E> bellmanFordShortestPath;
        int cPaths = 1;
        do {
            setUp(cPaths);
            bellmanFordShortestPath = new BellmanFordShortestPath<>(this.graph);
            currentPath = bellmanFordShortestPath.getPath(startVertex, endVertex);
            if (currentPath != null) {
                cPaths++;                
                pathList.add(currentPath.getEdgeList());
            }            
        } while (currentPath != null && cPaths <= this.nPaths);

        return pathList.size() > 0 ? resolvePaths(startVertex, endVertex) : Collections.emptyList();
    }

    /**
     * Prepares the graph for a search of the next path:
     * Replacing the edges of the previous path with reversed edges
     * with negative weight
     * 
     * @param cPath the number of the next path to search 
     */
    private void setUp(int cPath) {
        //no setup for first path
        if (cPath == 1) {
            return;
        }
        
        V source, target;
        E reversedEdge;
        //replace previous path edges with reversed edges with negative weight
        for (E originalEdge : this.pathList.get(cPath - 2)) {
            source = graph.getEdgeSource(originalEdge);
            target = graph.getEdgeTarget(originalEdge);
            graph.removeEdge(originalEdge);    
            reversedEdge = graph.addEdge(target, source);
            graph.setEdgeWeight(reversedEdge, - graph.getEdgeWeight(originalEdge));
        }
    }
    
    /**
     * At the end of the search we have list of intermediate paths - not necessarily
     * disjoint and may contain reversed edges. Here we go over all, removing overlapping
     * edges and merging them to valid paths (from start to end). Finally, we sort
     * them according to their weight.
     * 
     * @param endVertex the end vertex
     * 
     * @return sorted list of disjoint paths from start vertex to end vertex.
     */
    private List<GraphPath<V, E>> resolvePaths(V startVertex, V endVertex) {
        //first we need to remove overlapping edges.        
        removeOverlappingEdges();
        
        //now we might be left with path fragments (not necessarily leading from start to end).
        //We need to merge them to valid paths.
        List<GraphPath<V, E>> paths = mergePaths(startVertex, endVertex);
        
        //sort paths by overall weight (ascending)
        Collections.sort(paths, (o1, o2) -> Double.compare(o1.getWeight(), o2.getWeight()));        
        return paths;
    }
    
    /**
     * After removing overlapping edges, each path is not necessarily connecting
     * start to end vertex. Here we connect the path fragments to valid paths
     * (from start to end).
     * 
     * @param endVertex the end vertex
     * 
     * @return list of disjoint paths from start to end.
     */
    private List<GraphPath<V, E>> mergePaths(V startVertex, V endVertex) {
        List<GraphPath<V, E>> graphPaths = new ArrayList<GraphPath<V, E>>();
        for (int i = 0; i < this.pathList.size(); i++) {            
            V nextHop = startVertex;
            List<E> mergedPath = new ArrayList<E>();
            while (! nextHop.equals(endVertex)) {
                E nextEdge = null;
                //search an edge connecting current and next hops at any of the paths
                //once it is found, add it to the current (merged) path and remove it
                //from the original path so it will not be used anymore.
                for (List<E> path : pathList) {
                    boolean connectionFound = false;
                    Iterator<E> pathIter = path.iterator();
                    while (pathIter.hasNext()) {
                        E edge = pathIter.next();
                        if (graph.getEdgeSource(edge).equals(nextHop) || graph.getEdgeTarget(edge).equals(nextHop)) {
                            nextEdge = edge;
                            //remove edge so it will not be used again in any other path.
                            pathIter.remove();
                            connectionFound = true;
                            break;
                        }
                    }
                    if (connectionFound) {
                        break;
                    }
                }
                if (nextEdge == null) {
                    throw new IllegalArgumentException("Could not find a path from start to end vertex");
                }
                mergedPath.add(nextEdge);
                nextHop = graph.getEdgeSource(nextEdge).equals(nextHop) ? 
                        graph.getEdgeTarget(nextEdge) : graph.getEdgeSource(nextEdge);
            }
            //path is ready, wrap it up.
            graphPaths.add(createGraphPath(mergedPath, startVertex, endVertex));
        }
        return graphPaths;
    }
    
    /**
     * Iterating over all paths to removes overlapping edges (contained
     * in more than single path). At the end of this method, each path
     * contains unique edges but not necessarily connecting the start
     * to end vertex.
     * 
     */
    private void removeOverlappingEdges() {
        Iterator<E> path1Iter, path2Iter;
        E e1, e2;
        boolean found;
        //removing overlapping edges
        for (List<E> path1 : pathList) {
            path1Iter = path1.iterator();
            while (path1Iter.hasNext()) {
                e1 = path1Iter.next();
                found = false;
                for (List<E> path2 : pathList) {
                    if (path2 == path1) {
                        continue;
                    }
                    path2Iter = path2.iterator();
                    while (path2Iter.hasNext()) {
                        e2 = path2Iter.next();
                        //graph is directed, checking both options.
                        if ((graph.getEdgeSource(e1).equals(graph.getEdgeSource(e2)) &&
                                graph.getEdgeTarget(e1).equals(graph.getEdgeTarget(e2))) ||
                                
                                (graph.getEdgeSource(e1).equals(graph.getEdgeTarget(e2)) &&
                                        graph.getEdgeTarget(e1).equals(graph.getEdgeSource(e2)))) {
                            found = true;
                            path2Iter.remove();
                        }
                    }
                }
                if (found) {
                    path1Iter.remove();
                }
            }
        }
        
    }
    
    private GraphPath<V, E> createGraphPath(List<E> edgeList, V startVertex, V endVertex) {
        double weight = 0;
        for (E edge : edgeList) {
            weight += graph.getEdgeWeight(edge);
        }
        return new GraphWalk<>(graph, startVertex, endVertex, edgeList, weight);
    }
    
}
