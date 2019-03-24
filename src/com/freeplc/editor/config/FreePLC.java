package com.freeplc.editor.config;

public class FreePLC
{
    private String icon;

    private String projects_folder;

    public String getIcon ()
    {
        return icon;
    }

    public void setIcon (String icon)
    {
        this.icon = icon;
    }

    public String getProjects_folder ()
    {
        return projects_folder;
    }

    public void setProjects_folder (String projects_folder)
    {
        this.projects_folder = projects_folder;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [icon = "+icon+", projects_folder = "+projects_folder+"]";
    }
}
			
	