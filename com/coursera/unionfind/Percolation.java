package com.coursera.unionfind;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * data structure
 * mapping rules
 * (row, col) -> index of uf and sites : (row - 1) * n + col - 1  ,
 * Indexes n * n  and n * n + 1 for the top and bottom virtual sites
 * index of uf and sites -> index  -> (row, col) : index / n + 1 , index % n + 1
 */

public class Percolation {
    // virtual site top and bottom
    private int topVirtualSiteIndex;
    private int bottomVirtualSiteIndex;

    // stores the status of each site, false for blocked, true for open
    private boolean[] sitesStatus;

    private int openSiteCount = 0;

    private int n;

    private WeightedQuickUnionUF topForPercolationUF;

    private WeightedQuickUnionUF topForFullUF;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {

        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }

        this.n = n;

        // initialize the status of each site
        this.sitesStatus = new boolean[n * n + 2];

        // initialize the uf
        this.topForPercolationUF = new WeightedQuickUnionUF(n * n + 2);
        this.topForPercolationUF.union(n * n, n * n); // top
        this.topVirtualSiteIndex = n * n;
        this.topForPercolationUF.union(n * n + 1, n * n + 1); // bottom
        this.bottomVirtualSiteIndex = n * n + 1;

        // because of backwash, we need to create another uf
        this.topForFullUF = new WeightedQuickUnionUF(n * n + 1);
        this.topForFullUF.union(n * n, n * n); // top

    }

    // 返回相邻的site
    private int[] getAdjacentSites(int row, int col) {
        int[] adjacentSites = { -1, -1, -1, -1 };
        // up, down, left, right
        if (row - 1 >= 1) {
            adjacentSites[0] = getSiteIndex(row - 1, col);
        }
        if (row + 1 <= n) {
            adjacentSites[1] = getSiteIndex(row + 1, col);
        }
        if (col - 1 >= 1) {
            adjacentSites[2] = getSiteIndex(row, col - 1);
        }
        if (col + 1 <= n) {
            adjacentSites[3] = getSiteIndex(row, col + 1);
        }
        return adjacentSites;
    }

    // (row, col) -> index of uf and sites
    private int getSiteIndex(int row, int col) {
        return (row - 1) * this.n + col - 1;
    }

    // index of uf and sites -> index  -> (row, col)
    private int getSiteRow(int index) {
        return index / this.n + 1;
    }

    private int getSiteCol(int index) {
        return index % this.n + 1;
    }

    private void setSitesStatus(int row, int col) {
        this.sitesStatus[getSiteIndex(row, col)] = true;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || row > this.n || col < 1 || col > this.n) {
            throw new IllegalArgumentException("row or col is illegal");
        }
        if (this.isOpen(row, col)) {
            return;
        }

        setSitesStatus(row, col);
        this.openSiteCount++;

        int currentSiteIndex = getSiteIndex(row, col);

        // if the site is in the first row, connect to the top
        if (row == 1) {
            this.topForFullUF.union(currentSiteIndex, topVirtualSiteIndex);
            this.topForPercolationUF.union(currentSiteIndex, topVirtualSiteIndex);
        }
        // if the site is in the last row, connect to the bottom
        if (row == this.n) {
            this.topForPercolationUF.union(currentSiteIndex, bottomVirtualSiteIndex);
        }

        // 检查周围的是否有开放的节点，如果有则合并

        for (int adjacentSite : this.getAdjacentSites(row, col)) {
            if (adjacentSite != -1 && this.isOpen(getSiteRow(adjacentSite),
                                                  getSiteCol(adjacentSite))) {
                this.topForPercolationUF.union(currentSiteIndex, adjacentSite);
                this.topForFullUF.union(currentSiteIndex, adjacentSite);
            }
        }

    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {

        if (row < 1 || row > this.n || col < 1 || col > this.n) {
            throw new IllegalArgumentException("row or col is illegal");
        }
        return this.sitesStatus[getSiteIndex(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {

        if (row < 1 || row > this.n || col < 1 || col > this.n) {
            throw new IllegalArgumentException("row or col is illegal");
        }

        // check whether the virtual top is connected to the bottom site
        return this.topForFullUF.find(this.topVirtualSiteIndex)
                == this.topForFullUF.find(getSiteIndex(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.openSiteCount;
    }

    // does the system percolate?
    public boolean percolates() {
        return this.topForPercolationUF.find(this.topVirtualSiteIndex)
                == this.topForPercolationUF.find(this.bottomVirtualSiteIndex);
    }


    // test client (optional)
    public static void main(String[] args) {
        Percolation percolation = new Percolation(3);
        percolation.open(1, 3);
        percolation.open(2, 3);
        percolation.open(3, 3);
        percolation.open(3, 1);

        System.out.println(percolation.percolates());
        System.out.println(percolation.isFull(3, 1));
        System.out.println(percolation.numberOfOpenSites());

    }
}
