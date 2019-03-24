package com.freeplc.editor.config;

public class LadderConf
{
    private Folders[] folders;

     public Folders[] getFolders ()
    {
        return folders;
    }

    public void setFolders (Folders[] folders)
    {
        this.folders = folders;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [folders = "+folders+"]";
    }
}


