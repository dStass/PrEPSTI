/*
 * Presenter class, takes data from Reporter objects and presents them
 * graphically.
 */
package reporter.presenter;

import reporter.* ;

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
    protected void plotChart(String scoreName, String reportName)
    {
        // Get full report reportName
        ArrayList<String> reportArray = getReportArray(reportName) ;
        
        callPlotChart(scoreName, reportArray) ;
        return ;
    }
    
    /**
     * Generates network diagram from data in hashMapArray.
     * @param xLabel
     * @param yLabel
     * @param hashMapArray 
     */
    protected void callPlotNetwork(String xLabel, String yLabel, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> hashMapArray)
    {
        int xHub = 0;
        for (int key : hashMapArray.keySet())
        {
            xHub = key ;
            break ;
        }
        int yHub = 0 ;
        chart_awt.callPlotNetwork(chartTitle, hashMapArray, xHub, yHub, xLabel, yLabel) ;
    }

    /**
     * Presents reportArray as a function of time/cycle.
     * @param scoreName
     * @param reportArray 
     */
    protected void callPlotChart(String scoreName, ArrayList<String> reportArray)
    {
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
        ArrayList<String> reportArray = getReportArray(reportName) ;
        
        callPlotChartDefault(categoryName, scoreName, reportArray, cycle) ;
    }
    
    protected void plotHashMapScatter(String categoryName, String scoreName, HashMap<Integer,ArrayList<Integer>> hashMapReport )
    {
        chart_awt.callPlotScatterPlot(chartTitle, hashMapReport, scoreName, categoryName) ;
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
        
        callPlotChart(scoreName,eventsPerCycle) ;
    }

    public void plotCycleValue(String scoreName, ArrayList<String> reportArray)
    {
        callPlotChart(scoreName,reportArray) ;
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
        private void callPlotNetwork(String chartTitle, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> networkData, 
                int hub, int hubCycle, String yLabel, String xLabel)
        {
            XYSeriesCollection dataset = createHubDataset(hub, hubCycle, networkData, chartTitle) ;
            plotLineChart(chartTitle, dataset, yLabel, xLabel) ;
        }

        /**
         * Calls Method plotLineChart() for plots over time after generating dataset
         * @param chartTitle
         * @param dataArray
         * @param yLabel 
         */
        private void callPlotChart(String chartTitle, ArrayList<String> dataArray, String yLabel)
        {
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
        private void callPlotScatterPlots(String chartTitle, ArrayList<HashMap<Integer,ArrayList<Integer>>> dataArray, String yLabel, String xLabel)
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
        private void callPlotScatterPlot(String chartTitle, HashMap<Integer,ArrayList<Integer>> dataHashMap, String yLabel, String xLabel)
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
        private void callPlotChart(String chartTitle, ArrayList<String> categoryArray, ArrayList<String> scoreArray, String yLabel, String xLabel)
        {
            CategoryDataset dataset = createDataset(xLabel, categoryArray, scoreArray) ;
            plotBarChart(chartTitle, dataset, yLabel, xLabel) ;
        }
        
        private void callPlotChartInteger(String chartTitle, ArrayList<String> categoryArray, ArrayList<Integer> scoreArray, String yLabel, String xLabel)
        {
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
            JFreeChart lineChart = ChartFactory.createXYLineChart(applicationTitle,xLabel,
                yLabel,dataset,PlotOrientation.VERTICAL,true, true, false);
            
            NumberAxis domainAxis = (NumberAxis) lineChart.getXYPlot().getDomainAxis() ;
            domainAxis.setTickUnit(new NumberTickUnit(dataset.getItemCount(0)/20)) ;
            
            // Set unit tick distance if range is integer.
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
            chartPanel.setPreferredSize(new java.awt.Dimension( 2240 , 734 ) );        
            //chartPanel.setPreferredSize(new java.awt.Dimension( 1120 , 367 ) );        
            //chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
            setContentPane( chartPanel ); 
            pack() ;
            setVisible(true) ;
        }
        
        private void saveChart(JFreeChart barChart, String title)
        {
            String directory = "../output/test/" ;
            String address = directory + title + ".jpg" ;
            int width = 2560 ;
            //int width = 1280 ;
            //int width = 640 ;
            int height = 960 ;
            //int height = 480 ;
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
        private XYSeriesCollection createXYDataset(ArrayList<String> scoreData)
        {
            XYSeries xySeries = new XYSeries(applicationTitle) ;
            // ArrayList<String> categoryData = data.get(0) ;
            // ArrayList<String> scoreData = data.get(1) ;
            
            Number scoreValue ;
            int dataSize = scoreData.size() ;
            
            for (int index = 0 ; index < dataSize; index++ )
            {
                String scoreString = scoreData.get(index) ;
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
        private XYSeriesCollection createScatterPlotDataset(ArrayList<HashMap<Integer,ArrayList<Integer>>> hashMapArrayList, 
                String plotTitle)
        {
            XYSeriesCollection scatterPlotDataset = new XYSeriesCollection() ;
            String seriesTitle ;
            HashMap<Integer,ArrayList<Integer>> hashMap ;
                
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
        private XYSeriesCollection createScatterPlotDataset(HashMap<Integer,ArrayList<Integer>> agentToAgentHashMap, String plotTitle)
        {
            return new XYSeriesCollection(createScatterPlotSeries(agentToAgentHashMap,plotTitle)) ;
        }
        
        /**
         * 
         * @param agentToAgentHashMap
         * @param seriesTitle
         * @return (XYSeries) with entires suitable for XYPlot.
         */
        private XYSeries createScatterPlotSeries(HashMap<Integer,ArrayList<Integer>> agentToAgentHashMap, String seriesTitle)
        {
            XYSeries scatterPlotDataset = new XYSeries(seriesTitle) ;
            for (int positiveAgent : agentToAgentHashMap.keySet())
                for (int negativeAgent : agentToAgentHashMap.get(positiveAgent))
                    scatterPlotDataset.add(positiveAgent, negativeAgent) ;
            return scatterPlotDataset ;
        }
        
        private XYSeriesCollection createHubDataset(int hubId, int hubCycle, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> agentToCycleArray, String hubTitle)
        {
            XYSeries hubSeries = new XYSeries(hubTitle,false,true) ;
            // for (int receiverId : agentToAgentArray.keySet())
            {
                LOGGER.log(Level.INFO, "{0}", agentToCycleArray.get(hubId)) ;
                hubSeries = generateHub(hubId, hubCycle, agentToCycleArray.get(hubId), hubTitle) ;
            }
            return new XYSeriesCollection(hubSeries) ;
        }
        
        /**
         * 
         * @param hubId
         * @param hubCycle
         * @param hubHashMap
         * @param nodeCycle
         * @return (XYSeries) with additional nodes showing transmissions in cycle nodeCycle from Agent hubId infected in cycle hubCycle.
         */
        private XYSeries generateHub(int hubId, int hubCycle, HashMap<Integer,ArrayList<Integer>> hubHashMap, String hubTitle)
        {
            XYSeries hubSeries = new XYSeries(hubTitle,false,true) ;
            for (int nodeId : hubHashMap.keySet())
                hubSeries = generateHubNode(hubId, hubCycle, nodeId, hubHashMap.get(nodeId), hubSeries) ;
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
        private XYSeries generateHubNode(int hubId, int hubCycle, int nodeId, ArrayList<Integer> nodeCycles, XYSeries hubSeries)
        {
            //XYSeries hubSeries = new XYSeries("Infections by agentId " + String.valueOf(hubId)) ;
            //hubSeries.add(hubId, hubCycle) ;
            for ( int nodeCycle : nodeCycles )
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
        private XYSeries hubEntry(int hubId, int hubCycle, int nodeId, int nodeCycle, XYSeries xySeries)
        {
            xySeries.add(nodeId, nodeCycle);
            xySeries.add(hubId, hubCycle);
            return xySeries ;
        }
    }
    
}
