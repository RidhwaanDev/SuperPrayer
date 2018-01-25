package com.example.home.superprayer.Model;

import java.io.Serializable;

/**
 * Created by Home on 1/24/2018.
 */

public class RequestParam implements Serializable {

    private boolean isNotifcationsOn;
        private int method;
        private int school;

        public boolean isNotifcationsOn() {
            return isNotifcationsOn;
        }

        public int getMethod() {
            return method;
        }

        public int getSchool() {
            return school;
        }

}
