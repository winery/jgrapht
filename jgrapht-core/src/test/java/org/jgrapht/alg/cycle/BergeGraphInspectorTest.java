/*
 * (C) Copyright 2016-2018, by Philipp S. Kaesgen and Contributors.
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
package org.jgrapht.alg.cycle;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.SlowTests;
import org.jgrapht.alg.cycle.BergeGraphInspector;
import org.jgrapht.generate.*;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.util.SupplierUtil;

public class BergeGraphInspectorTest extends BergeGraphInspector<Integer,Integer>{
    private SimpleGraph<Integer,Integer> stimulus;
    
    
    
    private void reset(){
        stimulus= new SimpleGraph<Integer,Integer>(SupplierUtil.createIntegerSupplier(),SupplierUtil.createIntegerSupplier(),false);
    }
    
    private boolean verifyCertificate(GraphPath<Integer,Integer> certificate) {
        if (certificate==null) return false;
        Set<Integer> set = new HashSet<Integer>();
        set.addAll(certificate.getVertexList());
        Graph<Integer,Integer> subg = new AsSubgraph<Integer,Integer>(certificate.getGraph(),set);
        return subg.vertexSet().size()==subg.edgeSet().size()&&
                subg.edgeSet().size()==subg.vertexSet().size()&&
                subg.vertexSet().size()%2==1&&
                subg.vertexSet().stream().allMatch(new Predicate<Integer>() {

                    @Override
                    public boolean test(Integer t) {
                        return subg.edgesOf(t).size()==2;
                    }});
            
    }

    private int maximalNumberOfVertices = 17,
            minimalNumberOfVertices = 14;
    
    private int repititionsPerTestCase = 1;
    

    
    @Test
    public void checkPyramid(){
        reset();
                stimulus.addVertex(1);//b1
                stimulus.addVertex(2);//b2
                stimulus.addVertex(3);//b3
                
                stimulus.addEdge(1, 2);//0
                stimulus.addEdge(2, 3);//1
                stimulus.addEdge(3, 1);//2
                
                stimulus.addVertex(4);//s1
                stimulus.addVertex(5);//s2
                stimulus.addVertex(6);//s3
                stimulus.addVertex(7);//a
                
                stimulus.addEdge(4, 7);//3
                stimulus.addEdge(5, 7);//4
                stimulus.addEdge(6, 7);//5
                
                /*optional either
                stimulus.addEdge(7,1);iff 1 in {4,5,6}
                stimulus.addEdge(7,2);iff 2 in {4,5,6}
                stimulus.addEdge(7,3);iff 3 in {4,5,6}
                */
                
                
                stimulus.addVertex(8);//m1
                stimulus.addVertex(9);//m2
                stimulus.addVertex(10);//m3
                
                stimulus.addVertex(11);//S1
                stimulus.addVertex(12);//S2
                stimulus.addVertex(13);//S3
                stimulus.addVertex(14);//T1
                stimulus.addVertex(15);//T2
                stimulus.addVertex(16);//T3
                
                stimulus.addEdge(8, 11);//6
                stimulus.addEdge(11, 4);//7
                stimulus.addEdge(9, 12);//8
                stimulus.addEdge(12, 5);//9
                stimulus.addEdge(10, 13);//10
                stimulus.addEdge(13, 6);//11
                
                stimulus.addEdge(1, 14);//12
                stimulus.addEdge(14, 8);//13
                stimulus.addEdge(2, 15);//14
                stimulus.addEdge(15, 9);//15
                stimulus.addEdge(3, 16);//16
                stimulus.addEdge(16, 10);//17

                assertTrue(containsPyramid(stimulus));
				isBerge(stimulus,true);
                assertTrue(verifyCertificate(getCertificate()));
                
                stimulus.addEdge(8, 2);
				isBerge(stimulus,true);
                assertTrue(verifyCertificate(getCertificate()));
                
                stimulus.removeEdge(8,2);
                stimulus.addEdge(9, 3);

				isBerge(stimulus,true);
                assertTrue(verifyCertificate(getCertificate()));
                
                stimulus.removeEdge(9,3);
                stimulus.addEdge(10, 1);

				isBerge(stimulus,true);
                assertTrue(verifyCertificate(getCertificate()));
                
                stimulus.addEdge(11, 2);
                assertFalse(containsPyramid(stimulus));

                
    }
    
    @Test
    public void checkJewel(){
        reset();
        
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(2, 3);
        stimulus.addEdge(3, 4);
        stimulus.addEdge(4, 5);
        stimulus.addEdge(5, 1);
        
        /*
            non-edges:
            v1v3
            v2v4
            v1v4
        */

        stimulus.addVertex(6);
        stimulus.addVertex(7);
        stimulus.addVertex(8);
        
        stimulus.addEdge(1, 6);
        stimulus.addEdge(6, 7);
        stimulus.addEdge(7, 8);
        stimulus.addEdge(8, 4);
        
        assertTrue(containsJewel(stimulus));
        isBerge(stimulus,true);
        assertTrue(verifyCertificate(getCertificate()));
        
        
        stimulus.addEdge(1, 3);
        assertFalse(containsJewel(stimulus));
        
    }
    
    @Test
    public void checkIsYXComplete(){
        reset();
        
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        
        stimulus.addEdge(1, 4);
        stimulus.addEdge(1, 2);
        stimulus.addEdge(1, 3);
        Set<Integer> X = new HashSet<Integer>();
        X.add(2);
        X.add(3);
        X.add(4);
        assertTrue(isYXComplete(stimulus,1,X));
        
        stimulus.removeEdge(1,4);
        assertFalse(isYXComplete(stimulus,1,X));
        stimulus.addEdge(1, 4);
        
        X.clear();
        X.add(2);
        X.add(1);
        assertFalse(isYXComplete(stimulus,3,X));
        
    }
    
    @Test
    public void checkConfigurationType2(){
        reset();
                
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);//p1
        stimulus.addVertex(6);//x
        stimulus.addVertex(7);//p2=P*
        stimulus.addVertex(8);//p3
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(2, 3);
        stimulus.addEdge(3, 4);
        
        stimulus.addEdge(1, 6);
        stimulus.addEdge(2, 6);
        stimulus.addEdge(4, 6);
        
        stimulus.addEdge(1, 5);
        stimulus.addEdge(5, 7);
        stimulus.addEdge(7, 8);
        stimulus.addEdge(4, 8);
        
        assertTrue(hasConfigurationType2(stimulus));
        

        stimulus.addEdge(3, 6);
        assertTrue(hasConfigurationType2(stimulus));
        
        
        stimulus.addEdge(7, 6);
        
        assertFalse( hasConfigurationType2(stimulus));
        
        
        
        stimulus.removeEdge(3,6);
        stimulus.removeEdge(4,8);
        assertFalse( hasConfigurationType2(stimulus));
        
    }
    
    @Test
    public void checkConfigurationType3(){
        reset();
                
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);
        stimulus.addVertex(6);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(3, 4);
        stimulus.addEdge(1, 4);
        stimulus.addEdge(2, 3);
        stimulus.addEdge(3, 5);
        stimulus.addEdge(4, 6);
        
        /*
          Non-edges:
         stimulus.addEdge(1,3);
         stimulus.addEdge(2,4);
         stimulus.addEdge(1,5);
         stimulus.addEdge(2,5);
         stimulus.addEdge(1,6);
         stimulus.addEdge(2,6);
         stimulus.addEdge(4,5);
         
         Optional edges:
         stimulus.addEdge(3,5);
         stimulus.addEdge(3,6);
         
         stimulus.addEdge(5,6);
         implies non-edge
         stimulus.addEdge(6,7);
         */

        stimulus.addVertex(7);//x
        
        stimulus.addEdge(1, 7);
        stimulus.addEdge(2, 7);
        stimulus.addEdge(5, 7);
        
        /*
         Non-edges either:
         stimulus.addEdge(3,7);
         or
         stimulus.addEdge(4,7);
!!         Note: one is to choose, otherwise it is a 5-Cycle        !!
         
         Optional edges if non-edge stimulus.addEdge(5,6);
         stimulus.addEdge(6,7);
         */
        
        
        stimulus.addVertex(8);//p1
        stimulus.addVertex(9);//p2
        stimulus.addVertex(10);//p3
        
        stimulus.addEdge(5, 8);
        stimulus.addEdge(8, 9);
        stimulus.addEdge(9, 10);
        stimulus.addEdge(10, 6);
        
        /*
        Non-edges:
        stimulus.addEdge(1,9);
        stimulus.addEdge(2,9);
        stimulus.addEdge(7,9);
        
        Optional edges:
        stimulus.addEdge(1,8);
        stimulus.addEdge(2,8);
        stimulus.addEdge(3,8);
        stimulus.addEdge(4,8);
        stimulus.addEdge(6,8);
        stimulus.addEdge(7,8);
        stimulus.addEdge(8,10);
        stimulus.addEdge(3,9);
        stimulus.addEdge(4,9);
        stimulus.addEdge(5,9);
        stimulus.addEdge(6,9);
    
        */

        assertTrue(hasConfigurationType3(stimulus));
        

        stimulus.addEdge(4, 7);
        assertFalse(hasConfigurationType3(stimulus));
        
    }
    
    @Test
    public void checkCleanOddHole(){
        reset();
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);
        stimulus.addVertex(6);
        stimulus.addVertex(7);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(3, 2);
        stimulus.addEdge(4, 3);
        stimulus.addEdge(4, 5);
        stimulus.addEdge(6, 5);
        stimulus.addEdge(6, 7);
        stimulus.addEdge(7, 1);
        
        assertTrue(containsCleanShortestOddHole(stimulus));
        
        
        stimulus.addEdge(3, 7);
        stimulus.addEdge(4, 7);
        assertFalse(containsCleanShortestOddHole(stimulus));
        
    }
    
    @Test
    public void checkContainsShortestOddHole() {
        reset();
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);
        stimulus.addVertex(6);
        stimulus.addVertex(7);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(3, 2);
        stimulus.addEdge(4, 3);
        stimulus.addEdge(4, 5);
        stimulus.addEdge(6, 5);
        stimulus.addEdge(6, 7);
        stimulus.addEdge(7, 1);
        
        stimulus.addVertex(8);//Cleaner
        stimulus.addEdge(3, 8);
        stimulus.addEdge(8, 7);
        stimulus.addEdge(8, 5);
        assertTrue(containsCleanShortestOddHole(stimulus));
        
        List<Integer> golden = new LinkedList<Integer>();
        golden.add(1);
        golden.add(2);
        golden.add(3);
        golden.add(8);
        golden.add(7);
        golden.add(1);
       // assertEquals(golden,certificate.getVertexList());
        
        stimulus.removeVertex(8);
        assertTrue(containsCleanShortestOddHole(stimulus));
        
        
        

        
    }
    
    @Test
    public void checkRoutine3(){
        reset();
        
        stimulus.addVertex(1);//u
        stimulus.addVertex(2);//v
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(2, 3);
        stimulus.addEdge(2, 4);
        stimulus.addEdge(1, 3);
        stimulus.addEdge(1, 4);
        
        Set<Set<Integer>> golden = new HashSet<Set<Integer>>();
        Set<Integer> golden1 = new HashSet<Integer>(), golden2 = new HashSet<Integer>();
        golden1.add(1);
        golden1.add(2);
        golden2.add(1);
        golden2.add(2);
        golden2.add(3);
        golden2.add(4);
        golden.add(golden1);
        golden.add(golden2);
        
        assertEquals(golden,routine3(stimulus));
    }
    
    @Test
    public void checkPetersenGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generatePetersenGraph(stimulus);
        assertFalse(isBerge(stimulus,true));
        assertTrue(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkDodecahedronGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateDodecahedronGraph(stimulus);
        assertFalse(isBerge(stimulus,true));
        assertTrue(verifyCertificate(getCertificate()));
    }
    
    @Test
    @Category(SlowTests.class)
    public void checkMöbiusKantorGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateMöbiusKantorGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkBullGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateBullGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkButterflyGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateButterflyGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkClawGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateClawGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkGrötzschGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateGrötzschGraph(stimulus);
        assertFalse(isBerge(stimulus,true));
        assertTrue(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkDiamondGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateDiamondGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }

    @Test
    @Category(SlowTests.class)
    public void checkFranklinGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateFranklinGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkFruchtGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateFruchtGraph(stimulus);
        assertFalse(isBerge(stimulus,true));
        assertTrue(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkGoldnerHararyGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateGoldnerHararyGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    @Category(SlowTests.class)
    public void checkHeawoodGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateHeawoodGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    @Category(SlowTests.class)
    public void checkHerschelGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateHerschelGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkKrackhardtKiteGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateKrackhardtKiteGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkMoserSpindleGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateMoserSpindleGraph(stimulus);
        assertFalse(isBerge(stimulus,true));
        assertTrue(verifyCertificate(getCertificate()));
    }
    
    @Test
    @Category(SlowTests.class)
    public void checkPappusGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generatePappusGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkTietzeGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateTietzeGraph(stimulus);
        assertFalse(isBerge(stimulus,true));
        assertTrue(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkThomsenGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateThomsenGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    public void checkTutteGraph() {
        reset();
        new NamedGraphGenerator<Integer, Integer>().generateTutteGraph(stimulus);
        assertFalse(isBerge(stimulus,true));
        assertTrue(verifyCertificate(getCertificate()));
    }
    
    
    @Test
    public void checkEmptyGraph() {
        reset();
        int numberOfVertices = new Random().nextInt(maximalNumberOfVertices-minimalNumberOfVertices)+minimalNumberOfVertices;
        new EmptyGraphGenerator<Integer,Integer>(numberOfVertices).generateGraph(stimulus);
        assertTrue(isBerge(stimulus,true));
        assertFalse(verifyCertificate(getCertificate()));
    }
    
    @Test
    @Category(SlowTests.class)
    public void checkBipartiteGraphs(){
        int repititions = repititionsPerTestCase;
        reset();
        while(repititions-->0){
            int n1 = new Random().nextInt(maximalNumberOfVertices-minimalNumberOfVertices)/2+minimalNumberOfVertices/2,
                    n2 = maximalNumberOfVertices-n1;
            
            
            int maximalNumberOfEdges = n1*n2;        
            int numberOfEdges = new Random().nextInt(maximalNumberOfEdges);    
            
            reset();
            new GnmRandomBipartiteGraphGenerator<Integer,Integer>(n1,n2,numberOfEdges).generateGraph(stimulus);
            
            assertTrue(isBerge(stimulus));
        }
        
        
    }
    
    @Test
    @Category(SlowTests.class)
    public void checkWheelGraphs(){

        int repititions=repititionsPerTestCase;
        while (repititions-->0){

            int numberOfVertices = new Random().nextInt(maximalNumberOfVertices-minimalNumberOfVertices)+minimalNumberOfVertices;
            if (numberOfVertices%2==0) numberOfVertices+=1;
            assertTrue(maximalNumberOfVertices>minimalNumberOfVertices);
            
            
            reset();
            new WheelGraphGenerator<Integer,Integer>(numberOfVertices).generateGraph(stimulus);
            
            
            assertTrue(isBerge(stimulus));
        }
        
        repititions=repititionsPerTestCase;
        while (repititions-->0){

            int numberOfVertices = new Random().nextInt(maximalNumberOfVertices-minimalNumberOfVertices)+minimalNumberOfVertices;
            if (numberOfVertices%2==1) numberOfVertices+=1;
            assertTrue(maximalNumberOfVertices>minimalNumberOfVertices);
            
            
            reset();
            new WheelGraphGenerator<Integer,Integer>(numberOfVertices).generateGraph(stimulus);
            
            
            assertFalse(isBerge(stimulus));
        }
    }
    
    @Test
    @Category(SlowTests.class)
    public void checkWindmillGraphs() {
        int repititions=repititionsPerTestCase;
        while (repititions-->0) {
            int m = 2;
            int numberOfVertices = new Random().nextInt(maximalNumberOfVertices-3)+3;
            reset();

            new WindmillGraphsGenerator<Integer,Integer>(
                    WindmillGraphsGenerator.Mode.WINDMILL, m, numberOfVertices).generateGraph(stimulus);
            
            assertTrue(isBerge(stimulus));
        }
    }
    
}
