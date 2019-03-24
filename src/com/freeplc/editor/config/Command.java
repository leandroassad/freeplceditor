package com.freeplc.editor.config;

import com.freeplc.editor.project.component.LadderComponent;

import lombok.Getter;
import lombok.Setter;

public class Command
{
	@Getter @Setter private LadderComponent component;
	
    private String description;

    private String classname;

    private String name;

    private String image;

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getClassname ()
    {
        return classname;
    }

    public void setClassname (String classname)
    {
        this.classname = classname;
        try {
			component = (LadderComponent)Class.forName(classname).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			component = null;
		}
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [description = "+description+", classname = "+classname+", name = "+name+", image = "+image+"]";
    }
}
