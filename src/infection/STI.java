/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infection;

/**
 *
 * @author MichaelWalker
 */
public class STI {
    
    // Designated number for Infection Class. Always 0 for base class please.
    static int INFECTION_NB = 0 ;
    // Probability of Urethra infection being symptomatic
    static double URETHRA_SYMPTOMATIC ;
    // Probability of Rectum infection being symptomatic
    static double RECTUM_SYMPTOMATIC ;
    // Probability of Pharynx infection being symptomatic
    static double PHARYNX_SYMPTOMATIC ;
    
    // Probability of transmission from Urethra to Urethra
    static double URETHRA_URETHRA = 0.1 ;
    // Probability of transmission from Urethra to Rectum
    static double URETHRA_RECTUM = 0.8 ;
    // Probability of transmission from Urethra to Pharynx
    static double URETHRA_PHARYNX = 0.6 ;
    // Probability of transmission from Rectum to Urethra
    static double RECTUM_URETHRA = 0.5 ;
    // Probability of transmission from Rectum to Rectum
    static double RECTUM_RECTUM = 0.1 ;
    // Probability of transmission from Rectum to Pharynx
    static double RECTUM_PHARYNX = 0.2 ;
    // Probability of transmission from Pharynx to Urethra
    static double PHARYNX_URETHRA = 0.5 ;
    // Probability of transmission from Pharynx to Rectum
    static double PHARYNX_RECTUM = 0.5 ;
    // Probability of transmission from Pharynx to Pharynx
    static double PHARYNX_PHARYNX = 0.5 ;
    
    /** Probability that condom use will block STI transmission. */
    static double CONDOM_EFFECT = 0.60 ;
    
    /** Whether this STI currently causing an infection. */
    private boolean infectionStatus = false ;
    
    /** Whether this STI currently causing symptoms (false if !infectionStatus). */
    private boolean symptomatic = false ;
    
    /**
     * Set INFECTION_NB. Always zero for STI Base class
     * @param nb
     * @return Description INFECTION_NB or warning if nb != 0
     */
    public static String SET_INFECTION_NB(int nb)
    {
        if (nb == 0)
        {
            return "STI nb:0 " ;
        }
        return "WARNING: Infection base class should have INFECTION_NB 0" ;
    }
    
    
    public static int GET_INFECTION_NB()
    {
        if (INFECTION_NB != 0)
        {
            // TODO: Replace with proper LOGGER
            System.out.println("WARNING: Infection base class should have INFECTION_NB 0");
        }
        return 0 ;
    }
    
    
    /**
     * 
     * @return  (Double) Probability of Urethra infection being symptomatic
     */
    public double getUrethraSymptomatic()
    {
        return URETHRA_SYMPTOMATIC ;
    }
    
    /**
     * 
     * @return (Double) Probability of Rectum infection being symptomatic
     */
    public double getRectumSymptomatic()
    {
        return RECTUM_SYMPTOMATIC ;
    }
    
    /**
     * 
     * @return (Double) Probability of Pharynx infection being symptomatic
     */
    public double getPharynxSymptomatic()
    {
        return PHARYNX_SYMPTOMATIC ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Urethra to Urethra
     */ 
    public double getUrethraUrethraTransmission()
    {
        return URETHRA_URETHRA ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Urethra to Rectum
     */ 
    public double getUrethraRectumTransmission()
    {
        return URETHRA_RECTUM ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Urethra to Pharynx
     */ 
    public double getUrethraPharynxTransmission()
    {
        return URETHRA_PHARYNX ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Rectum to Urethra
     */ 
    public double getRectumUrethraTransmission()
    {
        return RECTUM_URETHRA ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Rectum to Rectum
     */ 
    public double getRectumRectumTransmission()
    {
        return RECTUM_RECTUM ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Rectum to Pharynx
     */ 
    public double getRectumPharynxTransmission()
    {
        return RECTUM_PHARYNX ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Pharynx to Urethra
     */ 
    public double getPharynxUrethraTransmission()
    {
        return PHARYNX_URETHRA ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Pharynx to Rectum
     */
    public double getPharynxRectumTransmission()
    {
        return PHARYNX_RECTUM ;
    }
    
    /**
     * 
     * @return (Double)  Probability of transmission from Pharynx to Rectum
     */
    public double getPharynxPharynxTransmission()
    {
        return PHARYNX_PHARYNX ;
    }
    
    /**
     * 
     * @return (Double) Probability of condom preventing STI transmission, if used
     */
    public double getCondomEffect()
    {
        return CONDOM_EFFECT ;
    }
    
    public String getName()
    {
        return "Infection" ;
    }
    
}
