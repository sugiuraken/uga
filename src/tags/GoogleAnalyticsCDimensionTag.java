package tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import uga.UniversalAnalytics;

public class GoogleAnalyticsCDimensionTag implements Tag
{
    private PageContext pageContext = null;
	private Tag parentTag = null;
	private String index = "";
	private String value = "";

	public void setPageContext(PageContext pageContext)
	{
		this.pageContext = pageContext;
	}

	public void setParent(Tag parentTag)
	{
	    this.parentTag = parentTag;
	}

	public Tag getParent()
	{
		return this.parentTag;
	}

	public void setindex(String index)
	{
		this.index = index;
	}

	public void setvalue(String value)
	{
		this.value = value;
	}


	public int doStartTag() throws JspException
	{

    	try
	    {
			UniversalAnalytics ua = (UniversalAnalytics)pageContext.getAttribute(GoogleAnalyticsTag.UGA_INSTANCE);
			ua.setCustomDimension(this.index, this.value);
	    }
	    catch(Exception ex)
	    {
	    }
    	return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
	  	return EVAL_PAGE;
	}

    public void release() {}
}


