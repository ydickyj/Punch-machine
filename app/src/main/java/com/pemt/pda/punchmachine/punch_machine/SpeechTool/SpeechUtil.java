package com.pemt.pda.punchmachine.punch_machine.SpeechTool;

/**
 * Created by eng005 on 2016/11/7.
 */

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 语音合成工具类
 *
 * @author JPH
 * @date 2015-4-14 下午1:33:37
 */
public class SpeechUtil implements SpeechSynthesizerListener {
    private static Logger logger = LoggerFactory.getLogger(SpeechUtil.class);

    protected static final int UI_LOG_TO_VIEW = 0;
    private SpeechSynthesizer speechSynthesizer;
    private Context context;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";

    public SpeechUtil(Context activity) {
        this.context = activity;
        init();
    }
    /**
     * 初始化合成相关组件
     *
     * @author JPH
     * @date 2015-4-14 下午1:36:53
     */
    private void init() {
        // 获取语音合成对象实例
       speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(context);
        // 此处需要将setApiKey方法的两个参数替换为你在百度开发者中心注册应用所得到的apiKey和secretKey
        speechSynthesizer.setApiKey("fZHjo18UBGZIcFp9zAzK86F5",
                "e447dd54e7cf28109d0935907b2457f5");
        speechSynthesizer.setAppId("8861715");
        speechSynthesizer.setSpeechSynthesizerListener(this);
        speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//      activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initialEnv();
        setParams();
    }

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = "/sdcard/PDA/";
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 开始文本合成并朗读
     * @author JPH
     * @date 2015-4-14 下午1:47:05
     * @param content
     */
    public void speak(final String content) {
        // 设置Mix模式的合成策略
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 获取语音合成授权信息
        AuthInfo authInfo =  speechSynthesizer.auth(TtsMode.MIX);
        // 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
        speechSynthesizer.initTts(TtsMode.MIX);
        if (authInfo.isSuccess()) {
            speechSynthesizer.speak(content);
            logger .error("已播报");
        } else {
            logger .error("授权失败"); // 授权失败
        }
    }

    /**
     * 暂停文本朗读，如果没有调用speak(String)方法或者合成器初始化失败，该方法将无任何效果
     * @author JPH
     * @date 2015-4-14 下午2:21:07
     */
    public void pause() {
        speechSynthesizer.pause();
    }
    /**
     * 继续文本朗读，如果没有调用speak(String)方法或者合成器初始化失败，该方法将无任何效果
     * @author JPH
     * @date 2015-4-14 下午2:21:29
     */
    public void resume() {
        speechSynthesizer.resume();
    }
    /**
     * 为语音合成器设置相关参数
     * @author JPH
     * @date 2015-4-14 下午1:45:11
     */
    private void setParams() {
        speechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了正式离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        // 如果合成结果出现临时授权文件将要到期的提示，说明使用了临时授权文件，请删除临时授权即可。
       speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
                + LICENSE_FILE_NAME);

        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");//发音人，目前支持女声(0)和男声(1)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");//音量，取值范围[0, 9]，数值越大，音量越大
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");//朗读语速，取值范围[0, 9]，数值越大，语速越快
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");//音调，取值范围[0, 9]，数值越大，音量越高
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE,
                SpeechSynthesizer.AUDIO_ENCODE_AMR);//音频格式，支持bv/amr/opus/mp3，取值详见随后常量声明
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE,
                SpeechSynthesizer.AUDIO_BITRATE_AMR_15K85);//音频比特率，各音频格式支持的比特率详见随后常量声明

    }


    @Override
    public void onSynthesizeStart(String s) {
        Log.i("msg", "朗读开始");
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        // 监听到有合成数据到达，在此添加相关操作
    }

    @Override
    public void onSynthesizeFinish(String s) {

    }

    @Override
    public void onSpeechStart(String s) {

    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {

    }

    @Override
    public void onSpeechFinish(String s) {

    }

    @Override
    public void onError(String s, SpeechError speechError) {
        Log.i("msg", "发生错误：" + s + "(" + speechError
                + ")");
    }
}