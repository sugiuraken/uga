package uga;

import javax.servlet.ServletContext;

public class UgaIniFile
{
	public static final String SECTION_ENV = "Env";
	public static final String KEY_PROPID = "PropId";
	public static final String KEY_CHARSET = "Charset";
	public static final String KEY_IMGPREFIX = "ImgPrefix";
	public static final String KEY_MASKPATTERN = "MaskPattern";
	public static final String KEY_SOCKETTIMEOUT = "SocketTimeout";
	public static final String KEY_NOEFFECT = "NoEffect";
	public static final String KEY_NOURIENCODING = "NoUrlEncoding";
	public static final String KEY_PREGETUID = "PreGetUid";

	public static final String KEY_REDIRECTWHITELIST = "RedirectWhiteList";
	public static final String KEY_REDIRECTERRORPAGE = "RedirectErrorPage";

	public static final String SECTION_DEVELOP = "Develop";
	public static final String KEY_DEBUG = "Debug";
	public static final String KEY_DEBUGLEVEL = "DebugLevel";
	public static final String KEY_LOGFILE = "LogFile";
	public static final String KEY_LOG4J_XML = "Log4jXml";
	public static final String KEY_LOG4J_PREFIX = "Log4jPrefix";




	private ServletContext application;

	public UgaIniFile(ServletContext application)
	{
		this.application = application;
	}

	// --- iniファイルの情報取得
	//   $section : セクション名
	//   $key     : キー名
	//   戻り値   : 設定値
	public String getSetting(String section, String key)
	{
		String contextkey = "uga" + section + key;
		String value = this.application.getInitParameter(contextkey);
		if (value == null)
			return "";

		return value;
	}
}
