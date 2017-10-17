package com.chatchat.chatchat.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.chatchat.Adapter.ForumAdapter;
import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.Forum;
import com.chatchat.chatchat.Model.Hotel;
import com.chatchat.chatchat.R;
import com.chatchat.chatchat.RequestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class ForumFragment extends Fragment {

    private Hotel hotel;
    private Context context = getActivity();
    TextView emptyforum;
    private ListView listViewforum;
    private ArrayList<Forum> forumList;
    private ForumAdapter adapter;
    public String username;
    public String password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forum,container,false);
        listViewforum = (ListView) view.findViewById(R.id.forumListView);
        emptyforum = (TextView) view.findViewById(R.id.emptyforum);
        forumList = new ArrayList<Forum>();
        adapter = new ForumAdapter(context,R.layout.forum_list,forumList);
        listViewforum.setAdapter(adapter);

        listViewforum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Forum fo = forumList.get(position);
                Intent intent = new Intent(context,RequestActivity.class);
                intent.putExtra("name",fo.name);
                intent.putExtra("code",fo.code);
                intent.putExtra("order",position);
                intent.putExtra("ho_username",hotel.username);
                intent.putExtra("startdate",hotel.startdate);
                intent.putExtra("enddate",hotel.enddate);
                startActivity(intent);
            }
        });

        if(Defined.checkInternetConnection(context)) {
            new API_DoGetListForums(getActivity()).execute();
        }else{
            emptyforum.setVisibility(TextView.VISIBLE);
            listViewforum.setVisibility(ListView.INVISIBLE);
        }
        return view;
    }

    public ForumFragment(Context context, Hotel hotel) {
        this.context = context;
        this.hotel = hotel;
    }


    private void refreshList(){
        ForumAdapter lh = new ForumAdapter(getActivity(), R.layout.forum_list, forumList);
        listViewforum.setAdapter(lh);
    }

    class API_DoGetListForums extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String language;

        API_DoGetListForums(Activity context)
        {
            this.context = context;
            pDialog= new ProgressDialog(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog.setMessage("Please wait for requesting...");
            //  pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(String... args) {
            JSONObject json = new JSONObject();
            language = Locale.getDefault().getLanguage();
//            if(!language.equals("it") && !language.equals("en")){
//                language = "en";
//            }
            try {
                json.put("ApiKey", Defined.API_KEY);
                json.put("Login", username);
                json.put("Password", password);
                json.put("language", language);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            String requestBody = null;
            try {
                requestBody = Defined.getPostDataString(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            URL url = null;
            try {
                url = new URL(Defined.API_getlistForum);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" +
                        Integer.toString(requestBody.getBytes().length));
                urlConnection.setRequestProperty("Content-Language", "en-US");
                urlConnection.setUseCaches (false);

                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                try {
                    writer.write(requestBody);
                    writer.flush();
                    writer.close();
                    outputStream.close();
                    InputStream inputStream;

                    // get stream
                    if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                        inputStream = urlConnection.getInputStream();
                    } else {
                        inputStream = urlConnection.getErrorStream();
                    }
                    // parse stream
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String temp, response = "";
                    while ((temp = bufferedReader.readLine()) != null) {
                        response += temp;
                    }
                    jsonstr = response;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            Log.e("Ket qua tra ve",jsonstr);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) pDialog.dismiss();
            try{
                JSONObject response = new JSONObject(jsonstr);
                String status = response.getString("result");
                if (status.equals("ok")) {
                    String str = response.getString("number");
                    int num = Integer.parseInt(str);
                    if(num == 0){
                        emptyforum.setVisibility(TextView.VISIBLE);
                        listViewforum.setVisibility(ListView.INVISIBLE);
                    }else{
                        emptyforum.setVisibility(TextView.INVISIBLE);
                        listViewforum.setVisibility(ListView.VISIBLE);
                        String str1 = response.getString("forums");
                        JSONArray arr = new JSONArray(str1);
                        for (int i = 0;i<arr.length();i++){
                            JSONObject inner = arr.getJSONObject(i);
                            String fo_code = inner.getString("fo_code");
                            String fo_text = inner.getString("fo_text");
                            forumList.add(new Forum(fo_text,fo_code,i + 1));
                        }
                        refreshList();
                    }
                } else {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException es){
                return;
            }
        }
    }


//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ForumFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ForumFragment newInstance(String param1, String param2) {
//        ForumFragment fragment = new ForumFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_forum, container, false);
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
