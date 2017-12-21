package com.omneagate.printer;

public class PrinterFactory {

    static public Printer getPrinter(PrinterTypes ptype) {

        switch (ptype) {

            case BLUETOOTH:
                return new BlueToothPrinter();
            default:
                return new BlueToothPrinter();
        }
    }

}
