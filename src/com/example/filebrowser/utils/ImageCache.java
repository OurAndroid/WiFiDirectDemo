package com.example.filebrowser.utils;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.util.LruCache;

public class ImageCache {
	
	    //图片缓存
		private static LruCache<String,BitmapDrawable> mMemoryCache;
		//软引用作为二级缓存
		private static Set<SoftReference<Bitmap>> reusableSet = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
		
		
		public ImageCache(int cacheSize){
			mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize){

				@Override
				protected int sizeOf(String key, BitmapDrawable value) {
					// TODO Auto-generated method stub
					return value.getBitmap().getByteCount()/1024;
				}

				@Override
				protected void entryRemoved(boolean evicted, String key,
						BitmapDrawable oldValue, BitmapDrawable newValue) {
					// 将清除出的资源存入二级缓存
					Log.i("fl----entryRemove", "二级缓存大小"+reusableSet.size());
					reusableSet.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
				}
				
				
				
			};
		}
		
		
		public void addBitmapToMemCache(String key, BitmapDrawable value){
			if(key == null||value == null){
				return;
			}
			
			Log.i("fl---addBitmap", ""+mMemoryCache.size());
			mMemoryCache.put(key, value);
		}
		
		public BitmapDrawable getBitmapFromMemCache(String key){
			Log.i("fl---getBitmap", "获取缓存");
			return mMemoryCache.get(key);
		}
		
		public Bitmap getBitmapFromReuseableSet(BitmapFactory.Options options){
			
			Log.i("fl---getBitmapFromReusableSet", reusableSet.size()+"二级缓存大小");
			Bitmap bitmap = null;
			if(null !=reusableSet&&!reusableSet.isEmpty()){
				
				
				synchronized (reusableSet) {
					final Iterator<SoftReference<Bitmap>> iterator = reusableSet
							.iterator();
					Bitmap item;
					while(iterator.hasNext()){
						item = iterator.next().get();
						
						if(item != null && item.isMutable()){  //这个item的mutable是在addInbitmap中设置的
							
							if(canUseForInbitmap(item, options)){
								bitmap = item;
								
								iterator.remove();//从软引用中清除
								break;
							}
							
						}else{
							iterator.remove();
						}
					}
				}
				
			}
			return bitmap;
		}
		
		
		@TargetApi(VERSION_CODES.KITKAT)
		public boolean canUseForInbitmap(Bitmap bitmap, BitmapFactory.Options options){
			//这里需要对4.4以下系统做出兼容
			int height = options.outHeight/options.inSampleSize;
			int width = options.outWidth/options.inSampleSize;
			int byteCount = width * height *getBytesPerPixel(bitmap.getConfig());
			
			Log.i("fl---canUseForInbitmap", ""+byteCount +"----"+bitmap.getAllocationByteCount());
			return byteCount<= bitmap.getAllocationByteCount();
		}
		
		
		 private static int getBytesPerPixel(Config config) {
		        if (config == Config.ARGB_8888) {
		            return 4;
		        } else if (config == Config.RGB_565) {
		            return 2;
		        } else if (config == Config.ARGB_4444) {
		            return 2;
		        } else if (config == Config.ALPHA_8) {
		            return 1;
		        }
		        return 1;
		    }

}
