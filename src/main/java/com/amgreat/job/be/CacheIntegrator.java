package com.amgreat.job.be;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.ListVO;
import com.amgreat.vo.PageVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.RequestVO;
import com.amgreat.vo.TemplateVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Component
public class CacheIntegrator implements CacheIntegratorIntf {
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Value("${cache.uri}")
	private String cacheUri;
	
	@Value("${cache.form.uri}")
	private String cacheFormUri;
	
	@Value("${cache.page.uri}")
	private String cachePageUri;
	
	@Value("${cache.template.uri}")
	private String cacheTemplateUri;
	
	@Value("${cache.html.uri}")
	private String cacheHtmlUri;
	
	public AttributeVO recordVO2AttributeVO( RequestVO request ) {
		
		AttributeVO rq = new AttributeVO();
		
		if( request.getPageId() != null && !request.getPageId().trim().equals("") ) {
			
		}
		return rq;
	}
	
	public AttributeVO callCache( AttributeVO req ) {
		AttributeVO rp = null;
		try {
			if ( req != null ) {
				rp = restTemplate.postForObject( cacheUri, req, AttributeVO.class );
            }
		} catch (Exception e) {
			System.out.println("CacheIntegrator.callData.AttributeVO: " + e.getMessage());
			rp = null;
		}
		return rp;
	}
	
	@Override
	public RecordVO callCache(RequestVO request) {
		
		AttributeVO req = recordVO2AttributeVO( request );  RecordVO rp = null;
		
		try {
			if ( req != null ) {
				rp = restTemplate.postForObject( cacheUri, req, RecordVO.class );
            }
		} catch (Exception e) {
			System.out.println("CacheIntegrator.callData.RequestVO: " + e.getMessage());
		}
		return rp;
	}

	@Override
	public ListVO callCache( ListVO request ) {
		ListVO rp = null;
		try {
			if ( request != null ) {
				rp = restTemplate.postForObject( cacheFormUri, request, ListVO.class );
            }
		} catch (Exception e) {
			System.out.println("CacheIntegrator.callData.ListVO: " + e.getMessage());
		}
		return rp;
	}
	
	@Override
	public PageVO callCache( PageVO request ) {
		PageVO rp = null;
		try {
			if ( request != null ) {
				rp = restTemplate.postForObject( cachePageUri, request, PageVO.class );
            }
		} catch (Exception e) {
			System.out.println("CacheIntegrator.callData.PageVO: " + e.getMessage());
		}
		return rp;
	}

	@Override
	public PageVO callCache( PageVO request, String par) {
		PageVO rp = null;
		try {
			if ( request != null ) {
				System.out.println("callCache(pageVO, par) " + cacheHtmlUri );
				rp = restTemplate.postForObject( cacheHtmlUri, request, PageVO.class );
            }
		} catch (Exception e) {
			System.out.println("CacheIntegrator.callData.PageVO: " + e.getMessage());
		}
		return rp;
	}
	
	@Override
	public TemplateVO callCache(TemplateVO request) {
		TemplateVO rp = null;
		try {
			if ( request != null ) {
				rp = restTemplate.postForObject( cacheTemplateUri, request, TemplateVO.class );
            }
		} catch (Exception e) {
			System.out.println("CacheIntegrator.callData.TemplateVO: " + e.getMessage());
		}
		return rp;
	}

	@Override
	public RecordVO load2Cache(RequestVO request) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
