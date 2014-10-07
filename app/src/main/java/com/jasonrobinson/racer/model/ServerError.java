package com.jasonrobinson.racer.model;

public class ServerError {

    private GGGError error;

    private ServerError() {

    }

    public GGGError getError() {

        return error;
    }

    public static class GGGError {

        private int code;
        private String message;

        private GGGError() {

        }

        public int getCode() {

            return code;
        }

        public String getMessage() {

            return message;
        }
    }
}
