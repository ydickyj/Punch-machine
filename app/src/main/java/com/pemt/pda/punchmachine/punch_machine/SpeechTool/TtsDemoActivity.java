/**
 * Copyright (C) 2015  塘上科技,tangsci.cn
 */
package com.pemt.pda.punchmachine.punch_machine.SpeechTool;


import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.View.OnClickListener;

import com.pemt.pda.punchmachine.punch_machine.R;
import com.pemt.pda.punchmachine.punch_machine.SpeechTool.TtsEngine;
import com.pemt.pda.punchmachine.punch_machine.SpeechTool.TtsPlayer;

import java.io.IOException;
import java.io.InputStream;

public class TtsDemoActivity extends Activity
{
    private Button m_btPlay;
    private Button m_btPause;
    private Button m_btStop;
    private Button m_btMaleVoice;
    private Button m_btFemaleVoice;
    private Button m_btClearText;
    private EditText m_etInput;
    private SeekBar m_sbVolume;
    private SeekBar m_sbSpeed;
    private SeekBar m_sbPitch;
    private SeekBar m_sbFreqScale;
    private TextView m_tvVolume;
    private TextView m_tvSpeed;
    private TextView m_tvPitch;
    private TextView m_tvFreqScale;

    String m_playState = "not_ready";

	private Handler m_handler = new Handler()
    {
    	 @Override
         public void handleMessage(Message msg) 
         {
 	        ///合成线程会在播放完当前文本段后，会发一个播放完成的消息，这个函数负责接收处理
 	        super.handleMessage(msg);
 	        Bundle b = msg.getData();
 	        String playState = b.getString("play_state");
 	        if (playState == "idle")
 	        {
 	        	setState("idle");
 	        }
         }
    };

    private TtsPlayer m_ttsPlayer = new TtsPlayer(m_handler);

    private boolean initTtsPlay()
    {
        byte[] ttsResBytes;
        InputStream ttsResStream = getResources().openRawResource(R.raw.ttsres);
        try {
            ttsResBytes = new byte[ttsResStream.available()];
            ttsResStream.read(ttsResBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return m_ttsPlayer.initEngine(ttsResBytes);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        m_playState = "not_ready";
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        m_btPlay = (Button) findViewById(R.id.Play);
//        m_btPlay.setOnClickListener(onClickPlay);
//        m_btPause = (Button) findViewById(R.id.Pause);
//        m_btPause.setOnClickListener(onClickPause);
//        m_btStop = (Button) findViewById(R.id.Stop);
//        m_btStop.setOnClickListener(onClickStop);
//        m_btMaleVoice = (Button) findViewById(R.id.MaleVoice);
//        m_btMaleVoice.setOnClickListener(onClickMaleVoice);
//        m_btFemaleVoice = (Button) findViewById(R.id.FemaleVoice);
//        m_btFemaleVoice.setOnClickListener(onClickFemaleVoice);
//        m_btClearText = (Button) findViewById(R.id.ClearText);
//        m_btClearText.setOnClickListener(onClickClearText);
//        setState("not_ready");
//
//        m_etInput = (EditText) findViewById(R.id.InputText);
//
//
//        m_sbVolume = (SeekBar) findViewById(R.id.SeekVolume);
//        m_sbVolume.setOnSeekBarChangeListener(onSeekBarVolume);
//        m_sbSpeed = (SeekBar) findViewById(R.id.SeekSpeed);
//        m_sbSpeed.setOnSeekBarChangeListener(onSeekBarSpeed);
//        m_sbPitch = (SeekBar) findViewById(R.id.SeekPitch);
//        m_sbPitch.setOnSeekBarChangeListener(onSeekBarPitch);
//        m_sbFreqScale = (SeekBar) findViewById(R.id.SeekFreqScale);
//        m_sbFreqScale.setOnSeekBarChangeListener(onSeekBarFreqScale);
//
//        m_tvVolume = (TextView) findViewById(R.id.Volume);
//        m_tvSpeed = (TextView) findViewById(R.id.Speed);
//        m_tvPitch = (TextView) findViewById(R.id.Pitch);
//        m_tvFreqScale = (TextView) findViewById(R.id.Freqscale);
        
        initTtsPlay();
        ///该license code将于2018年1月1日到期
        m_ttsPlayer.setGlobalParam("LicenseCode", "GH4V980IOG37H0ADU6IN7HO3");
        ///licenseType非"0"说明授权成功
		@SuppressWarnings("unused")
		String licenseType = m_ttsPlayer.getGlobalParam("LicenseType");
		
        
		m_ttsPlayer.setParam("Encoding", TtsEngine.ENCODING_UTF8);///输入文本是"utf8"编码
		
        setState("idle");
        
        
        ///set volume = 0，音量
        m_sbVolume.setProgress((int) (Math.round((0.0f + 1.0f) * m_sbVolume.getMax() / 2.0f)));
        ///set speed = 0，语速
        m_sbSpeed.setProgress((int) (Math.round((0.15f + 1.0f) * m_sbSpeed.getMax() / 2.0f)));
        ///set pitch = -0.45，音高
        m_sbPitch.setProgress((int) (Math.round((-0.45f + 1.0f) * m_sbPitch.getMax() / 2.0f)));
        ///set freqscale = -0.5,变声
        m_sbFreqScale.setProgress((int) (Math.round((-0.5f + 1.0f) * m_sbFreqScale.getMax() / 2.0f)));
        
        m_etInput.setText("如果与任人宰割的时代相比，中国确实强大了。改革开放二十五年来，" +
                "经济增长了七点五倍。过去中国一条高速公路都没有，现在中国有二三万公里的高速公路，" +
                "位居世界第二。中国每月增加电话机五百万门，目前已达到五亿门，其中手提电话和固定电话各占一半。" +
                "今年十月份，中国又成功地把载人航天飞船送上了天。这是大家看到的祖国一天天在变化，" +
                "但由于长期落后，底子薄，人口多，中国面临的建设任务还十分艰巨。");
    }

    @Override
    public void onDestroy()
    {
        if (null != m_ttsPlayer)
        {
            m_ttsPlayer.uninitEngine();
        }
        super.onDestroy();
    }

    private void setState(String strState)
    {
        m_playState = strState;
        if (m_playState.equals("idle"))
        {
            m_btPlay.setEnabled(true);
            m_btPause.setEnabled(false);
            m_btStop.setEnabled(false);
        }
        else if (m_playState.equals("play"))
        {
            m_btPlay.setEnabled(false);
            m_btPause.setEnabled(true);
            m_btStop.setEnabled(true);
        }
        else if (m_playState.equals("pause"))
        {
            m_btPlay.setEnabled(true);
            m_btPause.setEnabled(false);
            m_btStop.setEnabled(true);
        }
        else if (m_playState.equals("not_ready"))
        {
            m_btPlay.setEnabled(false);
            m_btPause.setEnabled(false);
            m_btStop.setEnabled(false);
        }
    }

    private String getState()
    {
        return m_playState;
    }

    
    
    private OnSeekBarChangeListener onSeekBarVolume = new OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            int barLen = seekBar.getMax();
            Float value = (progress - barLen / 2.0f) / barLen * 2.0f;//range(-1.0,1.0)
            m_ttsPlayer.setParam("V", value.toString());//set volume
            m_tvVolume.setText(value.toString());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {

        }
    };
    private OnSeekBarChangeListener onSeekBarSpeed = new OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            int barLen = seekBar.getMax();
            Float value = (progress - barLen / 2.0f) / barLen * 2.0f;//range(-1.0,1.0)
            m_ttsPlayer.setParam("S", value.toString());//set speed
            m_tvSpeed.setText(value.toString());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private OnSeekBarChangeListener onSeekBarPitch = new OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            int barLen = seekBar.getMax();
            Float value = (progress - barLen / 2.0f) / barLen * 2.0f;//range(-1.0,1.0)
            m_ttsPlayer.setParam("P", value.toString());//set pitch
            m_tvPitch.setText(value.toString());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {

        }
    };
    private OnSeekBarChangeListener onSeekBarFreqScale = new OnSeekBarChangeListener()
    {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) 
		{
			// TODO Auto-generated method stub
			int barLen = seekBar.getMax();
            Float value = (progress - barLen / 2.0f) / barLen * 2.0f;//range(-1.0,1.0)
            m_ttsPlayer.setParam("F", value.toString());//set freqscale
            m_tvFreqScale.setText(value.toString());
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    private OnClickListener onClickPlay = new OnClickListener()
    {
        @SuppressWarnings("unused")
		boolean rt;
        @Override
        public void onClick(View v)
        {
            String strCurState = getState();
            if (strCurState.equals("play") || strCurState.equals("not_ready"))
            {
                return;
            }
            if (strCurState.equals("idle"))
            {
                String inputText = m_etInput.getText().toString();
                rt = m_ttsPlayer.playText(inputText);
            }
            else if (strCurState.equals("pause"))
            {
                m_ttsPlayer.play();
            }
            setState("play");
        }
    };
    private OnClickListener onClickPause = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            m_ttsPlayer.pause();
            setState("pause");
        }
    };
    private OnClickListener onClickStop = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            m_ttsPlayer.stop();
            //setState("stop");//当当前合成任务停止或者终止时，handleMessage会接收到消息
        }
    };
    private OnClickListener onClickMaleVoice = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //male voice:pitch=-0.45,freqscale=-0.5
            m_sbPitch.setProgress((int) (Math.round((-0.45f + 1.0f) * m_sbPitch.getMax() / 2.0f)));
            m_sbFreqScale.setProgress((int) (Math.round((-0.5f + 1.0f) * m_sbFreqScale.getMax() / 2.0f)));
        }
    };
    private OnClickListener onClickFemaleVoice = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //female voice:pitch=0.25,freqscale=0
            m_sbPitch.setProgress((int) (Math.round((0.25f + 1.0f) * m_sbPitch.getMax() / 2.0f)));
            m_sbFreqScale.setProgress((int) (Math.round((0.0f + 1.0f) * m_sbFreqScale.getMax() / 2.0f)));
        }
    };
    private OnClickListener onClickClearText = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            m_etInput.setText("");
        }
    };
}
