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
    // Probability of Penis infection being symptomatic
    static double PENIS_SYMPTOMATIC ;
    // Probability of Rectum infection being symptomatic
    static double RECTUM_SYMPTOMATIC ;
    // Probability of Pharynx infection being symptomatic
    static double PHARYNX_SYMPTOMATIC ;
    
    // Probability of transmission from Penis to Penis
    static double PENIS_PENIS = 0.1 ;
    // Probability of transmission from Penis to Rectum
    static double PENIS_RECTUM = 0.8 ;
    // Probability of transmission from Penis to Pharynx
    static double PENIS_PHARYNX = 0.6 ;
    // Probability of transmission from Rectum to Penis
    static double RECTUM_PENIS = 0.5 ;
    // Probability of transmission from Rectum to Rectum
    static double RECTUM_RECTUM = 0.1 ;
    // Probability of transmission from Rectum to Pharynx
    static double RECTUM_PHARYNX = 0.2 ;
    // Probability of transmission from Pharynx to Penis
    static double PHARYNX_PENIS = 0.5 ;
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
     * @return  (Double) Probability of Penis infection being symptomatic
     */
    public double getPenisSymptomatic()
    {
        return PENIS_SYMPTOMATIC ;
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
     * @return (Double) Probability of transmission from Penis to Penis
     */ 
    public double getPenisPenisTransmission()
    {
        return PENIS_PENIS ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Penis to Rectum
     */ 
    public double getPenisRectumTransmission()
    {
        return PENIS_RECTUM ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Penis to Pharynx
     */ 
    public double getPenisPharynxTransmission()
    {
        return PENIS_PHARYNX ;
    }
    
    /**
     * 
     * @return (Double) Probability of transmission from Rectum to Penis
     */ 
    public double getRectumPenisTransmission()
    {
        return RECTUM_PENIS ;
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
     * @return (Double) Probability of transmission from Pharynx to Penis
     */ 
    public double getPharynxPenisTransmission()
    {
        return PHARYNX_PENIS ;
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
