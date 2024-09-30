package com.amgreat.job.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amgreat.job.util.Utilities;

@Component
public class DataModelling implements DataModellingIntf{

	private java.util.logging.Logger logger =  java.util.logging.Logger.getLogger(this.getClass().getName());
	
	@Value("${file.in.upload}")
	private String fileInPath;
	
	@Value("${file.in.log}")
	private String fileLogPath;
	
	@Value("${file.in.download}")
	private String fileDownloadPath;
	

	public String extractDBStructure () {
		
		return fileReader("Database"); 
	}
	
	private String genformsSQL( Table t ) {
		String s = "";
		/*
		 * table: t_form: id varchar(50), pid varchar(50), 
		 * label varchar(100), description varchar(200), tablename varchar(50), 
		 * issql varchar(50), sqlid varchar(50), status varchar(50)	
		 */
		
		if(t != null) {
			
			Table tc = t;
			
			s += this.formSQL( tc );
			
			while( tc.getNext() != null ) {
				
				tc = tc.getNext();
				
				s += this.formSQL( tc );
				
			}
		}
		
		return s;
	}
	
	private String formSQL(Table t) {
		
		String s = "";
		
		if(t != null) {
			String formId = Utilities.getRandomText(50); t.setFormId( formId );
			s = "insert into t_form(id, pid, label, description, tablename, issql, sqlid, status) values("  ;
			s+="'" + formId +"','','"+t.getTableName().toUpperCase()+"','decription of "+ t.getTableName().toLowerCase() + "','" + t.getTableName() + "','false','','active'";
			s+=");\n\n";
		}
		
		return s;
	}
	
	private String fileReader( String fnm ) {
		
		String status ="";
		 
		try {
		      File myObj = new File( fileInPath + fnm );
		      
		      Scanner myReader = new Scanner(myObj);
		      
		      Table tparent = new Table(); Table t = tparent; 
		      
		      while ( myReader.hasNextLine() ) {
		        
		    	  String data = myReader.nextLine();
		        
		    	  t = this.processALine( data, t );
		      }
		      
		      myReader.close();
		      
		      this.writeToFile( fnm, tparent );
		      
		      status = "extractDBStructure done!";
		      
		    } catch (FileNotFoundException e) {
		    	
		      System.out.println("[DataModelling.fileReader] FileNotFoundException: An error occurred.");
		      
		      e.printStackTrace();
		      
		    }
		 
		 return status;
	}

	
	private String appendToFile(String fnm, String s) {
		
		String status = "";
		
		if( fnm != null && s != null ) {
			
			 try {
				 
				 File myObj = new File( fileDownloadPath + fnm );
			      if ( myObj.createNewFile() ) {
			    	  System.out.println("File created: " + myObj.getName());
			      } else {
			    	  System.out.println("File already exists.");
			      }
			      FileWriter myWriter = new FileWriter( fileDownloadPath + fnm );
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

	private String generateFormColumnsSQL( Table t) {
		/*
		 * table: t_form_columns : id varchar(50), pid varchar(50), 
		 * label varchar(100), t_col_id varchar(100), tbl_col_nm varchar(50), 
		 * seqno int(3), def_val varchar(50), issql varchar(50), 
		 * is_attribute_list varchar(50), sqlid varchar(50), attribute_grp_id varchar(50), 
		 * nullable varchar(50), maxlength int(3), status varchar(50)
		 */
		String s = "";
		
		if(t != null) {
				
				Table tc = t;
				
				s += this.formColumnsSQL( tc );
				
				while( tc.getNext() != null ) {
					
					tc = tc.getNext();
					
					s += this.formColumnsSQL( tc );
					
				}
			}
		
		return s;
	}
	
	private String formColumnsSQL( Table t) {
		String s = "";
		if( t != null ) {
			String cols = t.getCleanColumns( t.getColumns() + "," + t.getMandatoryColumns() );
			String[] arrs = cols.split(",");
			if (arrs != null && arrs.length > 0) {
				for ( int i=0; i< arrs.length; i++ ) {
					s+="insert into t_form_columns(id, pid, label, t_col_id, tbl_col_nm, seqno) values(";
					s+="'" + Utilities.getRandomText(50) + "','" + t.getFormId() + "','" + arrs[i].trim() + "','tcolid','"+arrs[i].trim()+"','" + (i + 1) + "'";
					s+=");\n\n";
				}
			}
		}
		return s;
	}

	private String writeToFile(String fnm, Table t) {
		
		String status = "";
		
		if( t != null) {
			
			 try {
				 
			      File myObj = new File( fileDownloadPath + fnm );
			      
			      if ( myObj.createNewFile() ) {
			    	  System.out.println("File created: " + myObj.getName());
			      } else {
			    	  System.out.println("File already exists.");
			      }
			    
			      FileWriter myWriter = new FileWriter( fileDownloadPath + fnm );
			      
			      Table temp = t;
			      
					while( temp != null && temp.getTable() != null ){
					
						String s = temp.getTableInfo();

						myWriter.write( s + "\n\n" );

						temp = temp.getNext();
					}
				
					myWriter.write( this.genformsSQL( t ) );
					myWriter.write( generateFormColumnsSQL(t) );
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

	private Table processALine( String s, Table t) {
		
		int idx = 0;
		
		if( s != null && !s.trim().equalsIgnoreCase("") && (idx = s.indexOf(':')) > 1 ) {
			
			String[] arrs = s.split(":");
			
			switch ( arrs[0].toLowerCase() ) {
				case "table":
				{
					t = t.addTable( new Table().setTableName( arrs[1] ).setColumns( arrs[2] ) );
				}
					break;
				case "record":
				{
					t.setRecord( new Record().setLine( arrs[1].trim().toUpperCase() ) );
				}
					break;
				case "mandatory-columns":
				{
					t.setMandatoryColumns( arrs[1].trim().toUpperCase() );
				}
					break;
			}
		}
		return t;
	}
}

class Table {
	private String table;
	private String columns;
	private Record record;
	private String mandatoryColumns;
	private Table next;
	private String formId;
	
	
	public String getFormId() { return formId; }
	public void setFormId(String formId) { this.formId = formId; }

	public String getTable() {
		String s = "create table " + this.table + "(";
		s += this.columns +","+this.mandatoryColumns;
		s += ");\n\n";
		return s;
	}
	
	public String getTableName() {
		return this.table;
	}
	public Table setTableName( String table ) { 
			this.table = table;
		return this;
	}
	
	public Record getRecord() {  return record; }
	public Record setRecord(Record record) {
		if( this.record != null ) {
			this.record.addRecord( record );
		} else {
			this.record = record;
		}
		return this.record;
	}
	public String getMandatoryColumns() { return mandatoryColumns; }
	public void setMandatoryColumns(String mandatoryColumns) { this.mandatoryColumns = mandatoryColumns; }
	public Table getNext() { return next; }
	public Table setNext(Table next) { this.next = next; return this.next;}
	public String getColumns() { 
		return this.columns;
	}
	public Table setColumns(String columns) { this.columns = columns; return this;}
	
	public Table addTable(Table t) {
		
		t.setMandatoryColumns( this.getMandatoryColumns() );
		
		if ( this.getTableName() == null || this.getTableName().trim().equals("") ) {
			
			this.table = t.getTableName();
			
			this.columns = t.getColumns();
			
			return this;
			
		} else {
			
			this.next = t;
			
			return t;
		}
	}
	
	public String getTableInfo () {
		String s = "";
		if( this.getTableName() != null && !getTableName().trim().equals("") && this.mandatoryColumns!=null && !this.mandatoryColumns.trim().equals("") ) {
			s += this.getTable();
			//s += ( this.getRecord() != null ? this.getRecord().getRecords() : "" );
			String tname = this.getTableName();
			String cols = getCleanColumns( this.columns + "," + this.mandatoryColumns );
			s += ( this.getRecord() != null ? this.getRecord().getRecordsInsertSQL(tname, cols) : "" );
		}
		return s;
	}
	
	public String getCleanColumns(String cols) {
		if(cols!=null) {
			String[] c = cols.split(","); cols = "";
			for( int i=0; i < c.length; i++ ) {
				String[] cc = c[i].trim().split(" ");
				cols += cc[0].trim() + ",";
			}
			return cols.substring(0, cols.length() - 1);
		}
		return cols;
	}
}

class Record {
	private String line;
	private Record next;
	
	public String getLine() { 
		String[] arrs = line.split(";");
		if ( arrs.length > 1 ) {
			line = "'" + Utilities.getRandomText(50) + "', '', "; //add uniqueID, and PID
			for( int i=0; i<arrs.length; i++ ) {
				line += "'" + arrs[i].trim() + "',";
			}
			return (line.substring( 0, ( line.length() - 1 ) ) );
		}
		return line;
	}
	
	public String getInsertSQL(String tname, String cols) {
		return "insert into " + tname + "(" +cols+") values("+ this.getLine()+",'Batch', CURRENT_TIMESTAMP,'',CURRENT_TIMESTAMP,'');";
	}

	public Record setLine(String line) { this.line = line; return this;}
	public Record getNext() { return next; }
	public void setNext(Record next) { this.next = next; }
	public Record addRecord( Record r ) {
		if( this.line == null || this.line.trim().equals("") ) {
			this.line = r.getLine();
		} else if( this.next != null ){
			
			Record t = this.next;
			
			while ( t.getNext() != null) {
				t = t.getNext();
			};
			
			t.setNext( r );
			
		} else if( this.next == null ){
			this.next = r;
		}
		return this;
	}
	
	public String getRecordsInsertSQL(String tname, String cols ) {
		String s = "";

		s += ( this.getInsertSQL( tname, cols ) != null ? this.getInsertSQL( tname, cols ) : "" ); s +="\n\n";

		Record t = this.getNext() ;
		
		while ( t != null ) {
			s += t.getInsertSQL( tname, cols ) + "\n\n";
			t = t.getNext();
		}
		return s;
	}
	
	public String getRecords( ) {
		String s = "";

		s += ( this.getLine() != null ? this.getLine() : "" ); s +="\n\n";

		Record t = this;
		
		while ( t.getNext() != null ) {
			t = t.getNext(); s += t.getLine() + "\n\n";
		}
		return s;
	}
}

class Column{
	private String name;
	private String type;
	private Column next;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Column getNext() {
		return next;
	}
	public void setNext(Column next) {
		this.next = next;
	}
}