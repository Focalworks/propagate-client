package propagate.com.propagate_client.volleyRequest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.utils.CommonFunctions;

/**
 * Created by kaustubh on 12/3/15.
 */
public class VolleyStringRequest extends StringRequest {
  private final Map<String, String> params;
  private int appVersion;
  private Map<String,String> headers;

  public VolleyStringRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener,Context context) {
    super(method, url, listener, errorListener);
    this.params = params;
    appVersion = CommonFunctions.getVersion(context);
  }

  public VolleyStringRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener,Context context,Map<String,String> headers) {
    super(method, url, listener, errorListener);
    this.params = params;
    appVersion = CommonFunctions.getVersion(context);
    this.headers = headers;
  }

  @Override
  public Map<String, String> getParams() {
    return params;
  }

  @Override
  public Map<String, String> getHeaders() throws AuthFailureError {
/*

    Map<String,String> headers = new HashMap<String, String>();
    headers.put("Content-Type","application/x-www-form-urlencoded");
    headers.put("client-type","android");
    headers.put("client-version", appVersion+"");
*/
    return headers;
  }

  @Override
  protected Response parseNetworkResponse(NetworkResponse response) {
    String parsed = null;
    if(response.statusCode == 201 || response.statusCode == 200) {
      try {
        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
      } catch (UnsupportedEncodingException e) {
        parsed = new String(response.data);
      }
    }
    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
  }

  @Override
  protected VolleyError parseNetworkError(VolleyError volleyError) {
    return volleyError;
  }
}
