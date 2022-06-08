package com.example.acceliststoreforoldandroid;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.Context;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.acceliststoreforoldandroid.databinding.FragmentFirstBinding;

import java.io.File;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private static String file_url = "http://94.237.76.37:10000/dev/isuzu-id-mobile/my-app-346817d131724f81b546cad6586a1d01-signed.apk?sv=2020-10-02&st=2022-04-06T13%3A36%3A11Z&se=2022-04-07T13%3A36%3A11Z&sr=b&sp=r&sig=n9iC8p8cI91V23HNrZNMJsdmlM9xK5TL6YAZtkj7RtU%3D";
    private boolean isInstalled;

    DownloadManager mManager;
    String name;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getContext().getPackageManager().queryIntentActivities( mainIntent, 0);
        for (int a = 0;a<pkgAppsList.size();a++){
            ResolveInfo pkgApps = pkgAppsList.get(49);
            if( pkgApps.activityInfo.packageName.contains("Expo.App")){
                isInstalled = true;
            }
        }

/*        Button buttonDownload = (Button) getView().findViewById(R.id.button_download);
        if(isInstalled){
            buttonDownload.setVisibility(View.GONE);
        }*/
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getContext().getPackageManager().getLaunchIntentForPackage("Expo.App");
                //startActivity(i);
                //down();
            }
        });

        binding.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getContext(),"Toast!!",Toast.LENGTH_SHORT);
                toast.show();

                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = "AppName.apk";
                destination += fileName;
                final Uri uri = Uri.parse("file::///storage/emulated/0/Android/data/com.example.acceliststoreforoldandroid/files/Download/my-app-346817d131724f81b546cad6586a1d01-signed.apk");

                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(file_url));

                request.setTitle("Downloading...");  //set title for notification in status_bar
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  //flag for if you want to show notification in status or not
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                //String nameOfFile = "YourFileName.pdf";    //if you want to give file_name manually

                String nameOfFile = URLUtil.guessFileName(file_url, null, MimeTypeMap.getFileExtensionFromUrl(file_url)); //fetching name of file and type from server
                //String nameOfFile = "Nako.jpg";
                File f = new File(Environment.getExternalStorageDirectory() + "/" + "AccelistPlaystore");       // location, where to download file in external directory
                if (!f.exists()) {
                    f.mkdirs();
                }
                request.setDestinationInExternalFilesDir(getContext(),Environment.DIRECTORY_DOWNLOADS,nameOfFile);

                    final long downloadId = downloadManager.enqueue(request);

                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        install.setDataAndType(uri,
                                downloadManager.getMimeTypeForDownloadedFile(downloadId));
                        startActivity(install);

                        getContext().unregisterReceiver(this);
                        getActivity().finish();
                    }
                };
                //register receiver for when .apk download is compete
                getContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });
    }

    public void down() {
        Toast.makeText(getContext(),"IM HEREEE",Toast.LENGTH_LONG);
        mManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse("http://94.237.76.37:10000/dev/isuzu-id-mobile/20220301-INV-001592.pdf?sv=2020-10-02&st=2022-04-06T12%3A54%3A37Z&se=2022-04-07T12%3A54%3A37Z&sr=b&sp=r&sig=BO5v81NGO3XM35SHAHoDfSHoCTlsVQm63WFMTAbI9cI%3D");
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri)
                .setAllowedOverRoaming(false)
                .setTitle("Downloading")
                .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, name + "CV.pdf")
                .setDescription("Download in progress").setMimeType("pdf");
        mManager.enqueue(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getContext().registerReceiver(broadcast, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void showPdf() {
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/Download/" + name + "CV.pdf");//name here is the name of any string you want to pass to the method
            if (!file.isDirectory())
                file.mkdir();
            Intent testIntent = new Intent("com.adobe.reader");
            testIntent.setType("application/pdf");
            testIntent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            testIntent.setDataAndType(uri, "application/pdf");
            startActivity(testIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showPdf();
        }
    };
}