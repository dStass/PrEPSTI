/*
 * Presenter class, takes data from Reporter objects and presents them
 * graphically.
 */
package reporter.presenter;

import reporter.* ;

import org.jfree.chart.* ;
import org.jfree.chart.ui.ApplicationFrame ;
import org.jfree.chart.plot.PlotOrientation ;
import org.jfree.data.category.* ;
import org.jfree.data.general.* ;

import java.lang.reflect.* ;
import java.util.Arrays ;
import java.util.ArrayList ;


/**
 * Created 22/03/2018
 * @author Michael Walker
 * Email: mlwalker@kirby.unsw.edu.au, m.walker@aip.org.au
 */
public class Presenter {
    
    private Reporter reporter ;
    
    private ArrayList<String> categoryData ;
    private ArrayList<String> scoreData ;
    private String applicationTitle ;
    private String chartTitle ;
    
    private BarChart_AWT chart_awt ;
    private Dataset dataset ;

    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("presenter") ;
    
    public void Presenter(String applicationTitle, String chartTitle)
    {
        chart_awt = new BarChart_AWT(applicationTitle, chartTitle) ;
        
    }
    
    public void Presenter(String applicationTitle, String chartTitle, Reporter reporter)
    {
        chart_awt = new BarChart_AWT(applicationTitle, chartTitle) ;
        setReporter(reporter) ;
    }
    
    public void setReporter(Reporter reporter)
    {
        this.reporter = reporter ;
    }

    /**
     * Presents quantity scoreName as a function of time/cycle
     * @param scoreName name of quantity on y-axis
     * @param reportName which report are we presenting
     */
    protected void plotChartDefault(String scoreName, String reportName)
    {
        // Get full report reportName
        ArrayList<String> reportArray = getReportArray(reportName) ;
        
        // Extract data from reportArray
        parseReportArray(scoreName, reportArray) ;
        
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,scoreData,scoreName) ;
        return ;
    }
    
    /**
     * Presents scoreName as a function of categoryName from reportArray[cycle]
     * @param categoryName
     * @param scoreName
     * @param reportName
     * @param cycle 
     */
    protected void plotChartCycle(String categoryName, String scoreName, String reportName, int cycle)  //  ArrayList<String> reportArray,
    {
        // Get report from cycle
        ArrayList<String> reportArray = getReportArray(reportName) ;
        String report = reportArray.get(cycle) ;
        
        // Extract data from report
        parseReport(categoryName, scoreName, report) ;
        
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,categoryData,scoreData,scoreName,categoryName) ;
        return ;
    }
    
    /**
     * Uses reflect to call Method prepareReportNameReport()
     * @param reportName
     * @return reportArray returned by prepareReportNameReport()
     */
    private ArrayList<String> getReportArray(String reportName)
    {
        ArrayList<String> reportArray = new ArrayList<String>() ;
        
        // Name of Method which provides report
        String reportMethodName = "prepare" + reportName + "Report" ;
        try
        {
            Class reporterClass = reporter.getClass().asSubclass(reporter.getClass()) ;
            Method prepareReportMethod = reporterClass.getMethod(reportName) ;
            reportArray = (ArrayList<String>) prepareReportMethod.invoke(reporter) ;
        }
        catch ( Exception e )
        {
            LOGGER.info(e.getLocalizedMessage());
        }
        return reportArray ;
    }
    
    /**
     * Extracts category (x) and score (y) data and records in corresponding fields
     * @param categoryName
     * @param scoreName
     * @param report 
     */
    private void parseReport(String categoryName, String scoreName, String report)
    {        
        int categoryIndex = report.indexOf(categoryName) ;
        
        categoryData = Reporter.extractAllValues(categoryName, report, categoryIndex) ;
        scoreData = Reporter.extractAllValues(scoreName, report, categoryIndex) ;
        
        return ;
    }

    /**
     * Extracts one value for scoreName from each report cycle.
     * Intended for plots over time.
     * @param scoreName
     * @param reports 
     */
    private void parseReportArray(String scoreName, ArrayList<String> reports)
    {       
        for (String report : reports)
        {
            scoreData.add(Reporter.extractValue(scoreName,report)) ;
        }
        return ;
    }

    /**
     * private class to specifically handle JFreeChart functions such as
     * handling Datasets and plotting charts.
     */
    private class BarChart_AWT extends ApplicationFrame {
   
        private BarChart_AWT( String applicationTitle , String chartTitle ) 
        {
            super( applicationTitle );        
        }
        
        /**
         * Calls Method plotChart() for plots over time after generating dataset
         * @param chartTitle
         * @param dataArray
         * @param yLabel 
         */
        private void callPlotChart(String chartTitle, ArrayList<String> dataArray, String yLabel)
        {
            CategoryDataset dataset = createDataset(dataArray) ;
            plotChart(chartTitle, dataset, yLabel, "cycle") ;
        }
        
        /**
         * Calls method plotChart for within-cycle plots after generating dataset
         * @param chartTitle
         * @param categoryArray
         * @param scoreArray
         * @param yLabel
         * @param xLabel 
         */
        private void callPlotChart(String chartTitle, ArrayList<String> categoryArray, ArrayList<String> scoreArray, String yLabel, String xLabel)
        {
            CategoryDataset dataset = createDataset(xLabel, categoryArray, scoreArray) ;
            plotChart(chartTitle, dataset, yLabel, xLabel) ;
        }
        
        /**
         * Generates plot of dataset
         * @param chartTitle
         * @param dataset
         * @param yLabel
         * @param xLabel 
         */
        private void plotChart(String chartTitle, CategoryDataset dataset, String yLabel, String xLabel)
        {
            JFreeChart barChart = ChartFactory.createBarChart(chartTitle,xLabel,
                yLabel,dataset,PlotOrientation.VERTICAL,true, true, false);

            ChartPanel chartPanel = new ChartPanel( barChart );        
            chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
            setContentPane( chartPanel ); 
        }
        
        /**
         * Generate Dataset of scoreData as function of categoryData.
         * Usually used for within-cycle plots
         * @param category
         * @param categoryData
         * @param scoreData
         * @return 
         */
        private CategoryDataset createDataset(String category, ArrayList<String> categoryData, ArrayList<String> scoreData)
        {
            DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset() ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            
            String categoryValue ;
            int scoreValue ;
            
            for (int index = 0 ; index < scoreData.size() ; index++ )
            {
                categoryValue = categoryData.get(index) ;
                scoreValue = Integer.valueOf(scoreData.get(index)) ;
                categoryDataset.addValue( scoreValue, category, categoryValue ) ;
            }
            return categoryDataset ;
        }
        
        /**
         * Generate Dataset from scoreData.
         * Intended for plots over time/cycles
         * @param scoreData
         * @return CategoryDataset of score over cycle
         */
        private CategoryDataset createDataset(ArrayList<String> scoreData)
        {
            DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset() ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            
            String categoryValue ;
            int scoreValue ;
            
            for (int index = 0 ; index < scoreData.size() ; index++ )
            {
                categoryValue = Integer.toString(index) ;
                scoreValue = Integer.valueOf(scoreData.get(index)) ;
                categoryDataset.addValue( scoreValue, "cycle", categoryValue ) ;
            }
            return categoryDataset ;
        }
        
        
    }
    
}
