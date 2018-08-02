/*
 * Presenter class, takes data from Reporter objects and presents them
 * graphically.
 */
package reporter.presenter;

import reporter.* ;
import community.Community ;

import org.jfree.chart.* ;
import org.jfree.chart.ui.ApplicationFrame ;
import org.jfree.chart.axis.* ;
import org.jfree.chart.plot.PlotOrientation ;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ChartUtils ;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.* ;
import org.jfree.data.general.* ;
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.XYSeries ;  
import org.jfree.data.xy.XYSeriesCollection ;

import java.lang.reflect.* ;
import java.util.Arrays ;
import java.util.ArrayList ;
import java.util.* ;

import java.io.File ;
import java.io.IOException ;
import java.util.logging.Level;

/**
 * Created 22/03/2018
 * @author Michael Walker
 * Email: mlwalker@kirby.unsw.edu.au, m.walker@aip.org.au
 */
public class Presenter {
    
    private Reporter reporter ;
    
    protected ArrayList<ArrayList<Object>> categoryData = new ArrayList<ArrayList<Object>>() ;
    protected ArrayList<ArrayList<Object>> scoreData = new ArrayList<ArrayList<Object>>() ;
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
    
    public Presenter(String simName, String chartTitle, String reportFilePath)
    {
        this.applicationTitle = simName ;
        this.chartTitle = chartTitle ;
        chart_awt = new BarChart_AWT(applicationTitle, chartTitle) ;
        setReporter(new Reporter(simName,reportFilePath)) ;
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
    protected void plotChart(String scoreName, String reportName)
    {
        // Get full report reportName
        ArrayList<Object> reportArray = getReportArray(reportName) ;
        
        callPlotChart(scoreName, reportArray) ;
        return ;
    }
    
    /**
     * Generates network diagram from data in hashMapArray.
     * @param xLabel
     * @param yLabel
     * @param hashMapArray 
     */
    protected void callPlotNetwork(String xLabel, String yLabel, ArrayList<HashMap<Object,ArrayList<Object>>> hashMapArray)
    {
        chart_awt.callPlotNetwork(chartTitle, hashMapArray, xLabel, yLabel) ;
    }

    /**
     * Presents reportArray as a function of time/cycle.
     * @param scoreName
     * @param reportArray 
     */
    protected void callPlotChart(String scoreName, ArrayList<Object> reportArray)
    {
        //LOGGER.info("callPlotChart()") ;
        // Extract data from reportArray
        parseReportArray(scoreName, reportArray) ;
        
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,scoreData,scoreName) ;
        return ;
    }
    
    /**
     * Presents reportArray as a function of time/cycle.
     * @param scoreName
     * @param reportArray 
     */
    protected void callMultiPlotChart(String scoreName, ArrayList<ArrayList<Object>> reportArrays, String[] legend)
    {
        //LOGGER.info("callPlotChart()") ;
        // Extract data from reportArray
        parseReportArrays(scoreName, reportArrays) ;
        
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,scoreData,scoreName,legend) ;
        return ;
    }
    
    /**
     * Presents reportArray as a function of time/cycle.
     * @param scoreName
     * @param reportArray 
     */
    protected void callMultiPlotChart(ArrayList<String> scoreNames, ArrayList<ArrayList<Object>> reportArrays, String[] legend)
    {
        //LOGGER.info("callPlotChart()") ;
        // Extract data from reportArray
        parseReportArrays(scoreNames, reportArrays) ;
        
        // Generate approriate scoreName from scoreNames with no repetition
        String scoreName = "" ;
        for (String name : scoreNames)
            if (scoreName.indexOf(name) >= 0)
                scoreName += "/" + name ;
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,scoreData,scoreName,legend) ;
        return ;
    }
    
    /**
     * Presents reportArray as a function of time/cycle.
     * @param scoreName
     * @param reportArray 
     */
    protected void callMultiPlotChart(ArrayList<String> scoreNames, ArrayList<Object> reportArrays)
    {
        //LOGGER.info("callPlotChart()") ;
        // Extract data from reportArray
        parseReportArray(scoreNames, reportArrays) ;
        
        // Generate approriate scoreName from scoreNames with no repetition
        String[] legend = new String[scoreNames.size()] ;
        String scoreName = "" ;
        String name ;
        for (int scoreIndex = 0 ; scoreIndex < scoreNames.size() ; scoreIndex++ )
        {
            name = scoreNames.get(scoreIndex) ;
            scoreName += "/" + name ;
            legend[scoreIndex] = name ;
            LOGGER.info(name);
        }
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,scoreData,scoreName,legend) ;
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
        ArrayList<Object> reportArray = getReportArray(reportName) ;
        
        callPlotChartDefault(categoryName, scoreName, reportArray, cycle) ;
    }
    
    protected void plotHashMapScatter(String categoryName, String scoreName, HashMap<Object,ArrayList<Object>> hashMapReport )
    {
        chart_awt.callPlotScatterPlot(chartTitle, hashMapReport, scoreName, categoryName) ;
    }
    
    protected void plotHashMap(String categoryName, String scoreName, HashMap<Object,Integer> hashMapReport )
    {
        HashMap<Object,Number> numberHashMap = new HashMap<Object,Number>() ;
        for (Object key : hashMapReport.keySet())
            numberHashMap.put(key, (Number) hashMapReport.get(key)) ;
        
        plotHashMapNumber(categoryName,scoreName,numberHashMap) ;
    }
    
    protected void plotHashMapDouble(String categoryName, String scoreName, HashMap<Object,Double> hashMapReport )
    {
        HashMap<Object,Number> numberHashMap = new HashMap<Object,Number>() ;
        for (Object key : hashMapReport.keySet())
            numberHashMap.put(key, (Number) hashMapReport.get(key)) ;
        
        plotHashMapNumber(categoryName,scoreName,numberHashMap) ;
    }
    
    protected void plotHashMapNumber(String categoryName, String scoreName, HashMap<Object,Number> hashMapReport ) 
    {
        HashMap<Object,Number[]> newHashMapReport = new HashMap<Object,Number[]>() ;
        
        for (Object key : hashMapReport.keySet())
            newHashMapReport.put(key, new Number[] {hashMapReport.get(key)}) ;
        
        plotHashMapNumber(categoryName, new String[] {scoreName}, newHashMapReport) ;
    }
    
    protected void plotHashMapNumber(String categoryName, String[] scoreNames, HashMap<Object,Number[]> hashMapReport )
    {
        LOGGER.info("plotHashMap()") ;
        //ArrayList<String> categoryInteger = new ArrayList<String>() ;
        ArrayList<ArrayList<Number>> scoreNumbers = new ArrayList<ArrayList<Number>>() ;
        ArrayList<Object> categoryEntry = new ArrayList<Object>() ;
        Number[] hashMapValue ;
        
        categoryData.clear();
        for (Object key : hashMapReport.keySet())
        {
            if (key.equals(null))
                continue ;
            categoryEntry.add(key) ;
        }
        categoryEntry.sort(null);
        for (Object key : categoryEntry)
        {
            ArrayList<Number> scoreEntry = new ArrayList<Number>() ;
            hashMapValue = hashMapReport.get(key) ;
            for (int valueIndex = 0 ; valueIndex < hashMapValue.length ; valueIndex++ )
                scoreEntry.add(hashMapValue[valueIndex]) ;
            scoreNumbers.add((ArrayList<Number>) scoreEntry.clone()) ;
        }
        //categoryData.add(categoryEntry) ;
        chart_awt.callStackedPlotChart(chartTitle,categoryEntry,scoreNumbers,scoreNames,categoryName) ;
    }
    
    /**
     * Presents scoreName as a function of categoryName from reportArray[cycle]
     * or HashMap
     * @param categoryName
     * @param scoreName
     * @param reportArray
     * @param cycle 
     */
    protected void callPlotChartDefault(String categoryName, String scoreName, ArrayList<Object> reportArray, int cycle)
    {
        String record = (String) reportArray.get(cycle) ;
        
        // Extract data from report
        parseRecord(categoryName, scoreName, record) ;
        
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,categoryData.get(0),scoreData.get(0),scoreName,categoryName) ;
    }
    
    /**
     * 
     * @param (String) scoreName
     * @param (ArrayList<ArrayList<String>>) reportArray
     * @return (String[]) Each entry is String.valueOf(the number of entries in each entry of reportArray)
     */
    protected ArrayList<Object> prepareEventsPerCycle(String scoreName, ArrayList<ArrayList<Object>> reportArray)
    {
        ArrayList<Object> eventsPerCycle = new ArrayList<Object>() ;
        scoreName += ":" ;
        
        for (ArrayList<Object> report : reportArray)
        {
            eventsPerCycle.add(scoreName + Integer.toString(report.size()) + " ") ;
        }
        
        return eventsPerCycle ;
        
    }
    
    public void plotEventsPerCycle(String scoreName, ArrayList<ArrayList<Object>> reportArray)
    {
        ArrayList<Object> eventsPerCycle = prepareEventsPerCycle(scoreName,reportArray) ;
        
        callPlotChart(scoreName,eventsPerCycle) ;
    }

    public void plotCycleValue(String scoreName, ArrayList<Object> reportArray)
    {
        //LOGGER.info("plotCycleValue") ;
        callPlotChart(scoreName,reportArray) ;
    }            
            
    public void multiPlotCycleValue(String scoreName, ArrayList<ArrayList<Object>> reportArrays, String[] legend)
    {
        //LOGGER.info("plotCycleValue") ;
        callMultiPlotChart(scoreName,reportArrays,legend) ;
    }            
            
    public void multiPlotCycleValue(ArrayList<String> scoreNames, ArrayList<ArrayList<Object>> reportArrays, String[] legend)
    {
        //LOGGER.info("plotCycleValue") ;
        callMultiPlotChart(scoreNames,reportArrays,legend) ;
    }            
            
    public void multiPlotCycleValue(ArrayList<String> scoreNames, ArrayList<Object> reportArrays)
    {
        //LOGGER.info("plotCycleValue") ;
        callMultiPlotChart(scoreNames,reportArrays) ;
    }            
            
    /**
     * Uses reflect to call Method prepareReportNameReport()
     * @param reportName
     * @return reportArray returned by prepareReportNameReport()
     */
    private ArrayList<Object> getReportArray(String reportName)
    {
        ArrayList<Object> reportArray = new ArrayList<Object>() ;
        
        // Name of Method which provides report
        String reportMethodName = "prepare" + reportName + "Report" ;
        try
        {
            Class reporterClass = reporter.getClass().asSubclass(reporter.getClass()) ;
            Method prepareReportMethod = reporterClass.getMethod(reportName) ;
            reportArray = (ArrayList<Object>) prepareReportMethod.invoke(reporter) ;
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
    private void parseRecord(String categoryName, String scoreNames, String report)
    {
        parseRecord(new String[] {categoryName}, scoreNames, report) ;
    }
    
    /**
     * Extracts category (x) and score (y) data and records in corresponding fields
     * @param categoryName
     * @param scoreName
     * @param report 
     */
    private void parseRecord(String[] categoryNames, String scoreName, String report)
    {        
        for (int plotIndex = 0 ; plotIndex < categoryNames.length ; plotIndex++ )
        {
            int categoryIndex = Reporter.indexOfProperty(categoryNames[plotIndex],report) ;

            categoryData.add(Reporter.extractAllValues(categoryNames[plotIndex], report, categoryIndex)) ;
            scoreData.add(Reporter.extractAllValues(scoreName, report, categoryIndex)) ;
        }
    }

    /**
     * Extracts one value for scoreName from each report cycle.
     * Intended for plots over time.
     * @param scoreName
     * @param report 
     */
    private void parseReportArray(String scoreName, ArrayList<Object> report)
    {       
        ArrayList<Object> plotArray = new ArrayList<Object>() ;
        for (Object record : report)
        {
            String value = Reporter.extractValue(scoreName,String.valueOf(record)) ;
            plotArray.add(value) ;
        }
        scoreData.add(plotArray) ;
    }

    /**
     * Extracts one value for scoreName from each report cycle of each report.
     * Intended for plots over time.
     * @param scoreName
     * @param reports 
     */
    private void parseReportArrays(String scoreName, ArrayList<ArrayList<Object>> reports)
    {       
        ArrayList<Object> plotArray ;
        
        for (ArrayList<Object> report : reports)
        {
            plotArray = new ArrayList<Object>() ;
            for (Object record : report)
            {
                String value = Reporter.extractValue(scoreName,String.valueOf(record)) ;
                plotArray.add(value) ;
            }
            scoreData.add(plotArray) ;
        }
    }

    /**
     * Extracts one value for scoreName from each report cycle of each report.
     * Intended for plots over time.
     * @param scoreName
     * @param reports 
     */
    private void parseReportArrays(ArrayList<String> scoreNames, ArrayList<ArrayList<Object>> reports)
    {       
        ArrayList<Object> plotArray ;
        String scoreName ;
        ArrayList<Object> report ;
        
        for (int index = 0 ; index < scoreNames.size() ; index++ )
        {
            plotArray = new ArrayList<Object>() ;
            scoreName = scoreNames.get(index) ;
            report = reports.get(index) ;
            
            // An empty entry in reports indicates that the previous report should be used.
            if (report.isEmpty())
                report = reports.get(index - 1) ;
            
            // Add value to plotArray for scoreData
            for (Object record : report)
            {
                String value = Reporter.extractValue(scoreName,String.valueOf(record)) ;
                plotArray.add(value) ;
            }
            scoreData.add(plotArray) ;
        }
    }

    /**
     * Extracts one value for scoreName from each report cycle of each report.
     * Intended for plots over time.
     * @param scoreName
     * @param reports 
     */
    private void parseReportArray(ArrayList<String> scoreNames, ArrayList<Object> report)
    {       
        ArrayList<Object> plotArray ;
        
        for (String scoreName : scoreNames)
        {
            plotArray = new ArrayList<Object>() ;
            
            // Add value to plotArray for scoreData
            for (Object record : report)
            {
                String value = Reporter.extractValue(scoreName,String.valueOf(record)) ;
                plotArray.add(value) ;
            }
            scoreData.add((ArrayList<Object>) plotArray.clone()) ;
        }
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
         * Invokes plotLineChart() for networks (eg. Agent-to-Agent) after invoking
         * createHubData() to generate Dataset
         * @param chartTitle
         * @param networkData
         * @param hub
         * @param hubCycle
         * @param yLabel
         * @param xLabel 
         */
        private void callPlotNetwork(String chartTitle, ArrayList<HashMap<Object,ArrayList<Object>>> networkData, 
                String yLabel, String xLabel)
        {
            XYSeriesCollection dataset = createHubDataset(networkData) ;
            plotLineChart(chartTitle, dataset, yLabel, xLabel, new String[] {""}) ;
        }

        /**
         * Calls Method callPlotChart() for plots over time after generating dataset without legend
         * @param chartTitle
         * @param dataArray
         * @param yLabel 
         */
        private void callPlotChart(String chartTitle, ArrayList<ArrayList<Object>> dataArray, String yLabel)
        {
            callPlotChart(chartTitle, dataArray, yLabel, new String[] {""}) ;
        }
        
        /**
         * Calls Method plotLineChart() for plots over time after generating dataset
         * @param chartTitle
         * @param dataArray
         * @param yLabel 
         */
        private void callPlotChart(String chartTitle, ArrayList<ArrayList<Object>> dataArray, String yLabel, String[] legend)
        {
            //LOGGER.info("callPlotChart()") ;
            XYSeriesCollection dataset = createXYDataset(dataArray,legend) ;
            plotLineChart(chartTitle, dataset, yLabel, "cycle", legend) ;
        }
        
        /**
         * Creates scatter plot by generating a suitable Dataset from HashMaps in dataArray and feeding it plotScatterPlot().
         * @param chartTitle
         * @param dataArray
         * @param yLabel
         * @param xLabel 
         */
        private void callPlotScatterPlots(String chartTitle, ArrayList<HashMap<Object,ArrayList<Object>>> dataArray, String yLabel, String xLabel)
        {
            XYSeriesCollection dataset = createScatterPlotDataset(dataArray, chartTitle) ;
            plotScatterPlot(chartTitle, dataset, yLabel, xLabel) ;
        }
       
        /**
         * Creates scatter plot by generating a suitable Dataset from dataHashMap and feeding it plotScatterPlot().
         * @param chartTitle
         * @param dataHashMap
         * @param yLabel
         * @param xLabel 
         */
        private void callPlotScatterPlot(String chartTitle, HashMap<Object,ArrayList<Object>> dataHashMap, String yLabel, String xLabel)
        {
            XYSeriesCollection dataset = createScatterPlotDataset(dataHashMap, chartTitle) ;
            plotScatterPlot(chartTitle, dataset, yLabel, xLabel) ;
        }
       
        /**
         * Calls method plotChart for within-cycle plots after generating dataset
         * @param chartTitle
         * @param categoryArray
         * @param scoreArray
         * @param yLabel
         * @param xLabel 
         */
        private void callPlotChart(String chartTitle, ArrayList<Object> categoryArray, ArrayList<Object> scoreArray, String yLabel, String xLabel)
        {
            CategoryDataset dataset = createDataset(xLabel, categoryArray, scoreArray) ;
            plotBarChart(chartTitle, dataset, yLabel, xLabel) ;
        }
        
        
        private void callStackedPlotChart(String chartTitle, ArrayList<Object> categoryArray, ArrayList<ArrayList<Number>> scoreArray, String[] scoreNames, String xLabel)
        {
            //LOGGER.info("callPlotChartInteger()") ;
            CategoryDataset dataset = createDataset(scoreNames, categoryArray, scoreArray) ;
            plotStackedBarChart(chartTitle, dataset, scoreNames, xLabel) ;
        }
        
        
        
        /**
         * Redundant version of callPlotChart allowing for scoreArray to be integer instead of Object.
         * TODO: Refactor and remove, replace with callPlotChart() 
         * @param chartTitle
         * @param categoryArray
         * @param scoreArray
         * @param yLabel
         * @param xLabel 
         */
        private void callPlotChartInteger(String chartTitle, ArrayList<Object> categoryArray, ArrayList<Number> scoreArray, String yLabel, String xLabel)
        {
            //LOGGER.info("callPlotChartInteger()") ;
            CategoryDataset dataset = createDatasetInteger(xLabel, categoryArray, scoreArray) ;
            plotBarChart(chartTitle, dataset, yLabel, xLabel) ;
        }
        
        /**
         * Generates plot of dataset
         * @param chartTitle
         * @param dataset
         * @param yLabel
         * @param xLabel 
         */
        private void plotBarChart(String chartTitle, CategoryDataset dataset, String yLabel, String xLabel)
        {
            //LOGGER.info("plotBarChart()");
            JFreeChart barChart = ChartFactory.createBarChart(chartTitle,xLabel,
                yLabel,dataset,PlotOrientation.VERTICAL,true, true, false);
            
            //barChart.getXYPlot().getDomainAxis().set.setTickUnit(new NumberTickUnit(dataset.getColumnCount()/20)) ;
            saveChart(barChart,chartTitle) ;
            displayChart(barChart) ;
            
        }
        
        private void plotStackedBarChart(String chartTitle, CategoryDataset dataset, String[] scoreNames , String xLabel)
        {
            //LOGGER.info("plotBarChart()");
            JFreeChart barChart = ChartFactory.createStackedBarChart(chartTitle,xLabel,
                scoreNames[0],dataset,PlotOrientation.VERTICAL,true, true, false);
            
            GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
            KeyToGroupMap map = new KeyToGroupMap("G1");
            for (String name : scoreNames)
                map.mapKeyToGroup(name, "G1");
            renderer.setSeriesToGroupMap(map); 
            CategoryPlot plot = (CategoryPlot) barChart.getPlot();
            plot.setRenderer(renderer);
            //plot.setFixedLegendItems(createLegendItems());
        
            saveChart(barChart,chartTitle) ;
            displayChart(barChart) ;
            
        }
        
        private void plotScatterPlot(String chartTitle, XYDataset dataset, String yLabel, String xLabel)
        {
            JFreeChart scatterPlot = ChartFactory.createScatterPlot(chartTitle, xLabel, yLabel, dataset, PlotOrientation.VERTICAL,true,true,false) ;
            //JFreeChart scatterPlot = ChartFactory.createScatterPlot(xLabel, xLabel, xLabel, dataset, PlotOrientation.HORIZONTAL, rootPaneCheckingEnabled, rootPaneCheckingEnabled, rootPaneCheckingEnabled)
            
            NumberAxis rangeAxis = (NumberAxis) scatterPlot.getXYPlot().getRangeAxis() ;
            rangeAxis.setTickUnit(new NumberTickUnit(1)) ;
            saveChart(scatterPlot,chartTitle) ;
            displayChart(scatterPlot) ;
            
        }
        
        /**
         * Generates plot of dataset
         * @param chartTitle
         * @param dataset
         * @param yLabel
         * @param xLabel 
         */
        private void plotLineChart(String chartTitle, XYDataset dataset, String yLabel, String xLabel, String[] legend)
        {
            boolean showLegend = !(legend[0].isEmpty()) ;
            JFreeChart lineChart = ChartFactory.createXYLineChart(applicationTitle,xLabel,
                yLabel,dataset,PlotOrientation.VERTICAL,showLegend, true, false);
            
            NumberAxis domainAxis = (NumberAxis) lineChart.getXYPlot().getDomainAxis() ;
            domainAxis.setTickUnit(new NumberTickUnit(dataset.getItemCount(0)/20)) ;
            
            // Set unit tick distance if range is integer.
            //LOGGER.info(String.valueOf(dataset.getX(0,0)));
            if (int.class.isInstance(dataset.getX(0,0)))
            {
                NumberAxis rangeAxis = (NumberAxis) lineChart.getXYPlot().getRangeAxis() ;
                rangeAxis.setTickUnit(new NumberTickUnit(1)) ;
            }
            saveChart(lineChart,chartTitle) ;
            displayChart(lineChart) ;
            
        }
        
        private void displayChart(JFreeChart barChart)
        {
            ChartPanel chartPanel = new ChartPanel( barChart );        
            //chartPanel.setPreferredSize(new java.awt.Dimension( 2240 , 734 ) );        
            chartPanel.setPreferredSize(new java.awt.Dimension( 1120 , 367 ) );        
            //chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
            setContentPane( chartPanel ); 
            pack() ;
            setVisible(true) ;
            LOGGER.info(System.getProperty("os.name")) ;
        }
        
        private void saveChart(JFreeChart barChart, String title)
        {
            String directory = "../" + Community.FILE_PATH ;
            String address = directory + title + Community.NAME_ROOT + ".jpg" ;
            //int width = 2560 ;
            int width = 1280 ;
            //int width = 640 ;
            //int height = 960 ;
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
        }
        
        /**
         * Generate Dataset of scoreData as function of categoryData.
         * Usually used for within-cycle plots
         * @param category
         * @param categoryData
         * @param scoreData
         * @return 
         */
        private CategoryDataset createDataset(String category, ArrayList<Object> categoryData, ArrayList<Object> scoreData)
        {
            DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset() ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            
            String categoryValue ;
            int scoreValue ;
            
            for (int index = 0 ; index < scoreData.size() ; index++ )
            {
                categoryValue = (String) categoryData.get(index) ;
                scoreValue = Integer.valueOf((String) scoreData.get(index)) ;
                categoryDataset.addValue( scoreValue, category, categoryValue ) ;
            }
            return categoryDataset ;
        }
        
        private CategoryDataset createDatasetInteger(String category, ArrayList<Object> categoryData, ArrayList<Number> scoreData)
        {
            DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset() ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            
            String categoryValue ;
            Number scoreValue ;
            
            for (int index = 0 ; index < scoreData.size() ; index++ )
            {
                categoryValue = String.valueOf(categoryData.get(index)) ;
                scoreValue = scoreData.get(index) ;
                categoryDataset.addValue( scoreValue, category, categoryValue ) ;
            }
            return categoryDataset ;
        }
        
        private CategoryDataset createDataset(String[] scoreNames, ArrayList<Object> categoryData, ArrayList<ArrayList<Number>> scoreData)
        {
            DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset() ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            
            String categoryValue ;
            ArrayList<Number> scoreValueArray ;
            //Number scoreValue ;
            
            for (int index = 0 ; index < scoreData.size() ; index++ )
            {
                categoryValue = String.valueOf(categoryData.get(index)) ;
                scoreValueArray = scoreData.get(index) ;
                for (int scoreIndex = 0 ; scoreIndex < scoreValueArray.size() ; scoreIndex++ )
                {
                    Number scoreValue = scoreValueArray.get(scoreIndex) ;
                    String scoreName = scoreNames[scoreIndex] ;
                    categoryDataset.addValue( scoreValue, scoreName, categoryValue ) ;
                }
            }
            return categoryDataset ;
        }
        
        /**
         * Generate Dataset from scoreData.
         * Intended for plots over time/cycles
         * @param scoreData
         * @return CategoryDataset of score over cycle
         */
        private XYSeriesCollection createXYDataset(ArrayList<ArrayList<Object>> scoreData, String[] legend)
        {
            XYSeriesCollection xySeriesCollection = new XYSeriesCollection() ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            Number scoreValue ;
            int dataSize ;
            ArrayList<Object> data ;
            int plotTotal ;
            if (legend.length > 0)
                plotTotal = legend.length ;
            else
            {
                plotTotal = 1 ;
                legend = new String[] {""} ;
            }
            
            for (int plotNumber = 0 ; plotNumber < plotTotal ; plotNumber++ )
            {
                XYSeries xySeries = new XYSeries(legend[plotNumber]) ;

                data = scoreData.get(plotNumber) ;
                dataSize = data.size();
                scoreValue = 0 ;

                for (int index = 0 ; index < dataSize; index++ )
                {
                    String scoreString = (String) data.get(index) ;
                    if (int.class.isInstance(scoreString)) 
                        scoreValue = Integer.valueOf(scoreString) ;
                    else
                        scoreValue = Double.valueOf(scoreString) ;
                    xySeries.add(1 + index, scoreValue, false);
                }
                try
                {
                    xySeriesCollection.addSeries((XYSeries) xySeries.clone()) ;
                }
                catch ( CloneNotSupportedException cnse )
                {
                    LOGGER.log(Level.SEVERE, cnse.toString());
                }
            }
            return xySeriesCollection ;
        }
        
        /**
         * Generates Dataset suitable for scatter plots on XYPlot from an ArrayList<HashMap>.
         * Suitable for plots over multiple cycles.
         * @param hashMapArrayList
         * @param plotTitle
         * @return 
         */
        private XYSeriesCollection createScatterPlotDataset(ArrayList<HashMap<Object,ArrayList<Object>>> hashMapArrayList, 
                String plotTitle)
        {
            XYSeriesCollection scatterPlotDataset = new XYSeriesCollection() ;
            String seriesTitle ;
            HashMap<Object,ArrayList<Object>> hashMap ;
                
            for (int index = 0 ; index < hashMapArrayList.size() ; index++ )
            {
                seriesTitle = plotTitle + "_" + String.valueOf(index);
                hashMap = hashMapArrayList.get(index);
                scatterPlotDataset.addSeries(createScatterPlotSeries(hashMap,seriesTitle));
            }
            return scatterPlotDataset ;
        }
        
        /**
         * Generates Dataset suitable for scatter plots on XYPlot from single HashMap.
         * Suitable for plots with a single cycle.
         * @param agentToAgentHashMap
         * @param plotTitle
         * @return (XYSeriesCollection) 
         */
        private XYSeriesCollection createScatterPlotDataset(HashMap<Object,ArrayList<Object>> agentToAgentHashMap, String plotTitle)
        {
            return new XYSeriesCollection(createScatterPlotSeries(agentToAgentHashMap,plotTitle)) ;
        }
        
        /**
         * 
         * @param agentToAgentHashMap
         * @param seriesTitle
         * @return (XYSeries) with entires suitable for XYPlot.
         */
        private XYSeries createScatterPlotSeries(HashMap<Object,ArrayList<Object>> agentToAgentHashMap, String seriesTitle)
        {
            XYSeries scatterPlotDataset = new XYSeries(seriesTitle) ;
            for (Object positiveAgent : agentToAgentHashMap.keySet())
                for (Object negativeAgent : agentToAgentHashMap.get(positiveAgent))
                    scatterPlotDataset.add((Number) positiveAgent, (Number) negativeAgent) ;
            return scatterPlotDataset ;
        }
        
        /**
         * 
         * @param cycleToAgentArray
         * @param hubTitle
         * @return 
         */
        private XYSeriesCollection createHubDataset(ArrayList<HashMap<Object,ArrayList<Object>>> cycleToAgentArray)
        {
            XYSeriesCollection hubSeriesCollection = new XYSeriesCollection() ;
            HashMap<Object,Number> lastInfected = new HashMap<Object,Number>() ;
            
            for (int cycle = 0 ; cycle < cycleToAgentArray.size() ; cycle++ )
            {
                HashMap<Object,ArrayList<Object>> agentToAgentHashMap = cycleToAgentArray.get(cycle) ;
                for (Object transmitterId : agentToAgentHashMap.keySet())
                {
                    String seriesTitle = "cycle" + String.valueOf(cycle) 
                            + "agentId" + String.valueOf(transmitterId) ;
                    XYSeries hubSeries = new XYSeries(seriesTitle,false,true) ;
            
                    if (!lastInfected.containsKey(transmitterId))
                        lastInfected.put(transmitterId, 0) ;
                    ArrayList<Object> toAgentArray = agentToAgentHashMap.get(transmitterId) ;
                    //LOGGER.log(Level.INFO, "{0}", toAgentArray ) ;
                    hubSeries = generateHub((Number) transmitterId, lastInfected.get(transmitterId), toAgentArray, cycle, hubSeries) ;
                    for (Object receiverId : toAgentArray)
                        lastInfected.put(receiverId, cycle) ;
                    try
                    {
                        hubSeriesCollection.addSeries((XYSeries) hubSeries.clone());
                    }
                    catch ( CloneNotSupportedException cnse )
                    {
                        LOGGER.info(cnse.toString());
                        hubSeriesCollection.addSeries(hubSeries);
                    }
                }
            }
            return hubSeriesCollection ;
        }
        
        /**
         * 
         * @param hubId
         * @param hubCycle
         * @param hubHashMap
         * @param nodeCycle
         * @return (XYSeries) with additional nodes showing transmissions in cycle nodeCycle from Agent hubId infected in cycle hubCycle.
         */
        private XYSeries generateHub(Number hubId, Number hubCycle, ArrayList<Object> hubArray, Number nodeCycle, XYSeries hubSeries)
        {
            for (Object nodeId : hubArray)
                hubSeries = generateHubNode(hubId, hubCycle, (Number) nodeId, nodeCycle, hubSeries) ;
            //for (double[] entry : hubSeries.toArray()) 
              //  System.out.println(entry[0] + entry[1]);
            return hubSeries ;
        }

       /**
         * 
         * @param hubId
         * @param hubCycle
         * @param hubHashMap
         * @param nodeCycle
         * @param hubSeries (XYSeries) to be added to
         * @return (XYSeries) with additional nodes showing transmissions in cycle nodeCycle from Agent hubId infected in cycle hubCycle.
         */
        private XYSeries generateHubNode(Number hubId, Number hubCycle, Number nodeId, Number nodeCycle, XYSeries hubSeries)
        {
            //XYSeries hubSeries = new XYSeries("Infections by agentId " + String.valueOf(hubId)) ;
            //hubSeries.add(hubId, hubCycle) ;
            hubSeries = hubEntry(hubId, hubCycle, nodeId, nodeCycle, hubSeries) ;
            
            return hubSeries ;
        }
        
        /**
         * Adds to xySeries the lines needed to add a node to a hub and be 
         * ready to add next node
         * @param hub
         * @param node
         * @param xySeries 
         */
        private XYSeries hubEntry(Number hubId, Number hubCycle, Number nodeId, Number nodeCycle, XYSeries xySeries)
        {
            xySeries.add(hubId, hubCycle);
            xySeries.add(nodeId, nodeCycle);
            xySeries.add(hubId, hubCycle);
            return xySeries ;
        }
    }
    
}
