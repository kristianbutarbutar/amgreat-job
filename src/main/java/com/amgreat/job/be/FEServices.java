package com.amgreat.job.be;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amgreat.job.cache.LoadTableMapping2Cache;
import com.amgreat.job.util.Utilities;
import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.RequestVO;

@Component
public class FEServices implements FEServicesInterface {

	@Autowired private DataIntegrator dataAPI;
	@Autowired private CacheIntegrator  cacheAPI;
	@Autowired private LoadTableMapping2Cache loader;
	
	@Override
	public RecordVO doCmd( RequestVO vo ) {
		
		RecordVO r = new RecordVO();
		
		if( vo!=null && vo.getCmdString()!=null && vo.getPageId() !=null && !vo.getPageId().trim().equals("") ) {
			
			AttributeVO attr = new AttributeVO();
			
			attr.setId( vo.getPageId() ); attr.setCmdName("s");
			
			attr = cacheAPI.callCache( attr );
			
			if( attr == null || attr.getTabelName() == null ) {
				
				boolean bool = loader.loadTableMapping2Cache( vo.getPageId() ); //---load to chache --
				
				if( bool )
					attr = cacheAPI.callCache( attr ); //--pull from cache--
				else
					System.out.println("[FEServices.doCmd] Cache failed for single pageId: " + vo.getPageId() );
			}
			
			System.out.println( " 1 call data, tablename = " + (attr!=null && attr.getTabelName()!=null ? attr.getTabelName(): " null") );
			
			if( attr != null && attr.getTabelName() != null ) {
				
				System.out.println("2 call data, tablename = " + attr.getTabelName() );
				
				r = dataAPI.callData( attr );
				
				Utilities.printResponse( r );
			}
			
			AttributeVO tcache = attr;
			
			Utilities.printAttributes( tcache );
		}
		return r;
	}
	
	private AttributeVO setFilter( RequestVO r, AttributeVO at) {
		if( r != null && r.getFilter() != null) {
			RequestVO rf = r; AttributeVO searchby = new AttributeVO(); at.setFilter( searchby );
			
			while( rf != null ) {
				searchby.setColumnName( rf.getName() );  
				
				searchby.setName( "__" + rf.getName() ); 
				
				searchby.setValue( rf.getVal() ); 
				
				searchby.setType( getStringType( at, rf.getName() ) );
				
				rf = rf.getNext();
			}
		}
		return at;
	}
	
	private String getStringType(AttributeVO at, String key) {
		String t = "";
		AttributeVO attemp = at;
			while( attemp != null) {
				if( attemp.getColumnName() != null && attemp.getType() != null && attemp.getColumnName().trim().equalsIgnoreCase( key ) ) {
					t = attemp.getType(); break;
				}
				attemp = attemp.getNext();
			}
		return t;
	}
}
