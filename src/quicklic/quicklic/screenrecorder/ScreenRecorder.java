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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;



/**
 * @author Yrom
 */
public class ScreenRecorder extends Thread {
		
	private static final String aMIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;	// 44.1[KHz] is only setting guaranteed to be available on all devices.
    private static final int BIT_RATE = 64000;    
    
    private AudioThread mAudioThread = null;    
	
    protected final Object mSync = new Object();
	
	private static final String TAG = "ScreenRecorder";
    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private String mDstPath;
    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 163; // 30 fps
    private static final int IFRAME_INTERVAL = 5; // 10 seconds between I-frames
    private static final long TIMEOUT_US = 10000;
    
    private MediaCodec mEncoder;
    private MediaCodec aEncoder;
    public static Surface mSurface = null;
    public static MediaMuxer mMuxer;
    public static boolean mMuxerStarted = false;
    public static boolean mIsCapturing = true;
    public static boolean swapbuffer ;
    private int mVideoTrackIndex = -1;
    private int aVideoTrackIndex = -1;
    public static AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private MediaCodec.BufferInfo aBufferInfo = new MediaCodec.BufferInfo();
    private VirtualDisplay mVirtualDisplay;

    public ScreenRecorder(int width, int height, int bitrate, int dpi, MediaProjection mp, String dstPath) {
        super(TAG);
        mWidth = width;
        mHeight = height;
        mBitRate = bitrate;
        mDpi = dpi;
        mMediaProjection = mp;
        mDstPath = dstPath;
        mQuit = new AtomicBoolean(false);
        mMuxerStarted = false;
    }


    public ScreenRecorder(MediaProjection mp) {
        // 480p 2Mbps
        this(640, 480, 2000000, 1, mp, "/sdcard/test.mp4");
    }

    /**
     * stop task
     */
    public final void quit() {
        mQuit.set(true);
    }

    @Override
    public void run() {
        try {
            try {            	
                prepareEncoder();
                mMuxer = new MediaMuxer(mDstPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                
                            
               
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                    mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);            
            
            recordVirtualDisplay();

        } finally {
            release();
        }
    }

    private void recordVirtualDisplay() {
        while (!mQuit.get()) {    
        	int index = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);  
        	     	 
        	
        	if ( index == -1 )
			{        		
        		      		
			}else if(index == -2){
				  resetOutputFormat();
			}else if(index >= 0 ){
				
				 encodeToVideoTrack(mEncoder,index, mVideoTrackIndex);
                 mEncoder.releaseOutputBuffer(index, false);
			}
        	/*	
        	if( index2 == -1){        		
        		
        		 if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                   
                     
                 } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                     try {
                         // wait 10ms
                         Thread.sleep(10);
                     } catch (InterruptedException e) {
                     }                     
                 
                 	
                 	
                 } else if (index >= 0) {              
                     encodeToVideoTrack(mEncoder,index, mVideoTrackIndex);
                     mEncoder.releaseOutputBuffer(index, false);
                 }
        	}*/
        }
    }

    private void encodeToVideoTrack(MediaCodec localEncoder , int index, int TrackIndex  ) {
    	MediaCodec localMediaCodec = localEncoder;
        ByteBuffer encodedData = localMediaCodec.getOutputBuffer(index);
        mBufferInfo.presentationTimeUs = getPTSUs();
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {          
            //Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0) {
           // Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
                    + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs
                    + ", offset=" + mBufferInfo.offset);
        }
        if (encodedData != null && mMuxerStarted) {
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
            mMuxer.writeSampleData(TrackIndex, encodedData, mBufferInfo);
            
        	prevOutputPTSUs = mBufferInfo.presentationTimeUs;
        	if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            	// when EOS come.
           		//mIsCapturing = false;
        		ScreenRecorder.mQuit.set(true);
               
            }
        }
    }

    private void encodeToAudioTrack(MediaCodec localEncoder , int index, int TrackIndex  ) {
    	MediaCodec localMediaCodec = localEncoder;
        ByteBuffer encodedData = localMediaCodec.getOutputBuffer(index);
       
        if ((aBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {          
            //Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            aBufferInfo.size = 0;
        }
        if (aBufferInfo.size == 0) {
           // Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "Audio got buffer, info: size=" + aBufferInfo.size
                    + ", presentationTimeUs=" + aBufferInfo.presentationTimeUs
                    + ", offset=" + aBufferInfo.offset);
        }
        aBufferInfo.presentationTimeUs = getPTSUs() -120000 ;
        if (encodedData != null && mMuxerStarted) {
            encodedData.position(aBufferInfo.offset);
            encodedData.limit(aBufferInfo.offset + aBufferInfo.size);
            mMuxer.writeSampleData(TrackIndex, encodedData, aBufferInfo);
            
        	//prevOutputPTSUs = aBufferInfo.presentationTimeUs;
        	if ((aBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            	// when EOS come.
           		//mIsCapturing = false;
        		ScreenRecorder.mQuit.set(true);
               
            }
        }
    }
    private void resetOutputFormat() {
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mEncoder.getOutputFormat();
        MediaFormat newFormat2 = aEncoder.getOutputFormat();
        //Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());       
        mVideoTrackIndex = mMuxer.addTrack(newFormat);   
        aVideoTrackIndex = mMuxer.addTrack(newFormat2);                
		mMuxer.start();		
		
		 if (mAudioThread == null) {
		        mAudioThread = new AudioThread();
				mAudioThread.start();
			}       
        mMuxerStarted = true;
      
    }

    private void prepareEncoder() throws IOException {

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);       
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        Log.d(TAG, "created video format: " + format);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
     
        mSurface = mEncoder.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder.start();   
       

        final MediaFormat audioFormat = MediaFormat.createAudioFormat(aMIME_TYPE, SAMPLE_RATE, 1);
		audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
		audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
		audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
		audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
	
        aEncoder = MediaCodec.createEncoderByType(aMIME_TYPE);
        aEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        aEncoder.start();        
        
    }    
    
    class AudioThread extends Thread {
    	@Override
    	public void run() {
            final int buf_sz = AudioRecord.getMinBufferSize(
            	SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) ;
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
            	SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buf_sz);
            try {            					
	                final byte[] buf = new byte[buf_sz];
	                int readBytes;
	                audioRecord.startRecording();
	                try {
			    		while (!mQuit.get()) {
			    			// read audio data from internal mic
			    			readBytes = audioRecord.read(buf, 0, buf_sz);
			    			if (readBytes > 0 && !mQuit.get() && mIsCapturing) {
			    			    // set audio data to encoder
			    				encode(buf, readBytes, getPTSUs());			    			
			    			}
			    			
			    		}	    			
	                } finally {
	                	audioRecord.stop();
	                }            	
            } finally {
            	audioRecord.release();
            }		
    	}
    }


    protected void encode(byte[] buffer, int length, long presentationTimeUs) {
      	int ix = 0, sz;
        final ByteBuffer[] inputBuffers = aEncoder.getInputBuffers();
        while (!mQuit.get() && ix < length) {
	        final int inputBufferIndex = aEncoder.dequeueInputBuffer(TIMEOUT_US);
	        if (inputBufferIndex >= 0) {
	            final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
	            inputBuffer.clear();
	            sz = inputBuffer.remaining();
	            sz = (ix + sz < length) ? sz : length - ix; 
	            if (sz > 0 && (buffer != null)) {
	            	inputBuffer.put(buffer, ix, sz);
	            }
	            ix += sz;
	            if (length <= 0) {	            	
	            	aEncoder.queueInputBuffer(inputBufferIndex, 0, 0,
	            		presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
		            break;
	            } else {
	            	aEncoder.queueInputBuffer(inputBufferIndex, 0, sz,
	            		presentationTimeUs, 0);
	            }
	        } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
	        	
	        }       
	        
	        int index2 = aEncoder.dequeueOutputBuffer(aBufferInfo, TIMEOUT_US);       	        		
	        if (index2 == MediaCodec.INFO_TRY_AGAIN_LATER) {
                                                      
                  
              }else if (index2 >= 0) {        
           	   
                  encodeToAudioTrack(aEncoder,index2, aVideoTrackIndex);
                  aEncoder.releaseOutputBuffer(index2, false);
              }  
	        
	        
        }
    }
    
    protected void signalEndOfInputStream() {	
        encode(null, 0, System.nanoTime() / 1000L);
	}
    
    private long prevOutputPTSUs = 0;
    
    protected long getPTSUs() {
  		long result = System.nanoTime() / 1000L;
  		if (result < prevOutputPTSUs)
  			result = (prevOutputPTSUs - result) + result;  		 		
         
         return result;
      }
    
    
    protected void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }    
        if (aEncoder != null) {
            aEncoder.stop();
            aEncoder.release();
            aEncoder = null;
        }
       
        if ( mAudioThread != null )
		{
        	 mAudioThread = null;
		}      
        
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }
    
     
}
