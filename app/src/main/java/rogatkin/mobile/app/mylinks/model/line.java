package rogatkin.mobile.app.mylinks.model;

import java.util.Date;

import rogatkin.mobile.app.mylinks.R;
import rogatkin.mobile.data.pertusin.PresentA;
import rogatkin.mobile.data.pertusin.StoreA;

import static rogatkin.mobile.data.pertusin.PresentA.FieldType.Hidden;

public class line extends Id {
    @StoreA(index=true)
    @PresentA(required=true, viewFieldId= R.id.ed_linkname, listViewFieldId = R.id.tv_name)
    public String name;
    @StoreA()
    @PresentA(required=true, viewFieldId= R.id.ed_url)
    public String url;
    @StoreA(index=true)
    public Date accessed;
    @StoreA()
    @PresentA(viewFieldId= R.id.ed_description)
    public String description;
    @StoreA(index = true)
    @PresentA(presentType = Hidden)
    public long group_id;

    public line clear() {
        name = "";
        url = "";
        description = "";
        group_id = 0;
        return  this;
    }
}
