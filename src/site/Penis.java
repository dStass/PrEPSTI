/**
 * 
 */
package site;

import com.sun.media.jfxmedia.logging.Logger;

/**
 * @author MichaelWalker
 *
 */
public class Penis extends Site {

	// Constants that vary according to Site subclass
	// Probability of initial gonorrhoea infection 
	static double INITIAL = 0.3 ;
	
	// Probability of positive symptomatic status if infected
	static double SYMPTOMATIC = 0.9 ;
	
	// Probability of site transmitting infection, if all other probabilities unity
	static double TRANSMIT = 0.9 ;

	// Probability of site becoming infected, if all other probabilities unity
    static double RECEIVE = 0.5 ;
     
	
	/**
	 * 
	 */
	public Penis() 
	{
		super() ;
		Logger.logMsg(0, this.getSite());
	}

}
