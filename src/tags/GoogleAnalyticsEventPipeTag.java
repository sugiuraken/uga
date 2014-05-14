package tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import uga.UniversalAnalytics_event;

public class GoogleAnalyticsEventPipeTag implements Tag
{

    private PageContext pageContext = null;
	private Tag parentTag = null;

	private String redirectUri = null;

	private String docTitle = "";
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

	public void setredirecturi(String uri)
	{
		this.redirectUri = uri;
	}

	public void setdoctitle(String docTitle)
	{
		this.docTitle = docTitle;
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
    	JspWriter out = pageContext.getOut();

	   	try
	    {
	   		UniversalAnalytics_event ua = new UniversalAnalytics_event(pageContext);

	    	if (this.docTitle != null)
	    		ua.setDocumentTitle(this.docTitle);

			ua.setEventParams(category, action, label, value);
	    	String redirectUrl = ua.makeEventRedirectUrl(this.redirectUri);
			out.print(redirectUrl);

	    }
	    catch(Exception ex)
	    {
	    	try
	    	{
	    		out.print("<!-- " + ex.getMessage() + " -->");
			}
	    	catch (IOException ex2)
			{
			}
	    }
    	return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
   		return EVAL_PAGE;
	}

    public void release() {}
}
