package com.amgreat.job.be;

import com.amgreat.vo.ListVO;
import com.amgreat.vo.PageVO;
import com.amgreat.vo.RecordVO;
import com.amgreat.vo.RequestVO;

public interface CacheIntegratorIntf {
	public RecordVO callCache( RequestVO request );
	public RecordVO load2Cache( RequestVO request );
	public ListVO callCache( ListVO request );
	public PageVO callCache( PageVO request );
}
