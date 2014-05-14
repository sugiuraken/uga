package uga;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.DecoderException;

public class UniversalAnalytics_page extends UniversalAnalytics {

	public UniversalAnalytics_page(PageContext pageContext)
	{
		super(pageContext);
	}

	// ---- タイプIDの取得
	public String getHitTypeId()
	{
		return HITTYPE_PAGE_TRACKING;
	}


    public void sendAnalytics() throws DecoderException
    {
    	if (bNoEffect == true)
    		return;

    	try
    	{
			this.analyzeRequest();
			this.postAnalyticsParam(this.getAppendParams());
		}
    	catch (UnsupportedEncodingException e)
    	{
			e.printStackTrace();
		}
    	catch (IOException e)
    	{
			e.printStackTrace();
		}
    }


	protected HashMap<String, String> getAppendParams()
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("t",  "pageview");

		return params;
	}

}
