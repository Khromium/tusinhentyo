package as;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * Created by khrom on 2017/04/22.
 */
public class Main extends JFrame {
    private static int SN_RANGE = 15;
    private static double BIT_NUM = Math.pow(10, 7);
    private static int FC = 50; //carrier freq
    private static double BPSK = 1 * Math.cos(2 * Math.PI * FC * 0 + (1) * Math.PI);
    private static double QPSK = Math.sqrt(2) * Math.cos(2 * Math.PI * FC * 0 + (1 / 4.0) * Math.PI);//sinも同じ
    private static double QAM_COS = Math.sqrt(32) * Math.cos(2 * Math.PI * FC * 0 + (1 / 4.0) * Math.PI);


    public static void main(String[] args) {
        Main main = new Main();
        long start = System.currentTimeMillis();

//
//        List<CalBPSK> calBPSKS = new ArrayList<>();
//        for (int i = 0; i < SN_RANGE; i++) {
//            calBPSKS.add(new CalBPSK(i));
//            calBPSKS.get(i).start();
//        }
//        calBPSKS.forEach(s -> {
//            try {
//                s.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//        List<Double> dataList = new ArrayList<>();
//        calBPSKS.forEach(s -> dataList.add(s.res));
//
//        main.createChart(dataList, "bpsk");


//        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        main.setBounds(10, 10, 500, 500);
//        main.setTitle("にゃあ");
//        main.setVisible(true);
        long end = System.currentTimeMillis();
        System.out.println("time" + (end - start) + "ms");

////        //qpsk
//        start = System.currentTimeMillis();
//
//        List<CalQPSK> calQPSKS = new ArrayList<>();
//        for (int i = 0; i < SN_RANGE; i++) {
//            calQPSKS.add(new CalQPSK(i));
//            calQPSKS.get(i).start();
//        }
//        calQPSKS.forEach(s -> {
//            try {
//                s.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//        List<Double> dataLists = new ArrayList<>();
//        calQPSKS.forEach(s -> dataLists.add(s.res));
//
//        main.createChart(dataLists, "qpsk");
//        end = System.currentTimeMillis();
//        System.out.println("time" + (end - start) + "ms");

        //16qam
        start = System.currentTimeMillis();

        List<CalQAM> CalQAM = new ArrayList<>();
        for (int i = 0; i < SN_RANGE; i++) {
            CalQAM.add(new CalQAM(i));
            CalQAM.get(i).start();
        }
        CalQAM.forEach(s -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        List<Double> dataLists1 = new ArrayList<>();
        CalQAM.forEach(s -> dataLists1.add(s.res));

        main.createChart(dataLists1, "16qam");

        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setBounds(10, 10, 500, 500);
        main.setTitle("にゃあ");
        main.setVisible(true);
        end = System.currentTimeMillis();
        System.out.println("time" + (end - start) + "ms");
    }


    public void createChart(List<Double> value, String name) {
        JFreeChart freeChart = ChartFactory.createXYLineChart(name, "SNR [dB]", "nyaa", createData(value), PlotOrientation.VERTICAL, false, false, false);
        ChartPanel cpanel = new ChartPanel(freeChart);
        getContentPane().add(cpanel, BorderLayout.CENTER);
        setLogAxis(freeChart);
        try {
            ChartUtilities.saveChartAsJPEG(new File(name + ".jpg"), freeChart, 500, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setLogAxis(JFreeChart chart) {
        LogAxis axis = new LogAxis("BER");
        axis.setLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        axis.setRange(Math.pow(10, -7), Math.pow(10, 0));
        chart.getXYPlot().setRangeAxis(axis);
        TickUnits tickUnits = new TickUnits();
        tickUnits.add(new NumberTickUnit(1));
        chart.getXYPlot().getDomainAxis().setAutoTickUnitSelection(true);
        chart.getXYPlot().getRangeAxis().setAutoTickUnitSelection(true);
    }

    private static XYSeriesCollection createData(List<Double> value) {
        XYSeriesCollection trace = new XYSeriesCollection();
        XYSeries data = new XYSeries("nyaa");
        for (int i = 0; i < value.size(); i++) data.add(i, value.get(i));
        trace.addSeries(data);
        return trace;
    }

    public static class CalBPSK extends Thread {
        private double sn;
        public double res;

        public CalBPSK(double sn) {
            this.sn = sn;
        }

        public void run() {
            int error = 0;
            double sigma = Math.sqrt(Math.pow(10, -(1.0 / 10) * sn));
            for (int i = 0; i < BIT_NUM; i++) {
                double noise = new Random().nextGaussian() * sigma;
                double fukutyo1 = (BPSK + noise) * Math.cos(2 * Math.PI * FC * 0);
                if (fukutyo1 > 0) error++;
                res = error / BIT_NUM;
            }
        }
    }

    public static class CalQPSK extends Thread {
        private double sn;
        public double res;

        public CalQPSK(double sn) {
            this.sn = sn;
        }

        public void run() {
            int error = 0;
            double sigma = Math.sqrt(Math.pow(10, -(1.0 / 10) * sn));
            for (int i = 0; i < BIT_NUM; i++) {
                double noise = new Random().nextGaussian() * sigma;
                double fukutyo1 = (QPSK + noise) * Math.cos(2 * Math.PI * FC * 0);
                noise = new Random().nextGaussian() * sigma;
                double fukutyo2 = (QPSK + noise) * Math.sin(2 * Math.PI * FC * 0);
                if (fukutyo1 < 0 || fukutyo2 < 0) error++;
                res = error / BIT_NUM;
            }
        }
    }

    public static class CalQAM extends Thread {
        private double sn;
        public double res;

        public CalQAM(double sn) {
            this.sn = sn;
        }

        public void run() {
            int error = 0;
            double sigma = Math.sqrt(Math.pow(10, -(1.0 / 10) * sn));
            for (int i = 0; i < BIT_NUM; i++) {
                double noise = new Random().nextGaussian() * sigma;
                double fukutyo1 = (QAM_COS + noise) * Math.cos(2 * Math.PI * FC * 0 + (1 / 4.0) * Math.PI);
                noise = new Random().nextGaussian() * sigma;
                double fukutyo2 = (QAM_COS + noise) * Math.sin(2 * Math.PI * FC * 0 + (1 / 4.0) * Math.PI);
//                System.out.println(fukutyo1 + "," + fukutyo2);
                if (fukutyo1 < 2 || fukutyo2 < 2 || fukutyo1 > 4 || fukutyo2 > 4) error++;
                res = error / BIT_NUM;
            }
        }
    }
}
