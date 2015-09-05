/*
 * Copyright (c) 2014 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quicklic.quicklic.screenrecorder;

import java.io.File;
import java.io.IOException;

import quicklic.quicklic.util.BaseQuicklic;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	private static final String LUNCH_COUNT = "lunch_count";
	private int lunchCount;
	private SharedPreferences sharedPreferences;
	
	private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    MediaProjection mediaProjection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
      //setContentView(R.layout.activity_main);
      mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
               
      Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
      
      
          startActivityForResult(captureIntent, REQUEST_CODE);
            
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e("@@", "media projection is null");
            return;
        }
        // video size
        final int width = getWindowManager().getDefaultDisplay().getWidth(); //1280;
        final int height = getWindowManager().getDefaultDisplay().getHeight();// 720;
        File file = new File(Environment.getExternalStorageDirectory(),
                "record-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");
        final int bitrate = 6000000;
        mRecorder = new ScreenRecorder(width, height, bitrate, 1, mediaProjection, file.getAbsolutePath());        
        mRecorder.start();
               
        Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
        BaseQuicklic.recorderFlag = false;
        moveTaskToBack(true);
    }
  
   
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRecorder != null){
            ScreenRecorder.mQuit.set(true);
            mRecorder = null;
        }
    }    
   
}
