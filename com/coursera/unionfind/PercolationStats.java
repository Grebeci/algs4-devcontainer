package com.coursera.unionfind;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    // n * n grid
    private int n;

    // trials times
    private int trials;

    // fractions of open sites
    private double[] fractions;
    
    /**
     * perform independent trials on an n-by-n grid
     *
     * @param n      n * n grid
     * @param trials 实验的次数
     */
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException(
                    "n and trials should be greater than 0");
        }

        this.n = n;
        this.trials = trials;
        this.fractions = new double[trials];

        // 1. 初始化 n * n 的网格
        // 2. 随机打开一个站点
        // 3. 检查系统是否开放
        for (int i = 0; i < trials; i++) {
            // init Percolation
            Percolation percolation = new Percolation(n);
            // 1. 随机开放一个站点..
            // 2. 检查系统是否开放
            do {
                int row = StdRandom.uniformInt(1, n + 1);
                int col = StdRandom.uniformInt(1, n + 1);
                percolation.open(row, col);
            } while (!percolation.percolates());
            fractions[i] = percolation.numberOfOpenSites() * 1.0 / (n * n);
        }
    }


    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(fractions);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(fractions);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - CONFIDENCE_95 * stddev() / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + CONFIDENCE_95 * stddev() / Math.sqrt(trials);
    }

    // test client (see below)
    public static void main(String[] args) {
        PercolationStats percolationStats = new PercolationStats(Integer.parseInt(args[0]),
                                                                 Integer.parseInt(args[1]));

        double mean = percolationStats.mean();
        double stddev = percolationStats.stddev();
        double low = percolationStats.confidenceLo();
        double hi = percolationStats.confidenceHi();

        StdOut.println("mean                    = " + mean);
        StdOut.println("stddev                  = " + stddev);
        StdOut.println(
                "95% confidence interval = [" + low + ", " + hi + "]");
    }
}
