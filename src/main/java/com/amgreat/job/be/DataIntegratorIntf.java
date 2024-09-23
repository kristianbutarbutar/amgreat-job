package com.amgreat.job.be;

import org.springframework.web.bind.annotation.RequestBody;

import com.amgreat.vo.AttributeVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.RequestVO;

public interface DataIntegratorIntf {
	public RecordVO callData( RequestVO request );
	public RecordVO transformData( RequestVO request );
	public AttributeVO recordVO2AttributeVO( RequestVO request );
	public RecordVO callDataAdapter( RequestVO request );
}
