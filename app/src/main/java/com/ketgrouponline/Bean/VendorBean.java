package com.ketgrouponline.Bean;

public class VendorBean {
    private String shop_name,
            id,
            opening_time,
            closing_time,
    todays_result;

    public VendorBean() {
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpening_time() {
        return opening_time;
    }

    public void setOpening_time(String opening_time) {
        this.opening_time = opening_time;
    }

    public String getClosing_time() {
        return closing_time;
    }

    public void setClosing_time(String closing_time) {
        this.closing_time = closing_time;
    }

    public String getTodays_result() {
        return todays_result;
    }

    public void setTodays_result(String todays_result) {
        this.todays_result = todays_result;
    }
}
