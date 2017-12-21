package com.omneagate.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.omneagate.DTO.FailedKycDto;
import com.omneagate.DTO.POSAadharAuthRequestDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TLSSocketFactory;
import com.omneagate.Util.Util;
import com.omneagate.activity.R;
import com.omneagate.activity.dialog.EncrypterUtil;
import com.omneagate.activity.dialog.SessionKeyDetailsUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Service for checking connection of server
 */
public class BufferedAuthService extends Service {

    Timer bufferedAuthTimer;
    BufferedAuthTimerTask bufferedAuthTimerTask;
    Date posReqDate;

    @Override
    public void onCreate() {
        super.onCreate();
        bufferedAuthTimer = new Timer();
        bufferedAuthTimerTask = new BufferedAuthTimerTask();
        Util.LoggingQueue(this, "Info", "BufferedAuthService created");
        Long timerWaitTime = Long.parseLong(getString(R.string.serviceTimeout));
        bufferedAuthTimer.schedule(bufferedAuthTimerTask, 1000, timerWaitTime);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            if (bufferedAuthTimer != null) {
                bufferedAuthTimer.cancel();
                bufferedAuthTimer = null;
            }
        } catch (Exception e) {
            Log.e("BufferedAuthService exception", "Error in BufferedAuthService", e);
        }

    }

    /*return http POST method using parameters*/
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 15000;
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        int timeoutSocket = 15000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpPost postRequest = new HttpPost();
        postRequest.setURI(website);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setHeader("Store_type", "fps");
        postRequest.setHeader("Cookie", "JSESSIONID=" + SessionId.getInstance().getSessionId());
        postRequest.setHeader("Cookie", "SESSION=" + SessionId.getInstance().getSessionId());
        postRequest.setEntity(entity);
        return client.execute(postRequest);
    }

    class BufferedAuthTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                Util.LoggingQueue(com.omneagate.service.BufferedAuthService.this, "Info", "Started to retrieve KYC request details");
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    GregorianCalendar gc = new GregorianCalendar();
                    String dateString = sdf.format(gc.getTime());
                    posReqDate = sdf.parse(dateString);
                }
                catch(Exception e) {}
                if (SessionId.getInstance().getSessionId() != null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId()) && SessionId.getInstance().getSessionId().length() > 0) {
                    NetworkConnection network = new NetworkConnection(com.omneagate.service.BufferedAuthService.this);
                    if (network.isNetworkAvailable()) {
                        new ConnectionBufferedAuth().execute("");
                    }
                } else {
                    Util.LoggingQueue(com.omneagate.service.BufferedAuthService.this, "BufferedAuthService Error ", "Session is null or zero");
                }
                Util.LoggingQueue(com.omneagate.service.BufferedAuthService.this, "Info", "End of BufferedAuthService");
            } catch (Exception e) {
                Util.LoggingQueue(com.omneagate.service.BufferedAuthService.this, "BufferedAuthService Exception ", e.getMessage());
            }
        }
    }

    //Async task for BufferedAuth
    private class ConnectionBufferedAuth extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... f_url) {
            List<FailedKycDto> failedKycDtoList = FPSDBHelper.getInstance(com.omneagate.service.BufferedAuthService.this).getFailedKYCRequest();
            for(int i=0;i<failedKycDtoList.size();i++) {
                try {
                    sendKYCRequest(failedKycDtoList.get(i));
                } catch (Exception e) {}
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

        }
    }

    private void sendKYCRequest(final FailedKycDto failedKycDto) {
        try {
            HttpStack stack = null;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            }
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(com.omneagate.service.BufferedAuthService.this, stack);
            String pidXml;
            final String encodedBioMetricInfo = Base64.encodeToString(failedKycDto.getFingerPrintData(), Base64.DEFAULT);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            final Date authReqDate = sdf.parse(dateString);
            pidXml = buildPidXml(dateString, encodedBioMetricInfo, null);
            String fileNameWithPath = Environment.getExternalStorageDirectory().toString() + "/Fps/" + "uidai_auth_prod.cer";
            EncrypterUtil encrypterUtil = new EncrypterUtil(fileNameWithPath);
            byte[] sessionKey = encrypterUtil.generateSessionKey();
            byte[] encryptedSessionKey = encrypterUtil.encryptUsingPublicKey(sessionKey);
            SessionKeyDetailsUtil sessionKeyDetails = SessionKeyDetailsUtil.createNormalSkey(encryptedSessionKey);
            byte[] pidXmlBytes = pidXml.getBytes();
            byte[] encXMLPIDData = encrypterUtil.encryptUsingSessionKey(sessionKey, pidXmlBytes);
            byte[] hmac = generateSha256Hash(pidXmlBytes);
            byte[] encryptedHmacBytes = encrypterUtil.encryptUsingSessionKey(sessionKey, hmac);
            String certificateIdentifier = encrypterUtil.getCertificateIdentifier();
            JSONObject jsonData = new JSONObject();
            jsonData.put("Uid", failedKycDto.getAadharNumber());
            jsonData.put("TerminalId", "public");
            jsonData.put("EncryptedPid", Base64.encodeToString(encXMLPIDData, Base64.DEFAULT));
            jsonData.put("EncryptedHmac", Base64.encodeToString(encryptedHmacBytes, Base64.DEFAULT));
            jsonData.put("Ci", certificateIdentifier);
            jsonData.put("Ts", dateString);
            jsonData.put("EncryptedSessionKey", Base64.encodeToString(sessionKeyDetails.getSkeyValue(), Base64.DEFAULT));
            jsonData.put("Fdc", "NC");
            jsonData.put("Lov", "560103");
            jsonData.put("PublicIp", "127.0.0.1");
            jsonData.put("Udc", "MTA-231755");
            if (false)
                jsonData.put("IsKyc", "true");
            else
                jsonData.put("IsKyc", "false");
            jsonData.put("securityToken", "JAH73VBSshsksdk23VSJDO928vskWIIPPQ837A");
            jsonData.put("clientId", "1");
            final String json = jsonData.toString();
            Log.e("Request", "Request json " + json);
            final String uriString = "https://devkua.finahub.com/KUAServer/kyc";
            final JSONObject jsonBody = new JSONObject(json);
            JsonObjectRequest req = new JsonObjectRequest(uriString, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    Log.e("BufferedAuthService", "Response is " + response.toString());
                    try {
                        String status = (String) response.get("Status");
                        // Insert datas into biometric_authentication table
                        POSAadharAuthRequestDto posAadharAuthRequestDto = new POSAadharAuthRequestDto();
                        posAadharAuthRequestDto.setUid(failedKycDto.getAadharNumber());
                        posAadharAuthRequestDto.setFpsId(SessionId.getInstance().getFpsId());
                        try {
                            posAadharAuthRequestDto.setBeneficiaryId(Long.valueOf(failedKycDto.getBeneficiaryId()));
                        }
                        catch(Exception e) {}
                        posAadharAuthRequestDto.setAuthReponse(response.toString());
                        if (status.equalsIgnoreCase("Y")) {
                            posAadharAuthRequestDto.setAuthenticationStatus(true);
                        }
                        else if (status.equalsIgnoreCase("N")) {
                            posAadharAuthRequestDto.setAuthenticationStatus(false);
                        }
                        posAadharAuthRequestDto.setPosRequestDate(posReqDate.getTime());
                        posAadharAuthRequestDto.setAuthRequestDate(authReqDate.getTime());
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            GregorianCalendar gc = new GregorianCalendar();
                            String dateString = sdf.format(gc.getTime());
                            Date authRespDate = sdf.parse(dateString);
                            posAadharAuthRequestDto.setAuthResponseDate(authRespDate.getTime());
                            posAadharAuthRequestDto.setPosResponseDate(authRespDate.getTime());
                        }
                        catch(Exception e) {}
                        posAadharAuthRequestDto.setFingerPrintData(failedKycDto.getFingerPrintData());
                        FPSDBHelper.getInstance(com.omneagate.service.BufferedAuthService.this).insertBiometric(posAadharAuthRequestDto);
                        FPSDBHelper.getInstance(com.omneagate.service.BufferedAuthService.this).updateKycRequestDetails(failedKycDto.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("BufferedAuthService", "Response  Error is " + error.toString());
                        }
                    });
            queue.add(req);
        }
        catch(Exception e) {
            Log.e("BufferedAuthService", "buffered authentication request exc...." + e.toString());
            e.printStackTrace();
        }
    }

    private String buildPidXml(String timeStamp, String encodedBiometric, String encodedBiometric2ndFingur) {
        StringBuffer buff = new StringBuffer();
        buff.append("<Pid ts=");
        buff.append("\"" + timeStamp + "\"");
        buff.append(">");
        buff.append("<Bios>");
        buff.append("<Bio type=\"FMR\" posh=\"UNKNOWN\">");
        buff.append(encodedBiometric);
        buff.append("</Bio>");
        if (encodedBiometric2ndFingur != null) {
            buff.append("<Bio type=\"FMR\" posh=\"UNKNOWN\">");
            buff.append(encodedBiometric2ndFingur);
            buff.append("</Bio>");
        }
        buff.append("</Bios></Pid>");
        return buff.toString();
    }

    public byte[] generateSha256Hash(byte[] message) {
        String algorithm = "SHA-256";
        String SECURITY_PROVIDER = "BC";
        byte[] hash = null;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm, SECURITY_PROVIDER);
            digest.reset();
            hash = digest.digest(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }


}
