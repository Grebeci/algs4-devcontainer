package com.coursera.unionfind;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by grebeci on 7/1/16.
 * <p>
 * This class is used to debug the WeightedQuickUnionUF class.
 * It is used to print the internal data structure of the WeightedQuickUnionUF class.
 * <p>
 * 1. print WeightedQuickUnionUF's internal data structure
 * 2. print the tree
 * 3. print the connected components
 */
public class DebugWeightedQuickUnionUF {
    WeightedQuickUnionUF uf;

    public DebugWeightedQuickUnionUF(WeightedQuickUnionUF uf) {
        this.uf = uf;
    }

    public int[] getParent() {
        try {
            Field idField = this.uf.getClass().getDeclaredField("parent");
            idField.setAccessible(true);
            int[] parent = (int[]) idField.get(this.uf);
            return parent;
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printQuickFindUFIds() {
        int[] parent = getParent();

        // print the array with index
        System.out.print("node parent: ");
        Arrays.stream(parent).forEach(i -> System.out.print(i + " "));
        System.out.println();
        System.out.print("child node:  ");
        IntStream.range(0, parent.length).forEach(i -> System.out.print(i + " "));
        System.out.println();

        // print the tree
        IntStream.range(0, parent.length).filter(i -> parent[i] == i).forEach(i -> {
            System.out.println("==========");
            printTree(parent, i, 0);

        });

        // print the connected components
        Map<Integer, List<Integer>> connectedComponents = getConnectedComponents(parent);
        System.out.println("connected components: ");
        connectedComponents.forEach((root, nodes) -> {
            System.out.print(root + ": ");
            nodes.forEach(i -> System.out.print(i + " "));
            System.out.println();
        });
    }

    public Map<Integer, List<Integer>> getConnectedComponents(int[] parent) {
        // store the connected components and their root
        Map<Integer, List<Integer>> rootToComponents = new HashMap<>();

        for (int i = 0; i < parent.length; i++) {
            int root = uf.find(i);
            rootToComponents.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
        }

        return rootToComponents;
    }


    private void printTree(int[] parent, int node, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.println(node);
        for (int i = 0; i < parent.length; i++) {
            if (parent[i] == node && i != node) {
                printTree(parent, i, depth + 1);
            }
        }
    }

    public static void main(String[] args) {
        DebugWeightedQuickUnionUF debugUF = new DebugWeightedQuickUnionUF(
                new WeightedQuickUnionUF(10));

        // create a union-find for test
        debugUF.uf.union(0, 1);
        debugUF.uf.union(0, 2);
        debugUF.uf.union(4, 5);
        debugUF.uf.union(6, 7);
        debugUF.uf.union(6, 8);
        debugUF.uf.union(1, 9);

        new DebugWeightedQuickUnionUF(debugUF.uf).printQuickFindUFIds();


    }

}


