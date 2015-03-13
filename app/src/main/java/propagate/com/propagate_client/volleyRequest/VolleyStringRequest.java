package propagate.com.propagate_client.volleyRequest;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.utils.SessionManager;

/**
 * Created by kaustubh on 12/3/15.
 */
public class VolleyStringRequest extends StringRequest {
  private SessionManager sessionManager;
  private final Map<String, String> params;

  public VolleyStringRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener,Context context) {
    super(method, url, listener, errorListener);
    this.params = params;
    sessionManager = new SessionManager(context);
  }

  @Override
  public Map<String, String> getParams() {
    return params;
  }

  @Override
  public Map<String, String> getHeaders() throws AuthFailureError {
    HashMap<String,String> sessionTokens = sessionManager.getSessionTokens();
//    String XCSRFToken = sessionTokens.get(sessionManager.KEY_XCSRF_TOKEN);
    String xAuthToken = sessionTokens.get(sessionManager.KEY_XAUTH_TOKEN);

    Map<String,String> headers = new HashMap<String, String>();
    headers.put("Content-Type","application/x-www-form-urlencoded");
    /*if(XCSRFToken != null)
      headers.put("X-CSRF-TOKEN",XCSRFToken);*/

    if(xAuthToken != null)
      headers.put("X-AUTH-TOKEN",xAuthToken);

    return headers;
  }

  @Override
  protected VolleyError parseNetworkError(VolleyError volleyError) {
    return super.parseNetworkError(volleyError);
  }
}
