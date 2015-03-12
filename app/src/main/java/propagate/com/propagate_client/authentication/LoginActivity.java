package propagate.com.propagate_client.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import propagate.com.propagate_client.R;

/**
 * Created by kaustubh on 11/3/15.
 */
public class LoginActivity extends Activity {

  EditText etEmail,etPassword;
  Button btnLogin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_screen);

    etEmail = (EditText) findViewById(R.id.etEmailId);
    etPassword = (EditText) findViewById(R.id.etPassword);

    btnLogin = (Button) findViewById(R.id.btnLogin);
    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
  }
}
