package com.omneagate.Util;

/**
 * Created by user1 on 18/3/15.
 */
// DownloadFile AsyncTask

public class DownloadNewVersion // extends AsyncTask<String, Integer, String> {
{

   /* //Activity context
   public  Activity context;

   public ProgressDialog progressBar;
    // Constructor
   public DownloadNewVersion(Activity context) {
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
      //  context.showDialog(AutoUpgrationActivity.progress_bar_type);

        progressBar = new ProgressDialog(context);

        progressBar.setIndeterminate(false);
        progressBar.setMessage("File downloading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.setCancelable(true);
        progressBar.show();

        Log.e("onPre","onPre");

    }

    @Override
    protected String doInBackground(String... sUrl) {
        Log.e("doIn","Background");
        String title =context. getResources().getString(R.string.app_name);
        String path = Environment.getExternalStorageDirectory() + "/"+ title+".apk";
        try {
            URL url = new URL(sUrl[0]);
            Log.e("doIn","url"+url);
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            Log.e("fileL",""+fileLength);                    // 297619  .jpg

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),8192);//8192
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                total += count;
                this.publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
                Log.e("ProgBack",""+"" + (int) (total * 100 / fileLength));
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("YourApp", "Well that didn't work out so well...");
            Log.e("YourApp", e.getMessage());
        }
        return path;
    }

    // Update the progress dialog

    @Override
    protected void onProgressUpdate(Integer... values) {

        progressBar.setProgress(values[0]);

    }

  // begin the installation by opening the resulting file

    @Override
    protected void onPostExecute(String path) {

        progressBar.dismiss();
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive" );
        Log.d("Lofting", "About to install new .apk");
        this.context.startActivity(i);

    }*/


}


