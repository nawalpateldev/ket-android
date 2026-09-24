package com.ketgrouponline.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterModel{


        @SerializedName("return")
        @Expose
        private Boolean _return;
        @SerializedName("message")
        @Expose
        private String message;

        public Boolean getReturn() {
            return _return;
        }

        public void setReturn(Boolean _return) {
            this._return = _return;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


}


