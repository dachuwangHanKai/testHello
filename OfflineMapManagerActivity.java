package com.dachuwang.crm.activity;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.dachuwang.crm.common.OfflineMapManagerAdapter;
import com.dachuwang.crm.util.OtherUtil;
import com.dachuwang.crm.util.ProgressDialogUtil;
import com.dachuwang.crm.util.ToastUtil;
import com.dachuwang.crm.util.UserDataUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OfflineMapManagerActivity extends Activity implements OfflineMapDownloadListener{

	private TextView currentCityName;
	private Button startDownLoad;
	private Button updateOfflineMap;
	private Button deleteOfflineMap;
	private TextView downloadStatus;
	
	//高德离线地图管理类
	private OfflineMapManager offlineMapManager;
	private OfflineMapManagerAdapter mapManagerAdapter;
	
	private ExpandableListView offlineMapManagerListView;
	
	public final static int INIT_UI = 0;
	public final static int REFRESH_UI = 1;
	
	//------------------------activity life cycle --------------//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.offline_map_manager);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initData();
		ProgressDialogUtil.showProgressDialog(this, "正在加载离线地图");
	}
	
	/**
	 * 从高德地图获取所有离线地图数据并检查
	 */
	private void initData(){
		offlineMapManager = new OfflineMapManager(OfflineMapManagerActivity.this,OfflineMapManagerActivity.this);
        mapManagerAdapter = new OfflineMapManagerAdapter(this);
		mapManagerAdapter.allProviceList = offlineMapManager.getOfflineMapProvinceList();
        mapManagerAdapter.downloadedCityList = offlineMapManager.getDownloadingCityList();
        initView();
	}
	
	private void initView(){
		offlineMapManagerListView = (ExpandableListView) this.findViewById(R.id.offline_map_manager_offline_map_list);
		offlineMapManagerListView.setAdapter(mapManagerAdapter);
		//初始化当前城市UI
		currentCityName   = (TextView) this.findViewById(R.id.offline_map_manager_current_city);
		startDownLoad     = (Button) this.findViewById(R.id.offline_map_manager_current_city_start_download);
		updateOfflineMap  = (Button) this.findViewById(R.id.offline_map_manager_current_city_update);
		deleteOfflineMap  = (Button) this.findViewById(R.id.offline_map_manager_current_city_delete);
		downloadStatus = (TextView) this.findViewById(R.id.offline_map_manager_current_city_status);
		
		currentCityName.setText(UserDataUtil.getCurrentCity());
		if(UserDataUtil.getCurrentCityMapDownloaded()){
			//已完成当前城市离线下载
			updateOfflineMap.setVisibility(View.VISIBLE);
			deleteOfflineMap.setVisibility(View.VISIBLE);
		}else{
			startDownLoad.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	//-----------------高德地图 下载通知----------------------------------//
	@Override
	public void onDownload(int status, int completeCode, String arg2) {
		// TODO Auto-generated method stub
		switch (status) {
		case OfflineMapStatus.SUCCESS:
			downloadStatus.setVisibility(View.VISIBLE);
			downloadStatus.setText("下载完成");
			break;
		case OfflineMapStatus.LOADING:
			downloadStatus.setVisibility(View.VISIBLE);
			downloadStatus.setText("已完成" + String.valueOf(completeCode) + "%");
			break;
		case OfflineMapStatus.UNZIP:
			downloadStatus.setVisibility(View.VISIBLE);
			downloadStatus.setText("解压中");
			break;
		case OfflineMapStatus.WAITING:
			break;
		case OfflineMapStatus.PAUSE:
			
			break;
		case OfflineMapStatus.STOP:
			
			break;
		case OfflineMapStatus.ERROR:
			downloadStatus.setVisibility(View.VISIBLE);
			downloadStatus.setText("下载错误");
			break;
		default:
			break;
		}
	}
	
	
	
//---------------button methods--------------------------------------//
	public void startDownLoad(View v){
		OfflineMapManager offlineMapManager = new OfflineMapManager(this,this);
		try {
			offlineMapManager.downloadByCityName(UserDataUtil.getCurrentCity());
		} catch (AMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateCurrentCity(View v){
		ToastUtil.makeShortToast(this, "正在开发中");
	}
	
	public void removeCurrentCity(View v){
		offlineMapManager.remove(UserDataUtil.getCurrentCity());
		initData();
	}
	
//---------------------------------------------------//
	
	
	
	
	/**
	 *  UI操作handler
	 */
	private Handler UIThreadHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case OfflineMapManagerActivity.INIT_UI:
				//更新UI
				OfflineMapManagerActivity.this.initView();
				break;
			case OfflineMapManagerActivity.REFRESH_UI:
				mapManagerAdapter.notifyDataSetChanged();
				break;
			}
				
		}
		
	};
	
	
}
