package android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

/**
 * Created by Alex on 6/29/2015.
 */
public class LogInFragment extends Fragment {

    private Button mLogInButton;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private String mUsername;
    private String mPassword;
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);

        pause = false;

        mLogInButton = (Button)view.findViewById(R.id.loginButton);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUsername != null && mPassword != null && pause == false) {
                    pause = true;
                    AsyncCallback<BackendlessUser> callback = new AsyncCallback<BackendlessUser>()
                    {
                        @Override
                        public void handleResponse( BackendlessUser loggedInUser )
                        {
                            System.out.println( "User has been logged in - " + loggedInUser.getObjectId() );
                            Log.i("Login", "Info: " + Backendless.UserService.CurrentUser().getProperty("name"));
                            Intent returnIntent = new Intent();
                            getActivity().setResult(getActivity().RESULT_OK, returnIntent);
                            getActivity().finish();
                        }

                        @Override
                        public void handleFault( BackendlessFault backendlessFault )
                        {
                            System.out.println( "Server reported an error - " + backendlessFault.getMessage() );
                        }
                    };

                    Backendless.UserService.login( mUsername, mPassword, callback );



                }
            }
        });
        mUsernameField = (EditText)view.findViewById(R.id.username);

        mUsernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsername = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPasswordField = (EditText)view.findViewById(R.id.password);

        mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPassword = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }

}
