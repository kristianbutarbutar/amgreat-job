package com.amgreat.job.be;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter;
import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.RequestVO;

@Component
public class DataIntegrator implements DataIntegratorIntf{
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Value("${data.uri}")
	private String dataUri;
	
	public AttributeVO recordVO2AttributeVO( RequestVO request ) {
		
		AttributeVO rq = new AttributeVO();
		
		if( request.getPageId() != null && !request.getPageId().trim().equals("") ) {
			
		}
		return rq;
	}
	
	@Override
	public RecordVO transformData(RequestVO request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordVO callDataAdapter(RequestVO request) {
		// TODO Auto-generated method stub
		return null;
	}

	public RecordVO callData( RequestVO request ) {
		
		AttributeVO req = recordVO2AttributeVO( request );  RecordVO rp = null;
		
		try {
			if ( req != null ) {
						
				rp = restTemplate.postForObject( dataUri, req, RecordVO.class );
				
				if ( rp != null ) {
					
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
					String json = ow.writeValueAsString( rp );
					
	                //System.out.println("JSON: " + json );
				}
            }
		} catch (Exception e) {
			System.out.println("DataIntegrator.callData.RequestVO: " + e.getMessage());
		}
		return rp;
	}
	
	public RecordVO callData( AttributeVO req ) {
		
		RecordVO rp = null;
		
		try {
			if ( req != null ) {
				rp = restTemplate.postForObject( dataUri, req, RecordVO.class );
				
				//if ( rp != null ) {
				//	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				//	String json = ow.writeValueAsString( rp );
	                // System.out.println("JSON: " + json );
				//}
            }
		} catch (Exception e) {
			System.out.println("DataIntegrator.callData.AttributeVO: " + e.getMessage());
		}
		return rp;
	}
}
