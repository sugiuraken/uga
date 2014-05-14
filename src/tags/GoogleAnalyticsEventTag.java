package tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import uga.UniversalAnalytics_event;


public class GoogleAnalyticsEventTag implements Tag
{
    private PageContext pageContext = null;
	private Tag parentTag = null;
	private String category = "";
	private String action = "";
	private String label = "";
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

	public void setcategory(String category)
	{
		this.category = category;
	}

	public void setaction(String action)
	{
		this.action = action;
	}
	public void setlabel(String label)
	{
		this.label = label;
	}


	public void setvalue(String value)
	{
		this.value = value;
	}

	public int doStartTag() throws JspException
	{

    	try
	    {
			UniversalAnalytics_event ua = (UniversalAnalytics_event)pageContext.getAttribute(GoogleAnalyticsTag.UGA_INSTANCE);

			ua.setEventParams(category, action, label, value);
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
