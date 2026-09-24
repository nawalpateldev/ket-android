package com.ketgrouponline.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterError {


        @SerializedName("return")
        @Expose
        private Boolean _return;
        @SerializedName("errors")
        @Expose
        private Errors errors;

        public Boolean getReturn() {
            return _return;
        }

        public void setReturn(Boolean _return) {
            this._return = _return;
        }

        public Errors getErrors() {
            return errors;
        }

        public void setErrors(Errors errors) {
            this.errors = errors;
        }



    public class Errors {

        @SerializedName("message")
        @Expose
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
