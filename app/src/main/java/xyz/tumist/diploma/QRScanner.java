package xyz.tumist.diploma;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class QRScanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    final String LOG_TAG = PurchaseActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        final Activity activity = this;
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String scanResult = result.getText();
                        String FN = scanResult.substring(scanResult.indexOf("fn=") + 3, scanResult.indexOf("&i="));
                        String FD = scanResult.substring(scanResult.indexOf("i=") + 2, scanResult.indexOf("&fp="));
                        String FPD = scanResult.substring(scanResult.indexOf("&fp=") + 4, scanResult.indexOf("&n="));
                        Log.v("QRScanner", "FN=" + FN);
                        Log.v("QRScanner", "FD=" + FD);
                        Log.v("QRScanner", "FPD=" + FPD);
                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                        downloadJson DJ = new downloadJson(getApplicationContext(), FN, FD, FPD);
                        DJ.execute();
                        finish();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
