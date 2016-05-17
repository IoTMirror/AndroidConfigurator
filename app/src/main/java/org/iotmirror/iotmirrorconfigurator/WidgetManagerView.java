package org.iotmirror.iotmirrorconfigurator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Vector;

public class WidgetManagerView extends View implements View.OnTouchListener
{
    private int columnCount;

    private int rowCount;

    private int gridColor;

    private boolean showGrid;

    private Vector<Widget> widgets;

    private Widget selectedWidget;

    private int beginningColumn;

    private int beginningRow;

    private int currentColumn;

    private int currentRow;

    private boolean scaling;

    public WidgetManagerView(Context context)
    {
        this(context,null);
    }

    public WidgetManagerView(Context context, AttributeSet attrs)
    {
        this(context, attrs,0);
    }

    public WidgetManagerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        setRowCount(1);
        setColumnCount(1);
        setGridColor(Color.BLACK);
        setShowGrid(true);
        widgets = new Vector<>();
        selectedWidget = null;
        beginningColumn = 0;
        beginningRow = 0;
        currentColumn = 0;
        currentRow = 0;
        scaling = false;
        if(attrs!=null)
        {
            TypedArray attribs = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.WidgetManagerView,
                    0, 0);
            try
            {
                setColumnCount(
                        attribs.getInteger(R.styleable.WidgetManagerView_columnCount, columnCount)
                );
                setRowCount(
                        attribs.getInteger(R.styleable.WidgetManagerView_rowCount, rowCount)
                );
                setGridColor(
                        attribs.getColor(R.styleable.WidgetManagerView_gridColor, gridColor)
                );
                setShowGrid(
                        attribs.getBoolean(R.styleable.WidgetManagerView_showGrid, showGrid)
                );
            }
            finally
            {
                attribs.recycle();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(isShowGrid() == true) drawGrid(canvas);
        drawWidgets(canvas);
        drawHelperWidget(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=300;
        int height=300;

        switch(MeasureSpec.getMode(widthMeasureSpec))
        {
            case MeasureSpec.EXACTLY:
            {
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
            }
            case MeasureSpec.AT_MOST:
            {
                width = Math.min(MeasureSpec.getSize(widthMeasureSpec), width);
                break;
            }
        }
        switch(MeasureSpec.getMode(heightMeasureSpec))
        {
            case MeasureSpec.EXACTLY:
            {
                height = MeasureSpec.getSize(heightMeasureSpec);;
                break;
            }
            case MeasureSpec.AT_MOST:
            {
                height = Math.min(MeasureSpec.getSize(heightMeasureSpec), height);
                break;
            }
        }

        setMeasuredDimension(width, height);
    }

    protected void drawGrid(Canvas canvas)
    {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(getGridColor());
        float columnSize = getColumnSize();
        float rowSize = getRowSize();
        for(int i=0;i<getColumnCount();++i)
        {
            canvas.drawLine(i*columnSize,0,i*columnSize,getHeight(),p);
        }
        for(int i=0;i<getRowCount();++i)
        {
            canvas.drawLine(0,i*rowSize,getWidth(),i*rowSize,p);
        }
        canvas.drawLine(0,getHeight()-1,getWidth(),getHeight()-1,p);
        canvas.drawLine(getWidth()-1,0,getWidth()-1,getHeight(),p);
    }

    protected void drawWidgets(Canvas canvas)
    {
        for(Widget widget : widgets)
        {
            drawWidget(widget, canvas);
        }
    }

    protected void drawWidget(Widget widget, Canvas canvas)
    {
        if(widget!=null && !(widget == selectedWidget && scaling == true))
        {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(widget.getColor());
            drawRect(canvas,widget.getColumn(),widget.getRow(),
                    widget.getColSpan(),
                    widget.getRowSpan(),p);
        }
    }

    protected void drawHelperWidget(Canvas canvas)
    {
        if(selectedWidget != null)
        {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            int color = Color.argb(Color.alpha(selectedWidget.getColor())/2,
                    Color.red(selectedWidget.getColor()),
                    Color.green(selectedWidget.getColor()),
                    Color.blue(selectedWidget.getColor()));
            p.setColor(color);
            if(scaling)
            {
                int colSpan = selectedWidget.getColSpan()+(currentColumn-beginningColumn);
                int rowSpan = selectedWidget.getRowSpan()+(currentRow-beginningRow);
                drawRect(canvas, selectedWidget.getColumn(),selectedWidget.getRow(),
                        colSpan,
                        rowSpan,p);
            }
            else
            {
                int column = selectedWidget.getColumn()+(currentColumn-beginningColumn);
                int row = selectedWidget.getRow()+(currentRow-beginningRow);
                drawRect(canvas, column,row,
                        selectedWidget.getColSpan(),
                        selectedWidget.getRowSpan(),p);
            }
        }
    }

    protected void drawRect(Canvas canvas, int column, int row, int colSpan, int rowSpan, Paint p)
    {
        canvas.drawRect(column*getColumnSize(),row*getRowSize(),
                (column+colSpan)*getColumnSize(),
                (row+rowSpan)*getRowSize(),p);
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        if(columnCount<=0)return;
        this.columnCount = columnCount;
        invalidate();
    }

    public float getColumnSize()
    {
        return (1.0f*getWidth())/getColumnCount();
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        if(rowCount<=0)return;
        this.rowCount = rowCount;
        invalidate();
    }

    public float getRowSize()
    {
        return (1.0f*getHeight())/getRowCount();
    }

    public int getGridColor() {
        return gridColor;
    }

    public void setGridColor(int gridColor) {
        this.gridColor = gridColor;
        invalidate();
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        invalidate();
    }

    public void addWidget(Widget widget)
    {
        widgets.addElement(widget);
        widget.setManager(this);
        invalidate();
    }

    public void removeWidget(Widget widget)
    {
        widgets.removeElement(widget);
        widget.setManager(null);
        invalidate();
    }

    public void removeWidgets()
    {
        for(Widget w : widgets)
        {
            w.setManager(null);
        }
        widgets.clear();
        invalidate();
    }

    public int getColumn(float x)
    {
        if(x<0)return 0;
        return Math.min((int) (x/getColumnSize()),getColumnCount());
    }

    public int getRow(float y)
    {
        if(y < 0)return 0;
        return Math.min((int) (y/getRowSize()), getRowCount());
    }

    public Widget getWidgetAt(int column, int row)
    {
        for(Widget w : widgets)
        {
            if( (column >= w.getColumn() && column < w.getColumn()+w.getColSpan())
                && (row >= w.getRow() && row < w.getRow()+w.getRowSpan())) return w;
        }
        return null;
    }

    public boolean isWidgetRightDownCorner(Widget w,float x, float y, float cornerSize)
    {
        if(w==null)return false;
        cornerSize = Math.min(Math.max(0f,cornerSize),1f);
        float wL = w.getColumn()*getColumnSize();
        float wT = w.getRow()*getRowSize();
        float wWidth = w.getColSpan()*getColumnSize();
        float wHeight = w.getRowSpan()*getRowSize();
        float wR = wL + wWidth;
        float wB = wT + wHeight;
        float cornerL=wR - wWidth*cornerSize;
        float cornerT=wB - wHeight*cornerSize;
        float cornerR=wR;
        float cornerB=wB;
        return (x>=cornerL && x<cornerR) && (y>=cornerT && y<cornerB);
    }

    public boolean canBePlaced(Widget widget, int column, int row, int width, int height)
    {
        if(column+width>columnCount)return false;
        if(row+height>rowCount)return false;
        if(column<0)return false;
        if(row<0)return false;
        for(int i=column;i<column+width;++i)
        {
            for(int j=row;j<row+height;++j)
            {
                if(getWidgetAt(i,j)!=null && getWidgetAt(i,j)!=widget)return false;
            }
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                beginningColumn = getColumn(motionEvent.getX());
                beginningRow = getRow(motionEvent.getY());
                selectedWidget = getWidgetAt(beginningColumn,beginningRow);
                scaling = isWidgetRightDownCorner(selectedWidget,
                        motionEvent.getX(),motionEvent.getY(),0.3f);
                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                int oldColumn = currentColumn;
                int oldRow = currentRow;
                currentColumn = getColumn(motionEvent.getX());
                currentRow = getRow(motionEvent.getY());
                if (oldColumn!=currentColumn || oldRow!=currentRow) invalidate();
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                if(selectedWidget != null)
                {
                    int endColumn = getColumn(motionEvent.getX());
                    int endRow = getRow(motionEvent.getY());
                    if(scaling)
                    {
                        int newColspan = selectedWidget.getColSpan()+(endColumn-beginningColumn);
                        int newRowspan = selectedWidget.getRowSpan()+(endRow-beginningRow);
                        selectedWidget.setCellSpan(newColspan,newRowspan);
                    }
                    else
                    {
                        int newColumn = selectedWidget.getColumn()+(endColumn-beginningColumn);
                        int newRow = selectedWidget.getRow()+(endRow-beginningRow);
                        selectedWidget.setCell(newColumn,newRow);
                    }
                }
            }
            case MotionEvent.ACTION_CANCEL:
            {
                selectedWidget = null;
                beginningColumn=0;
                beginningRow=0;
                currentColumn=0;
                currentRow=0;
                scaling=false;
                invalidate();
                return true;
            }
            default:
            {
                return false;
            }
        }
    }
}
