package tags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.codec.DecoderException;

import uga.UgaIniFile;
import uga.UgaPropertiesFile;
import uga.UniversalAnalytics;
import uga.UniversalAnalytics_ecom;
import uga.UniversalAnalytics_event;
import uga.UniversalAnalytics_page;

public class GoogleAnalyticsTag implements Tag
{
	public static final String UGA_INSTANCE = "uga_instance";

    private PageContext pageContext = null;
	private Tag parentTag = null;
	private String hitType = null;
	private String docTitle = null;

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

	public void sethittype(String hitType)
	{
		this.hitType = hitType;
	}

	public void setdoctitle(String docTitle)
	{
		this.docTitle = docTitle;
	}


	public int doStartTag() throws JspException
	{
		UgaPropertiesFile iniFile = new UgaPropertiesFile();
    	boolean bNoEffect = iniFile.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_NOEFFECT).equals("true") ? true : false;
    	if (bNoEffect == true)
    	{
    	  	return EVAL_BODY_INCLUDE;
    	}

    	JspWriter out = pageContext.getOut();

    	try
	    {
	    	UniversalAnalytics ua = null;

	    	if (this.hitType.equals(UniversalAnalytics.HITTYPE_PAGE_TRACKING))
	    	{
	    		ua = new UniversalAnalytics_page(pageContext);
	    	}
	    	else if (this.hitType.equals(UniversalAnalytics.HITTYPE_ECOMMERCE_TRACKING))
	    	{
	    		ua = new UniversalAnalytics_ecom(pageContext);
	    	}
		    else if (this.hitType.equals(UniversalAnalytics.HITTYPE_EVENT_TRACKING ))
		    {
	    		ua = new UniversalAnalytics_event(pageContext);
	    	}

	    	if (this.docTitle != null)
	    		ua.setDocumentTitle(this.docTitle);

	    	pageContext.setAttribute(UGA_INSTANCE,  ua);


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
		UgaPropertiesFile iniFile = new UgaPropertiesFile();
    	boolean bNoEffect = iniFile.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_NOEFFECT).equals("true") ? true : false;
    	if (bNoEffect == true)
    	{
    	  	return EVAL_PAGE;
    	}

    	UniversalAnalytics ua = (UniversalAnalytics)pageContext.getAttribute(UGA_INSTANCE);

    	String imageUrl = "";

    	try
		{
    		try {
				imageUrl = ua.makeImageUrl();
			} catch (DecoderException e) {
				e.printStackTrace();
			}
    		JspWriter out = pageContext.getOut();
    		try
			{
    			out.print("<img src=\"" + imageUrl + "\" />");
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

	  	return EVAL_PAGE;
	}

    public void release() {}
}
