package uga;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.DecoderException;
//import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class uga_img
 */
//@WebServlet("/uga_img")
public class UgaImage extends HttpServlet
{
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UgaImage()
    {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		 JspFactory jspFactory = JspFactory.getDefaultFactory();
		 PageContext pageContext = jspFactory.getPageContext(this, request, response, null, true , 0, false);

		 UniversalAnalytics ua = null;

		 String hitType = request.getParameter(UniversalAnalytics.PNAME_HIT_TYPE);

		if (hitType.equals(UniversalAnalytics.HITTYPE_PAGE_TRACKING))
		{
			ua = new UniversalAnalytics_page(pageContext);
		}
		else if (hitType.equals(UniversalAnalytics.HITTYPE_ECOMMERCE_TRACKING))
		{
			ua = new UniversalAnalytics_ecom(pageContext);
		}
		else if (hitType.equals(UniversalAnalytics.HITTYPE_EVENT_TRACKING ))
		{
			ua = new UniversalAnalytics_event(pageContext);
		}


		 // Goole Analytics 発行
		 try
		 {
			ua.sendAnalytics();
		 }
		 catch (DecoderException e)
		 {
			e.printStackTrace();
		 }

		 // GIFイメージの描画
		 ua.writeGifData();
	}

}
