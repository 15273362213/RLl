package spring;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * 鐧惧害缈昏瘧寮曟搸java绀轰緥浠ｇ爜
 */
public class BaiduTranslateDemo{
	
	private static final String UTF8 = "utf-8";
	
	//鐢宠鑰呭紑鍙戣�id锛屽疄闄呬娇鐢ㄦ椂璇蜂慨鏀规垚寮�彂鑰呰嚜宸辩殑appid
	private static final String appId = "20170530000049571";

	//鐢宠鎴愬姛鍚庣殑璇佷功token锛屽疄闄呬娇鐢ㄦ椂璇蜂慨鏀规垚寮�彂鑰呰嚜宸辩殑token
	private static final String token = "_5Etn6o6NHYDpAImEJqU";

	private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";

	//闅忔満鏁帮紝鐢ㄤ簬鐢熸垚md5鍊硷紝寮�彂鑰呬娇鐢ㄦ椂璇锋縺娲讳笅杈圭鍥涜浠ｇ爜
	private static final Random random = new Random();
	
	public static String translate(String q, String from, String to) throws Exception{
		//鐢ㄤ簬md5鍔犲瘑
		//int salt = random.nextInt(10000);
		//鏈紨绀轰娇鐢ㄦ寚瀹氱殑闅忔満鏁颁负1435660288
		int salt = 1435660288;
		
		// 瀵筧ppId+婧愭枃+闅忔満鏁�token璁＄畻md5鍊�
		StringBuilder md5String = new StringBuilder();
		md5String.append(appId).append(q).append(salt).append(token);
		String md5 = DigestUtils.md5Hex(md5String.toString());

		//浣跨敤Post鏂瑰紡锛岀粍瑁呭弬鏁�
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
		nvps.add(new BasicNameValuePair("q", q));  
		   nvps.add(new BasicNameValuePair("from", from));  
		   nvps.add(new BasicNameValuePair("to", to));  
		   nvps.add(new BasicNameValuePair("appid", appId));  
		   nvps.add(new BasicNameValuePair("salt", String.valueOf(salt)));  
		   nvps.add(new BasicNameValuePair("sign", md5));  
		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));  

		//鍒涘缓httpclient閾炬帴锛屽苟鎵ц
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httpost);
	    
	    //瀵逛簬杩斿洖瀹炰綋杩涜瑙ｆ瀽
		HttpEntity entity = response.getEntity();
		InputStream returnStream = entity.getContent();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(returnStream, UTF8));
		StringBuilder result = new StringBuilder();
		String str = null;
		while ((str = reader.readLine()) != null) {
			result.append(str).append("\n");
		}
		
		//杞寲涓簀son瀵硅薄锛屾敞锛欽son瑙ｆ瀽鐨刯ar鍖呭彲閫夊叾瀹�
		JSONObject resultJson = new JSONObject(result.toString());

		//寮�彂鑰呰嚜琛屽鐞嗛敊璇紝鏈ず渚嬪け璐ヨ繑鍥炰负null
		try {
			String error_code = resultJson.getString("error_code");
			if (error_code != null) {
				System.out.println("鍑洪敊浠ｇ爜:" + error_code);
				System.out.println("鍑洪敊淇℃伅:" + resultJson.getString("error_msg"));
				return null;
			}
		} catch (Exception e) {}
		
		//鑾峰彇杩斿洖缈昏瘧缁撴灉
		JSONArray array = (JSONArray) resultJson.get("trans_result");
		JSONObject dst = (JSONObject) array.get(0);
		String text = dst.getString("dst");
		text = URLDecoder.decode(text, UTF8);

		return text;
	}
	
	//瀹為檯鎶涘嚭寮傚父鐢卞紑鍙戣�鑷繁澶勭悊
	public static  String translateToEn(String q) throws Exception{
		ApplicationContext container=new FileSystemXmlApplicationContext("src//spring//resource//baidu.xml");
		BaiduTranslateDemo baidu = (BaiduTranslateDemo)container.getBean("baidu");
		
		String result = null;
		try {
			result = baidu.translate(q, "zh", "en");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
