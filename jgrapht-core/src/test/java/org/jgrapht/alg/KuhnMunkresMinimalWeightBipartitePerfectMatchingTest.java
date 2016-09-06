/* This program and the accompanying materials are dual-licensed under
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
package org.jgrapht.alg;

import junit.framework.TestCase;
import org.jgrapht.WeightedGraph;
import org.jgrapht.generate.SimpleWeightedBipartiteGraphMatrixGenerator;
import org.jgrapht.generate.WeightedGraphGeneratorAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.VertexPair;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class KuhnMunkresMinimalWeightBipartitePerfectMatchingTest extends TestCase {

    interface V {}

    /**
     * First partition
     */
    enum FIRST_PARTITION implements V {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V
    }

    static List<? extends V> firstPartition = Arrays.asList(FIRST_PARTITION.values());

    /**
     * Second partition
     */
    enum SECOND_PARTITION implements V {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V
    }

    static List<? extends V> secondPartition = Arrays.asList(SECOND_PARTITION.values());


    static class WeightedEdge extends DefaultWeightedEdge {

        class _ extends VertexPair<V> {
            public _(V _1, V _2) {
                super(_1, _2);
            }
        }

        WeightedEdge(V _1, V _2) {
            __ = new _(_1, _2);
        }

        static WeightedEdge make(V source, V target) {
            return new WeightedEdge(source, target);
        }

        @Override
        public boolean equals(Object edge) {
            return (edge instanceof WeightedEdge) && __.equals(((WeightedEdge) edge).__);
        }

        @Override
        public int hashCode() {
            return __.hashCode();
        }

        @Override
        public String toString() {
            return __.toString() + " : " + getWeight();
        }

        _ __;

    }


    static KuhnMunkresMinimalWeightBipartitePerfectMatching<V, WeightedEdge>
        match(final double[][] costMatrix, final int partitionCardinality) {

        List<? extends V> first     = firstPartition.subList(0, partitionCardinality);
        List<? extends V> second    = secondPartition.subList(0, partitionCardinality);

        WeightedGraph<V, WeightedEdge> target =
                new SimpleWeightedGraph<>((sourceVertex, targetVertex) -> {
                    return WeightedEdge.make(sourceVertex, targetVertex);
                });

        WeightedGraphGeneratorAdapter<V, WeightedEdge, V> generator =
          new SimpleWeightedBipartiteGraphMatrixGenerator<V, WeightedEdge>()
              .first  (first)
              .second (second)
              .weights(costMatrix);

        generator.generateGraph(target, null, null);

        return new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(target, first, second);

    }



    public void testForEmptyGraph() {

        WeightedGraph<V, WeightedEdge> graph =
                new SimpleWeightedGraph<>(WeightedEdge.class);

        List<? extends V> emptyList = Collections.emptyList();

        Assert.assertTrue(new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(
                        graph,
                        emptyList,
                        emptyList).getMatching().isEmpty()
        );
    }

    public void test3x3SimpleAssignmentTask() {

        // Obvious case:
        //    Optimal selection being disposed on the diagonal of the given matrix

        double[][] costMatrix = new double[][] {
            { 1, 2, 3 },
            { 5, 4, 6 },
            { 8, 9, 7 }
        };

        double w = match(costMatrix, costMatrix.length).getMatchingWeight();

        Assert.assertTrue(w == 12);

    }

    public void test3x3SimpleAssignmentTaskNo2() {

        // Simple case:
        //    Every selection gives the same value of 15

        double[][] costMatrix = new double[][] {
            { 1, 2, 3 },
            { 4, 5, 6 },
            { 7, 8, 9 }
        };

        double w = match(costMatrix, costMatrix.length).getMatchingWeight();


        Assert.assertTrue(w == 15);

    }

    public void test5x5AssignmentTask() {

        // Not so obvious case

        double[][] costMatrix = new double[][] {
            { 1, 2, 3, 4, 5 },
            { 6, 7, 8, 7, 2 },
            { 1, 3, 4, 4, 5 },
            { 3, 6, 2, 8, 7 },
            { 4, 1, 3, 5, 4 }
        };

        double w = match(costMatrix, costMatrix.length).getMatchingWeight();

        Assert.assertTrue(w == 10);

    }

    public void test5x5InvertedAssignmentTask() {

        // Assignment minimizing total cost according to given cost-matrix
        // maximizes total-cost according to the following cost-matrix:
        //
        //    { 1, 2, 3, 4, 5 }
        //    { 6, 7, 8, 7, 2 }
        //    { 1, 3, 4, 4, 5 }
        //    { 3, 6, 2, 8, 7 }
        //    { 4, 1, 3, 5, 4 }
        //
        // NOTE:
        //    Cost-matrix being under test derived from the listed above
        //    by subtraction from the maximal element

        double[][] costMatrix = new double[][] {
            { 7, 6, 5, 4, 3 },
            { 2, 1, 0, 1, 6 },
            { 7, 5, 4, 4, 3 },
            { 5, 2, 6, 0, 1 },
            { 4, 7, 5, 3, 4 }
        };

        double w = match(costMatrix, costMatrix.length).getMatchingWeight();

        Assert.assertTrue(w == 12);

    }

    public void test6x6DegeneratedAssignmentTask() {

        // First DEGENERATED case:
        //    Degenerated worker and degenerated task added
        //
        // NOTE:
        //    Answer have to stay the same as in previous case #4

        double[][] costMatrix = new double[][] {
            { 7, 6, 5, 4, 3, 9 },
            { 2, 1, 0, 1, 6, 9 },
            { 7, 5, 4, 4, 3, 9 },
            { 5, 2, 6, 0, 1, 9 },
            { 4, 7, 5, 3, 4, 9 },
            { 9, 9, 9, 9, 9, 9 }
        };

        double w = match(costMatrix, costMatrix.length).getMatchingWeight();

        Assert.assertTrue(w == 21);

    }

    public void test6x6DegeneratedAssignmentTaskNo2() {

        // Second DEGENERATED case:
        //
        //    |Workers| > |Tasks|
        //
        //  degenerated task added

        double[][] costMatrix = new double[][] {
            { 7, 6, 5, 4, 3, 9 },
            { 2, 1, 0, 1, 6, 9 },
            { 7, 5, 4, 4, 3, 9 },
            { 5, 2, 6, 0, 1, 9 },
            { 4, 7, 5, 3, 4, 9 },
            { 3, 5, 8, 7, 1, 9 }
        };

        double w = match(costMatrix, costMatrix.length).getMatchingWeight();

        Assert.assertTrue(w == 19);

    }

    public void test5x5DegeneratedAssignmentTask() {

        // Third DEGENERATED case:
        //
        //  Task #1 can't be performed by the worker  #1      (designated by the MAX + 1 value (9))
        //  Task #3 can't be performed by the worker  #3      (designated by the MAX + 1 value (9))
        //  Task #4 can't be performed by the worker  #1      (designated by the MAX + 1 value (9))
        //  Task #5 can't be performed by the workers #2, #4  (designated by the MAX + 1 value (9))
        //
        //  degenerated task added

        double[][] costMatrix = new double[][] {
            { 9, 6, 5, 9, 3 },
            { 2, 1, 0, 1, 6 },
            { 7, 5, 9, 4, 3 },
            { 9, 2, 6, 0, 1 },
            { 4, 9, 5, 9, 4 },
        };

        double w = match(costMatrix, costMatrix.length).getMatchingWeight();

        Assert.assertTrue(w == 12);

    }

    public void test8x8BulkyAssignmentTask() {

        double[][] costMatrix = new double[][] {
            {233160,  1485901,  3245737,  25965896, 25965896, 25965896, 25965896, 25965896},
            {238594,  25965896, 25965896, 25965896, 25965896, 25965896, 25965896, 25965896},
            {242403,  25965896, 25965896, 25965896, 25965896, 25965896, 25965896, 25965896},
            {233408,  25965896, 25965896, 25965896, 25965896, 25965896, 25965896, 25965896},
            {233160,  25965896, 25965896, 25965896, 25965896, 25965896, 25965896, 25965896},
            {258074,  25965896, 25965896, 25965896, 25965896, 25965896, 25965896, 25965896},
            {233160,  25965896, 25965896, 25965896, 25965896, 25965896, 25965896, 25965896},
            {233625,  25965896, 25965896, 25965896, 25965896, 25965896, 25965896, 25965896}
        };

        // Case entailing set-cover algo drastically degenerating, therefore need just to pass

        match(costMatrix, costMatrix.length);

    }

    public void test21x21BulkyAssignmentTask() {

        double[][] costMatrix = new double[][] {
            {284169900,16680,27111,0,25914,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,16680,27305,0,25914,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,16834,60173,0,25981,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,16680,43679,0,32979,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {16656,270745874,270739560,270769776,270686589,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,271120238,271113924,271144140,271060953,374364,374364,374364,374364,374364,374364,374364,374364,374364,374364,374364,374364,374364,374364,374364,374364},
            {0,270959812,270953498,270983714,270900527,213938,213938,213938,213938,213938,213938,213938,213938,213938,213938,213938,213938,213938,213938,213938,213938},
            {284182260,0,33241,12360,0,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484,13412484},
            {284169900,16680,27305,0,25914,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,17630,25747,0,31348,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,34668,28398,0,35157,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,17503,20151,0,0,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,18099,77279,0,26162,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,16804,27869,0,25914,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284175472,6700,0,5572,24108,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696,13405696},
            {284169900,31543,22343,0,50828,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,16623,27215,0,25830,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,16680,27305,0,14463,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284174196,0,4296,4296,30210,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420,13404420},
            {284169900,17377,27999,0,25914,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124},
            {284169900,76034,27305,0,26379,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124,13400124}
        };

        // Case entailing set-cover algo drastically degenerating, therefore need just to pass

        match(costMatrix, costMatrix.length);

    }

    public void test20x20BulkyAssignmentTask() {

        double[][] costMatrix = new double[][] {
            {284309466,162348,179093,121766,230867,175501,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,162348,179287,121766,230867,133304,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,162502,212155,121766,230934,192658,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,162348,195661,121766,237932,175347,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {13538546,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {13147526,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {13307952,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,133308,172863,121766,192593,145039,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,162348,179287,121766,230867,175287,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,163298,177729,121766,236301,184972,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,180336,180380,121766,240110,181736,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,163171,172133,121766,204953,175649,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,163767,229261,121766,231115,205427,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,162472,179851,121766,230867,221341,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,146796,146410,121766,223489,156487,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,177211,174325,121766,255781,235876,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,162291,179197,121766,230783,175287,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,162348,179287,121766,219416,175460,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,141372,151982,121766,230867,161161,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,163045,179981,121766,230867,175740,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466},
            {284309466,221702,179287,121766,231332,175287,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466,284309466}
        };

        // Case entailing set-cover algo drastically degenerating, therefore need just to pass

        match(costMatrix, costMatrix.length);

    }

}


