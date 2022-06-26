package com.orsoot.spoton.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.orsoot.spoton.MainActivity;
import com.orsoot.spoton.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class register extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button register_bt;
    TextView movetolog,error_tv;
    EditText full_name,email,alternate_email,dob,password,phone_number,alternate_number;
    final Calendar myCalendar= Calendar.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public register() {
        // Required empty public constructor
    }

    public static register newInstance(String param1, String param2) {
        register fragment = new register();
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
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        AssignWidgets(v);
        register_bt.setOnClickListener(this);
        movetolog.setOnClickListener(this);
        return v;
    }

    private void AssignWidgets(View v) {
        error_tv = v.findViewById(R.id.errormessage);
        register_bt =(Button) v.findViewById(R.id.btn_register);
        full_name =(EditText) v.findViewById(R.id.full_name);
        email =(EditText) v.findViewById(R.id.email_id);
        alternate_email =(EditText) v.findViewById(R.id.alt_email_id);
        dob =(EditText) v.findViewById(R.id.dob);
        phone_number =(EditText) v.findViewById(R.id.phone_number);
        alternate_number =(EditText) v.findViewById(R.id.alt_phone_no);
        password =(EditText) v.findViewById(R.id.register_password);
        movetolog = v.findViewById(R.id.movetologin);
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:

                if(data_available()){
                    getrespone(full_name.getText().toString(),
                            dob.getText().toString(),
                            password.getText().toString(),
                            email.getText().toString(),
                            alternate_email.getText().toString(),
                            phone_number.getText().toString(),
                            alternate_number.getText().toString());

                }


                break;
            case R.id.movetologin:
                ((loginandregister) getActivity()).getPager().setCurrentItem(1);

                break;
        }
    }

    private void getrespone(String name, String date_ob, String pass, String email, String alt_email, String number, String alt_number) {
        String url = "https://spoton.orsoot.com/api/register";
       // loadingPB.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //loadingPB.setVisibility(View.GONE);

                try {
                    JSONArray respObj = new JSONArray(response);
                    String status = respObj.getJSONObject(0).getString("status");
                    if(status.equals("success")){
                        Intent movetomainactivity = new Intent(getContext(), MainActivity.class);
                        startActivity(movetomainactivity);
                        getActivity().finish();
                    }else{
                        String msg = respObj.getJSONObject(0).getString("msg");
                        msg = msg.replace("<p>","");
                        msg = msg.replace("</p>","");
                        error_tv.setText(msg);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response ","Name : " + error.toString());
                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("full_name", name);
                params.put("dob", date_ob);
                params.put("email", email);
                params.put("alternate_email", alt_email);
                params.put("password", pass);
                params.put("phone_number", number);
                params.put("alternate_number", alt_number);

                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    private boolean data_available() {
        boolean available = true;
        if(TextUtils.isEmpty(full_name.getText()))
        {
            full_name.setError(" Please Enter Name ");
            available = false;
        }
        if(TextUtils.isEmpty(email.getText()))
        {
            email.setError(" Please Enter email ");
            available = false;
        }
        if(TextUtils.isEmpty(phone_number.getText()) || (phone_number.getText().length()!=10))
        {
            phone_number.setError(" Please Enter valid phone no ");
            available = false;
        }
        if(TextUtils.isEmpty(dob.getText()))
        {
            dob.setError(" Please Enter DOB ");
            available = false;
        }
        if(TextUtils.isEmpty(password.getText()))
        {
            password.setError(" Please Enter password ");
            available = false;
        }
        if(available){
            return true;
        }else{
            return false;
        }
    }
}