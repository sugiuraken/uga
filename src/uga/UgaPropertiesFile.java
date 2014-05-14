package uga;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UgaPropertiesFile
{
	final Properties properties = new Properties();
	public UgaPropertiesFile()
	{
        InputStream inStream = null;
        try {
            inStream = this.getClass().getClassLoader().getResourceAsStream("resources/uga.properties"); 
            properties.loadFromXML(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	// --- propertiesファイルの情報取得
	//   $section : セクション名
	//   $key     : キー名
	//   戻り値   : 設定値
	public String getSetting(String section, String key)
	{
		String contextkey = "uga" + section + key;
		String value = this.properties.getProperty(contextkey);
		if (value == null)
			return "";

		return value;
	}
}
