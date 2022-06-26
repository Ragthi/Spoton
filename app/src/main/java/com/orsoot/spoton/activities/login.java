package com.orsoot.spoton.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orsoot.spoton.MainActivity;
import com.orsoot.spoton.Mysingleton;
import com.orsoot.spoton.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class login extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button login_bt;
    EditText username_login,password_login;
    TextView login_error,movetoregister;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public login() {
        // Required empty public constructor
    }

    public static login newInstance(String param1, String param2) {
        login fragment = new login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        login_bt =(Button) v.findViewById(R.id.btn_login);
        username_login = v.findViewById(R.id.phone_number_login);
        password_login = v.findViewById(R.id.password_login);
        login_error = v.findViewById(R.id.errormessage_login);
        movetoregister = v.findViewById(R.id.movetoregister);
        movetoregister.setOnClickListener(this);
        login_bt.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if(data_available()){
                    getrespone(username_login.getText().toString(), password_login.getText().toString());
                }

                break;
            case R.id.movetoregister:
                ((loginandregister) getActivity()).getPager().setCurrentItem(0);
                break;
        }

    }

    private void getrespone(String username, String pass) {
        boolean result = false;
        String url = "https://spoton.orsoot.com/api/auth?username="+username+"&password="+pass;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if(status.equals("success")){
                                Intent movetomainactivity = new Intent(getContext(), MainActivity.class);
                                startActivity(movetomainactivity);
                                getActivity().finish();
                            }else{
                                String msg = response.getString("msg");
                                login_error.setText(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        login_error.setText("Error" + error.toString());

                    }
                });
        Mysingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private boolean data_available() {
        boolean available = true;
        if(TextUtils.isEmpty(username_login.getText()) || (username_login.getText().length()!=10))
        {
            username_login.setError(" Please Enter Valid Phone number ");
            available = false;
        }
        if(TextUtils.isEmpty(password_login.getText()))
        {
            password_login.setError(" Please Enter password ");
            available = false;
        }

        if(available){
            return true;
        }else{
            return false;
        }
    }
}