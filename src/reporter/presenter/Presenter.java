/*
 * Presenter class, takes data from Reporter objects and presents them
 * graphically.
 */
package reporter.presenter;

import reporter.* ;

import org.jfree.chart.* ;
import org.jfree.chart.ui.ApplicationFrame ;
import org.jfree.chart.plot.PlotOrientation ;
import org.jfree.chart.ChartUtils ;
import org.jfree.data.category.* ;
import org.jfree.data.general.* ;

import java.lang.reflect.* ;
import java.util.Arrays ;
import java.util.ArrayList ;
import java.util.logging.Level;
import java.util.* ;

import java.io.File ;
import java.io.IOException ;

/**
 * Created 22/03/2018
 * @author Michael Walker
 * Email: mlwalker@kirby.unsw.edu.au, m.walker@aip.org.au
 */
public class Presenter {
    
    private Reporter reporter ;
    
    protected ArrayList<String> categoryData = new ArrayList<String>() ;
    protected ArrayList<String> scoreData = new ArrayList<String>() ;
    protected String applicationTitle ;
    protected String chartTitle ;
    
    private BarChart_AWT chart_awt ;
    private Dataset dataset ;

    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("presenter") ;
    
    public Presenter()
    {
        
    }
    
    public Presenter(String applicationTitle, String chartTitle)
    {
        this.applicationTitle = applicationTitle ;
        this.chartTitle = chartTitle ;
        chart_awt = new BarChart_AWT(applicationTitle, chartTitle) ;
        
    }
    
    public Presenter(String applicationTitle, String chartTitle, Reporter reporter)
    {
        this.applicationTitle = applicationTitle ;
        this.chartTitle = chartTitle ;
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
        
        callPlotChartDefault(scoreName, reportArray) ;
        return ;
    }
    

    /**
     * Presents reportArray as a function of time/cycle
     * @param scoreName
     * @param reportArray 
     */
    protected void callPlotChartDefault(String scoreName, ArrayList<String> reportArray)
    {
        // Extract data from reportArray
        parseReportArray(scoreName, reportArray) ;
        
        // Send data to be processed and presented
        LOGGER.info(chartTitle);
        chart_awt.callPlotChart(chartTitle,scoreData,scoreName) ;
        return ;
    }
    
    /**
     * Presents scoreName as a function of categoryName after calling prepareReportNameReport()
     * @param categoryName
     * @param scoreName
     * @param reportName
     * @param cycle 
     */
    protected void plotChartCategory(String categoryName, String scoreName, String reportName, int cycle)  //  ArrayList<String> reportArray,
    {
        // Get report from cycle
        ArrayList<String> reportArray = getReportArray(reportName) ;
        
        callPlotChartDefault(categoryName, scoreName, reportArray, cycle) ;
    }
    
    protected void plotHashMap(String categoryName, String scoreName, HashMap<String,Integer> hashMapReport )
    {
        //ArrayList<String> categoryInteger = new ArrayList<String>() ;
        ArrayList<Integer> scoreInteger = new ArrayList<Integer>() ;
        
        categoryData.clear();
        for (String key : hashMapReport.keySet())
        {
            categoryData.add(key) ;
        }
        categoryData.sort(null);
        for (String key : categoryData)
        {
            scoreInteger.add(hashMapReport.get(key)) ;
        }
        chart_awt.callPlotChartInteger(chartTitle,categoryData,scoreInteger,scoreName,categoryName) ;
    }
    
    /**
     * Presents scoreName as a function of categoryName from reportArray[cycle]
     * or HashMap
     * @param categoryName
     * @param scoreName
     * @param reportArray
     * @param cycle 
     */
    protected void callPlotChartDefault(String categoryName, String scoreName, ArrayList<String> reportArray, int cycle)
    {
        String report = reportArray.get(cycle) ;
        
        // Extract data from report
        parseReport(categoryName, scoreName, report) ;
        
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,categoryData,scoreData,scoreName,categoryName) ;
        return ;
    }
    
    protected ArrayList<String> prepareEventsPerCycle(String scoreName, ArrayList<ArrayList<String>> reportArray)
    {
        ArrayList<String> eventsPerCycle = new ArrayList<String>() ;
        scoreName += ":" ;
        
        for (ArrayList<String> report : reportArray)
        {
            eventsPerCycle.add(scoreName + Integer.toString(report.size()) + " ") ;
        }
        
        return eventsPerCycle ;
        
    }
    
    public void plotEventsPerCycle(String scoreName, ArrayList<ArrayList<String>> reportArray)
    {
        ArrayList<String> eventsPerCycle = prepareEventsPerCycle(scoreName,reportArray) ;
        
        callPlotChartDefault(scoreName,eventsPerCycle) ;
        
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
        int categoryIndex = Reporter.indexOfProperty(categoryName,report) ;
        
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
            String value = Reporter.extractValue(scoreName,report) ;
            scoreData.add(value) ;
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
        
        private void callPlotChartInteger(String chartTitle, ArrayList<String> categoryArray, ArrayList<Integer> scoreArray, String yLabel, String xLabel)
        {
            CategoryDataset dataset = createDatasetInteger(xLabel, categoryArray, scoreArray) ;
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
            saveChart(barChart,chartTitle) ;
            displayChart(barChart) ;
            
        }
        
        private void displayChart(JFreeChart barChart)
        {
            ChartPanel chartPanel = new ChartPanel( barChart );        
            chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
            setContentPane( chartPanel ); 
            pack() ;
            setVisible(true) ;
        }
        
        private void saveChart(JFreeChart barChart, String title)
        {
            String directory = "../output/test/" ;
            String address = directory + title + ".jpg" ;
            int width = 640 ;
            int height = 480 ;
            File file = new File(address) ;
            //File file = new File(directory) ;
            //String[] files = file.list() ;
            try
            {
            //LOGGER.info(file.getCanonicalPath());
            //for (String fileName : files)
              //  LOGGER.info(fileName);
                ChartUtils.saveChartAsJPEG(file, barChart, width, height);
            }
            catch ( IOException ioe)
            {
                //LOGGER.log(Level.SEVERE, ioe.getMessage());
                LOGGER.info(ioe.getLocalizedMessage());
            }
            LOGGER.info("saveChart() complete");
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
        
        private CategoryDataset createDatasetInteger(String category, ArrayList<String> categoryData, ArrayList<Integer> scoreData)
        {
            DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset() ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            
            String categoryValue ;
            int scoreValue ;
            
            for (int index = 0 ; index < scoreData.size() ; index++ )
            {
                categoryValue = categoryData.get(index) ;
                scoreValue = scoreData.get(index) ;
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
