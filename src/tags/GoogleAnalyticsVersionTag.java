package tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import uga.UgaIniFile;
import uga.UgaPropertiesFile;
import uga.UniversalAnalytics;

public class GoogleAnalyticsVersionTag  implements Tag
{
	private PageContext pageContext = null;
	private Tag parentTag = null;

	public GoogleAnalyticsVersionTag()
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

	public int doStartTag() throws JspException
	{
		UgaPropertiesFile iniFile = new UgaPropertiesFile();
    	boolean bNoEffect = iniFile.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_NOEFFECT).equals("true") ? true : false;
    	if (bNoEffect == true)
    	{
    	  	return SKIP_BODY;
    	}
    	try
	    {
			UniversalAnalytics ua = new UniversalAnalytics(pageContext);
			pageContext.getOut().print(ua.getVersion());
	    }
	    catch(Exception ex)
	    {
	    }
    	return SKIP_BODY;
	}

	public int doEndTag() throws JspException
	{
	  	return EVAL_PAGE;
	}

    public void release() {}
}
