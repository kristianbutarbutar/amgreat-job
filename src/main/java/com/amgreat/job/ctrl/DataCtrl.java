package com.amgreat.job.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amgreat.job.be.DataIntegratorIntf;
import com.amgreat.job.cache.LoadTableMapping2Cache;
import com.amgreat.job.data.DataModellingIntf;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.RequestVO;

@RestController
public class DataCtrl {

	@Autowired
	private DataIntegratorIntf data;
	
	@Autowired
	private LoadTableMapping2Cache loader;
	
	@Autowired
	private DataModellingIntf dm; 
	
	@RequestMapping( "/amgreate/api/job/interface" )
	public RecordVO callData( @RequestBody RequestVO request ) {
		RecordVO r = null;
		try {
			r = data.callData(request);
		} catch (Exception e) {
			System.out.println("[DataCtrl.callData]:" + e.getMessage());
		}
		return r; 
	}
	
	@RequestMapping( "/amgreate/api/job/dm" )
	public String genDM(  ) {
		return dm.extractDBStructure();
	}
	
	@RequestMapping( "/amgreate/api/job/loadcache" )
	public boolean loadCache( ) {
		return loader.loadTableMapping2Cache("all");
	}
	
}