package tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import uga.UniversalAnalytics_ecom;

public class GoogleAnalyticsItemTag  implements Tag
{
	private PageContext pageContext = null;
	private Tag parentTag = null;
	private String price = "";
	private String quantity = "";
	private String code = "";
	private String name = "";
	private String variation = "";
	private String currency = "";

	public GoogleAnalyticsItemTag()
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

	public void setprice(String price)
	{
		this.price = price;
	}

	public void setquantity(String quantity)
	{
		this.quantity = quantity;
	}

	public void setcode(String code)
	{
		this.code = code;
	}

	public void setname(String name)
	{
		this.name = name;
	}

	public void setvariation(String variation)
	{
		this.variation = variation;
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
			ua.addItemParams(price, quantity, code, name, variation, currency);
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
