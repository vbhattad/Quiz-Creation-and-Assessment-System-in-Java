/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Charts;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 *
 * @author mac
 */
public class StuPassAndFailC {

    //Creat a new barchart and name it chart
    BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
    //creat a new DAO.resultDAOImpl 
    DAO.ResultDAOImpl resultDao = new DAO.ResultDAOImpl();
    int[] array = new int[6];

    /**
     * Get the Bar Charts for Number of Pass and Fails over time.
     * @return
     */
    public BarChart<String, Number> getChart() {
        array = resultDao.getPassandFallForStu("002");
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        //set the series column names as last month,last quarter and last year and pass the value 
        series1.setName("Pass during the last month,last quarter and over the last year");
        series1.getData().add(new XYChart.Data<>("Last month", array[0]));
        series1.getData().add(new XYChart.Data<>("Last quater", array[2]));
        series1.getData().add(new XYChart.Data<>("Last year", array[4]));
        series2.setName("Fail during the last month,last quarter and over the last year");
        series2.getData().add(new XYChart.Data<>("Last month", array[1]));
        series2.getData().add(new XYChart.Data<>("Last quater", array[3]));
        series2.getData().add(new XYChart.Data<>("Last year", array[5]));

        chart.getData().addAll(series1, series2);

        return chart;
    }

}
