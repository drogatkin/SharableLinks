package rogatkin.mobile.app.mylinks.model;

import rogatkin.mobile.app.mylinks.R;
import rogatkin.mobile.data.pertusin.PresentA;
import rogatkin.mobile.data.pertusin.StoreA;

@StoreA(sql="group_tb")
public class group extends Id {
    @PresentA(required=true, viewFieldId= R.id.ed_groupname, listViewFieldId = R.id.tv_name)
    @StoreA(index=true)
    public String name;
}
