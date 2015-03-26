package propagate.com.propagate_client.volleyRequest;

import com.android.volley.VolleyError;

import propagate.com.propagate_client.contact.Contact;

/**
 * Created by kaustubh on 26/3/15.
 */
public interface APIHandlerInterface {

  public void OnRequestResponse(String response);
  public void OnRequestErrorResponse(VolleyError error);

}
