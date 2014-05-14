package uga;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.DecoderException;

public class UniversalAnalytics_event extends UniversalAnalytics
{

	// Event リダイレクトURL
	public static final String REDIRECT_URL 	= "/uga_event.jsp";			// リダイレクトURL
	public static final String PNAME_REDIRECT_URI	= "uga_x_rct";

	private String strRedirectUri = "";

	public UniversalAnalytics_event(PageContext pageContext)
	{
		super(pageContext);
	}

	// ---- タイプIDの取得
	public String getHitTypeId()
	{
		return HITTYPE_EVENT_TRACKING ;
	}

	protected HashMap<String, String> getAppendParams() throws DecoderException
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("t",  "event");

		String value;

		try
		{
			value = this.getUrlEncodedParam(PNAME_EVENT_CATEGORY);
			if (value.isEmpty() == false)
				params.put("ec", value);

			value = this.getUrlEncodedParam(PNAME_EVENT_ACTION);
			if (value.isEmpty() == false)
				params.put("ea", value);

			value = this.getUrlEncodedParam(PNAME_EVENT_LABEL);
			if (value.isEmpty() == false)
				params.put("el", value);

			value = this.getUrlEncodedParam(PNAME_EVENT_VALUE);
			if (value.isEmpty() == false)
				params.put("ev", value);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		return params;
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

	public void setEventParams(String category, String action, String label, String value)
	{
		this.appendOptionalParam(PNAME_EVENT_CATEGORY,  category);
		this.appendOptionalParam(PNAME_EVENT_ACTION,  action);
		this.appendOptionalParam(PNAME_EVENT_LABEL,  label);
		this.appendOptionalParam(PNAME_EVENT_VALUE,  value);
	}


	// ---- リダイレクトURLの生成 -----------------------------------------------
	public String makeEventRedirectUrl(
				String redirectUri)
									throws UnsupportedEncodingException
	{
		// イメージURL、およびパラメタを生成する
		String url = "";
	    url += this.strImagePrefix  +  REDIRECT_URL + "?";

	    url += PNAME_REDIRECT_URI + "=" +  urlCodec.encode(redirectUri, this.strCharset);
		url += "&" + PNAME_RANDUM_NUMBER + "=" + this.getRandomNumber();		// キャッシュを無効にするための乱数

		// DOCOMO 携帯のユーザIDを取得するためのパラメタ
		url += "&" + "guid=ON";
		url += "&" + "uid=" + DOCOMO_NULLGWDOCOMO;

		// 現在のページに遷移する前にユーザーエージェントが参照していたページのアドレス（HTTP_REFERER）設定
	    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
	    this.strDocumentReferer = request.getHeader("referer");
	    if (this.strDocumentReferer == null || this.strDocumentReferer.isEmpty())
	    {
	  		this.strDocumentReferer = "-";
	    }

	    url += "&" + PNAME_HTTP_REFERER + "=" + urlCodec.encode(this.strDocumentReferer, this.strCharset);


		// ページにアクセスするために指定された URI（REQUEST_URI）設定
	    this.strRequestUri = (String)request.getRequestURL().toString();
	    String queryString = (String)request.getQueryString();
	    if (queryString != null && !queryString.isEmpty())
	    {
	    	this.strRequestUri += "?" + queryString;
	    }

	    if (this.strRequestUri != null && !this.strRequestUri.isEmpty())
	    {
	    	url += "&" + PNAME_REQUEST_URI + "=" +  urlCodec.encode(this.strRequestUri, this.strCharset);
	    }

		// オプショナルパラメタの設定
	    if (this.arrayOptionalParams.size() > 0)
	    {
	    	url += "&" + http_build_query(this.arrayOptionalParams);
	    }
		return url;
	}

	public void parseRedirect() throws UnsupportedEncodingException, DecoderException
	{
		this.strRedirectUri = getUrlEncodedParam(PNAME_REDIRECT_URI);

		// setEventParams() を呼ぶ
	}

	 public void redirectPage() throws ServletException, IOException
	 {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String strRequestUrl = request.getRequestURL().toString();

		java.net.URI objUrl = null;
		try {
			objUrl = new java.net.URI(strRequestUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String strRedirectDomain = objUrl.resolve(this.strRedirectUri).getHost();

		String [] strRedirectWhiteList = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_REDIRECTWHITELIST).split(",");
		String strRedirectErrorPage = objUrl.resolve(this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_REDIRECTERRORPAGE)).toString();

		boolean bMatch = false;
		for (String strRegEx : strRedirectWhiteList)
		{
			if (strRedirectDomain.matches(strRegEx) == true)
			{
				bMatch = true;
				break;
			}
		}

		String strTargetUri = (bMatch == true ) ? this.strRedirectUri : strRedirectErrorPage;
//		ServletContext sc = pageContext.getServletContext();
//		sc.getRequestDispatcher(strTargetUri).forward(pageContext.getRequest(), pageContext.getResponse());

		HttpServletResponse responce = (HttpServletResponse)pageContext.getResponse();
		responce.sendRedirect(strTargetUri);
 	 }
}

