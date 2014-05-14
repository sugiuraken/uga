package uga;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.DecoderException;

public class UniversalAnalytics_ecom extends UniversalAnalytics
{
//	private String strTransactionId = "";			// Transaction ID

	public UniversalAnalytics_ecom(PageContext pageContext)
	{
		super(pageContext);
	}

	// ---- タイプIDの取得
	public String getHitTypeId()
	{
		return HITTYPE_ECOMMERCE_TRACKING;
	}

    public void sendAnalytics() throws DecoderException
    {
    	if (bNoEffect == true)
    		return;
    	
    	String value;
    	try
    	{
			this.analyzeRequest();

			HashMap<String, String> paramsTransaction = new HashMap<String, String>();
			paramsTransaction.put("t",  "transaction");


			value = this.getUrlEncodedParam(PNAME_TRANSACTION_ID);
			paramsTransaction.put("ti", value);

			value = this.getUrlEncodedParam(PNAME_TRANSACTION_AFFILIATION);
			if (value.isEmpty() == false)
				paramsTransaction.put("ta", value);

			value = this.getUrlEncodedParam(PNAME_TRANSACTION_REVENUE);
			if (value.isEmpty() == false)
				paramsTransaction.put("tr", value);

			value = this.getUrlEncodedParam(PNAME_TRANSACTION_SHIPPING);
			if (value.isEmpty() == false)
				paramsTransaction.put("ts", value);

			value = this.getUrlEncodedParam(PNAME_TRANSACTION_TAX);
			if (value.isEmpty() == false)
				paramsTransaction.put("tt", value);

	        String strCurrencyCode = this.getUrlEncodedParam(PNAME_TRANSACTION_CURRENCY_CODE);
	        if (!strCurrencyCode.isEmpty())
		        paramsTransaction.put("cu", strCurrencyCode);


	        this.postAnalyticsParam(paramsTransaction);


        	String items = this.getUrlEncodedParam(PNAME_TRANSACTION_ITEMS);
	        if (items != null && !items.isEmpty())
	        {
		        HashMap<String, String> paramsItem = new HashMap<String, String>();
		        paramsItem.put("t",  "item");
		        paramsItem.put("ti", this.getUrlEncodedParam(PNAME_TRANSACTION_ID));

		        String [] arrayItem = items.split("\t");

        		int i = 0;
        		while (i < arrayItem.length)
        		{
        			value = arrayItem[i++];
        			if (value.isEmpty() == false)
        				paramsItem.put("ip", value);

        			if (i < arrayItem.length)
        			{
            			value = arrayItem[i++];
            			if (value.isEmpty() == false)
            				paramsItem.put("iq", value);
        			}

        			if (i < arrayItem.length)
        			{
            			value = arrayItem[i++];
            			if (value.isEmpty() == false)
            				paramsItem.put("ic", value);
        			}


        			if (i < arrayItem.length)
        			{
            			value = arrayItem[i++];
            			if (value.isEmpty() == false)
            				paramsItem.put("in", value);
        			}

        			if (i < arrayItem.length)
        			{
            			value = arrayItem[i++];
            			if (value.isEmpty() == false)
            				paramsItem.put("iv", value);
        			}

        			if (i < arrayItem.length)
        			{
	        	        String strCurrencyCodeItem = arrayItem[i++];
	        	        if (!strCurrencyCodeItem.isEmpty())
	        	        	paramsItem.put("cu", strCurrencyCodeItem);
        			}

    	    		this.postAnalyticsParam(paramsItem);
        		}
	        }
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

	public void setTransactionId(String strTransactionId)
	{
//		this.strTransactionId = strTransactionId;
		this.appendOptionalParam(PNAME_TRANSACTION_ID,  strTransactionId);
	}


	public void setTransactionParams(String strAffiliation, String revenue, String shipping, String tax, String curCode)
	{
		this.appendOptionalParam(PNAME_TRANSACTION_AFFILIATION,  strAffiliation);
		this.appendOptionalParam(PNAME_TRANSACTION_REVENUE,  revenue);
		this.appendOptionalParam(PNAME_TRANSACTION_SHIPPING,  shipping);
		this.appendOptionalParam(PNAME_TRANSACTION_TAX,  tax);
		this.appendOptionalParam(PNAME_TRANSACTION_CURRENCY_CODE,  curCode);
	}

	public void addItemParams(String price, String quantity, String strCode, String strName, String strVariation, String curCode)
	{
		String strItems = "";

		if (this.arrayOptionalParams.containsKey(PNAME_TRANSACTION_ITEMS) ==  true)
		{
			strItems = this.arrayOptionalParams.get(PNAME_TRANSACTION_ITEMS);
			strItems += "\t";
		}
		strItems += price + "\t" + quantity + "\t" + strCode + "\t" + strName + "\t" + strVariation + "\t" + curCode;
		this.appendOptionalParam(PNAME_TRANSACTION_ITEMS,  strItems);
	}
}
