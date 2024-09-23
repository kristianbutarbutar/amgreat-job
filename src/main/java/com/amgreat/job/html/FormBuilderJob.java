package com.amgreat.job.html;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amgreat.job.be.CacheIntegrator;
import com.amgreat.job.be.DataIntegrator;
import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.ListVO;
import com.amgreat.vo.PageVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.ResponseVO;

@Component
public class FormBuilderJob implements JobInterface {

	@Autowired private CacheIntegrator  cacheAPI;
	@Autowired private DataIntegrator dataAPI;
	
	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean generateFormHtml() {
		return loadTableMapping2Cache( "all" );
	}
	
	public boolean loadTableMapping2Cache( String pageId) {
		
		boolean bool = false;
		
		AttributeVO preq = new AttributeVO(); AttributeVO req = preq;
		
		req.setTabelName("t_form");
		req.setColumnName("id");req.setName("__id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("pid");req.setName("__pid");req.setValue(""); req.setType("str");
		
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
				
				String tableId = this.getColumAt( result.getResponse(), 1);
				
				AttributeVO findVO = new AttributeVO(); findVO.setId( tableId ); findVO.setCmdName("s");
				
				findVO = cacheIt( findVO );
				
				//---generate html form here---
				if( findVO != null && findVO.getTypeId() != null && !findVO.getTypeId().trim().equals("") ) {
					
					PageVO pvo = new PageVO(); pvo.setId( tableId ); pvo.setCmdName("i");
					
					String page = wrapPage( findVO );
					
					pvo.setHtml( page );
					
					if( page != null && page.trim().length() > 10 ) this.cacheIt(pvo);
				}
				result = result.getNext();
			}
		}
		return bool;
	}
	
	private String wrapPage(AttributeVO vo) {
		String s = "";
		if( vo != null ) {
			
			AttributeVO t = vo;
			
			while( t != null ) {
				
				ListVO searchInCache = new ListVO(); 
				
				searchInCache.setId( t.getTypeId() ); searchInCache.setCmdName("s");
				
				searchInCache = this.cacheIt( searchInCache );
				
				if( searchInCache == null ) {
					//---load t_columns to cache ---
					this.getColumns(); 
					
					searchInCache = this.cacheIt( searchInCache );
				}
				
				String reps = ( searchInCache.getVal()!=null ? searchInCache.getVal(): "" );
				
				if( reps != null && reps.trim().length() > 10 ) {
					reps = replaceInString(reps, "[[label]]", t.getLabel()); //label
					reps = replaceInString(reps, "[[id]]", t.getName() ); //id
					reps = replaceInString(reps, "[[value]]",t.getValue() ); //value
				} else s+= "error in wrapping " + t.getName();
				
				s += reps;
				
				t = t.getNext();
			}
		}
		return s;
	}
	
	private String replaceInString(String src, String find, String replace) {
		int idx = src.indexOf( find ); 
		
		src = src.substring( 0, idx ) + replace + src.substring( idx + ( find.length()-1 ), ( src.length()-1 ) );
		
		return src;
	}
	
	public ListVO getColumns( ) {
		ListVO l = null;
		
		AttributeVO preq = new AttributeVO(); AttributeVO req = preq;
		
		req.setTabelName("t_columns");
		req.setColumnName("id");req.setName("__id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("template");req.setName("__template");req.setValue(""); req.setType("str");
		
		preq.setCmdName("s");
		
		RecordVO r = dataAPI.callData( preq );
		
		if( r != null ) {
			
			RecordVO rt = r;
			
			ListVO rootList = new ListVO(); ListVO lvo = rootList;
			
			while( rt != null ) {
				
				ResponseVO columns = rt.getResponse() ;
				String id = getColumAt(columns, 1);  
				String template = getColumAt(columns, 2);
				lvo.setId( id );
				lvo.setType( id );
				lvo.setVal( template );
				lvo.setCmdName("i");
				
				this.cacheIt( lvo );
				
				rt = rt.getNext();
				
				if(rt != null) {
					lvo.setNext( new ListVO() );
					lvo = lvo.getNext();
				}
			}
			return rootList;
		}
		return l;
	} 
	

	public PageVO cacheIt( PageVO vo ) {
		return cacheAPI.callCache( vo );
	}

	
	public AttributeVO cacheIt( AttributeVO vo ) {
		return cacheAPI.callCache( vo );
	}
	
	public ListVO cacheIt( ListVO vo ) {
		return cacheAPI.callCache( vo );
	}

	private String getColumAt(ResponseVO list, int at){
		String s = "";
		ResponseVO searchOn = list; 
		
		if( searchOn != null && at == 1) {
			return searchOn.getVal();
		} else if( searchOn != null && at > 1 ) {
			searchOn = searchOn.getNext(); int i = 2 ;
			while( searchOn != null ) {
				if( at == i ) { break; }
				searchOn = searchOn.getNext(); i++;
			}
			s = (searchOn!=null && searchOn.getVal()!=null ? searchOn.getVal():"");
		}
		return s;
	}
}
