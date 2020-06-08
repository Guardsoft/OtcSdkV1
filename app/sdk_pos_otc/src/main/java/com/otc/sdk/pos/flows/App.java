package com.otc.sdk.pos.flows;


import com.otc.sdk.pos.flows.util.UIHelper;

import java.util.Map;

/**
 * Created by foxit on 12/02/18.
 */
public class App {

    //request codes
    public static final int AUTHORIZE = 5000;
    public static final int INITIALIZE = 4000;
    public static final String KEY_SUCCESS = "keySuccess";
    public static final String KEY_ERROR = "keyError";

    public static Channel channel;
    public static boolean countable = true;
    public static String merchantID = "";
    public static String cardNumber = "";
    public static String purchaseNumber = "";
    public static String productID = "";
    public static String username = "";
    public static String password = "";
    public static Double amount;
    public static boolean showAmount;
    public static String endpoint = "";
    public static String tenant = "";
    public static boolean initializeKeys = false;
    public static String serialNumberTest = "";


    public static final String OTC_CHANNEL = "channel";
    public static final String OTC_COUNTABLE = "countable";
    public static final String OTC_MERCHANT = "merchant";
    public static final String OTC_CARD_NUMBER = "cardNumber";
    public static final String OTC_PURCHASE_NUMBER = "purchaseNumber";
    public static final String OTC_PRODUCT_ID = "productID";
    public static final String OTC_USERNAME = "username";
    public static final String OTC_PASSWORD = "password";
    public static final String OTC_AMOUNT = "amount";
    public static final String OTC_SHOW_AMOUNT = "showAmount";
    public static final String OTC_ENDPOINT_URL = "endpointURL";
    public static final String OTC_TENANT = "tenant";
    public static final String OTC_INITIALIZE_KEYS = "initializeKeys";
    public static final String OTC_SERIAL_NUMBER_TEST = "serialNumberTest";
    public static int keyData = -1;
    public static int keyPin = -1;
    public static int keyMac = -1;
    public static int keyTmk = 1; //default

//    public static void authorization(Activity activity, Map<String, Object> data, VisaNetViewAuthorizationCustom custom) throws Exception {
//        parseData(data);
//        VisaNetAuthorizationActivity.startVisaNet(activity, amount, custom);
//    }
//
//    public static void authorization(Activity activity, Map<String, Object> data) throws Exception {
//        parseData(data);
//        VisaNetAuthorizationActivity.startVisaNet(activity, amount);
//    }

    private static void parseData(Map<String, Object> data) throws Exception {

        if (data != null) {

            if (data.containsKey(OTC_CHANNEL)) {
                channel = Channel.parse(data.get(OTC_CHANNEL).toString());
            } else {
                channel = null;
            }

            if (data.containsKey(OTC_COUNTABLE)) {
                countable = (boolean) data.get(OTC_COUNTABLE);
            } else {
                countable = true;
            }

            if (data.containsKey(OTC_MERCHANT)) {
                merchantID = (String) data.get(OTC_MERCHANT);

                if (merchantID.length() >= 40) {
                    throw new Exception("Longitud máxima de 40 caracteres");
                }
            } else {
                merchantID = "";
            }

            if (data.containsKey(OTC_MERCHANT)) {
                merchantID = (String) data.get(OTC_MERCHANT);
            } else {
                merchantID = "";
            }

            if (data.containsKey(OTC_CARD_NUMBER)) {
                cardNumber = (String) data.get(OTC_CARD_NUMBER);

                if (cardNumber.length() >= 16 || !UIHelper.luhnCheck(cardNumber)) {
                    throw new Exception("Número de Tarjeta Inválido");
                }
            } else {
                cardNumber = "";
            }

            if (data.containsKey(OTC_PURCHASE_NUMBER)) {
                purchaseNumber = (String) data.get(OTC_PURCHASE_NUMBER);
            } else {
                purchaseNumber = "";
            }

            if (data.containsKey(OTC_PRODUCT_ID)) {
                productID = (String) data.get(OTC_PRODUCT_ID);

                if (merchantID.length() >= 40) {
                    throw new Exception("Longitud máxima de 40 caracteres");
                }

            } else {
                productID = "";
            }

            if (data.containsKey(OTC_USERNAME)) {
                username = (String) data.get(OTC_USERNAME);
            } else {
                username = "";
            }

            if (data.containsKey(OTC_PASSWORD)) {
                password = (String) data.get(OTC_PASSWORD);
            } else {
                password = "";
            }


            if (data.containsKey(OTC_AMOUNT)) {
                try {
                    amount = Double.parseDouble(String.valueOf(data.get(OTC_AMOUNT)));
                } catch (Exception e) {
                    amount = 0.0;
                    e.printStackTrace();
                }
            } else {
                amount = 0.0;
            }

            if (data.containsKey(OTC_SHOW_AMOUNT)) {
                showAmount = (boolean) data.get(OTC_SHOW_AMOUNT);
            } else {
                showAmount = true;
            }

            if (data.containsKey(OTC_ENDPOINT_URL)) {
                endpoint = (String) data.get(OTC_ENDPOINT_URL);
            } else {
                endpoint = "";
            }

            if (data.containsKey(OTC_TENANT)) {
                tenant = (String) data.get(OTC_TENANT);
            } else {
                tenant = "";
            }

            if (data.containsKey(OTC_INITIALIZE_KEYS)) {
                initializeKeys = (boolean) data.get(OTC_INITIALIZE_KEYS);
            } else {
                initializeKeys = false;
            }


        } else {
            throw new Exception("Se ha producido una excepción con el mensaje: ");
        }
    }

    public enum Channel {

        MOBILE("mobile"),
        CALLCENTER("callcenter"),
        PASAPORTE("2");

        private final String value;

        Channel(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static Channel parse(String s) {
            if (s != null) {
                s = s.trim().toLowerCase();

                if (s.equals(MOBILE.toString().toLowerCase())) {
                    return MOBILE;
                } else if (s.equals(CALLCENTER.toString().toLowerCase())) {
                    return CALLCENTER;
                } else if (s.equals(PASAPORTE.toString().toLowerCase())) {
                    return PASAPORTE;
                }
            }
            return null;
        }
    }

    public enum DocumentType {

        DNI("0"),
        CARNET_EXTRANJERIA("1"),
        PASAPORTE("2"),
        DEFAULT("Código invalido");

        private final String value;

        DocumentType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static DocumentType parse(String s) {
            if (s != null) {
                s = s.trim().toLowerCase();

                if (s.equals(DNI.toString().toLowerCase())) {
                    return DNI;
                } else if (s.equals(CARNET_EXTRANJERIA.toString().toLowerCase())) {
                    return CARNET_EXTRANJERIA;
                } else if (s.equals(PASAPORTE.toString().toLowerCase())) {
                    return PASAPORTE;
                }else{
                    return DEFAULT;
                }
            }
            return null;
        }
    }

    public enum Currency {
        PEN("PEN"),
        USD("USD");

        private final String value;

        Currency(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static Currency parse(String s) {
            if (s != null) {
                s = s.trim().toLowerCase();

                if (s.equals(PEN.toString().toLowerCase())) {
                    return PEN;
                } else if (s.equals(USD.toString().toLowerCase())) {
                    return USD;
                }
            }
            return null;
        }
    }
}
