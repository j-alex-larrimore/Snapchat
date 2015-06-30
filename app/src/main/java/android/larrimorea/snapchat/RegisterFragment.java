package android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;

/**
 * Created by Alex on 6/29/2015.
 */
public class RegisterFragment extends Fragment {

    private Button mRegisterButton;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private String mUsername;
    private String mPassword;
    private User user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        mRegisterButton = (Button)view.findViewById(R.id.registerButton);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUsername != null && mPassword != null) {
                    user = new User();
                    user.setUsername(mUsername);
                    user.setPassword(mPassword);
                    try {
                        user.save();
                    }catch(ParseException p){
                        Log.e("Register", "Parse error " + p);
                    }

                    Intent returnIntent = new Intent();
                    //returnIntent.putExtra("Result", "true");
                    getActivity().setResult(getActivity().RESULT_OK, returnIntent);
                    getActivity().finish();
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

    public User getCurrentUser(){
        return user;
    }
}
