package com.example.mytravelbuddy.ui.home;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TTS {
    private static TextToSpeech textToSpeech;
    private static boolean chkSpeak = false;
    String Language1;
    public static void init (final Context context) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech (context, new TextToSpeech.OnInitListener () {

                @Override
                public void onInit (int i) {
                    chkSpeak = true;
                }
            });
        }
    }
    public static void speak (final String text, final String utteranceld,String Language) {
        Set<String> a=new HashSet<>();
        //All the setting use here is Default
        String name="en-us-x-sfg#female_2-local";
        String language="en";
        String country="US";
        Voice v=new Voice(name,new Locale("en","US"),400,200,true,a);
        switch (Language){
            case "en":
                 name="en-us-x-sfg#female_2-local";
                 language="en";
                 country="US";
                break;


            case "ms": case "id":
                //setup new Language
                name="id-id-x-dfz#female_1-local";
                language="in";
                country="ID";
                break;


            case "zh":
                //setup new Language
                name="zh-CN-language";
                language="zh";
                country="CN";

                break;

            case "ja":
                //setup new Language
                name="ja-jp-x-htm#female_3-local";
                language="ja";
                country="jp";
                break;

            case "ko":
                //setup new Language
                name="ko-kr-x-ism#female_2-local";
                language="ko";
                country="KR";
                break;

            case "ta":
                //setup new Language
                name="hi-in-x-cfn#female_2-local";
                language="hi";
                country="IN";
                break;

            case "de":
                //setup new Language
                name="de-de-x-nfh#male_2-local";
                language="de";
                country="DE";

                break;

            case "es":
                //setup new Language
                name="es-US-language";
                language="es";
                country="US";
                break;



            case "ru":
                //setup new Language
                name="ru-ru-x-dfc#female_1-local";
                language="ru";
                country="RU";

                break;

            case "th":
                //setup new Language
                name="th-th-x-mol#male_3-local";
                language="th";
                country="TH";

                break;


            case "vi":
                //setup new Language
                name="vi-VN-language";
                language="vi";
                country="VN";
                break;





        }
        v=new Voice(name,new Locale(language,country),400,200,true,a);
        textToSpeech.setVoice(v);
        textToSpeech.speak (text, TextToSpeech.QUEUE_FLUSH, null, utteranceld);


    }
    public static boolean isSpeaking () {
        return textToSpeech.isSpeaking ();
    }
    public static void killSpeaking () {
        textToSpeech.stop ();
    }
    public static void setOnUtteranceProgressListener (UtteranceProgressListener l) {
        textToSpeech.setOnUtteranceProgressListener (l);
    }
}