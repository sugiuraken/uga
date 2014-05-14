package uga;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class UgaLogging
{
	public static final int LEVEL_ERROR 		= 0;
	public static final int LEVEL_WARNING 		= 1;
	public static final int LEVEL_INFORMATION 	= 2;
	public static final int LEVEL_DEBUG			= 3;

//	protected String logFileName = null;
	protected Logger log4jLogger = null;
	protected static final String[] arrayLevelString = {"ERR", "WAR", "INF", "DBG"};


	// コンストラクタ
	public UgaLogging(String log4jXml, String log4jPrefix)
	{
		log4jLogger = Logger.getLogger(log4jPrefix + UgaLogging.class.getName());
		if (log4jXml != "")
			DOMConfigurator.configure(log4jXml);
	}

	// --- ログファイルへの出力
	//   $level   : メッセージレベル (LEVEL_ERROR | LEVEL_WARING | LEVEL_INFORMATION)
	//   $message : 出力メッセージ
	public boolean writeMessage(int level, String message)
	{
		if (this.log4jLogger != null)
		{
			Date currentTime = new Date();
			SimpleDateFormat sdFormat = new SimpleDateFormat("'['yyyy'-'MM'-'dd' 'HH':'mm':'ss'] '");
			String dateNow = sdFormat.format(currentTime);

			log4jLogger.debug(dateNow + message);
			return true;
		}
		return false;
	}

	public boolean writeObject(String title, String object)
	{
		if (writeMessage(LEVEL_DEBUG, title) == false)
			return false;
		return writeMessage(LEVEL_DEBUG, object);
	}
}
