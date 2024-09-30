package com.amgreat.job.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amgreat.job.cache.LoadTemplateCache;
import com.amgreat.job.html.FormBuilderJob;
import com.amgreat.job.html.HtmlGenerators;
import com.amgreat.job.html.LoadForm2HtmlTemplate;

@RestController
public class CacheCtrl {
	@Autowired private LoadTemplateCache cache; 
	
	@Autowired private FormBuilderJob job;
	
	@Autowired private LoadForm2HtmlTemplate pages;
	
	@Autowired private HtmlGenerators html;
	
	@RequestMapping( "/amgreate/api/job/cache/template" )
	public boolean loadCache( ) {
		return cache.loadTemplate2Cache("all");
	}
	
	@RequestMapping( "/amgreate/api/job/cache/form" )
	public boolean loadFormCache( ) {
		return job.generateFormHtml();
	}
	
	@RequestMapping( "/amgreate/api/job/cache/pages" )
	public boolean loadPageCache( ) {
		return pages.loadFormPages("all");
	}
	
	@RequestMapping( "/amgreate/api/job/html" )
	public boolean loadHtml( ) {
		return html.generateHtml("all");
	}
}
