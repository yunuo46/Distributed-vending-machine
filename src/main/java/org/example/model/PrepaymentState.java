package org.example.model;

public class PrepaymentState {

    /**
     * Default constructor
     */
    public PrepaymentState() {
    }

    /**
     *
     */
    private int item_code;

    /**
     *
     */
    private int item_num;

    /**
     *
     */
    private String cert_code;

    /**
     * @param item_code
     * @param item_num
     * @param cert_code
     */
    public void storePrePayment(int item_code, int item_num, String cert_code) {
        // TODO implement here
    }

    /**
     * @param cert_code
     */
    public void checkCode(String cert_code) {
        // TODO implement here
    }

    /**
     * @param cert_code
     */
    public void disposeCode(String cert_code) {
        // TODO implement here
    }

}
