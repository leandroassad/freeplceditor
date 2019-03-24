package com.freeplc.editor.config;

public class Folders
{
    private String name;

    private Command[] command;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Command[] getCommand ()
    {
        return command;
    }

    public void setCommand (Command[] command)
    {
        this.command = command;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [name = "+name+", command = "+command+"]";
    }
}
