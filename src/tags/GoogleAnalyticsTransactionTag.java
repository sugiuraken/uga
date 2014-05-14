package tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import uga.UniversalAnalytics_ecom;

public class GoogleAnalyticsTransactionTag implements Tag
{
	private PageContext pageContext = null;
	private Tag parentTag = null;
	private String id = "";
	private String affiliation = "";
	private String revenue = "";
	private String shipping = "";
	private String tax = "";
	private String currency = "";

	public GoogleAnalyticsTransactionTag()
	{
	}

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

	public void setid(String id)
	{
		this.id = id;
	}

	public void setaffiliation(String affiliation)
	{
		this.affiliation = affiliation;
	}

	public void setrevenue(String revenue)
	{
		this.revenue = revenue;
	}

	public void setshipping(String shipping)
	{
		this.shipping = shipping;
	}

	public void settax(String tax)
	{
		this.tax = tax;
	}

	public void setcurrency(String currency)
	{
		this.currency = currency;
	}

	public int doStartTag() throws JspException
	{

    	try
	    {
    		UniversalAnalytics_ecom ua = (UniversalAnalytics_ecom)pageContext.getAttribute(GoogleAnalyticsTag.UGA_INSTANCE);
			ua.setTransactionId(id);
			ua.setTransactionParams(affiliation, revenue, shipping, tax, currency);
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
