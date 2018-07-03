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
import org.jfree.chart.ChartUtils ;
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
    
    protected ArrayList<Object> categoryData = new ArrayList<Object>() ;
    protected ArrayList<Object> scoreData = new ArrayList<Object>() ;
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
        LOGGER.info("plotHashMap()") ;
        //ArrayList<String> categoryInteger = new ArrayList<String>() ;
        ArrayList<Number> scoreNumber = new ArrayList<Number>() ;
        
        categoryData.clear();
        for (Object key : hashMapReport.keySet())
        {
            if (key.equals(null))
                continue ;
            categoryData.add(key) ;
        }
        categoryData.sort(null);
        for (Object key : categoryData)
        {
            scoreNumber.add(hashMapReport.get(key)) ;
        }
        chart_awt.callPlotChartInteger(chartTitle,categoryData,scoreNumber,scoreName,categoryName) ;
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
        String report = (String) reportArray.get(cycle) ;
        
        // Extract data from report
        parseReport(categoryName, scoreName, report) ;
        
        // Send data to be processed and presented
        chart_awt.callPlotChart(chartTitle,categoryData,scoreData,scoreName,categoryName) ;
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
    private void parseReport(String categoryName, String scoreName, String report)
    {        
        int categoryIndex = Reporter.indexOfProperty(categoryName,report) ;
        
        categoryData = Reporter.extractAllValues(categoryName, report, categoryIndex) ;
        scoreData = Reporter.extractAllValues(scoreName, report, categoryIndex) ;
    }

    /**
     * Extracts one value for scoreName from each report cycle.
     * Intended for plots over time.
     * @param scoreName
     * @param reports 
     */
    private void parseReportArray(String scoreName, ArrayList<Object> reports)
    {       
        for (Object report : reports)
        {
            String value = Reporter.extractValue(scoreName,String.valueOf(report)) ;
            scoreData.add(value) ;
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
            XYSeriesCollection dataset = createHubDataset(networkData, chartTitle) ;
            plotLineChart(chartTitle, dataset, yLabel, xLabel) ;
        }

        /**
         * Calls Method plotLineChart() for plots over time after generating dataset
         * @param chartTitle
         * @param dataArray
         * @param yLabel 
         */
        private void callPlotChart(String chartTitle, ArrayList<Object> dataArray, String yLabel)
        {
            //LOGGER.info("callPlotChart()") ;
            XYSeriesCollection dataset = createXYDataset(dataArray) ;
            plotLineChart(chartTitle, dataset, yLabel, "cycle") ;
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
        private void plotLineChart(String chartTitle, XYDataset dataset, String yLabel, String xLabel)
        {
            //LOGGER.info("plotLineChart()") ;
            JFreeChart lineChart = ChartFactory.createXYLineChart(applicationTitle,xLabel,
                yLabel,dataset,PlotOrientation.VERTICAL,true, true, false);
            
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
        }
        
        private void saveChart(JFreeChart barChart, String title)
        {
            String directory = "../output/test/" ;
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
        
        /**
         * Generate Dataset from scoreData.
         * Intended for plots over time/cycles
         * @param scoreData
         * @return CategoryDataset of score over cycle
         */
        private XYSeriesCollection createXYDataset(ArrayList<Object> scoreData)
        {
            XYSeries xySeries = new XYSeries(applicationTitle) ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            
            Number scoreValue ;
            int dataSize = scoreData.size() ;
            
            for (int index = 0 ; index < dataSize; index++ )
            {
                String scoreString = (String) scoreData.get(index) ;
                if (int.class.isInstance(scoreString)) 
                    scoreValue = Integer.valueOf(scoreString) ;
                else
                    scoreValue = Double.valueOf(scoreString) ;
                xySeries.add(1 + index, scoreValue, false);
            }
            return new XYSeriesCollection(xySeries) ;
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
        private XYSeriesCollection createHubDataset(ArrayList<HashMap<Object,ArrayList<Object>>> cycleToAgentArray, String hubTitle)
        {
            XYSeriesCollection hubSeriesCollection = new XYSeriesCollection() ;
            HashMap<Object,Number> lastInfected = new HashMap<Object,Number>() ;
            
            for (int cycle = 0 ; cycle < cycleToAgentArray.size() ; cycle++ )
            {
                HashMap<Object,ArrayList<Object>> agentToAgentHashMap = cycleToAgentArray.get(cycle) ;
                for (Object transmitterId : agentToAgentHashMap.keySet())
                {
                    XYSeries hubSeries = new XYSeries(hubTitle,false,true) ;
            
                    if (!lastInfected.containsKey(transmitterId))
                        lastInfected.put(transmitterId, 0) ;
                    ArrayList<Object> toAgentArray = agentToAgentHashMap.get(transmitterId) ;
                    //LOGGER.log(Level.INFO, "{0}", toAgentArray ) ;
                    hubSeries = generateHub((Number) transmitterId, lastInfected.get(transmitterId), toAgentArray, cycle, hubTitle, hubSeries) ;
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
        private XYSeries generateHub(Number hubId, Number hubCycle, ArrayList<Object> hubArray, Number nodeCycle, String hubTitle, XYSeries hubSeries)
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
