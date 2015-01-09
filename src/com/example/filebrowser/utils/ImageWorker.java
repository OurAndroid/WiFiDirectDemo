package com.example.filebrowser.utils;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageWorker {
	
	private static final String Tag = "ImageWorker";
	
	private Bitmap mLoadingBitmap;
	
	private final int width = 80,height = 80;
	
	protected Resources mResources;
	
	//�µ��̳߳�
	private static final ThreadFactory  sThreadFactory = new ThreadFactory() {
	        private final AtomicInteger mCount = new AtomicInteger(1);

	        public Thread newThread(Runnable r) {
	            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
	        }
	    };
	    
	public final Executor DUAL_THREAD_EXECUTOR =
            Executors.newFixedThreadPool(2, sThreadFactory);
	
	public ImageWorker(Context context){
		mResources = context.getResources();
	}
	
	public void setLoadingImage(Bitmap bitmap){
		mLoadingBitmap = bitmap;
	}
	
	public void setLoadingImage(int resId){
		mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
	}
	
	public void loadImage(Object data, ImageView imageView){
		if(data == null){
			return;
		}
		
		//BitmapDrawable value = null;
		//��������뻺��
		if(cancelPotentialWork(data, imageView)){//�������´���task�����󶨵�imageView
			final BitmapWorkerTask task = new BitmapWorkerTask(data, imageView);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);
			
			imageView.setImageDrawable(asyncDrawable);
			
			task.executeOnExecutor(this.DUAL_THREAD_EXECUTOR);
			//task.execute();     //������ڲ�ִ��˳��
		}
	}
	
	private Bitmap processBitmap(String filePath){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		System.out.println(filePath);
		
		
		int be = 0;
		int height = options.outHeight;
		int width = options.outWidth;
		
		if(height!=0&&width!=0){
			
			be =  (int) (height/(float)80+width/(float)80)/2;
			 
		}
		options.inJustDecodeBounds = false;
		
		//be = (int) ((int)(height>width ? height:width)/(float)50);
		if(be<=0)
			be = 1;
		options.inSampleSize = be;
		
		bitmap = BitmapFactory.decodeFile(filePath, options);
	
		return bitmap;
	}
	
	//�жϵ�ǰimageView��ִ�е������Ƿ�ΪĿ������������ǣ������������true
	public static boolean cancelPotentialWork(Object data, ImageView imageView){
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		
		if(bitmapWorkerTask!=null){
			final Object bitmapData = bitmapWorkerTask.mData;
			
			if(bitmapData ==null||!bitmapData.equals(data)){
				bitmapWorkerTask.cancel(true);//�����cancel�ڶԶ�ȡͼƬ��IO����ʱ����һ����ȡ��
				Log.i("fl","cancelPotentialWorkǰ�󲻵�");
			}else{
				Log.i("fl","����ǰ�����");
				return false;
			}
			
		}return true;
	}
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView){
		if(imageView!=null){
			final Drawable drawable = imageView.getDrawable();
			if(drawable instanceof AsyncDrawable){
				AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}
	
	
	private class BitmapWorkerTask extends AsyncTask<Void, Void, BitmapDrawable>{
		
		private Object mData;
		private final WeakReference<ImageView> imageViewReference;
		
		public BitmapWorkerTask(Object data, ImageView imageView){
			mData = data;
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected BitmapDrawable doInBackground(Void... params) {
			Bitmap bitmap;
			BitmapDrawable drawable =null;
			
			String filePath = String.valueOf(mData);
			bitmap = processBitmap(filePath);
			if(bitmap!=null){
				drawable = new BitmapDrawable(mResources, bitmap);
			}
			
			
			return drawable;
		}

		@Override
		protected void onPostExecute(BitmapDrawable value) {
			// TODO Auto-generated method stub
			
			final ImageView imageView = getAttachedImageView();  //������ִ��һ�μ�⣬����ý����ѹ��ڵ�δ��ȡ���ɹ�������ʾͼƬ
			if(value!=null&&imageView!=null){
			
				imageView.setImageDrawable(value);
			}
		}
		
		
		private ImageView getAttachedImageView(){
			final ImageView imageView = imageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
			
			
			Log.i("fl", bitmapWorkerTask==this?"1":"0");
			if(this == bitmapWorkerTask){
				return imageView;
			
			}
			
			return null;
		}
		
		
	}
	
	private static class AsyncDrawable extends BitmapDrawable{
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
		
		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}
		
		public BitmapWorkerTask getBitmapWorkerTask(){
			return bitmapWorkerTaskReference.get();
		}
	}

}
