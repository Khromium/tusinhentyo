package as;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

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
    private static int SN_RANGE = 25;//計算するDB数。この数だけスレッド生成する
    private static double BIT_NUM = Math.pow(10, 7);
    private static int FC = 50; //carrier freq
    private static double BPSK = 1 * Math.cos(2 * Math.PI * FC * 0 + (1) * Math.PI);//r=1
    private static double QPSK = 1 * Math.cos(2 * Math.PI * FC * 0 + (1 / 4.0) * Math.PI);//r=1,象限で区切るためにπ/4だけずらしている。

    /**
     * グラフ上で1がr=1となるように
     *        Q
     *  1     ┤
     *1/√2    ┤   2     1
     *        │
     *1/(3√2) ┤   3     2
     *        │
     *        ┼───┬─────┬───┬──I
     *      0  1/(3√2) 1/√2 1
     *  2: r=√5/3
     *  3: r=1/3
     *  時間を進めないとsinが0になるので、1/4だけtを進める
     *  参考: http://get-mobilebb.com/v001s010c026h0002r001.html
     */
    public static void main(String[] args) {
        Main main = new Main();
        long start = System.currentTimeMillis();

        //bpsk
        List<CalBPSK> calBPSKS = new ArrayList<>();
        for (int i = 0; i < SN_RANGE - 10; i++) {
            calBPSKS.add(new CalBPSK(i));
            calBPSKS.get(i).start();
        }
        calBPSKS.forEach(s -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        List<Double> dataList = new ArrayList<>();
        calBPSKS.forEach(s -> dataList.add(s.res));

        long end = System.currentTimeMillis();
        System.out.println("time" + (end - start) + "ms");

//        //qpsk
        start = System.currentTimeMillis();

        List<CalQPSK> calQPSKS = new ArrayList<>();
        for (int i = 0; i < SN_RANGE - 7; i++) {
            calQPSKS.add(new CalQPSK(i));
            calQPSKS.get(i).start();
        }
        calQPSKS.forEach(s -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        List<Double> dataLists = new ArrayList<>();
        calQPSKS.forEach(s -> dataLists.add(s.res));

        end = System.currentTimeMillis();
        System.out.println("time" + (end - start) + "ms");

//        //16qam
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

        end = System.currentTimeMillis();
        System.out.println("time" + (end - start) + "ms");
        main.createChart("通信課題", main.createGraphData("BPSK", dataList), main.createGraphData("QPSK", dataLists), main.createGraphData("16QAM", dataLists1));


    }


    /**
     * @param chartName
     * @param value
     */
    public void createChart(String chartName, XYSeries... value) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (XYSeries xySeries : value)
            xySeriesCollection.addSeries(xySeries);

        JFreeChart freeChart = ChartFactory.createXYLineChart(chartName, "SNR [dB]", "nyaa", xySeriesCollection, PlotOrientation.VERTICAL, false, false, false);
        LegendTitle legend = new LegendTitle(freeChart.getPlot());

        legend.setPosition(RectangleEdge.BOTTOM);
        freeChart.addLegend(legend);
        ChartPanel cpanel = new ChartPanel(freeChart);
        getContentPane().add(cpanel, BorderLayout.CENTER);
        setLogAxis(freeChart);
        try {
            ChartUtilities.saveChartAsJPEG(new File(chartName + ".jpg"), freeChart, 800, 800);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 対数軸を設定します。
     *
     * @param chart
     */
    static void setLogAxis(JFreeChart chart) {
        LogAxis axis = new LogAxis("BER");
        axis.setLabelFont(new Font("SansSerif", Font.PLAIN, 22));
        axis.setRange(Math.pow(10, -7), Math.pow(10, 0));
        chart.getXYPlot().setRangeAxis(axis);
        TickUnits tickUnits = new TickUnits();
        tickUnits.add(new NumberTickUnit(2));
//        chart.setBackgroundPaint(new Color(0,250,250));
        chart.getXYPlot().setRangeGridlinePaint(new Color(11, 11, 11));
        chart.getXYPlot().setDomainGridlinePaint(new Color(111, 111, 111));

        chart.getPlot().setBackgroundPaint(new Color(250, 250, 250));
        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(4.0f));
        chart.getXYPlot().getRenderer().setSeriesStroke(1, new BasicStroke(4.0f));
        chart.getXYPlot().getRenderer().setSeriesStroke(2, new BasicStroke(4.0f));
        chart.getXYPlot().getDomainAxis().setAutoTickUnitSelection(true);
        chart.getXYPlot().getRangeAxis().setAutoTickUnitSelection(true);
        chart.getXYPlot().getDomainAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 20));
        chart.getXYPlot().getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 20));
        chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        chart.getXYPlot().getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 16));
    }

    /**
     * グラフに設置するためのデータ生成
     */
    private static XYSeries createGraphData(String name, List<Double> value) {
        XYSeries data = new XYSeries(name);
        for (int i = 0; i < value.size(); i++) data.add(i, value.get(i));
        return data;
    }

    /**
     * BPSKの計算
     */
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

    /**
     * QPSKの計算
     */
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
                double fukutyo1 = (QPSK + noise) * Math.cos(2 * Math.PI * FC * 0 + (1 / 4.0) * Math.PI);//象限で区切るためにπ/4だけずらしている。
                noise = new Random().nextGaussian() * sigma;
                double fukutyo2 = (QPSK + noise) * Math.sin(2 * Math.PI * FC * 0 + (1 / 4.0) * Math.PI);//象限で区切るためにπ/4だけずらしている。
                if (fukutyo1 < 0 || fukutyo2 < 0) error++;
                res = error / BIT_NUM;
            }
        }
    }

    /**
     * 16QAMの計算
     * TODO: 複数ビット誤りを検出する
     */
    public static class CalQAM extends Thread {
        private double sn;
        public double res;

        public CalQAM(double sn) {
            this.sn = sn;
        }

        public void run() {
            int corner = 0, edge = 0, center = 0;
            double sigma = Math.sqrt(Math.pow(10, -(1.0 / 10) * sn));
            //角　ex.1000
            //I:x軸、Q:Y軸 16の内4つ 4/16 = 1/4
            for (int i = 0; i < BIT_NUM / 4; i++) {
                double noise = new Random().nextGaussian() * sigma;
                double I = 1 / Math.sqrt(2) + noise;
                noise = new Random().nextGaussian() * sigma;
                double Q = 1 / Math.sqrt(2) + noise;
                //Iの誤り
                if ((0 <= I && I < 2 / (3 * Math.sqrt(2))) || I < -2 / (3 * Math.sqrt(2))) corner += 1;//1ビット誤り
                if (0 > I && I > -2 / (3 * Math.sqrt(2))) corner += 2;//2ビット誤り
                //Qの誤り
                if ((0 <= Q && Q < 2 / (3 * Math.sqrt(2))) || Q < -2 / (3 * Math.sqrt(2))) corner += 1;//1ビット誤り
                if (0 > Q && Q > -2 / (3 * Math.sqrt(2))) corner += 2;//2ビット誤り
//                res = error / BIT_NUM;
            }

            //中心 ex.1101
            //I:x軸、Q:Y軸 16の内4つ 4/16 = 1/4
            for (int i = 0; i < BIT_NUM / 4; i++) {
                double noise = new Random().nextGaussian() * sigma;
                double I = 1 / (3 * Math.sqrt(2)) + noise;
                noise = new Random().nextGaussian() * sigma;
                double Q = 1 / (3 * Math.sqrt(2)) + noise;
                //Iの誤り
                if ((0 > I && I < -2 / (3 * Math.sqrt(2))) || I > 2 / (3 * Math.sqrt(2))) center += 1;//1ビット誤り
                if (I <= -2 / (3 * Math.sqrt(2))) center += 2;//2ビット誤り
                //Qの誤り
                if ((0 > Q && Q < -2 / (3 * Math.sqrt(2))) || Q > 2 / (3 * Math.sqrt(2))) center += 1;//1ビット誤り
                if (Q <= -2 / (3 * Math.sqrt(2))) center += 2;//2ビット誤り
//                res = error / BIT_NUM;
            }

            //隅 ex.1100
            //I:x軸、Q:Y軸 16の内4つ 8/16 = 1/2
            for (int i = 0; i < BIT_NUM / 2; i++) {
                double noise = new Random().nextGaussian() * sigma;
                double I = 1 / (3 * Math.sqrt(2)) + noise;
                noise = new Random().nextGaussian() * sigma;
                double Q = 1 / Math.sqrt(2) + noise;
                //Iの誤り
                if ((0 > I && I < -2 / (3 * Math.sqrt(2))) || I > 2 / (3 * Math.sqrt(2))) edge += 1;//1ビット誤り
                if (I <= -2 / (3 * Math.sqrt(2))) edge += 2;//2ビット誤り
                //Qの誤り
                if (0 <= Q && Q < 2 / (3 * Math.sqrt(2)) || Q < -2 / (3 * Math.sqrt(2))) edge += 1;//1ビット誤り
                if (Q < 0 && Q > -2 / (3 * Math.sqrt(2))) edge += 2;//2ビット誤り
//                res = error / BIT_NUM;
            }
            res = (corner + center + edge) / (BIT_NUM * 4.0);


        }
    }
}
