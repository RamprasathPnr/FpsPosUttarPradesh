package com.omneagate.printer;


import android.app.Activity;
import android.util.Log;

import lombok.Data;

@Data
public class PrintManager {

    static private PrintManager instance;

    private Activity activiti = null;

    private PrinterTypes printerType;

    private Printer printer;

    private PrinterLanguage prnlang;

    private boolean loadPRNFile = false;


    private PrintManager(Activity activiti) {
        // TODO : Need to set printer type from UI activity
        this.activiti = activiti;
        printerType = PrinterTypes.BLUETOOTH;
        prnlang = PrinterLanguage.CPCL;
        printer = PrinterFactory.getPrinter(printerType);
    }

    static public PrintManager getInstance(Activity activiti) {
        if (instance == null)
            instance = new PrintManager(activiti);
        return instance;
    }

    static public PrintManager getInstance() {
        return instance;
    }

    public void print(Activity activiti) {
        try {
            this.activiti = activiti;
            printer.print();
        } catch (Exception ex) {
            Log.e("Read Error", ex.toString(), ex);
        }
    }

    public void startDiscovery() {
        printer.print();
    }
}
