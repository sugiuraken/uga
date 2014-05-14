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
public class UgaEventRedirect extends HttpServlet
{
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UgaEventRedirect()
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

		 UniversalAnalytics_event ua =  new UniversalAnalytics_event(pageContext);

		 // Goole Analytics 発行
		 try
		 {
			 ua.parseRedirect();
			 ua.sendAnalytics();
		 }
		 catch (DecoderException e)
		 {
			e.printStackTrace();
		 }

		 // リダイレクト
		 ua.redirectPage();
	}

}
