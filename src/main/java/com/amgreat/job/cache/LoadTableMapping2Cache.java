package com.amgreat.job.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.amgreat.job.be.CacheIntegrator;
import com.amgreat.job.be.DataIntegrator;
import com.amgreat.job.util.Utilities;
import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.ResponseVO;

@Component
public class LoadTableMapping2Cache {
	
	@Autowired 
	private DataIntegrator dataAPI;
	
	@Autowired
	private CacheIntegrator cacheAPI;
	
	public AttributeVO cacheIt( AttributeVO vo ) {
		return cacheAPI.callCache( vo );
	}
	
	public boolean loadTableMapping2Cache( String pageId) {
		
		boolean bool = false;
		
		AttributeVO preq = new AttributeVO(); AttributeVO req = preq;
		
		req.setTabelName("t_form");
		req.setColumnName("id");req.setName("__id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("pid");req.setName("__pid");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("label");req.setName("__label");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("description");req.setName("__desc");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("tablename");req.setName("__tname");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("issql");req.setName("__issql");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("sqlid");req.setName("__sqlid");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
		req.setColumnName("status");req.setName("__status");req.setValue("16-02-1984"); req.setType("str"); 
		
		RecordVO rp = new RecordVO(); preq.setCmdName("s");
		
		if( pageId != null && !pageId.trim().equalsIgnoreCase("all")) {
			AttributeVO searchby = new AttributeVO(); preq.setFilter( searchby );
			searchby.setColumnName( "id" ); searchby.setType( "str" ); searchby.setValue( pageId );
		}
		
		RecordVO r = dataAPI.callData( preq );
		
		if( r != null ) {
			
			RecordVO result = r; int i = 1;
			
			System.out.println(" 1 loadTableMapping2Cache " + result.getResponse().getLabel() );
			
			while( result != null ) {
				
				ResponseVO columns = result.getResponse(); // -- get columns --
				
				String tableId = this.getColumAt( result.getResponse(), 1);
				String tableName = this.getColumAt( result.getResponse(), 5);
				
				if( tableId != null && !tableId.trim().equals("") ) {
					
					System.out.println(" 2 loadTableMapping2Cache tableId=" + tableId + ", tablename=" + tableName);
					
					bool = this.loadTableColumnsMapping( tableId, tableName ); //---column id = id = column index 1st---
				}
				
				result = result.getNext();
			}
		}
		
		return bool;
	}
	
	public boolean loadTableColumnsMapping( String tableId, String tableName ) {
		
			boolean bool = false;
				
			AttributeVO preq = new AttributeVO(); AttributeVO req = preq;
			
			System.out.println("tableId = " + tableId);
			
			req.setTabelName("t_form_columns a");
			req.setColumnName("a.id");req.setName("__id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.pid");req.setName("__pid");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.label");req.setName("__label");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.tbl_col_nm");req.setName("__tbl_col_nm");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("b.data_type");req.setName("__t_col_id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.seqno");req.setName("__seqno");req.setValue(""); req.setType("smallint"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.def_val");req.setName("__def_val");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.issql");req.setName("__issql");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.is_attribute_list");req.setName("__is_attribute_list");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.sqlid");req.setName("__sqlid");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.attribute_grp_id");req.setName("__attribute_grp_id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.nullable");req.setName("__nullable");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.maxlength");req.setName("__maxlength");req.setValue(""); req.setType("smallint"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.status");req.setName("__status");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			//select id,pid,label,tbl_col_nm,t_col_id,seqno,def_val,issql,is_attribute_list,sqlid,attribute_grp_id,nullable,maxlength,status from t_form_columns
			preq.setCmdName("s");
			
			AttributeVO searchby = new AttributeVO();
			preq.setFilter( searchby ); 
			
			searchby.setColumnName("a.pid"); 
			searchby.setName("__pid");
			searchby.setValue( tableId ); 
			searchby.setType("str"); 
			
			AttributeVO rjoin = new AttributeVO();
			rjoin.setCmdName("t_columns b on a.t_col_id = b.id ");
			preq.setLjoin( rjoin );
			
			RecordVO r = dataAPI.callData( preq );
			
			if( r != null ) {
				
				RecordVO result = r;
				
				AttributeVO cache = new AttributeVO(); 
				AttributeVO tcache = cache; 
				
				cache.setTabelName( tableName ); cache.setCmdName("i"); int i = 0; cache.setId( tableId );
				
				while( result != null && result.getResponse() != null) 
				{
					i++;
					
					ResponseVO columns = result.getResponse();

					tcache.setColumnName( this.getColumAt(columns, 4) );
					tcache.setName( "__"+this.getColumAt(columns, 4) ); 
					tcache.setValue(""); 
					tcache.setType( this.getColumAt(columns, 5) );
					
					result = result.getNext();
					
					if( result != null )
						tcache = tcache.setNext( new AttributeVO() );
				}
				
				tcache = cache;
				
				cache.setCmdName("s");
				
				AttributeVO cached = cacheIt( cache );
				
				if( cached !=null && cached.getId()!=null ) {//---delete 1st if exist then add cache---
					
					cache.setCmdName("d"); cacheIt( cache );
				}
				
				cache.setCmdName("i"); cache = cacheIt( cache ); //---insert and cache it once inserted---				
				
				if( cache != null && cache.getTabelName() != null && cache.getTabelName().trim().equals("") ) bool = true;
				
				Utilities.printAttributes( tcache );
				
				System.out.println( "-------------------------------------- " );
			}
			return bool;
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
