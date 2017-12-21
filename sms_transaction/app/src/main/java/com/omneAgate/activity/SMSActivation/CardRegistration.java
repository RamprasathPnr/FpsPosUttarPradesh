package com.omneagate.activity.SMSActivation;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by user1 on 29/4/15.
 */
public class CardRegistration {

    Context context;

    public CardRegistration(Context context) {
        this.context = context;
    }

    public void cardActivationRequest(String cardNumber, String mobileNumber) {
        Log.e("number", mobileNumber);
        mobileNumber = StringUtils.right(mobileNumber, 10);
        if (FPSDBHelper.getInstance(context).insertRegistration(mobileNumber, cardNumber)) {
            BenefActivNewDto benefActivNew = new BenefActivNewDto();
            benefActivNew.setMobileNum(mobileNumber);
            benefActivNew.setRationCardNumber(cardNumber);
            benefActivNew.setDeviceNum(Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            TransactionBaseDto transaction = new TransactionBaseDto();
            transaction.setTransactionType(TransactionTypes.BENEFICIARY_REGREQUEST_NEW);
            transaction.setType("com.omneagate.rest.dto.BenefActivNewDto");
            transaction.setBaseDto(benefActivNew);
            new CardRegistrationAsync().execute(transaction);
        }

    }

    //Async task to send bill to server
    class CardRegistrationAsync extends AsyncTask<TransactionBaseDto, String, String> {

        @Override
        protected String doInBackground(TransactionBaseDto... billData) {
            BufferedReader in = null;
            try {
                String url = GlobalAppState.serverUrl + "/transaction/process";
                URI website = new URI(url);
                String bill = new Gson().toJson(billData[0]);
                Log.e("bill", billData[0].toString());
                Log.e("url", url);
                StringEntity entity = new StringEntity(bill, HTTP.UTF_8);
                String responseText = EntityUtils.toString(entity);
                Log.e("responseText", responseText);
                Util.LoggingQueue(context, "Info", "Sending request bill" + billData[0]);
                HttpResponse response = requestType(website, entity);
                in = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                in.close();
                String responseData = sb.toString();
                Log.e("Response", responseData);
                Util.LoggingQueue(context, "Info", "Received response " + responseData);
                return responseData;
            } catch (Exception e) {
                Util.LoggingQueue(context, "Error", "Network exception" + e.getMessage());
                Log.e("Error: ", e.toString());
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                }
            }
            return null;
        }

        /*return http GET,POST and PUT method using parameters*/
        private HttpResponse requestType(URI website, StringEntity entity) throws IOException {

            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 50000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 50000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost postRequest = new HttpPost();
            postRequest.setURI(website);
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setHeader("Cookie", "JSESSIONID=" + SessionId.getInstance().getSessionId());
            postRequest.setEntity(entity);
            return client.execute(postRequest);

        }


        @Override
        protected void onPostExecute(String response) {
            try {
                if (response != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    BenefActivNewDto updateStock = gson.fromJson(response, BenefActivNewDto.class);
                    Log.e("Response", updateStock + "");
                    if (updateStock.getStatusCode() == 0)
                        FPSDBHelper.getInstance(context).updateRegistration(updateStock.getRationCardNumber(), "S", "Success");
                }
            } catch (Exception e) {
                Log.e("Error", e.toString(), e);
            }

        }
    }
}
