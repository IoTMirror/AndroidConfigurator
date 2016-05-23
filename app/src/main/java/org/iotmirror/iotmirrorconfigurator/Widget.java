package org.iotmirror.iotmirrorconfigurator;

import java.io.Serializable;

public class Widget implements Serializable
{

    private int column;
    private int row;
    private int colSpan;
    private int rowSpan;
    private int color;
    private WidgetManagerView manager;
    private String name;
    private boolean active;

    public Widget()
    {
        this("");
    }

    public Widget(String name)
    {
        this(name, 0,0);
    }

    public Widget(String name, int column, int row)
    {
        this(name, column,row,1,1);
    }

    public Widget(String name, int column, int row, int colSpan, int rowSpan)
    {
        this(name, column,row,colSpan,rowSpan,0);
    }

    public Widget(String name, int column, int row, int colSpan, int rowSpan, int color)
    {
        setName(name);
        setColumn(column);
        setRow(row);
        setColSpan(colSpan);
        setRowSpan(rowSpan);
        setColor(color);
        setActive(true);
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        if(column < 0)return;
        if(getManager()!=null)
        {
            if (getManager().canBePlaced(this,column,getRow(),getColSpan(),
                    getRowSpan())==false)return;
        }
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        if(row < 0)return;
        if(getManager()!=null)
        {
            if (getManager().canBePlaced(this,getColumn(),row,getColSpan(),
                    getRowSpan())==false)return;
        }
        this.row = row;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        if(colSpan <= 0)return;
        if(getManager()!=null)
        {
            if (getManager().canBePlaced(this,getColumn(),getRow(),colSpan,
                    getRowSpan())==false)return;
        }
        this.colSpan = colSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        if(rowSpan <= 0)return;
        if(getManager()!=null)
        {
            if (getManager().canBePlaced(this,getColumn(),getRow(),getColSpan(),
                    rowSpan)==false)return;
        }
        this.rowSpan = rowSpan;
    }

    public void setCell(int column, int row)
    {
        if(column < 0)return;
        if(row < 0)return;
        if(getManager()!=null)
        {
            if (getManager().canBePlaced(this,column,row,getColSpan(),
                    getRowSpan())==false)return;
        }
        this.column = column;
        this.row=row;
    }

    public void setCellSpan(int colSpan, int rowSpan)
    {
        if(colSpan <= 0)return;
        if(rowSpan <= 0)return;
        if(getManager()!=null)
        {
            if (getManager().canBePlaced(this,getColumn(),getRow(),colSpan,
                    rowSpan)==false)return;
        }
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public WidgetManagerView getManager() {
        return manager;
    }

    public void setManager(WidgetManagerView manager) {
        this.manager=manager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
