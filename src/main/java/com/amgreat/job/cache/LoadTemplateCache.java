package com.amgreat.job.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amgreat.job.be.CacheIntegratorIntf;
import com.amgreat.job.be.DataIntegratorIntf;
import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.ResponseVO;
import com.amgreat.vo.TemplateVO;

@Component
public class LoadTemplateCache {
	
	@Autowired private CacheIntegratorIntf cacheAPI; 
	@Autowired private DataIntegratorIntf dataAPI;
	
	public boolean loadTemplate2Cache(String pageId) {
		boolean bool =false;
			try {
				AttributeVO preq = new AttributeVO(); AttributeVO req = preq;
				
				req.setTabelName("t_form");
				req.setColumnName("id");req.setName("__id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
				req.setColumnName("pid");req.setName("__pid");req.setValue(""); req.setType("str"); ;
				
				RecordVO rp = new RecordVO(); preq.setCmdName("s");
				
				if( pageId != null && !pageId.trim().equalsIgnoreCase("all")) {
					AttributeVO searchby = new AttributeVO(); preq.setFilter( searchby );
					searchby.setColumnName( "id" ); searchby.setType( "str" ); searchby.setValue( pageId );
				}
				
				RecordVO r = dataAPI.callData( preq );
				
				if( r != null ) {
					
					RecordVO result = r; int i = 1;
					
					while( result != null ) {
						
						ResponseVO columns = result.getResponse(); // -- get columns --
						
						String tableId = dataAPI.getColumAt( result.getResponse(), 1);
						
						if( tableId != null && !tableId.trim().equals("") ) {
							
							bool = this.loadTemplate( tableId );
						}
						
						result = result.getNext();
					}
				}
				
				return bool;
			} catch (Exception e) {
				System.out.println("[LoadTemplateCache.loadTemplate2Cache]:" + e.getMessage() );
			}
		return bool;
	}
	
	private boolean loadTemplate(String pageId) {
		boolean bool = false;
		try 
		{
			AttributeVO preq = new AttributeVO(); AttributeVO req = preq;
			
			req.setTabelName("t_form_template");
			req.setColumnName("id");req.setName("__id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("pid");req.setName("__pid");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("viewtype");req.setName("__viewtype");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("view_template");req.setName("__view_template");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("status");req.setName("__status");req.setValue(""); req.setType("str");
			
			RecordVO rp = new RecordVO(); preq.setCmdName("s");

			AttributeVO searchby = new AttributeVO(); preq.setFilter( searchby );
			searchby.setColumnName( "pid" ); searchby.setType( "str" ); searchby.setValue( pageId ); searchby = searchby.setNext(new AttributeVO());
			searchby.setColumnName( "viewType" ); searchby.setType( "str" ); searchby.setValue( "viewtype0001" );
			searchby.setColumnName( "view_template" ); searchby.setType( "str" ); searchby.setValue( "ISNOTNULL" );
			
			RecordVO r = dataAPI.callData( preq );
			
			if( r != null ) {
				
				RecordVO result = r; int i = 1;
				
				while( result != null ) {
					
					ResponseVO columns = result.getResponse(); // -- get columns --
					
					String pid = dataAPI.getColumAt( result.getResponse(), 2);
					String viewTemplate = dataAPI.getColumAt( result.getResponse(), 4);
					
					TemplateVO templateVO = new TemplateVO(); 
					
					templateVO.setId(pid);
					
					templateVO.setTemplate(viewTemplate); templateVO.setCmdName("s");
					
					TemplateVO t = cacheAPI.callCache( templateVO ); 
					
					//----search in cache 1st---
					if( t != null ) {
						templateVO.setCmdName("d");
						cacheAPI.callCache( templateVO );
						templateVO.setCmdName("i");
						cacheAPI.callCache( templateVO );
						
					} else {
						//---cache it---
						templateVO.setCmdName("i");
						cacheAPI.callCache( templateVO ); 
					}
					
					result = result.getNext();
				}
				bool = true;
			}
			
			return bool;
		} catch (Exception e) {
			System.out.println("[LoadTemplateCache.loadTemplate2Cache]:" + e.getMessage() );
		}
		return bool;
	}
}
