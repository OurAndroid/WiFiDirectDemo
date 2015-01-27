package com.example.android.wifidirect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;


import com.example.filebrowser.utils.ImageWorker;

public class FileBrowser extends Activity{
	
	   File currentParent;
	   File[] currentFiles;
	   File[] orderFiles;
	   ListView listView;
	   TextView textView;
	   Button button;
	   long currentTime = 0;
	   
	   ImageWorker imageWorker;
	   
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.filebrowser);
			
			
			imageWorker = new ImageWorker(this);
			imageWorker.setLoadingImage(R.drawable.empty_photo);
			
			listView = (ListView) findViewById(R.id.list);
			textView = (TextView) findViewById(R.id.text);
			
			
			
			File root;
			
			 root = new File("/");
			
			
			
			
			if(root.exists()){
				currentParent = root;
				currentFiles = orderFiles(root.listFiles());
				
				inflateListView(currentFiles);
			}
			
			listView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					
					if(currentFiles[position].isDirectory()){
					currentParent = currentFiles[position];
					currentFiles = orderFiles(currentParent.listFiles());
					
					
					inflateListView(currentFiles);
					}else if(currentFiles[position].getName().toLowerCase().contains(".jpg")){
						/*Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.parse("file://"+currentFiles[position].getAbsolutePath()), "image/*");
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						startActivity(intent);*/
						
						Intent intent = new Intent();
						intent.putExtra("EXTRAS_FILE_PATH", currentFiles[position].getAbsolutePath().toString());
					    setResult(Activity.RESULT_OK , intent);
						Log.i("fl", currentFiles[position].getName());
						finish();
						
					}
					
					
				}
				
			});
			
			
			/*button.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(currentParent.getParentFile()!=null){
						currentParent = currentParent.getParentFile();
					    currentFiles = orderFiles(currentParent.listFiles());
					    inflateListView(currentFiles);
					}
					
					
				}
				
			});*/
			
		}

		//监听返回按钮

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			//super.onBackPressed();
			Date date = new Date();
			
			if(currentParent.getAbsolutePath().equals("/")){
				if(currentTime==0||(date.getTime()-currentTime>1000)){
					Toast.makeText(this, "再按一次返回退出",Toast.LENGTH_SHORT).show();
				    currentTime = date.getTime();
				}else{
					finish();
				}
					
			}
			else if(currentParent.getParentFile()!=null){
				currentParent = currentParent.getParentFile();
			    currentFiles = orderFiles(currentParent.listFiles());
			    inflateListView(currentFiles);
			}
			
		}



	
		
		
		private File[] orderFiles(File[] currentFiles){
			List<File> files = new ArrayList<File>();
			List<File> dirFiles = new ArrayList<File>();
			//判断currentFiles是否为空
			if(currentFiles==null){
				return currentFiles;
			}else{
				File[] orderFiles = new File[currentFiles.length];
				
				for(File file:currentFiles){
					if(file.isDirectory())
						dirFiles.add(file);
					else
						files.add(file);
				}
				
				
		         TreeSet<File> dirtree = new TreeSet<File>(dirFiles);
		         TreeSet<File> tree = new TreeSet<File>(files);
		         Iterator<File> it = dirtree.iterator();
		         
		         int i=0;
		         while(it.hasNext()){
		        	 orderFiles[i] = it.next();
		        	
		        	 i++;
		         }
		         
		         it = tree.iterator();
		         while(it.hasNext()){
		        	 orderFiles[i] = it.next();
		        	 i++;
		         }
		         
		         return orderFiles;
			}
			
	         
		}
		
		//填充listView
		private void inflateListView(File[] files){
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			
			if(files!=null){
			for(File file:files){
				String name = file.getName().toLowerCase();
				HashMap<String, Object> map = new HashMap<String, Object>();
				if(file.isDirectory())
					map.put("icon", R.drawable.wenjianjia);
				else if(name.contains(".jpg")||name.contains(".png")){
					
					String imagePath = file.getAbsolutePath();
					
					map.put("icon", imagePath);
					
					
				}
				else
					map.put("icon", R.drawable.wenjian);
				map.put("fileName", file.getName());
				listItems.add(map);
				
			}
		}
			
			listView.setAdapter(new ImageAdapter(this,listItems));
			
			
			try {
				textView.setText("当前路径为"+currentParent.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		public class ImageAdapter extends BaseAdapter{

			private LayoutInflater mInflater;
			private List<Map<String, Object>> data;
			
			public ImageAdapter(Context context, List<Map<String, Object>> list){
				mInflater = LayoutInflater.from(context);
				data = list;
			}
			
			//返回指定数据对应的视图类型
			@Override
			public int getItemViewType(int position) {
				
				String filename = String.valueOf(data.get(position).get("fileName")).toLowerCase();
				return isImage(filename) == true ? 1:0;
			}
	        
			//返回视图类型总数
			@Override
			public int getViewTypeCount() {
				return 2;
			}
	        
			//返回数据总数
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return data.size();
			}
	        
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return data.get(position);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
	       
			//填充视图并返回
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				String name = (String) data.get(position).get("fileName");
				ViewHolder viewHolder;
				Log.i("fl---getView", name);
				System.out.println(data.get(position).get("icon"));
				//如果对应的视图不存在，创建
				if(convertView == null){
					viewHolder = new ViewHolder();
					if(isImage(name.toLowerCase())){
						Log.i("fl", "图片路径");
						//为contentView填充，并绑定控件
						convertView = mInflater.inflate(R.layout.image_list_line, parent, false);
						viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_list_image);
						viewHolder.textView = (TextView) convertView.findViewById(R.id.image_list_text);
						
						convertView.setTag(viewHolder);
						
					}else{
						Log.i("fl", "文件路径");
						convertView = mInflater.inflate(R.layout.listline, parent, false);
						viewHolder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
						viewHolder.textView = (TextView) convertView.findViewById(R.id.list_text);
						
						convertView.setTag(viewHolder);
					
					}
				}else{
					Log.i("fl", "复用view");
					viewHolder = (ViewHolder) convertView.getTag();
					
				}
				
				//加载不同的图片类型
				if(isImage(name.toLowerCase())){
					imageWorker.loadImage(data.get(position).get("icon"), viewHolder.imageView);
				}else{
					viewHolder.imageView.setImageResource((Integer)data.get(position).get("icon"));
				}
				viewHolder.textView.setText(name);
				return convertView;
			}
			
			private class ViewHolder{
				private TextView textView;
				private ImageView imageView;
			}
			
		}
		
		
		private boolean isImage(String name){
			if(name.contains(".jpg")||name.contains(".png"))
				return true;
			return false;
		}


	}
