package com.example.mytravelbuddy.ui.home;

import static com.example.mytravelbuddy.R.mipmap.ic_cloudy;
import static com.example.mytravelbuddy.R.mipmap.ic_heavyrain;
import static com.example.mytravelbuddy.R.mipmap.ic_night;
import static com.example.mytravelbuddy.R.mipmap.ic_sun;
import static com.example.mytravelbuddy.R.mipmap.ic_thunder;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mytravelbuddy.R;
import com.example.mytravelbuddy.databinding.FragmentHomeBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.capture.WeatherStatusResponse;
import com.huawei.hms.kit.awareness.status.WeatherStatus;
import com.huawei.hms.kit.awareness.status.weather.Situation;
import com.huawei.hms.kit.awareness.status.weather.WeatherSituation;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Import the weather capture-related classes.

public class HomeFragment extends Fragment {


    private FragmentHomeBinding binding;
    TextView tv_location,tv_date,tv_temp,tv_windspeed,tv_weatherstatus,Weathertext,outputtext,outputtext2,tv_currentlanguage,tv_currentlanguage2,tv_translated,tv_translated2;
    private TextInputEditText inputtext1;
    ConstraintLayout weather_container,Appheader;
    private Button translate;
    Spinner spinner_languagefrom,spinner_languageto,spinner_languagephoto;
    ImageButton btn_weather,btn_copy,btn_copy2,btn_speech2,btn_speech;
    ImageView img_weather;
    public String language="en",languages2="en",languagesphoto="en",weatherclick="Closed";
    int weatherstatus=0,languagesFrom=0,languagesTo=0,languagesPhoto=0;
    public String texttoSpeech,texttoSpeech2,texttoSpeechLanguage,texttoSpeechLanguage2,texttoSpeechLanguagePhoto;
    List<String> list = new ArrayList<String>();
    String[]languagelist = {"en","ms", "zh", "ja", "ko", "ta","de","es","id","ru","th","vi"};
    String[]translatedtext={"Translate","Terjemah","翻譯","翻訳","번역하다","மொழிபெயர்","Übersetzen","Traducir","Menerjemahkan","Перевести","แปลภาษา","Phiên dịch"};
    String[]weatherInfoStr;
    SimpleDateFormat simpleDateFormat;
    int currentTime;
    private static final String TAG = HomeFragment.class.getSimpleName();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        weather_container=root.findViewById(R.id.weather_container);
        weather_container.setVisibility(View.GONE);
        tv_location=root.findViewById(R.id.tv_location);
        tv_date=root.findViewById(R.id.tv_date);
        tv_temp=root.findViewById(R.id.tv_temp);
        tv_windspeed=root.findViewById(R.id.tv_windspeed);
        tv_weatherstatus=root.findViewById(R.id.tv_weatherstatus);
        img_weather=root.findViewById(R.id.img_weather);
        tv_translated=root.findViewById(R.id.tv_translated);
        tv_translated2=root.findViewById(R.id.tv_translated2);
        btn_copy=root.findViewById(R.id.btn_copy);
        btn_copy2=root.findViewById(R.id.btn_copy2);
        getWeather(root.getContext());


        //Language Kit
        spinner_languagefrom=root.findViewById(R.id.spinner_languagefrom);
        spinner_languageto=root.findViewById(R.id.spinner_languageto);
        spinner_languagephoto=root.findViewById(R.id.spinner_languagephoto);

        //Populate List
        list.add("English");
        list.add("Malay");
        list.add("Traditional Chinese");
        list.add("Japanese");
        list.add("Korean");
        list.add("Tamil");
        list.add("German");
        list.add("Spanish");
        list.add("Indonesian");
        list.add("Russian");
        list.add("Thai");
        list.add("Vietnamese");

        //create an ArrayAdapter from the String Array
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(root.getContext(),
                android.R.layout.simple_spinner_item, list);
        //set the view for the Drop down list
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        spinner_languagefrom.setAdapter(dataAdapter);
        spinner_languagefrom.setPrompt("Language From");
        spinner_languagefrom.setOnItemSelectedListener(new HomeFragment.MyOnItemSelectedListener());


        spinner_languageto.setAdapter(dataAdapter);
        spinner_languageto.setPrompt("Language To");
        spinner_languageto.setOnItemSelectedListener(new HomeFragment.MyOnItemSelectedListener());

        //TranslationMethod
        inputtext1=root.findViewById(R.id.inputtext1);
        outputtext2=root.findViewById(R.id.outputtext2);
        outputtext=root.findViewById(R.id.outputtext);
        translate=root.findViewById(R.id.btn_translate);
        tv_currentlanguage=root.findViewById(R.id.tv_currentlanguage);
        tv_currentlanguage2=root.findViewById(R.id.tv_currentlanguage2);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateFunction(inputtext1.getText().toString());
                texttoSpeech=inputtext1.getText().toString();
                outputtext.setText(inputtext1.getText().toString());

            }
        });

         //TextToSpeach
        TTS.init (getContext ());
        btn_speech=root.findViewById(R.id.btn_speech);
        btn_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//

                // Set TextToSpeech reading end detection event listener
                TTS.setOnUtteranceProgressListener (new UtteranceProgressListener () {
                    @Override
                    public void onDone (String utteranceId) {
                        Toast.makeText(getContext(), "Speech", Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onError (String utteranceId) {
                        Toast.makeText(getContext(), "There's an Error", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onStart (String utteranceId) {}
                });

                TTS.speak (texttoSpeech, this.hashCode () + "",language);
                Toast.makeText(getContext(), language, Toast.LENGTH_SHORT).show();

            }
        });

        btn_speech2=root.findViewById(R.id.btn_speech2);
        btn_speech2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//

                // Set TextToSpeech reading end detection event listener
                TTS.setOnUtteranceProgressListener (new UtteranceProgressListener () {
                    @Override
                    public void onDone (String utteranceId) {
                        Toast.makeText(getContext(), "Speech", Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onError (String utteranceId) {
                        Toast.makeText(getContext(), "There's an Error", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onStart (String utteranceId) {}
                });

                TTS.speak (texttoSpeech2, this.hashCode () + "",languages2);
                Toast.makeText(getContext(), languages2, Toast.LENGTH_SHORT).show();


            }
        });

        //Copy to Clipboard
        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(texttoSpeech);
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        btn_copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(texttoSpeech2);
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


        //Setup Weather
        btn_weather=root.findViewById(R.id.btn_weather);
        Appheader=root.findViewById(R.id.Appheader);
        btn_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (weatherclick) {

                    case "Open":
                        weather_container.setVisibility(View.GONE);
                        Appheader.setBackgroundResource(R.color.purple_200);
                        weatherclick = "Closed";

                    break;

                    case "Closed":
                        weather_container.setVisibility(View.VISIBLE);
                        Appheader.setBackgroundResource(R.color.purple_200);
                        updateAppBar(weatherstatus,currentTime);
                        weatherclick = "Open";
                }
            }
        });


        return root;
    }
//IVPreviewImage

//
//        // create an instance of the
//        // intent of the type image
//        Intent i = new Intent();
//        i.setType("image/*");
//        i.setAction(Intent.ACTION_GET_CONTENT);
//
//        // pass the constant to compare it
//        // with the returned requestCode
//        startActivityForResult(Intent.createChooser(i, "Select Picture"),1);
//    }
////    // this function is triggered when user
////    // selects the image from the imageChooser
//    public void startActivityForResult (int requestCode, int resultCode, Intent data) {
//        super.startActivityForResult (requestCode, resultCode, data);
//
//        if (resultCode == 200) {
//            Toast.makeText(getContext(), "200", Toast.LENGTH_SHORT).show();
//            // compare the resultCode with the
//            // SELECT_PICTURE constant
//            if (requestCode == SELECT_PICTURE) {
//                Toast.makeText(getContext(), "Permission Selected", Toast.LENGTH_SHORT).show();
//                // Get the url of the image from data
//                Uri selectedImageUri = data.getData();
//                if (null != selectedImageUri) {
//                    // update the preview image in the layout
//                    IVPreviewImage.setImageURI(selectedImageUri);
//                    Toast.makeText(getContext(), "Image Selected", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        }


    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(@NonNull AdapterView<?> parent, View view, int pos, long id) {

            String selectedItem = parent.getItemAtPosition(pos).toString();

            //check which spinner triggered the listener
            switch (parent.getId()) {
                //Language From spinner
                case R.id.spinner_languagefrom:
                    //make sure the country was already selected during the onCreate
                    languagesFrom=list.indexOf(selectedItem);
                    language=languagelist[languagesFrom];
                    texttoSpeechLanguage=selectedItem;
                    tv_currentlanguage.setText(selectedItem);
                    tv_translated.setText(translatedtext[languagesFrom]);
//                    Toast.makeText(getContext(),"From"+ selectedItem +"\n"+language, Toast.LENGTH_SHORT).show();

                    break;

                case R.id.spinner_languageto:
                    languagesTo=list.indexOf(selectedItem);
                    languages2=languagelist[languagesTo];
                    texttoSpeechLanguage2=selectedItem;
                    tv_currentlanguage2.setText(selectedItem);
                    tv_translated2.setText(translatedtext[languagesTo]);
//                    Toast.makeText(getContext(), "To"+ selectedItem+"\n"+languages2, Toast.LENGTH_SHORT).show();

                    if(inputtext1!=null){
                        translateFunction(inputtext1.getText().toString());
                        texttoSpeech=inputtext1.getText().toString();
                        outputtext.setText(inputtext1.getText().toString());
                    }
                    break;


                case R.id.spinner_languagephoto:
                    languagesPhoto=list.indexOf(selectedItem);
                    languagesphoto=languagelist[languagesTo];
                    texttoSpeechLanguagePhoto=selectedItem;


                    break;
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(parent.getContext(), "Nothing" + language, Toast.LENGTH_SHORT).show();
        }

    }
    //End Speech Output
    private void translateFunction(final String inputtext) {
        MLApplication.getInstance().setApiKey(getResources().getString(R.string.api_key));
        // Create a text translator using custom parameter settings.
        MLRemoteTranslateSetting setting = new MLRemoteTranslateSetting
                .Factory()
                // Set the source language code. The BCP-47 standard is used for Traditional Chinese, and the ISO 639-1 standard is used for other languages. This parameter is optional. If this parameter is not set, the system automatically detects the language.
                .setSourceLangCode(language)
                // Set the target language code. The BCP-47 standard is used for Traditional Chinese, and the ISO 639-1 standard is used for other languages.
//                .setTargetLangCode(languageselected.toString())
                .setTargetLangCode(languages2)
                .create();

        MLRemoteTranslator mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
        // sourceText: text to be translated, with up to 5000 characters.
        final Task<String> task = mlRemoteTranslator.asyncTranslate(inputtext);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                outputtext2.setText(text);
                texttoSpeech2=text;
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

    private void getWeather(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Awareness.getCaptureClient(context).getWeatherByDevice()
                    // Callback listener for execution success.
                    .addOnSuccessListener(new OnSuccessListener<WeatherStatusResponse>() {
                        @Override
                        public void onSuccess(WeatherStatusResponse weatherStatusResponse) {
                            WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                            WeatherSituation weatherSituation = weatherStatus.getWeatherSituation();
                            Situation situation = weatherSituation.getSituation();

                            //Check Weather Status
                            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"+"\n"+"HH:mm:ss aaa z", Locale.getDefault());
                            tv_date.setText(simpleDateFormat.format(new Date()));
                            currentTime = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
                            weatherstatus=situation.getWeatherId();


                            weatherInfoStr= new String[]{weatherSituation.getCity().getName(),
                                    String.valueOf(situation.getTemperatureC()),
                                    String.valueOf(situation.getTemperatureF()),
                                    String.valueOf(situation.getWindSpeed()),
                                    situation.getHumidity(),
                                    String.valueOf(situation.getUpdateTime()),

                            };

                            //tv_location,tv_date,tv_temp,tv_windspeed,tv_humid
                            tv_location.setText(weatherInfoStr[0]);
                            tv_temp.setText(weatherInfoStr[1]+"℃ /"+weatherInfoStr[2]+"℉");
                            tv_windspeed.setText("WindSpeed: "+weatherInfoStr[3]+" km/h"+"\t"+" Humidity: "+weatherInfoStr[4]+"%");
//                          tv_date.setText(localTime);


                        }
                    })
                    // Callback listener for execution failure.
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getContext(), "get weather failed", Toast.LENGTH_LONG).show();


                        }
                    });
            return;
        }
        else{

            Weathertext.setText("Permission Not Granted");
            }

    }

    public void updateAppBar(int weatherstatus,int currentTimes){
           //Sunny
           if (weatherstatus >= 0 && weatherstatus <= 5) {
//               Appheader.setBackgroundResource(R.drawable.gradient_sunny);
//               tv_weatherstatus.setText("Sunny");
//               img_weather.setBackgroundResource(ic_sun);

                   if(currentTimes >= 6 && currentTimes <= 19){
                       Appheader.setBackgroundResource(R.drawable.gradient_sunny);
                       tv_weatherstatus.setText("Sunny");
                       img_weather.setBackgroundResource(ic_sun);
                       }

                   else {
                       Appheader.setBackgroundResource(R.drawable.gradient_mid);
                       tv_weatherstatus.setText("Night");
                       img_weather.setBackgroundResource(ic_night);
                   }

               }
           //Cloudy
           else if (weatherstatus >= 5 && weatherstatus <= 13) {
               Appheader.setBackgroundResource(R.drawable.gradient_cloudy);
               tv_weatherstatus.setText("Cloudy");
               img_weather.setBackgroundResource(ic_cloudy);
           }
           //Rain
           else if (weatherstatus >= 14 && weatherstatus <= 20) {
               Appheader.setBackgroundResource(R.drawable.gradient_rain);
               tv_weatherstatus.setText("Rain");
               img_weather.setBackgroundResource(ic_heavyrain);
           }
           //Others
           else if (weatherstatus >= 21) {
               Appheader.setBackgroundResource(R.drawable.gradient_thunderstorm);
               tv_weatherstatus.setText("Thunder Storm");
               img_weather.setBackgroundResource(ic_thunder);
           }
       }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

