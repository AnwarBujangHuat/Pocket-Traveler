package com.example.mytravelbuddy.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytravelbuddy.MainActivity;
import com.example.mytravelbuddy.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class camera_translate extends AppCompatActivity {

    LensEngine mLensEngine;
    MLTextAnalyzer analyzer;
    private int lensType = LensEngine.BACK_LENS;
    private LensEnginePreview mPreview;
    private TextView tv,tvFrom,tvTo,tvcamerastatus;
    int checkedItem=0;
    ImageButton pause;
    String LanguageSelectedFrom="ko";
    String[]listItems = {"Korean","English", "Traditional Chinese", "Japanese", "Malay", "Tamil","German","Spanish","Indonesian","Russian","Thai","Vietnamese"};
    String[]languageselected = {"ko","en", "zh", "ja", "ms", "ta","de","es","id","ru","th","vi"};
    public String Status="pause";
    List<String> list = new ArrayList<String>();
    Button btnlanguagefrom;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        mPreview = findViewById(R.id.lensengine_preview);
        tvFrom=findViewById(R.id.tvFrom);
        tvTo=findViewById(R.id.tvTo);
        tvcamerastatus=findViewById(R.id.tvcamerastatus);
        pause=findViewById(R.id.pause_btn);

        //Initialization
        analyzer = new MLTextAnalyzer.Factory(camera_translate.this).setLocalOCRMode(MLLocalTextSetting.OCR_DETECT_MODE).setLanguage(LanguageSelectedFrom).create();
        analyzer.setTransactor(new OcrDetectorProcessor());
        createLensEngine();
        startLensEngine(LanguageSelectedFrom);


        btnlanguagefrom=findViewById(R.id.btnlanguagefrom);
        btnlanguagefrom.setText(listItems[0]);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (Status){
                    case "pause":
                        onPause();
                        pause.setImageResource(R.drawable.ic_baseline_pause_circle_24);
                        Toast.makeText(camera_translate.this, "The Preview Is Pause ", Toast.LENGTH_SHORT).show();
                        tvcamerastatus.setText("The Camera Is Pause");
                        Status="play";

                        break;
                    case "play":
                        startLensEngine(LanguageSelectedFrom);
                        pause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        Toast.makeText(camera_translate.this, "The Preview Is Play ", Toast.LENGTH_SHORT).show();
                        tvcamerastatus.setText("The Camera Is Play");
                        Status="pause";

                        break;

            }}
        });

        btnlanguagefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(camera_translate.this);
                builder.setTitle("Pick a Language");

                //this will checked the item when user open the dialog
                builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(root.getContext(), "Position: " + which + " Value: " + listItems[which], Toast.LENGTH_LONG).show();
                        LanguageSelectedFrom=languageselected[which];
                        startLensEngine(LanguageSelectedFrom);
                        checkedItem = which;
                        btnlanguagefrom.setText(listItems[which]);


                    }
                });

                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void createLensEngine() {
        Context context = this.getApplicationContext();
        mLensEngine = new LensEngine.Creator(context, this.analyzer)
                .setLensType(this.lensType)
                .applyDisplayDimension(1600, 1024)
                .applyFps(60.0f)
                .enableAutomaticFocus(true)
                .create();
    }

    private void startLensEngine(String LanguageSelectedFroms) {
        //Translation Kit
        // Method 2: Use the customized parameter MLLocalTextSetting to configure the text analyzer on the device.
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                // Specify languages that can be recognized.
                .setLanguage(LanguageSelectedFroms)
                .create();
        Toast.makeText(camera_translate.this, "Language From: " + LanguageSelectedFrom, Toast.LENGTH_SHORT).show();
        Toast.makeText(camera_translate.this, "Language : " + LanguageSelectedFroms, Toast.LENGTH_SHORT).show();
        analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);
        if (this.mLensEngine != null) {
            try {
                this.mPreview.start(this.mLensEngine);
            } catch (IOException e) {
                this.mLensEngine.release();
                this.mLensEngine = null;
            }
        }
    }


    public class OcrDetectorProcessor implements MLAnalyzer.MLTransactor<MLText.Block> {

        @Override
        public void transactResult(MLAnalyzer.Result<MLText.Block> results) {

            SparseArray<MLText.Block> blocks = results.getAnalyseList();
            String result="";
                for (int i = 0; i < blocks.size(); i++) {
                    List<MLText.TextLine> lines = blocks.get(i).getContents();
                    for (int j = 0; j < lines.size(); j++) {
                        List<MLText.Word> elements = lines.get(j).getContents();
                        for (int k = 0; k < elements.size(); k++) {
                                    result += elements.get(k).getStringValue() + " ";
                        }

                    }
                    result += " ";

                }
            translateFunction(result);
            tvFrom.setText(result);
        }

        @Override
        public void destroy() {

        }

    }
    //End Speech Output
    private void translateFunction(String inputtext) {
        MLApplication.getInstance().setApiKey(getResources().getString(R.string.api_key));

        // Create a text translator using custom parameter settings.
        MLRemoteTranslateSetting setting = new MLRemoteTranslateSetting
                .Factory()
                // Set the source language code. The BCP-47 standard is used for Traditional Chinese, and the ISO 639-1 standard is used for other languages. This parameter is optional. If this parameter is not set, the system automatically detects the language.
                .setSourceLangCode(LanguageSelectedFrom)
                // Set the target language code. The BCP-47 standard is used for Traditional Chinese, and the ISO 639-1 standard is used for other languages.
//                .setTargetLangCode(languageselected.toString())
                .setTargetLangCode("en")
                .create();
//        tvFrom.setText(inputtext);
        MLRemoteTranslator mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
        // sourceText: text to be translated, with up to 5000 characters.
        final Task<String> task = mlRemoteTranslator.asyncTranslate(inputtext);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                tvTo.setText(text);
                Log.d(inputtext+"translated: ",text);
                // Processing logic for recognition success.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Processing logic for recognition failure.
                try {
                    MLException mlException = (MLException)e;
                    // Obtain the result code. You can process the result code and customize respective messages displayed to users.
                    int errorCode = mlException.getErrCode();
                    // Obtain the error information. You can quickly locate the fault based on the result code.
                    String errorMessage = mlException.getMessage();
                } catch (Exception error) {
                    if (mlRemoteTranslator!= null) {
                        mlRemoteTranslator.stop();
                    }
                    // Handle the conversion error.
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        this.mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mLensEngine != null) {
            this.mLensEngine.release();
        }
        if (this.analyzer != null) {
            try {
                this.analyzer.stop();
            } catch (IOException e) {
            }
        }
    }
}
