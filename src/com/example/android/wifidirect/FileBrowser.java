package com.example.android.wifidirect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.example.filebrowser.utils.ImageWorker;




public class FileBrowser extends Activity {
	
   //protected static final String EXTRAS_FILE_PATH = null;
   File currentParent;
   File[] currentFiles;
   File[] orderFiles;
   ListView listView;
   TextView textView;
   Button button;
   
   ImageWorker imageWorker;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filebrowser);
		
		imageWorker = new ImageWorker(this);
		imageWorker.setLoadingImage(R.drawable.empty_photo);
		
		listView = (ListView) findViewById(R.id.list);
		textView = (TextView) findViewById(R.id.text);
		button = (Button) findViewById(R.id.button);
		
		button.setText("...");
		
		/*
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String fileName = null;
		
		try{
			fileName = bundle.getString("fileName");
		}catch(NullPointerException e){
			fileName = null;
		}*/
		
		
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
				}else {//if(currentFiles[position].getName().toLowerCase().contains(".jpg"))
					/*Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://"+currentFiles[position].getAbsolutePath()), "image/*");
					intent.addCategory(Intent.ACTION_DEFAULT);
					startActivity(intent);*/
					Intent data = new Intent();
					data.putExtra("EXTRAS_FILE_PATH", currentFiles[position].getAbsolutePath().toString());
				    setResult(Activity.RESULT_OK , data);				   					
					Log.i("fl", currentFiles[position].getName());
					finish();
				}
				
				
			}
			
		});
		
		
		button.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currentParent.getParentFile()!=null){
					currentParent = currentParent.getParentFile();
				    currentFiles = orderFiles(currentParent.listFiles());
				    inflateListView(currentFiles);
				}
				
				
			}
			
		});
		
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	
	
	private File[] orderFiles(File[] currentFiles){
		List<File> files = new ArrayList<File>();
		List<File> dirFiles = new ArrayList<File>();
		//对文件排序
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
				Bitmap bitmap;
				
				//bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 80, 80);
				/*BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				
				bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
				System.out.println(file.getAbsolutePath());
				
				
				int be = 0;
				int height = options.outHeight;
				int width = options.outWidth;
				
				if(height!=0&&width!=0){
					
					be =  (int) (height/(float)50+width/(float)50)/2;
					 
				}
				options.inJustDecodeBounds = false;
				
				//be = (int) ((int)(height>width ? height:width)/(float)50);
				if(be<=0)
					be = 1;
				options.inSampleSize = be;
				
				bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
				System.out.println("f_height"+height+"l_height"+bitmap.getHeight());
				System.out.println("f_width"+width+"l_width"+bitmap.getWidth());*/
				map.put("icon", imagePath);
				//map.put("icon", bitmap);
				
			}
			else
				map.put("icon", R.drawable.wenjian);
			map.put("fileName", file.getName());
			listItems.add(map);
			
		}
	}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.listline, new String[]{"icon", "fileName"}, new int[]{R.id.list_image, R.id.list_text});
		simpleAdapter.setViewBinder(new ViewBinder(){

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				
				View v = view;
				String text = data ==null ?"":data.toString();
				// TODO Auto-generated method stub
				 if (v instanceof Checkable) {
                     if (data instanceof Boolean) {
                         ((Checkable) v).setChecked((Boolean) data);
                     } else if (v instanceof TextView) {
                         // Note: keep the instanceof TextView check at the bottom of these
                         // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                         ((TextView) v).setText(text);
                     } else {
                         throw new IllegalStateException(v.getClass().getName() +
                                 " should be bound to a Boolean, not a " +
                                 (data == null ? "<unknown type>" : data.getClass()));
                     }
                 } else if (v instanceof TextView) {
                     // Note: keep the instanceof TextView check at the bottom of these
                     // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                	 ((TextView) v).setText(text);
                	 
                 } else if (v instanceof ImageView) {
                     if (data instanceof Integer) {
                         ((ImageView) v).setImageResource((Integer) data);                            
                     }else if(data instanceof String){
                    	 imageWorker.loadImage(data, (ImageView)v);
                     }
                     else {
                         ((ImageView) v).setImageBitmap((Bitmap) data);
                     }
                 } else {
                     throw new IllegalStateException(v.getClass().getName() + " is not a " +
                             " view that can be bounds by this SimpleAdapter");
                 }
				return true;
			}
			
		});
		
		listView.setAdapter(simpleAdapter);
		
		
		try {
			textView.setText("当前目录"+currentParent.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
