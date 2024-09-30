package com.amgreat.job.html;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amgreat.job.be.CacheIntegratorIntf;
import com.amgreat.job.be.DataIntegratorIntf;
import com.amgreat.job.util.Utilities;
import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.PageVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.ResponseVO;

@Component
public class LoadForm2HtmlTemplate {

	@Autowired private DataIntegratorIntf dataAPI;
	
	@Autowired private CacheIntegratorIntf cacheAPI;
	
	@Value("${path.jsp.uri}")
	private String htmlPathUri;
	
	public boolean loadFormPages(String pageId) {
		boolean bool = false;
		try {
			AttributeVO preq = new AttributeVO(); AttributeVO req = preq;			
			req.setTabelName("t_form");
			req.setColumnName("id");req.setName("__id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("label");req.setName("__label");req.setValue(""); req.setType("str"); ;
			
			RecordVO rp = new RecordVO(); preq.setCmdName("s");
			
			if( pageId != null && !pageId.trim().equalsIgnoreCase("all")) {
				AttributeVO searchby = new AttributeVO(); preq.setFilter( searchby );
				searchby.setColumnName( "id" ); searchby.setType( "str" ); searchby.setValue( pageId );
			}
			
			RecordVO r = dataAPI.callData( preq );
			
			if( r != null ) {
				
				RecordVO result = r; int i = 1;
				
				while( result != null ) {
					
					ResponseVO columns = result.getResponse();
					
					String tableId = dataAPI.getColumAt( result.getResponse(), 1);
					
					if( tableId != null && !tableId.trim().equals("") ) {
						/*
						 * 1. viewtype0001 => restresponse 
						 * 2. view on page = viewtype0002 => search
						 * 3. view on page = viewtype0003 => add
						 * 4. view on page = viewtype0004 => edit
						 * 5. view on page = viewtype0005 => delete
						 * */
						//this.genFormAndTemplate(tableId);

						// for search page --
						String vt = "viewtype0002"; formPage( tableId.trim(), vt ); 
						
						// for add page --
						vt="viewtype0003"; formPage( tableId.trim(), vt );
						
						// for edit --
						vt="viewtype0004"; formPage( tableId.trim(), vt );
						
						// for preview page --
						vt="viewtype0005"; formPage( tableId.trim(), vt );
						
						vt="viewtype0006"; formPage( tableId.trim(), vt );
					}
					
					result = result.getNext();
				}
			}
			
			return true;
		} catch (Exception e) {
			System.out.println("[LoadForm2HtmlTemplate.loadFormPages]:" + e.getMessage() );
		}
		return bool;
	}
	
	/**
	 * 
	 * @param pageId
	 * @return
	 * viewType:
	 * 1. view on page = viewtype0001 => restresponse API 
	 * 2. view on page = viewtype0002 => search
	 * 3. view on page = viewtype0003 => add
	 * 4. view on page = viewtype0004 => edit
	 * 5. view on page = viewtype0005 => preview 
	 */
	private boolean formPage( String pageId, String vt ) {
		boolean bool = false;
		try {
			AttributeVO preq = new AttributeVO(); AttributeVO req = preq;			
			req.setTabelName("t_form_template a");
			req.setColumnName("a.id");req.setName("__id");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.pid");req.setName("__pid");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("a.viewtype");req.setName("__viewtype");req.setValue(""); req.setType("str"); req = req.setNext(new AttributeVO());
			req.setColumnName("b.template");req.setName("__template");req.setValue(""); req.setType("str"); 
			
			RecordVO rp = new RecordVO(); preq.setCmdName("s");
			
			AttributeVO searchby = new AttributeVO(); preq.setFilter( searchby );
			searchby.setColumnName( "a.pid" ); searchby.setType( "str" ); searchby.setValue( pageId ); searchby = searchby.setNext(new AttributeVO());
			searchby.setColumnName( "a.viewtype" ); searchby.setType( "str" ); searchby.setValue( vt ); 
			
			AttributeVO ljoin = new AttributeVO(); preq.setLjoin( ljoin );
			ljoin.setCmdName(" t_template b on a.viewtype = b.id");
			
			RecordVO r = dataAPI.callData( preq );
			
			if(r != null) {
				RecordVO rt = r;
				while(rt != null ) {
					ResponseVO rpt = rt.getResponse();
					ResponseVO id = rpt.getColumAt(1);
					ResponseVO pid = rpt.getColumAt(2);
					ResponseVO viewtype = rpt.getColumAt(3);
					ResponseVO viewtmplt = rpt.getColumAt(4);
					
					PageVO pvo = new PageVO();
					pvo.setHtml( viewtmplt.getVal() );
					pvo.setViewType( viewtype.getVal() );
					pvo.setPar( ( vt.equalsIgnoreCase("viewtype0001") ? "r" : ( vt.equalsIgnoreCase("viewtype0002") ? "s" : (vt.equalsIgnoreCase("viewtype0003") ? "a" : (vt.equalsIgnoreCase("viewtype0004") ? "e" : (vt.equalsIgnoreCase("viewtype0005") ? "v":(vt.equalsIgnoreCase("viewtype0006") ? "b":"")))))));
					pvo.setId( pvo.getPar() + pageId );
					
					bool = cacheIt( pvo );
					
					rt = rt.getNext();
				}
				bool = true;
			}
		} catch (Exception e) {
			System.out.println("[LoadForm2HtmlTemplate.formPage]:" + e.getMessage() );
		}
		return bool;
	}
	
	private boolean cacheIt(PageVO vo) {
		boolean bool = false;
		try {
			//---check isAvailable and delete it--
			vo.setCmdName("d");
			PageVO vot = cacheAPI.callCache(vo);
			
			//---cache it---
			vo.setCmdName("i");
			cacheAPI.callCache(vo, vo.getPar() );
			
			bool = true;
		} catch (Exception e) {
			System.out.println("[LoadForm2HtmlTemplate.cacheIt]:" + e.getMessage() );
		}
		
		return bool;
	}
	
	private boolean genFormAndTemplate(String pageId) {
		boolean bool = false;
		try {
			String s = "";

					/* 1. view on page = viewtype0001 => restresponse API 
					 * 2. view on page = viewtype0002 => search
					 * 3. view on page = viewtype0003 => add
					 * 4. view on page = viewtype0004 => edit
					 * 5. view on page = viewtype0005 => preview 
					 */
			s = "insert into t_form_template(id, pid, viewtype) values(";
			s+= "'"+Utilities.getRandomText(50) +"','"+ pageId+"','viewtype0001');";
			System.out.println(s);
			
			s = "insert into t_form_template(id, pid, viewtype) values(";
			s+= "'"+Utilities.getRandomText(50) +"','"+ pageId+"','viewtype0002');";
			System.out.println(s);
			
			s = "insert into t_form_template(id, pid, viewtype) values(";
			s+= "'"+Utilities.getRandomText(50) +"','"+ pageId+"','viewtype0003');";
			System.out.println(s);
			
			s = "insert into t_form_template(id, pid, viewtype) values(";
			s+= "'"+Utilities.getRandomText(50) +"','"+ pageId+"','viewtype0004');";
			System.out.println(s);
			
			s = "insert into t_form_template(id, pid, viewtype) values(";
			s+= "'"+Utilities.getRandomText(50) +"','"+ pageId+"','viewtype0005');";
			System.out.println(s);
			
			s = "insert into t_form_template(id, pid, viewtype) values(";
			s+= "'"+Utilities.getRandomText(50) +"','"+ pageId+"','viewtype0006');";
			System.out.println(s);
			
			return bool = true;
		} catch (Exception e) {
			System.out.println("[LoadForm2HtmlTemplate.genFormAndTemplate]:" + e.getMessage() );
		}
		return bool;
	}
}
