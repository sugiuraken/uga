package uga;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

// ----------------------------------------------------------------------
// Google Analytics Service
// 基底クラス : UniversalAnalytics
//   各種 Google Analytics サービスを行うクラスの基底クラス
//   また、処理起動のためのgifイメージも生成行う
// -----------------------------------------------------------------------
public class UniversalAnalytics
{
	public String getVersion()
	{
		return "05.31";
	}

	// 1x1 transparent GIF image
	protected static byte[] GIF_DATA = {
      (byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38, (byte)0x39, (byte)0x61,
      (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x80, (byte)0xff,
      (byte)0x00, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x00, (byte)0x00,
      (byte)0x00, (byte)0x2c, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
      (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x02,
      (byte)0x02, (byte)0x44, (byte)0x01, (byte)0x00, (byte)0x3b
	};

	//--------------------------------------------------------------------
	// 定数
	//--------------------------------------------------------------------
	// GIF イメージURL
	public static final String IMAGE_URL 	= "/uga_img.jsp";			// イメージURL
	protected String strImagePrefix = "";

	// クッキー関連
	public static final String COOKIE_NAME 	= "__uga_cookie__";		// クッキー名
	public static final String COOKIE_PATH 	= "/";					// クッキーパス
	public static final int COOKIE_USER_PERSISTENCE = 63072000;	// 有効期限（２年）

	// -----  Sending Common Hit Types -------------------------
	public static final String CLASS_BASE_NAME	= "UniversalAnalytics_";

	// Google Analytics Hit Type 定数
	public static final String HITTYPE_PAGE_TRACKING 		= "page";
	public static final String HITTYPE_EVENT_TRACKING 		= "event";
	public static final String HITTYPE_ECOMMERCE_TRACKING 	= "ecom";
	public static final String HITTYPE_SOCIAL_INTERACTIONS	= "social";
	public static final String HITTYPE_EXCEPTION_TRACKING	= "except";
	public static final String HITTYPE_USER_TIMING_TRACKING	= "user";
	public static final String HITTYPE_APP_TRACKING			= "app";

	// 本クラスで使用するパラメタ・キー
	public static final String PNAME_RANDUM_NUMBER	= "uga_x_nm";		// 乱数（キャッシュを無効にするため）
	public static final String PNAME_HTTP_REFERER	= "uga_x_rf";		// 元ページの$_SERVER["HTTP_REFERER"]
	public static final String PNAME_REQUEST_URI	= "uga_x_uri";		// 元ページの$_SERVER["REQUEST_URI"]

	public static final String PNAME_HIT_TYPE		= "uga_t";			// 送信する Hit Type
	public static final String PNAME_PREGET_UID		= "uga_x_uid";		// 先行して取得したUID(imgタグ生成時）

	// 共通パラメタ
	public static final String PNAME_DOCUMENT_TITLE		= "uga_dt";			// 送信する ドキュメントタイトル
	public static final String PNAME_CUSTUM_DIMENSIONS	= "uga_cd";		// Custum Dimensions
	public static final String PNAME_CUSTUM_METRICS		= "uga_cm";		// Custum Metrics

	// Transaction
	public static final String PNAME_TRANSACTION_ID				= "uga_ti";			// E-Commmerce Transaction ID
	public static final String PNAME_TRANSACTION_AFFILIATION	= "uga_ta";			// E-Commmerce Transaction affiliation
	public static final String PNAME_TRANSACTION_REVENUE		= "uga_tr";			// E-Commmerce Transaction revenue
	public static final String PNAME_TRANSACTION_SHIPPING		= "uga_ts";			// E-Commmerce Transaction shipping
	public static final String PNAME_TRANSACTION_TAX			= "uga_tt";			// E-Commmerce Transaction tax
	public static final String PNAME_TRANSACTION_CURRENCY_CODE  = "uga_cu";			// E-Commmerce Transaction currency code

	public static final String PNAME_TRANSACTION_ITEMS			= "uga_x_itm";		// E-Commmerce Transaction Item( tab区切り文字列）

	// Event
	public static final String PNAME_EVENT_CATEGORY				= "uga_ec";			// Event Category
	public static final String PNAME_EVENT_ACTION				= "uga_ea";			// Event Action
	public static final String PNAME_EVENT_LABEL				= "uga_el";			// Event Label
	public static final String PNAME_EVENT_VALUE				= "uga_ev";			// Event Value


	public static final String DOCOMO_NULLGWDOCOMO		= "NULLGWDOCOMO";	// DOCOMO携帯のuidを取得するためのキーワード

	// --------------------------------------------------------------------
	// メンバー変数
	// --------------------------------------------------------------------
	protected PageContext pageContext = null;

	protected UgaLogging objLogging = null;			// ログファイル管理
	protected UgaPropertiesFile objProperties = null;	// uga.properites管理

	protected String strPropId = "";											// Google Analytics プロパティID
	protected String strDomainHostName = "";									// ターゲットドメイン名
	protected String strDocumentReferer = "";									// HTTP_REFERER保持用
	protected String strDocumentPath = "";
	protected String strRequestUri = "";										// HTTP_REQUEST_URI
	protected HashMap<String, String> arrayUrlParams = new HashMap<String, String>();			// URLのパラメータ配列

	protected String strDocumentTitle = "";														// Document Title
	protected HashMap<String, String> arrayCustumDimensions = new HashMap<String, String>();	// Custum Demensions配列
	protected HashMap<String, String> arrayCustumMetrics =  new HashMap<String, String>();		// Custum Metrics配列

	protected HashMap<String, String> arrayOptionalParams = new HashMap<String, String>();

	protected String strVisiterId = "";											// 訪問者ID
	protected static byte [] usermask = {(byte)0x55, (byte)0xaa};				// 任意の暗合パターン数値群（３要素以上も可能）

	protected int timeout_msec = 10000; 										// ソケット通信タイムアウト 10秒

	protected boolean bDebug = false;											// 内部デバッグ状態(true: 有効)

	protected URLCodec urlCodec = new URLCodec();
	protected String strCharset = "UTF-8";
	protected String strCharsetUrl = "";

	protected boolean bNoUrlEncoding = false;
	protected boolean bNoEffect = false;

	protected boolean bPreGetUid = false;

	// コンストラクタ
	public UniversalAnalytics(PageContext pageContext)
	{
		this.pageContext = pageContext;

		this.objProperties = new UgaPropertiesFile();
		this.strPropId = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_PROPID);
		this.strImagePrefix = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_IMGPREFIX);
		this.strCharsetUrl = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_CHARSET);


		this.bDebug = this.objProperties.getSetting(UgaIniFile.SECTION_DEVELOP, UgaIniFile.KEY_DEBUG).equals("on") ? true : false;	// デバック状態の設定
		this.objLogging = new UgaLogging(
				this.objProperties.getSetting(UgaIniFile.SECTION_DEVELOP, UgaIniFile.KEY_LOG4J_XML),
				this.objProperties.getSetting(UgaIniFile.SECTION_DEVELOP, UgaIniFile.KEY_LOG4J_PREFIX));		// ログファイルの指定


		this.bNoEffect = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_NOEFFECT).equals("true") ? true : false;
		this.bNoUrlEncoding = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_NOURIENCODING).equals("true") ? true : false;
		this.bPreGetUid = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_PREGETUID).equals("true") ? true : false;


		// ユーザ指定のマスクパターンを設定
		try {
			String maskPattern = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_MASKPATTERN);
			if (!maskPattern.isEmpty()) {
				String[] maskSplit = maskPattern.split(",");
				if (maskSplit.length > 0) {
					byte [] new_usermask = new byte[maskSplit.length];
					for (int i = 0; i < maskSplit.length; i++) {
						new_usermask[i] = (byte)(Integer.decode(maskSplit[i]) & 0xff);
					}
					usermask = new_usermask;
				}
			}
		} catch (Exception ex) {
			this.objLogging.writeMessage(UgaLogging.LEVEL_ERROR , "UGA-ERROR-001 : ユーザマスクパターンの指定が正しくありません");
		}

		// ソケット通信タイムアウト値を設定
		try {
			String socketTimeout = this.objProperties.getSetting(UgaIniFile.SECTION_ENV, UgaIniFile.KEY_SOCKETTIMEOUT);
			if (!socketTimeout.isEmpty()) {
				int value = Integer.parseInt(socketTimeout);
				timeout_msec = value;
			}
		} catch (Exception ex) {
			this.objLogging.writeMessage(UgaLogging.LEVEL_ERROR , "UGA-ERROR-002 : ソケットタイムアウト値の指定が正しくありません");
		}
	}

	// --- プロパティIDの取得
	//    return : プロパティID 文字列
	public String getPropertyId() {return this.strPropId;}

	// ---- ロギング管理インスタンスの取得
	public UgaLogging getLoggingManager() {return this.objLogging;}

	// ---- タイプIDの取得 (派生クラスでオーバーライド）
	public String getHitTypeId() {return "";}

	// ---- ドキュメントタイトルの設定
	public void setDocumentTitle(String title)
	{
		this.appendOptionalParam(PNAME_DOCUMENT_TITLE, title);
	}

	// ---- Custom Dimensionsの設定
	//   $index     : 0-199
	//   $strValue  : 設定値(文字列）
	public boolean setCustomDimension(String index, String strValue)
	{
		return this.setCustumMetricsAndDimension(PNAME_CUSTUM_DIMENSIONS, index, strValue);
	}

	// ---- Custom Metricsの設定
	//   $index :   0-199
	//   $iValue  : 設定値（整数）
	public boolean setCustomMetric(String index, String strValue)
	{
		return this.setCustumMetricsAndDimension(PNAME_CUSTUM_METRICS, index, strValue);
	}

	// ---- Custom Dimensions / Custom Metricsの設定（内部）
	protected boolean setCustumMetricsAndDimension(String pname, String index, String value)
	{
		int iIndex = 0;
		try {
			iIndex = Integer.parseInt(index);
		}
		catch (NumberFormatException exNumber)
		{
			this.objLogging.writeMessage(UgaLogging.LEVEL_ERROR , "UGA-ERROR-003 : Custom Dimensions / Custom Metrics bad index.");
			return false;
		}

	    if (iIndex >= 0 && iIndex < 200)
	    {
			this.appendOptionalParam(pname + String.valueOf(iIndex), value);
			return true;
		}
		this.objLogging.writeMessage(UgaLogging.LEVEL_ERROR , "UGA-ERROR-004 : Custom Dimensions / Custom Metrics index out of range.");
		return false;
	}


	// ---- オプションパラメータの追加 ---------------------------------------
	protected void appendOptionalParam(String key, String value)
	{
		this.arrayOptionalParams.put(key, value);
	}

	// ---- イメージURLの生成 -----------------------------------------------
	public String makeImageUrl() throws UnsupportedEncodingException, DecoderException
	{
		// イメージURL、およびパラメタを生成する
		String url = "";
	    url += this.strImagePrefix  +  IMAGE_URL + "?";
    	url += PNAME_HIT_TYPE + "=" + this.getHitTypeId();				// Google Analytics Hit Type
    	url += "&" + PNAME_RANDUM_NUMBER + "=" + String.format("%1$010d", this.getRandomNumber());		// キャッシュを無効にするための乱数

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

	    if (this.bPreGetUid == true)
	    {
	    	url += "&" + PNAME_PREGET_UID + "=" +  urlCodec.encode(this.getVisitorId(), this.strCharset);
	    }



    	return url;
	}

	protected String http_build_query(HashMap<String, String> data) throws UnsupportedEncodingException
	{
		String preFix = "";
		StringBuffer queryString = new StringBuffer();
		for (Entry<String, String> pair : data.entrySet())
		{
			queryString.append(preFix + (String)pair.getKey() + "=");
			queryString.append(urlCodec.encode((String)pair.getValue(), this.strCharset));
			preFix = "&";
		}
		return queryString.toString();
	}

	protected String http_build_query_UTF8(HashMap<String, String> data) throws UnsupportedEncodingException
	{
		String preFix = "";
		StringBuffer queryString = new StringBuffer();
		for (Entry<String, String> pair : data.entrySet())
		{
			queryString.append(preFix + (String)pair.getKey() + "=");
			queryString.append(urlCodec.encode((String)pair.getValue(), "UTF-8"));
			preFix = "&";
		}
		return queryString.toString();
	}
	// ---- 透過GIFイメージの出力
	//      uga_img.phpより呼び出される
	public void writeGifData() throws IOException
	{
		HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
		response.resetBuffer();
		response.setContentType("image/gif");
		response.setContentLength(GIF_DATA.length);

		ServletOutputStream ostream = response.getOutputStream();
		ostream.write(GIF_DATA);
	}
	// ---- アナリティクスへデータ送信
	//  (派生クラスでオーバーライド）
	public void sendAnalytics() throws DecoderException
	{
		// 派生したクラスで実装
		return;
	}

	// --- 情報発行の基本パラメタの解析および内部変数への格納
	protected void analyzeRequest() throws UnsupportedEncodingException, DecoderException
	{
	    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

	    // ドメインホスト名の設定
		this.strDomainHostName = request.getLocalName();
		if (this.strDomainHostName == null)
		{
			this.strDomainHostName = "";
		}

		// パラメタで指定されたHTTP_REFFERE, REQUEST_URI、ドキュメントタイトルの取得
		this.strDocumentReferer = getUrlEncodedParam(PNAME_HTTP_REFERER);
		this.strRequestUri = getUrlEncodedParam(PNAME_REQUEST_URI);
		this.strDocumentTitle = getUrlEncodedParam(PNAME_DOCUMENT_TITLE);

		// 設定されたCustom Dimensions, Custom Metricsを取得する
		this.getCustomDimensionsAndMetrics();

		// PNAME_REQUEST_URIで引き渡されたパラメタを分解して配列に格納する
		int iPosQuestion = this.strRequestUri.indexOf('?');
		if (iPosQuestion != -1) {
			String urlParams = this.strRequestUri.substring(iPosQuestion + 1);
			String[] arrayParams = urlParams.split("&");

			for (String param : arrayParams)
			{
				String[] key_value = param.split("=");
				String strValue = "";
				if (key_value.length > 1)
				{
					if (!this.strCharsetUrl.isEmpty())
						strValue = new String(key_value[1].getBytes("ISO-8859-1"), this.strCharsetUrl);
					else
						strValue = urlCodec.decode(key_value[1], this.strCharset);
				}
				this.arrayUrlParams.put(key_value[0], strValue);
			}
		}
	}

	// --- URIで指定されたパラメタを取得し、デコードした文字列を得る
	protected String getUrlEncodedParam(String key) throws UnsupportedEncodingException, DecoderException
	{
	    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

	    String value = request.getParameter(key);
	    if (value == null)
	    	return "";

	    if (bNoUrlEncoding == false)
	    	value = new String(value.getBytes("ISO-8859-1"), this.strCharset);

	    return value;
	}

	// --- 設定されたCustom Dimensions, Custom Metricsを取得する
	@SuppressWarnings("unchecked")
	protected void getCustomDimensionsAndMetrics() throws UnsupportedEncodingException, DecoderException
	{
		int lenPNAME_CUSTUM_DIMENSIONS = PNAME_CUSTUM_DIMENSIONS.length();
		int lenPNAME_CUSTUM_METRICS = PNAME_CUSTUM_METRICS.length();

	    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

	    Map<String, String[]> mapParameter = request.getParameterMap();

	    for (String key : mapParameter.keySet())
		{
	    	String[] values = mapParameter.get(key);
			if (key.length() > lenPNAME_CUSTUM_DIMENSIONS &&
				    key.substring(0, lenPNAME_CUSTUM_DIMENSIONS).equals(PNAME_CUSTUM_DIMENSIONS))
			{
					String index = key.substring(lenPNAME_CUSTUM_DIMENSIONS);

					// エンコードしないモードの対応   （2012/07/19 修正）
					String value = values[0];
				    if (bNoUrlEncoding == false)
				    	value = new String(value.getBytes("ISO-8859-1"), this.strCharset);
					this.arrayCustumDimensions.put(index, value);
			}
			else if (key.length() > lenPNAME_CUSTUM_METRICS &&
			         key.substring(0, lenPNAME_CUSTUM_METRICS).equals(PNAME_CUSTUM_METRICS))
			{
					String index = key.substring(lenPNAME_CUSTUM_DIMENSIONS);

					// エンコードしないモードの対応   （2012/07/19 修正）
					String value = values[0];
				    if (bNoUrlEncoding == false)
				    	value = new String(value.getBytes("ISO-8859-1"), this.strCharset);
					this.arrayCustumMetrics.put(index, value);
			}
		}
	}

	// Google Analyticsへの情報発行処理
	//   AppendParams : 独自の送信パラメタ配列(array型)
	public boolean postAnalyticsParam(HashMap<String, String> appendParams) throws UnsupportedEncodingException, IOException, DecoderException
	{
		// 訪問者IDの取得
		if (this.bPreGetUid == false)
			this.strVisiterId = this.getVisitorId();
		else
			this.strVisiterId = getUrlEncodedParam(PNAME_PREGET_UID);

		// 送信パラメタの生成
		HashMap<String, String>postParams =  this.getBasicPostParams();

		// 独自の送信パラメタの追加
		if (appendParams != null)
			postParams.putAll(appendParams);

		// Google Analytics 情報発行
		this.sendSocket("www.google-analytics.com", 80, "/collect", http_build_query_UTF8(postParams));
		return true;
	}

	// --- 携帯のユーザID情報の取得およびCOOKIEの設定
	//        戻り値: ユーザID情報
	protected String getVisitorId() throws UnsupportedEncodingException, DecoderException
	{
	    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

	    String strCookieName = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				if (cookies[i].getName().equals(COOKIE_NAME)) {
					strCookieName = cookies[i].getValue();

					if (bDebug == true) {
						this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ ENCODED USER ID GET FROM COOKIE ] " + strCookieName);
					}
				}
			}
		}

		// クッキーにユーザID情報が設定されていればその情報を返す
    	if (strCookieName != null && !strCookieName.isEmpty())
      		return strCookieName;

		// --- 各携帯キャリアにより訪問者IDを設定する
		String strUidForAU = "x-up-subno";
		String strUidForSoftbank = "x-jphone-uid";
		String strUidForDocomo = "uid";
		String strXGuidForDocomo = "x-dcmguid";


		String uid = request.getHeader(strUidForAU);						// au
		if (uid == null || uid.isEmpty())
		{
			if (bDebug == true) {
				this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID GET FROM HTTP_X_UP_SUBNO(au) ] Failed");
			}
			uid = request.getHeader(strUidForSoftbank);						// softbank
			if (uid == null || uid.isEmpty())
			{
				if (bDebug == true) {
					this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID GET FROM HTTP_X_JPHONE_UID(softbank) ] Failed");
				}
				uid = request.getHeader(strXGuidForDocomo);					// docomo
				if (uid == null || uid.isEmpty())
				{
					if (bDebug == true) {
						this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID GET FROM HTTP_X_DCMGUID(docomo) ] Failed");
					}
					uid = this.getUrlEncodedParam(strUidForDocomo);
					if (uid == null || uid.isEmpty() || uid.equals(DOCOMO_NULLGWDOCOMO))
					{
						if (bDebug == true) {
							this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID GET FROM DOCOMO_NULLGWDOCOMO(docomo) ] Failed");
						}
						// 携帯情報からユーザIDを取得できない場合は、乱数をユーザIDとする
						uid =  "uid" + String.format("%1$010d", this.getRandomNumber());
						if (bDebug == true) {
							this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID MAKE FROM RANDOM NUMBER ] " + uid);
						}

					} else {
						if (bDebug == true) {
							this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID GET FROM DOCOMO_NULLGWDOCOMO(docomo) ] " + uid);
						}
					}
				} else {
					if (bDebug == true) {
						this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID GET FROM HTTP_X_DCMGUID(docomo) ] " + uid);
					}
				}
			} else {
				if (bDebug == true) {
					this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID GET FROM HTTP_X_JPHONE_UID(softbank) ] " + uid);
				}
			}
		} else {
			if (bDebug == true) {
				this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ USER ID GET FROM HTTP_X_UP_SUBNO(au) ] " + uid);
			}

		}


		// 各携帯キャリアより取得したユーザIDをエンコードする（暗号化する）
		uid = this.encodeUserId(uid);

	    HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

		// クッキーにユーザID情報を登録する
		strCookieName = uid;
		Cookie cookie = new Cookie(COOKIE_NAME, strCookieName);
		cookie.setMaxAge(Integer.MAX_VALUE);
		cookie.setPath(COOKIE_PATH);
		response.addCookie(cookie);

		if (bDebug == true) {
			this.objLogging.writeMessage(UgaLogging.LEVEL_DEBUG,  "[ ENCODED USER ID SET TO COOKIE ] " + strCookieName);
		}


   		return strCookieName;
	}

	// --- ユーザーID エンコード
	//   $uid : 元ユーザID
	//   戻り値 : エンコードされたユーザID
	protected String encodeUserId(String uid)
	{
		// STEP1 : 文字列の順序を入れ替える
		String reversed = "";
		for (int i = uid.length() - 1; i >= 0 ; i--)
			reversed += uid.charAt(i);

		byte[] bytes = null;

		// STEP2 : 暗号化パターンで、各文字の排他的論理和をとる
		try {
			bytes = reversed.getBytes("UTF-8");
			for (int i = 0; i < bytes.length; i++)
			{
				byte mask = usermask[i % usermask.length];
				bytes[i] = (byte) (bytes[i] ^ mask);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// STEP3 : 文字列を16進数文字列化する
		String hexEncoded = "";
		for (int i = 0; i < bytes.length; i++)
		{
			String hex = "0" + Integer.toHexString(bytes[i]);
            hexEncoded += hex.substring(hex.length() - 2);
		}

		return hexEncoded;
	}


	// --- ユーザID デコード
	//   $encoded : エンコードされたユーザID
	//   戻り値 : 復元したユーザID
	protected String decodeUserId(String encoded)
	{
		byte[] bytes = new byte[encoded.length() / 2];
		// STEP1 : 文字列をバイナ化する
		for (int i = 0; i < encoded.length() / 2; i++)
		{
			String sub = encoded.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte)Integer.parseInt(sub, 16);
		}

		// STEP2 : 暗号化パターンで、各文字の排他的論理和をとる
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = (byte) (bytes[i] ^ usermask[i % usermask.length]);
		}
		String decoded = "";
		try {
			decoded = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// STEP3 : 文字列の順序を入れ替える
		String reversed = "";
		for (int i = decoded.length() - 1; i >= 0; i--)
		{
			reversed += decoded.charAt(i);
		}

		return reversed;
	}

	// --- 乱数の生成
	//    戻り値 : 取得した乱数
	protected int getRandomNumber()
	{
		long seed = System.currentTimeMillis(); // 現在時刻のミリ秒
		Random rand = new Random(seed);
		return rand.nextInt(0x7fffffff);
	}


	// --- Google Analyticsへ発行する基本情報の設定
	//        Version, Tracking ID, ClientID
	//        Domain Host Name
	//        Document Title
	//        Document Path
	//        各種キャンペーン情報
	//        Google AdWords ID
	//		  Google Display Ads ID
	protected HashMap<String, String> getBasicPostParams()
	{
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("v", "1");					// Version.
		params.put("tid", this.strPropId);		// Tracking ID / Web property / Property ID.
		params.put("cid", this.strVisiterId);		// Anonymous Client ID.

		// Content Information パラメータの設定
//		params.put("dh", this.strDomainHostName);

		// Document Title
		if (strDocumentTitle.isEmpty() == false)
			params.put("dt", this.strDocumentTitle);

		// Custom Demensions / Custom Metrics
	    for (String key : this.arrayCustumDimensions.keySet())
		{
	    	String value = this.arrayCustumDimensions.get(key);
	    	if (value.isEmpty() == false)
	    		params.put("cd" + key, value);
		}
	    for (String key : this.arrayCustumMetrics.keySet())
		{
	    	String value = this.arrayCustumMetrics.get(key);
	    	if (value.isEmpty() == false)
	    		params.put("cm" + key, value);
		}


	    /*
		int iPosQuestion = this.strRequestUri.indexOf('?');
		if (iPosQuestion == -1)
		{
			params.put("dp", this.strRequestUri);
		}
		else
		{
			params.put("dp", this.strRequestUri.substring(0, iPosQuestion));
		}
*/
		// dlパラメタの追加   （2012/07/19 修正）
		params.put("dl", this.strRequestUri);


		// Traffic Sources パラメータの設定
		params.put("dr", this.strDocumentReferer);

		this.appendAnalyticsParamFromUrlParams(params, "cn", "utm_campaign");
		this.appendAnalyticsParamFromUrlParams(params, "cs", "utm_source");
		this.appendAnalyticsParamFromUrlParams(params, "cm", "utm_medium");
		this.appendAnalyticsParamFromUrlParams(params, "ck", "utm_term");
		this.appendAnalyticsParamFromUrlParams(params, "cc", "utm_content");
//		this.appendAnalyticsParamFromUrlParams(params, "ci", "(*** future support ***)");
		this.appendAnalyticsParamFromUrlParams(params, "gclid", "gclid");
		this.appendAnalyticsParamFromUrlParams(params, "dclid", "dclid");

		return params;
	}


	// --- URIの指定キーワードの情報をパラメータ配列（array型）に追加する処理
	protected void appendAnalyticsParamFromUrlParams(HashMap<String, String> analyticsParams, String analyticsKeyword, String urlKeyword)
	{
		if (this.arrayUrlParams.containsKey(urlKeyword) == true)
		{
			analyticsParams.put(analyticsKeyword, this.arrayUrlParams.get(urlKeyword));
		}
	}

	// --- 送信パラメターへの独自の情報の追加処理
	protected void appendPostParams(HashMap<String, String> post_params)
	{
		// 派生したクラスで実装
		return;
	}

	// --- ソケットによるPOST送信処理
	protected String sendSocket(String host, int port, String path, String param) throws IOException
	{
	    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String agent = request.getHeader("user-agent");

		this.objLogging.writeMessage(UgaLogging.LEVEL_INFORMATION, "[ param  ] " + param);
//		this.objLogging.writeObject("[ param ]", param);

		String data = "";
		String strRequest = "POST " + path + " HTTP/1.1\r\n"
				+ "Host: " + host + "\r\n"
				+ "Content-Type: application/x-www-form-urlencoded\r\n"
				+ "Content-Length: " + param.length() + "\r\n"
				+ "User-Agent: " + agent + "\r\n"
				+ "Connection: Close\r\n"
				+ "\r\n"
				+  param + "\r\n";

		Socket socket = null;
		DataOutputStream os = null;
		BufferedReader is = null;
		try
		{
			socket = new Socket();
			socket.setSoTimeout(timeout_msec);
			socket.connect(new InetSocketAddress(host, port), this.timeout_msec);

			os = new DataOutputStream(socket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			os.writeBytes(strRequest);
			data = is.readLine();
			String[] lineResult = data.split("\r\n");

			this.objLogging.writeMessage(UgaLogging.LEVEL_INFORMATION,  "[ result ] " + lineResult[0]);
		}
		catch (SocketTimeoutException ex_timeout)
		{
			this.objLogging.writeMessage(UgaLogging.LEVEL_ERROR, "UGA-ERROR-005 : Socket Timeout");
		}
		catch (Exception ex)
		{
			this.objLogging.writeMessage(UgaLogging.LEVEL_ERROR, "UGA-ERROR-006 : " + ex.getMessage());
		}
		finally
		{
			if (is != null)
				is.close();
			if (os != null)
				os.close();
			if (socket != null)
				socket.close();
		}

		return data;
	}
}


