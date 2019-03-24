package com.freeplc.editor.config;
                
public class FreePLCConfig
{
    private LadderConf ladderConf;

    private FreePLC freePLC;

    public LadderConf getLadderConf ()
    {
        return ladderConf;
    }

    public void setLadderConf (LadderConf ladderConf)
    {
        this.ladderConf = ladderConf;
    }

    public FreePLC getFreePLC ()
    {
        return freePLC;
    }

    public void setFreePLC (FreePLC freePLC)
    {
        this.freePLC = freePLC;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ladderConf = "+ladderConf+", freePLC = "+freePLC+"]";
    }
}
			
			