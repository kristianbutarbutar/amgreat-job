package com.amgreat.job.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amgreat.job.be.CacheIntegratorIntf;
import com.amgreat.job.be.DataIntegratorIntf;

import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.LineVO;
import com.amgreat.vo.PageVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.ResponseVO;

@Component
public class HtmlGenerators {

	@Autowired private DataIntegratorIntf dataAPI;
	@Autowired private CacheIntegratorIntf cacheAPI;
	
	@Value("${path.jsp.uri}")
	private String htmlPathUri;
	
	public boolean generateHtml( String pageId ) {
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
					String label = dataAPI.getColumAt( result.getResponse(), 2);
					
					if( tableId != null && !tableId.trim().equals("") ) {
						this.wrapHtml(tableId, label);
					}
					result = result.getNext();
				}
			}
			return true;
		} catch(Exception e) {
			System.out.println("[HtmlGenerators.generateHtml]:" + e.getMessage() );
		}
		
		return bool;
	}
	
	public boolean wrapHtml( String pageId, String label ) {
		boolean bool = false;
		try {
			if( pageId != null && !pageId.trim().equals("")) {
				
				//---get from cache: search---
				
				PageVO vo = new PageVO(); vo.setCmdName("s"); vo.setPar("s"); vo.setId( vo.getPar()+pageId);
				
				vo = this.cacheIt(vo);
				
				if( vo != null && vo.getHtml() != null ) {
					
					vo.setPageLabel(label);
					
					builHtml( vo );
					
					//System.out.println("[wrapHtml, template from cache[" + pageId + ": " + label + "]: ]" + vo.getHtml() );
				} else
					System.out.println("can not find cache " + vo.getId() );
			}
		} catch(Exception e) {
			System.out.println("[HtmlGenerators.wrapHtml]:" + e.getMessage() );
		}
		return bool;
	}
	
	private boolean deleteFile(String fnm) {
		boolean bool = false;
		try {
			File myObj = new File(fnm); 
		    if (myObj.delete()) { 
		      System.out.println("Deleted the file: " + myObj.getName());
		    } else {
		      System.out.println("Failed to delete the file.");
		    }
		} catch(Exception e) {
			System.out.println("[HtmlGenerators.deleteFile]: " + e.getMessage() );
		}
		return bool;
	}
	public boolean builHtml( PageVO vo ) {
		
		boolean bool = false;
		
		try {
			
			LineVO lines =  fileReader( "console.jsp" );
			
			System.out.println("[1 - buildHtml] " + lines.getRecNo() );
			
			if( lines != null ) {
				
				replace( lines, "[[TITLE]]", vo.getPageLabel() );
				//System.out.println("[2 - buildHtml] " + lines.getRecNo() );
				
				replace( lines, "[[CONTENT]]", vo.getHtml() );
				//System.out.println("[3 - buildHtml] " + lines.getRecNo() );
				
				String content = "";
				
				LineVO l = lines;
				
				while( l != null ) {
					if(l!=null && l.getLine()!=null && !l.getLine().equalsIgnoreCase("null")) {
						content = content + l.getLine();
					}
					l = l.getNext();
				}
				// -- delete before write new file and content--
				deleteFile(vo.getId() + ".jsp");
				write2File( vo.getId() + ".jsp", content );
				
			}
			
		} catch(Exception e) {
			System.out.println("[HtmlGenerators.builHtml]:" + e.getMessage() );
		}
		return bool;
	}
	
	/**
	 * Put the HTML to template before save 
	 * @param lines
	 * @param find
	 * @param tobe
	 * @return
	 */
	private LineVO replace(LineVO lines, String find, String tobe) {
		try {
			
			int len = lines.getRecNo(); 
			
			LineVO l = lines;
			
			while( l != null ) {
				if(l.getLine() != null) {
					int idx = l.getLine().indexOf( find );
					
					if( idx >= 0 ) {
						
						String s = l.getLine().substring( 0, idx );
						
						s = s + tobe + l.getLine().substring( (find.length() + idx) );
						
						l.setLine(s);
						
						break;
					}
				}
				l = l.getNext();
			}
			//System.out.println("[replace] " + lines.getRecNo());
		} catch(Exception e) {
			System.out.println("[HtmlGenerators.replace]:" + e.getMessage() );
		}
		return lines;
	}
	
	private LineVO fileReader( String fnm ) {
		
		LineVO data = new LineVO(); LineVO d = data;
		
		try {
			System.out.println("File Template Path: " + (this.htmlPathUri + fnm) );
		      File myObj = new File( (this.htmlPathUri + fnm) );
		      
		      Scanner myReader = new Scanner( myObj );
		      
		      int i = 0;
		      
		      while ( myReader.hasNextLine() ) {
		    	  d.setLine( myReader.nextLine() ) ; d.setNext(new LineVO()); i++;
		    	  d = d.getNext();
		      }
		      
		      data.setRecNo(i);
		      
		      myReader.close();
		      
		} catch (FileNotFoundException e) {
		    System.out.println("[HtmlGenerators.fileReader] FileNotFoundException: An error occurred.");
		    e.printStackTrace();
		}
		 return data;
	}

	private String write2File(String fnm, String s) {
		
		String status = "";
		
		if( fnm != null && s != null ) {
			
			 try {
				 
				  File myObj = new File( this.htmlPathUri + fnm );
			      
				  if ( myObj.createNewFile() ) {
			    	  System.out.println("File created: " + myObj.getName());
			      } else {
			    	  System.out.println("File already exists.");
			      }
				  
			      FileWriter myWriter = new FileWriter( this.htmlPathUri + fnm );
				  
			      myWriter.write( s );
				  
			      myWriter.close();

			 } catch (IOException e) {
			      System.out.println("[DataModelling.writeToFile] IOException: An error occurred.");
			      e.printStackTrace();
			 } catch (Exception e) {
				  System.out.println("[DataModelling.writeToFile] Exception: An error occurred.");
			      e.printStackTrace();
			 }
		}		
		return status;
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
	private PageVO cacheIt(PageVO vo) {
		try {
			vo.setCmdName("s"); vo = cacheAPI.callCache( vo, vo.getPar() );
		} catch (Exception e) {
			System.out.println("[HtmlGenerators.cacheIt]:" + e.getMessage() );
		}
		
		return vo;
	}

}
