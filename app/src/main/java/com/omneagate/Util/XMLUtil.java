/**
 *
 */
package com.omneagate.Util;

import android.util.Log;

import com.omneagate.DTO.AuthenticateMember;
import com.omneagate.DTO.CommBDetails;
import com.omneagate.DTO.DealerAuthResponse;
import com.omneagate.DTO.FPSAllotment;
import com.omneagate.DTO.FPSCardPosition;
import com.omneagate.DTO.FPSCurrentClosingBalance;
import com.omneagate.DTO.FPSDealer;
import com.omneagate.DTO.FPSDealerDetails;
import com.omneagate.DTO.FPSIssueDaywiseReport;
import com.omneagate.DTO.FPSIssueReport;
import com.omneagate.DTO.FPSLogout;
import com.omneagate.DTO.FPSPortablityDayWiseReport;
import com.omneagate.DTO.FPSPortablityMonthWiseReport;
import com.omneagate.DTO.FPSROAllocation;
import com.omneagate.DTO.FPSRationCard;
import com.omneagate.DTO.FPSRationCardDetails;
import com.omneagate.DTO.FPSReceiptReport;
import com.omneagate.DTO.GeneralResponse;
import com.omneagate.DTO.Product;
import com.omneagate.DTO.RCAuthResponse;
import com.omneagate.DTO.RCLastTransaction;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.exception.FPSException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author ftuser
 */
public class XMLUtil {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String AFSC_CARD_TYPE = "4";
    private static final String FSC_CARD_TYPE = "5";
    private static final String AAP_CARD_TYPE = "9";

    // public static final String DISTRICT_CODE = "538";
    public static final String PASSWORD = "1159abbb8b6c0b8a8964210af6954b17";
    public static final String RC_MEMBER_ID = "3";

    public static final String PREFIX_RCNO = "36";
    public static final String POSTFIX_RCNO = "0";

    public static final int autologOutTime=1800000;

    private static final int CONNECTION_TIME=30000;

    public static final double NIC_BUILD_NUMBER=1.9;

    /**
     *
     */
    public XMLUtil() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param inputXML
     * @return
     * @throws Exception
     */
    private static Document getDocument(String inputXML) throws Exception {
        Document doc = null;

        final Pattern pattern = Pattern.compile("<return>(.+?)</return>");
        final Matcher matcher = pattern.matcher(inputXML);
        matcher.find();

        String rtnXML = matcher.group(1);
        // System.out.println(rtnXML);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        doc = dBuilder.parse(new InputSource(new java.io.StringReader(rtnXML)));
        Element element = doc.getDocumentElement();
        element.normalize();

        return doc;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSDealerDetails getFPDDetails(Map<String, Object> inputMap) throws Exception {

        info("getFPDDetailsDetails-1");

        FPSDealerDetails fpsDealerDetails = new FPSDealerDetails();

		/*
         * Build request XML
		 */
        String reqXML = getFPSDealerDetailsReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

        // System.out.println(respXML);

        Document doc = getDocument(respXML);

        //
        NodeList nList = doc.getElementsByTagName("fpsDealerDetailsResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String currMonth = getValue("currMonth", element1);
                String currYear = getValue("currYear", element1);
                String currDateTime = getValue("currdatetime", element1);
                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                String transactionID = getValue("transactionID", element1);
                transactionID = transactionID == null ? "" : transactionID.trim();

                String distCode = getValue("distCode", element1);
                distCode = distCode == null ? "" : distCode.trim();

                fpsDealerDetails.setCurrMonth(currMonth);
                fpsDealerDetails.setCurrYear(currYear);
                fpsDealerDetails.setCurrDateTime(currDateTime);
                fpsDealerDetails.setRespMessage(respMessage);
                fpsDealerDetails.setRespMsgCode(respMsgCode);
                fpsDealerDetails.setTransactionID(transactionID);
                fpsDealerDetails.setDistCode(distCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList dealerDetailNL = element1.getElementsByTagName("fpsDealerDetailList");

                List<FPSDealer> fpsDealerList = new ArrayList<FPSDealer>();
                for (int j = 0; j < dealerDetailNL.getLength(); j++) {
                    Node dealerNode = dealerDetailNL.item(j);
                    if (dealerNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element dealerEL = (Element) dealerNode;
                        // System.out.println("bestFinger1 - " +
                        // getValue("bestFinger1", dealerEL));
                        FPSDealer fpsDealer = new FPSDealer();
                        fpsDealer.setBestFinger1(getValue("bestFinger1", dealerEL));
                        fpsDealer.setBestFinger2(getValue("bestFinger2", dealerEL));
                        fpsDealer.setBestFinger3(getValue("bestFinger3", dealerEL));
                        // dealerOrNomine
                        fpsDealer.setDealerOrNomine(getValue("dealerOrNomine", dealerEL));
                        // dealerOrNomineUidNo
                        fpsDealer.setDealerOrNomineUidNo(getValue("dealerOrNomineUidNo", dealerEL));
                        // dealerType
                        fpsDealer.setDealerType(getValue("dealerType", dealerEL));
                        // ePoSVersion
                        fpsDealer.setePoSVersion(getValue("ePoSVersion", dealerEL));
                        fpsDealerList.add(fpsDealer);

                    }
                }

                fpsDealerDetails.setFpsDealerList(fpsDealerList);

            }

        }

        return fpsDealerDetails;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSCardPosition getFPSCardPosition(Map<String, Object> inputMap) throws Exception {
        FPSCardPosition fpsCardPosition = new FPSCardPosition();

		/*
		 * Build request XML
		 */
        String reqXML = getFPSCardsPositionRptReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsCardsPositionRptResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsCardPosition.setRespMessage(respMessage);
                fpsCardPosition.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList cardPositionNL = element1.getElementsByTagName("cardpositionrpt");

                for (int j = 0; j < cardPositionNL.getLength(); j++) {
                    Node cardPositionNode = cardPositionNL.item(j);
                    if (cardPositionNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cardPositionNodeEL = (Element) cardPositionNode;

                        fpsCardPosition.setRcAap(getValue("rcAap", cardPositionNodeEL));
                        fpsCardPosition.setRcAapUnits(getValue("rcAapUnits", cardPositionNodeEL));
                        fpsCardPosition.setRcAfsc(getValue("rcAfsc", cardPositionNodeEL));
                        fpsCardPosition.setRcAfscUnits(getValue("rcAfscUnits", cardPositionNodeEL));
                        fpsCardPosition.setRcFsc(getValue("rcFsc", cardPositionNodeEL));
                        fpsCardPosition.setRcFscUnits(getValue("rcFscUnits", cardPositionNodeEL));

                    }
                }

            }

        }

        return fpsCardPosition;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSCurrentClosingBalance getFPSCurrentClosingBalance(Map<String, Object> inputMap) throws Exception {
        FPSCurrentClosingBalance fpsCurrentClosingBalance = new FPSCurrentClosingBalance();

        List<Product> productList = new ArrayList<Product>();

		/*
		 * Build request XML
		 */
        String reqXML = getFPSCurrentClosingBalanceRepReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsDealerClosingBalanceResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsCurrentClosingBalance.setRespMessage(respMessage);
                fpsCurrentClosingBalance.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList closingBalNL = element1.getElementsByTagName("fpsclsBalance");

                for (int j = 0; j < closingBalNL.getLength(); j++) {
                    Node closingBalNode = closingBalNL.item(j);
                    if (closingBalNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element closingBalNodeEL = (Element) closingBalNode;

                        NodeList closingBalChldNL = closingBalNodeEL.getChildNodes();
                        for (int k = 0; k < closingBalChldNL.getLength(); k++) {
                            Node closingBalChldNode = closingBalChldNL.item(k);
                            if (closingBalChldNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element closingBalChldNodeEL = (Element) closingBalChldNode;
                                String tagName = closingBalChldNodeEL.getTagName();
                                // info("tagName: " + tagName);
                                String productCode = ProductMap.productKeyMap.get(tagName);
                                // info("productCode: " + productCode);
                                Product product = ProductMap.getProductByCode(productCode);
                                String tagValue = closingBalChldNodeEL.getTextContent();
                                if (product != null) {
                                    product.setClosingBalance(AppUtil.stringToDouble(tagValue, 0D));

                                    productList.add(product);
                                }

                            }
                        }

                    }
                }

            }

        }
        fpsCurrentClosingBalance.setProductList(productList);
        return fpsCurrentClosingBalance;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSCurrentClosingBalance getFPSMonthlyClosingBalance(Map<String, Object> inputMap) throws Exception {
        FPSCurrentClosingBalance fpsCurrentClosingBalance = new FPSCurrentClosingBalance();

        List<Product> productList = new ArrayList<Product>();

		/*
		 * Build request XML
		 */
        String reqXML = getFPSMonthlyClosingBalanceRepReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsDealerMonthlyClosingBalanceResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsCurrentClosingBalance.setRespMessage(respMessage);
                fpsCurrentClosingBalance.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

               // NodeList closingBalNL = element1.getElementsByTagName("fpsclsBalance");
                NodeList closingBalNL = element1.getElementsByTagName("fpsmonthlyclsBalance");

                for (int j = 0; j < closingBalNL.getLength(); j++) {
                    Node closingBalNode = closingBalNL.item(j);
                    if (closingBalNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element closingBalNodeEL = (Element) closingBalNode;

                        NodeList closingBalChldNL = closingBalNodeEL.getChildNodes();
                        for (int k = 0; k < closingBalChldNL.getLength(); k++) {
                            Node closingBalChldNode = closingBalChldNL.item(k);
                            if (closingBalChldNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element closingBalChldNodeEL = (Element) closingBalChldNode;
                                String tagName = closingBalChldNodeEL.getTagName();
                                // info("tagName: " + tagName);
                                String productCode = ProductMap.productKeyMap.get(tagName);
                                // info("productCode: " + productCode);
                                Product product = ProductMap.getProductByCode(productCode);
                                String tagValue = closingBalChldNodeEL.getTextContent();
                                if (product != null) {
                                    product.setClosingBalance(AppUtil.stringToDouble(tagValue, 0D));

                                    productList.add(product);
                                }

                            }
                        }

                    }
                }

            }

        }
        fpsCurrentClosingBalance.setProductList(productList);
        return fpsCurrentClosingBalance;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSIssueReport getFPSIssuesReport(Map<String, Object> inputMap) throws Exception {
        FPSIssueReport fpsIssueReport = new FPSIssueReport();

        List<Product> productList = new ArrayList<Product>();

		/*
		 * Build request XML
		 */
        String reqXML = getFPSSalesRptReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

        // System.out.println(respXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsIssuesReportResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsIssueReport.setRespMessage(respMessage);
                fpsIssueReport.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList fpsIssuesNL = element1.getElementsByTagName("fpsissues");

                for (int j = 0; j < fpsIssuesNL.getLength(); j++) {
                    Node fpsIssuesNode = fpsIssuesNL.item(j);
                    if (fpsIssuesNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element fpsIssuesNodeEL = (Element) fpsIssuesNode;

                        NodeList fpsIssuesChldNL = fpsIssuesNodeEL.getChildNodes();
                        for (int k = 0; k < fpsIssuesChldNL.getLength(); k++) {
                            Node fpsIssuesChldNode = fpsIssuesChldNL.item(k);
                            if (fpsIssuesChldNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element fpsIssuesChldNodeEL = (Element) fpsIssuesChldNode;
                                String tagName = fpsIssuesChldNodeEL.getTagName();
                                String tagValue = fpsIssuesChldNodeEL.getTextContent();
                                // info("tagName: " + tagName);
                                String productCode = ProductMap.productIssueReportMap.get(tagName);
                                // info("productCode: " + productCode);
                                Product product = ProductMap.getProductByCode(productCode);
                                if (product != null) {
                                    product.setIssuedQuantity(AppUtil.stringToDouble(tagValue, 0D));

                                    productList.add(product);
                                }

                                if ("rcNo".equals(tagName)) {
                                    fpsIssueReport.setRcNo(tagValue);
                                } else if ("totalAmt".equals(tagName)) {
                                    fpsIssueReport.setTotalAmt(AppUtil.stringToDouble(tagValue, 0D));
                                } else if ("transRoNo".equals(tagName)) {
                                    fpsIssueReport.setTransRoNo(tagValue);
                                }
                            }
                        }

                    }
                }

            }

        }
        fpsIssueReport.setProductList(productList);
        return fpsIssueReport;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSReceiptReport getFPSReceiptReport(Map<String, Object> inputMap) throws Exception {
        FPSReceiptReport fpsReceiptReport = new FPSReceiptReport();

        List<Product> productList = new ArrayList<Product>();

		/*
		 * Build request XML
		 */
        String reqXML = getFPSReceiptsRptReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsreceiptsRptResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsReceiptReport.setRespMessage(respMessage);
                fpsReceiptReport.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }
              /*  String transactionID = getValue("transactionID", element1);
                transactionID = transactionID == null ? "" : transactionID.trim();
                fpsReceiptReport.setTransactionId(transactionID);*/




                NodeList fpsIssuesNL = element1.getElementsByTagName("fpsreceipt");

                for (int j = 0; j < fpsIssuesNL.getLength(); j++) {
                    Node fpsIssuesNode = fpsIssuesNL.item(j);
                    if (fpsIssuesNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element fpsIssuesNodeEL = (Element) fpsIssuesNode;

                        NodeList fpsIssuesChldNL = fpsIssuesNodeEL.getChildNodes();
                        for (int k = 0; k < fpsIssuesChldNL.getLength(); k++) {
                            Node fpsIssuesChldNode = fpsIssuesChldNL.item(k);
                            if (fpsIssuesChldNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element fpsIssuesChldNodeEL = (Element) fpsIssuesChldNode;
                                String tagName = fpsIssuesChldNodeEL.getTagName();
                                String tagValue = fpsIssuesChldNodeEL.getTextContent();
                                // info("tagName: " + tagName);
                                String productCode = ProductMap.productIssueReportMap.get(tagName);
                                // info("productCode: " + productCode);
                                Product product = ProductMap.getProductByCode(productCode);
                                if (product != null) {
                                    product.setReceivedQuantity(AppUtil.stringToDouble(tagValue, 0D));

                                    productList.add(product);
                                }

                                if ("transDate".equals(tagName)) {
                                    fpsReceiptReport.setTransDate(tagValue);
                                } else if ("totalAmt".equals(tagName)) {
                                    fpsReceiptReport.setTotalAmt(AppUtil.stringToDouble(tagValue, 0D));
                                } else if ("transRoNo".equals(tagName)) {
                                    fpsReceiptReport.setTransRoNo(tagValue);
                                }
                            }
                        }

                    }
                }

            }

        }
        fpsReceiptReport.setProductList(productList);
        return fpsReceiptReport;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static GeneralResponse updateMobileNumber(Map<String, Object> inputMap) throws Exception {
        GeneralResponse generalResponse = new GeneralResponse();

		/*
		 * Build request XML
		 */
        String reqXML = mobileUpdateReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("mobileUpdateResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                generalResponse.setRespMessage(respMessage);
                generalResponse.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

            }

        }

        return generalResponse;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSIssueDaywiseReport getFPSDaywiseIssueReport(Map<String, Object> inputMap) throws Exception {
        FPSIssueDaywiseReport fpsIssueDaywiseReport = new FPSIssueDaywiseReport();

        List<Product> productList = new ArrayList<Product>();

		/*
		 * Build request XML
		 */
        String reqXML = getFPSIssuesRptReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsIssuesDayWiseReportResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsIssueDaywiseReport.setRespMessage(respMessage);
                fpsIssueDaywiseReport.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList daywiseIssuesNL = element1.getElementsByTagName("issuesDaywise");

                for (int j = 0; j < daywiseIssuesNL.getLength(); j++) {
                    Node daywiseIssuesNode = daywiseIssuesNL.item(j);
                    if (daywiseIssuesNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element daywiseIssuesNodeEL = (Element) daywiseIssuesNode;

                        NodeList daywiseIssuesChldNL = daywiseIssuesNodeEL.getChildNodes();
                        for (int k = 0; k < daywiseIssuesChldNL.getLength(); k++) {
                            Node fpsIssuesChldNode = daywiseIssuesChldNL.item(k);
                            if (fpsIssuesChldNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element daywiseIssuesChldNodeEL = (Element) fpsIssuesChldNode;
                                String tagName = daywiseIssuesChldNodeEL.getTagName();
                                String tagValue = daywiseIssuesChldNodeEL.getTextContent();
                                // info("tagName: " + tagName);
                                String productCode = ProductMap.productIssueReportMap.get(tagName);
                                // info("productCode: " + productCode);
                                Product product = ProductMap.getProductByCode(productCode);
                                if (product != null) {
                                    product.setIssuedQuantity(AppUtil.stringToDouble(tagValue, 0D));
                                    productList.add(product);
                                }

                                if ("noOfTransNo".equals(tagName)) {
                                    fpsIssueDaywiseReport.setNoOfTransNo(tagValue);
                                } else if ("totalAmt".equals(tagName)) {
                                    fpsIssueDaywiseReport.setTotalAmt(AppUtil.stringToDouble(tagValue, 0D));
                                } else if ("transactionDate".equals(tagName)) {
                                    fpsIssueDaywiseReport.setTransactionDate(tagValue);
                                }
                            }
                        }

                    }
                }

            }

        }
        fpsIssueDaywiseReport.setProductList(productList);
        return fpsIssueDaywiseReport;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSLogout doFPSLogout(Map<String, Object> inputMap) throws Exception {
        FPSLogout fpsLogout = new FPSLogout();

		/*
		 * Build request XML
		 */
        String reqXML = doShopLogoutReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("shopLogout");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsLogout.setRespMessage(respMessage);
                fpsLogout.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

            }

        }
        return fpsLogout;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static AuthenticateMember authenticateMember(Map<String, Object> inputMap) throws Exception {
        AuthenticateMember authenticateMember = new AuthenticateMember();

		/*
		 * Build request XML
		 */
        String reqXML = postMemberBioAuthVerifyReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        // NodeList nList =
        // doc.getElementsByTagName("postMemberBioAuthVerifyResponse");

        NodeList nList = doc.getElementsByTagName("bioAuthResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respDescription = element1.getAttribute("respDescription");
                respDescription = respDescription == null ? "" : respDescription.trim();

                String respCode = element1.getAttribute("respCode");
                respCode = respCode == null ? "" : respCode.trim();

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? respDescription : respMessage.trim();

                error("respMessage :::::", respMessage);
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? respCode : respMsgCode.trim();

                authenticateMember.setRespMessage(respMessage);
                authenticateMember.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    FPSException fex = new FPSException();
                    fex.setMessage(respMessage);
                    throw fex;
                }

            }

        }
        return authenticateMember;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static GeneralResponse postUidBestFingers(Map<String, Object> inputMap) throws Exception {
        GeneralResponse generalResponse = new GeneralResponse();

		/*
		 * Build request XML
		 */
        String reqXML = postUidBestFingersReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("uidBestFingersResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                generalResponse.setRespMessage(respMessage);
                generalResponse.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

            }

        }
        return generalResponse;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSRationCardDetails getRationCardDetails(Map<String, Object> inputMap) throws Exception {
        FPSRationCardDetails fpsRationCardDetails = new FPSRationCardDetails();
		/*
		 * Build request XML
		 */
        String reqXML = getRationCardDetailsReq(inputMap);
		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);
		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        // NodeList nList = doc.getElementsByTagName("fpsDealerDetailList");

        NodeList nList = doc.getElementsByTagName("rationCardDetailsResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                String ePOSMessage = getValue("eposmessage", element1);
                ePOSMessage = ePOSMessage == null ? "" : ePOSMessage.trim();

                fpsRationCardDetails.setRespMessage(respMessage);
                fpsRationCardDetails.setRespMsgCode(respMsgCode);
                fpsRationCardDetails.setEposMessage(ePOSMessage);
                LoginData.getInstance().setEposMessage(""+ePOSMessage);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);

                }

                NodeList cardDetailNL = element1.getElementsByTagName("rcdetailsList");

                List<FPSRationCard> rationCardList = new ArrayList<FPSRationCard>();
                for (int j = 0; j < cardDetailNL.getLength(); j++) {
                    Node cardNode = cardDetailNL.item(j);
                    if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cardEL = (Element) cardNode;

                        NodeList bestFingerNL = cardEL.getElementsByTagName("bf");

                        FPSRationCard fpsRationCard = new FPSRationCard();

                        for (int k = 0; k < bestFingerNL.getLength(); k++) {
                            Node bfNode = bestFingerNL.item(k);
                            if (bfNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element bfEL = (Element) bfNode;
                                fpsRationCard.setBestFinger1(getValue("bf1", bfEL));
                                fpsRationCard.setBestFinger2(getValue("bf2", bfEL));
                                fpsRationCard.setBestFinger3(getValue("bf2", bfEL));
                            }
                        }

                        fpsRationCard.setErrorCode(getValue("errorCode", cardEL));
                        fpsRationCard.setMemberId(getValue("memberId", cardEL));
                        fpsRationCard.setMemberName(getValue("memberName", cardEL));
                        fpsRationCard.setRationCardNumber(getValue("rcNo", cardEL));
                        fpsRationCard.setStatus(getValue("status", cardEL));
                        fpsRationCard.setUidNo(getValue("uidNo", cardEL));

                        rationCardList.add(fpsRationCard);

                    }
                }

                fpsRationCardDetails.setRationCardList(rationCardList);

            }

        }

        return fpsRationCardDetails;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static DealerAuthResponse getDealerAuthResponse(Map<String, Object> inputMap) throws Exception {
        String rtnXML = null;

        DealerAuthResponse dealerAuthResponse = new DealerAuthResponse();
        List<Product> itemsCBList = new ArrayList<Product>();
        List<Product> itemsPriceList = new ArrayList<Product>();
        String reqXML = null;
        String respXML = null;
		/*
		 * Build request XML
		 */
        reqXML = getFPSDealerAuthenticationReq(inputMap);

        // reqXML = XMLUtilTest.readFileContent();
		/*
		 * Post and get response XML
		 */
        respXML = postRequest(reqXML);

        // respXML =
        // XMLUtilTest.readFileContent("E:/java/IDE/workspace1/TelunganaPDS/doc/testdata/dealerResp.xml");
		/*
		 * Take value in between <return> and </return>
		 */

        try {
            final Pattern pattern = Pattern.compile("<return>(.+?)</return>");
            final Matcher matcher = pattern.matcher(respXML);
            matcher.find();

            rtnXML = matcher.group(1);
            info("rtnXML " + rtnXML);
        } catch (Exception e) {
            error("Exception at getDealerAuthResponse(1) >>>>> " + e.toString());
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(new InputSource(new java.io.StringReader(rtnXML)));
        Element element = doc.getDocumentElement();
        element.normalize();

        NodeList dealRespNodeList = doc.getElementsByTagName("fpsDealerAuthentResponse");

        for (int i = 0; i < dealRespNodeList.getLength(); i++) {
            Node dealRespNode = dealRespNodeList.item(i);
            if (dealRespNode.getNodeType() == Node.ELEMENT_NODE) {
                Element dealRespEl = (Element) dealRespNode;

                String respMessage = getValue("respMessage", dealRespEl);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", dealRespEl);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                dealerAuthResponse.setRespMessage(respMessage);
                dealerAuthResponse.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList cbNodeList = dealRespEl.getElementsByTagName("closingBal");

                for (int j = 0; j < cbNodeList.getLength(); j++) {
                    Node cbNode = cbNodeList.item(i);
                    if (cbNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cbEL = (Element) cbNode;
                        NodeList cbChildList = cbEL.getChildNodes();
                        if (cbChildList != null && cbChildList.getLength() > 0) {
                            for (int k = 0; k < cbChildList.getLength(); k++) {
                                Node chNode = cbChildList.item(k);
                                if (chNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element chEl = (Element) chNode;
                                    String tagName = chEl.getTagName();
                                    info("TagName: " + tagName);

                                    String value = chEl.getTextContent();

                                    info("tagName " + tagName);
                                    info("tagValue " + value);

                                    if(tagName!=null && tagName.equalsIgnoreCase("weighingScale")){
                                        LoginData.getInstance().setWeighingScale(value);
                                    }

                                    value = value == null ? "0" : value.trim();
                                    Double closingBalance = AppUtil.stringToDouble(value, 0D);
                                    String prodcutCode = ProductMap.productKeyMap.get(tagName);
                                    Product product = ProductMap.getProductByCode(prodcutCode);
                                    if (product != null) {
                                        product.setTagName(tagName);
                                        product.setClosingBalance(closingBalance);
                                        itemsCBList.add(product);
                                    }

                                }
                            }
                        }
                    }

                }

                // -------------------------------------------------------------------

                NodeList priceNodeList = dealRespEl.getElementsByTagName("commPrice");

                for (int j = 0; j < priceNodeList.getLength(); j++) {
                    Node priceNode = priceNodeList.item(i);
                    if (priceNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element priceEL = (Element) priceNode;
                        NodeList priceChildList = priceEL.getChildNodes();
                        if (priceChildList != null && priceChildList.getLength() > 0) {
                            for (int k = 0; k < priceChildList.getLength(); k++) {
                                Node chNode = priceChildList.item(k);
                                if (chNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element chEl = (Element) chNode;
                                    String tagName = chEl.getTagName();
                                    info("TagName: " + tagName);

                                    String value = chEl.getTextContent();

                                    info("Price-tagName " + tagName);
                                    info("Price-tagValue " + value);
                                    value = value == null ? "0" : value.trim();
                                    Double productPrice = AppUtil.stringToDouble(value, 0D);
                                    String prodcutCode = ProductMap.productPriceMap.get(tagName);
                                    Product product = ProductMap.getProductByCode(prodcutCode);
                                    if (product != null) {
                                        product.setTagName(tagName);
                                        product.setProductPrice(productPrice);
                                        itemsPriceList.add(product);
                                    }

                                }
                            }
                        }
                    }

                }

            }
        }

        dealerAuthResponse.setItemsCBList(itemsCBList);
        dealerAuthResponse.setItemsPriceList(itemsPriceList);

        return dealerAuthResponse;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static RCAuthResponse getRCAuthResponse(Map<String, Object> inputMap) throws Exception {
        String rtnXML = null;

        RCAuthResponse rcAuthResponse = new RCAuthResponse();
        CommBDetails commBDetails = new CommBDetails();
        List<Product> itemsAllotedList = new ArrayList<Product>();
        List<Product> itemsPriceList = new ArrayList<Product>();
        String reqXML = null;
        String respXML = null;
		/*
		 * Build request XML
		 */
        reqXML = getRCAuthenticationReq(inputMap);

        // reqXML = XMLUtilTest.readFileContent();
		/*
		 * Post and get response XML
		 */
        respXML = postRequest(reqXML);

        // respXML =
        // XMLUtilTest.readFileContent("E:/java/IDE/workspace1/TelunganaPDS/doc/testdata/rcResp.xml");
		/*
		 * Take value in between <return> and </return>
		 */
        info("respXML -1 " + respXML);
        try {
            final Pattern pattern = Pattern.compile("<return>(.+?)</return>");
            final Matcher matcher = pattern.matcher(respXML);
            matcher.find();

            rtnXML = matcher.group(1);
            info("rtnXML -2 " + rtnXML);
        } catch (Exception e) {
            error("Exception at getRCAuthResponse(1) >>>>> " + e.toString());
        }

        if (rtnXML == null) {
            throw new FPSException("Invalid Response XML");
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(new InputSource(new java.io.StringReader(rtnXML)));
        Element element = doc.getDocumentElement();
        element.normalize();

        NodeList rcRespNodeList = doc.getElementsByTagName("rcAuthResponse");

        for (int i = 0; i < rcRespNodeList.getLength(); i++) {
            Node rcRespNode = rcRespNodeList.item(i);
            if (rcRespNode.getNodeType() == Node.ELEMENT_NODE) {
                Element rcRespEl = (Element) rcRespNode;

                String respMessage = getValue("respMessage", rcRespEl);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", rcRespEl);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                rcAuthResponse.setRespMessage(respMessage);
                rcAuthResponse.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList rcCallNodeList = rcRespEl.getElementsByTagName("rccalloc");

                for (int j = 0; j < rcCallNodeList.getLength(); j++) {
                    Node rcCallNode = rcCallNodeList.item(i);
                    if (rcCallNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element rcCallEL = (Element) rcCallNode;

                        NodeList commBDetailsNodeList = rcCallEL.getElementsByTagName("commBDetails");

                        for (int k = 0; k < commBDetailsNodeList.getLength(); k++) {
                            Node commBDetailsNode = commBDetailsNodeList.item(k);
                            if (commBDetailsNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element commBDetailsEL = (Element) commBDetailsNode;
                                System.out.println("commBDetailsEL " + commBDetailsEL.getTagName());

                                String existingRCNo = getValue("existingRCNo", commBDetailsEL);
                                String shopNo = getValue("shopNo", commBDetailsEL);
                                String typeId = getValue("typeId", commBDetailsEL);

                                commBDetails.setExistingRCNo(existingRCNo);
                                commBDetails.setShopNo(shopNo);
                                commBDetails.setTypeId(typeId);

                            }
                        }

                        // ------------------------------------------

                        NodeList commodityDetailsNodeList = rcCallEL.getElementsByTagName("commodityDetails");

                        for (int n = 0; n < commodityDetailsNodeList.getLength(); n++) {
                            Node commodityDetailsNode = commodityDetailsNodeList.item(n);
                            if (commodityDetailsNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element commodityDetailsEL = (Element) commodityDetailsNode;
                                System.out.println("commodityDetailsEL " + commodityDetailsEL.getTagName());

                                NodeList commDetailsChildList = commodityDetailsEL.getChildNodes();
                                if (commDetailsChildList != null && commDetailsChildList.getLength() > 0) {
                                    for (int p = 0; p < commDetailsChildList.getLength(); p++) {
                                        Node chNode = commDetailsChildList.item(p);
                                        if (chNode.getNodeType() == Node.ELEMENT_NODE) {
                                            Element chEl = (Element) chNode;
                                            String tagName = chEl.getTagName();
                                            // info("TagName: " + tagName);
                                            if (tagName.indexOf("alloted") > -1) {
                                                String prodCode = ProductMap.productAllotedMap.get(tagName);
                                                info("tagName: " + tagName + " / prodCode: " + prodCode);
                                                Product product = ProductMap.getProductByCode(prodCode);
                                                product.setQuantityEntered(null);
                                                product.setAmount(null);
                                                product.setUnitRate(null);
                                                String tagValue = chEl.getTextContent();
                                                Double allottedVal = AppUtil.stringToDouble(tagValue, 0D);
                                                if (product != null) {
                                                    product.setProductAllottedQty(allottedVal);
                                                    String balTagName = tagName.substring(0, tagName.length() - 7);
                                                    balTagName = balTagName + "bal";
                                                    info("balTagName: " + balTagName);
                                                    String balValue = getText(balTagName, commodityDetailsEL);
                                                    info("balValue: " + balValue);
                                                    Double productBalanceQty = AppUtil.stringToDouble(balValue, 0D);
                                                    product.setProductBalanceQty(productBalanceQty);
                                                    itemsAllotedList.add(product);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                }

                // -------------------------------------------------------------------

                // commBDetails
                rcAuthResponse.setCommBDetails(commBDetails);
                rcAuthResponse.setItemsAllotedList(itemsAllotedList);

            }
        }

        return rcAuthResponse;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSROAllocation getRODetails(Map<String, Object> inputMap) throws Exception {

        info("getFPDDetailsDetails-1");

        FPSROAllocation fpsROAllocation = new FPSROAllocation();

        List<Product> itemsAllocList = null;

		/*
		 * Build request XML
		 */
        String reqXML = getRODetsilsReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsroAllocationResponse");

        itemsAllocList = new ArrayList<Product>();

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsROAllocation.setRespMessage(respMessage);
                fpsROAllocation.setRespMsgCode(respMsgCode);

                info("respMsgCode " + respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList roDetailNL = element1.getElementsByTagName("fpsroDetails");

                List<FPSDealer> fpsDealerList = new ArrayList<FPSDealer>();
                for (int j = 0; j < roDetailNL.getLength(); j++) {
                    Node dealerNode = roDetailNL.item(j);
                    if (dealerNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element roEL = (Element) dealerNode;

                        String transRoNo = getValue("transRoNo", roEL);
                        transRoNo = transRoNo == null ? "" : transRoNo.trim();

                        String totalAmtStr = getValue("totalAmt", roEL);
                        totalAmtStr = totalAmtStr == null ? "0" : totalAmtStr.trim();
                        Double totalAmt = AppUtil.stringToDouble(totalAmtStr, 0D);

                        String transDate = getValue("transDate", roEL);
                        transDate = transDate == null ? "0" : transDate.trim();

                        fpsROAllocation.setTransRoNo(transRoNo);
                        fpsROAllocation.setTransDate(transDate);
                        fpsROAllocation.setTotalAmt(totalAmt);

                        NodeList childList = roEL.getChildNodes();
                        if (childList != null && childList.getLength() > 0) {
                            for (int k = 0; k < childList.getLength(); k++) {
                                Node chNode = childList.item(k);
                                if (chNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element chEl = (Element) chNode;

                                    String tagName = chEl.getTagName();
                                    String productCode = ProductMap.productIssueReportMap.get(tagName);
                                    Product product = ProductMap.getProductByCode(productCode);
									/*
									 * product.setIssuedQuantity(null);
									 * product.setReceivedQuantity(null);
									 */
                                    error("XML utils", "Tag name" + tagName);
                                    String value = chEl.getNodeValue();
                                    if (value == null) {
                                        value = chEl.getTextContent();
                                    }
                                    // getValue(tagName, chEl);
                                    error("XML utils", "qty" + value);
                                    if (product != null) {
                                        Double qty = AppUtil.stringToDouble(value, 0D);
                                        error("XML utils", "after conversion :" + qty);
                                        product.setIssuedQuantity(qty);
                                        product.setReceivedQuantity(qty);
                                        itemsAllocList.add(product);
                                    }
                                }
                            }
                        }

                    }
                }

            }

        }
        fpsROAllocation.setFpsProductList(itemsAllocList);

        return fpsROAllocation;
    }

    public static FPSROAllocation getKeroseneRODetails(Map<String, Object> inputMap) throws Exception {

        info("getFPDDetailsDetails-1");

        FPSROAllocation fpsROAllocation = new FPSROAllocation();

        List<Product> itemsAllocList = null;

		/*
		 * Build request XML
		 */
        String reqXML = getKeroseneRODetsilsReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsroAllocationResponse");

        itemsAllocList = new ArrayList<Product>();

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsROAllocation.setRespMessage(respMessage);
                fpsROAllocation.setRespMsgCode(respMsgCode);

                info("respMsgCode " + respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList roDetailNL = element1.getElementsByTagName("fpsroDetails");

                List<FPSDealer> fpsDealerList = new ArrayList<FPSDealer>();
                for (int j = 0; j < roDetailNL.getLength(); j++) {
                    Node dealerNode = roDetailNL.item(j);
                    if (dealerNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element roEL = (Element) dealerNode;

                        String transRoNo = getValue("transRoNo", roEL);
                        transRoNo = transRoNo == null ? "" : transRoNo.trim();

                        String totalAmtStr = getValue("totalAmt", roEL);
                        totalAmtStr = totalAmtStr == null ? "0" : totalAmtStr.trim();
                        Double totalAmt = AppUtil.stringToDouble(totalAmtStr, 0D);

                        String transDate = getValue("transDate", roEL);
                        transDate = transDate == null ? "0" : transDate.trim();

                        fpsROAllocation.setTransRoNo(transRoNo);
                        fpsROAllocation.setTransDate(transDate);
                        fpsROAllocation.setTotalAmt(totalAmt);

                        NodeList childList = roEL.getChildNodes();
                        if (childList != null && childList.getLength() > 0) {
                            for (int k = 0; k < childList.getLength(); k++) {
                                Node chNode = childList.item(k);
                                if (chNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element chEl = (Element) chNode;

                                    String tagName = chEl.getTagName();

                                    if ("koilQty".equals(tagName)) {
                                        String productCode = ProductMap.productIssueReportMap.get(tagName);
                                        Product product = ProductMap.getProductByCode(productCode);
										/*
										 * product.setIssuedQuantity(null);
										 * product.setReceivedQuantity(null);
										 */
                                        error("XML utils", "Tag name" + tagName);
                                        String value = chEl.getNodeValue();
                                        if (value == null) {
                                            value = chEl.getTextContent();
                                        }
                                        // getValue(tagName, chEl);
                                        error("XML utils", "qty" + value);
                                        if (product != null) {
                                            Double qty = AppUtil.stringToDouble(value, 0D);
                                            error("XML utils", "after conversion :" + qty);
                                            product.setIssuedQuantity(qty);
                                            product.setReceivedQuantity(qty);
                                            itemsAllocList.add(product);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }

        }
        fpsROAllocation.setFpsProductList(itemsAllocList);

        return fpsROAllocation;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static FPSAllotment getFPSAllotmentRport(Map<String, Object> inputMap) throws Exception {

        info("getFPDDetailsDetails-1");

        FPSAllotment fpsAllotment = new FPSAllotment();

		/*
		 * Build request XML
		 */
        String reqXML = getFPSAllotmentRptReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("fpsAllotmentRptResponse");

        List<Product> fpsAllotmentProductList = new ArrayList<Product>();

        List<Product> fpsProductPriceList = new ArrayList<Product>();

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();



                fpsAllotment.setRespMessage(respMessage);
                fpsAllotment.setRespMsgCode(respMsgCode);


                info("respMsgCode " + respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }
                String transactionID = getValue("transactionID", element1);
                transactionID = transactionID == null ? "" : transactionID.trim();
                fpsAllotment.setTransactionId(transactionID);

                NodeList fpsAllotNL = element1.getElementsByTagName("fpsallotment");

                // List<Product> fpsAllotList = new ArrayList<Product>();

                for (int j = 0; j < fpsAllotNL.getLength(); j++) {
                    Node dealerNode = fpsAllotNL.item(j);
                    if (dealerNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element roEL = (Element) dealerNode;

                        NodeList childList = roEL.getChildNodes();
                        if (childList != null && childList.getLength() > 0) {
                            for (int k = 0; k < childList.getLength(); k++) {
                                Node chNode = childList.item(k);
                                if (chNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element chEl = (Element) chNode;
                                    String tagName = chEl.getTagName();

                                    boolean priceTag = (tagName.indexOf("Price") > -1);
                                    boolean allotmentTag = (tagName.indexOf("Allot") > -1);

                                    // String value = getValue(tagName, chEl);

                                    String value = chEl.getTextContent();

                                    info("tagName " + tagName);
                                    info("tagValue " + value);
                                    value = value == null ? "0" : value.trim();
                                    Double valueD = AppUtil.stringToDouble(value, 0D);
                                    String prodcutCode = null;
                                    if (allotmentTag) {
                                        prodcutCode = ProductMap.productAllotMap.get(tagName);
                                    } else if (priceTag) {
                                        prodcutCode = ProductMap.productPriceMap.get(tagName);
                                    }
                                    info("prodcutCode " + prodcutCode);
                                    Product product = ProductMap.getProductByCode(prodcutCode);
                                    if (product != null) {
                                        product.setTagName(tagName);
                                        if (priceTag) {
                                            product.setProductPrice(valueD);
                                            fpsProductPriceList.add(product);
                                        } else if (allotmentTag) {
                                            product.setProductAllotment(valueD);
                                            fpsAllotmentProductList.add(product);
                                        }
                                    }

                                }
                            }
                        }

                    }
                }

                fpsAllotment.setFpsProductPriceList(fpsProductPriceList);
                fpsAllotment.setFpsAllotmentProductList(fpsAllotmentProductList);
            }

        }

        return fpsAllotment;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static RCLastTransaction getRCLastTransction(Map<String, Object> inputMap) throws Exception {

        info("getFPDDetailsDetails-1");

        RCLastTransaction rcLastTransaction = new RCLastTransaction();

		/*
		 * Build request XML
		 */
        String reqXML = getRCLastTransctionReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

        System.out.println("RESPONSE  :::" + respXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("rcLastTransctionResponse");

        List<Product> productList = new ArrayList<Product>();
        List<Product> productBalanceList = new ArrayList<Product>();

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                info("respMsgCode " + respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                rcLastTransaction.setRespMessage(respMessage);
                rcLastTransaction.setRespMsgCode(respMsgCode);

                //
				/*
				 * String distCode = getValue("distCode", element1); distCode =
				 * distCode == null ? "" : distCode.trim(); // String shopNo =
				 * getValue("shopNo", element1); shopNo = shopNo == null ? "" :
				 * shopNo.trim(); // String rationCard = getValue("rationCard",
				 * element1); rationCard = rationCard == null ? "" :
				 * rationCard.trim(); // String memberId = getValue("memberId",
				 * element1); memberId = memberId == null ? "" :
				 * memberId.trim(); // String uidNo = getValue("uidNo",
				 * element1); uidNo = uidNo == null ? "" : uidNo.trim(); //
				 * String totalAmtStr = getValue("totalAmt", element1);
				 * totalAmtStr = totalAmtStr == null ? "" : totalAmtStr.trim();
				 *
				 * Double totalAmt = AppUtil.stringToDouble(totalAmtStr, 0D); //
				 * String currYear = getValue("currYear", element1); currYear =
				 * currYear == null ? "" : currYear.trim(); // String currMonth
				 * = getValue("currMonth", element1); currMonth = currMonth ==
				 * null ? "" : currMonth.trim(); // String transactionId =
				 * getValue("transactionId", element1); transactionId =
				 * transactionId == null ? "" : transactionId.trim();
				 */

                NodeList rcTransNL = element1.getElementsByTagName("rctransction");

                for (int m = 0; m < rcTransNL.getLength(); m++) {

                    Node rcTransNode = rcTransNL.item(m);

                    if (rcTransNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element rcTransEL = (Element) rcTransNode;

                        NodeList commDetailNL = rcTransEL.getElementsByTagName("commBDetails");

                        for (int s = 0; s < commDetailNL.getLength(); s++) {

                            Node node1 = commDetailNL.item(i);
                            if (node1.getNodeType() == Node.ELEMENT_NODE) {
                                Element element2 = (Element) node1;

                                String existingRCNo = getValue("existingRCNo", element2);
                                existingRCNo = existingRCNo == null ? "" : existingRCNo.trim();
                                //
                                String shopNo = getValue("shopNo", element2);
                                shopNo = shopNo == null ? "" : shopNo.trim();

                                String typeId = getValue("typeId", element2);
                                typeId = typeId == null ? "" : typeId.trim();

                                rcLastTransaction.setRationCard(existingRCNo);
                                rcLastTransaction.setShopNo(shopNo);
                                rcLastTransaction.setMemberId(typeId);

                            }

                        }

                        NodeList commodityDetailsNL=rcTransEL.getElementsByTagName("commodityDetails");

                        for (int s = 0; s < commodityDetailsNL.getLength(); s++) {

                            Node commDetailNode1 = commodityDetailsNL.item(i);
                            if (commDetailNode1.getNodeType() == Node.ELEMENT_NODE) {
                                Element commodityDetailsEL = (Element) commDetailNode1;
                                NodeList ChldNL = commodityDetailsEL.getChildNodes();
                                for (int k = 0; k < ChldNL.getLength(); k++) {
                                    Node chldNode = ChldNL.item(k);
                                    if (chldNode.getNodeType() == Node.ELEMENT_NODE) {

                                        Element chldNodeEL = (Element) chldNode;
                                        String tagName = chldNodeEL.getTagName();
                                        String tagValue = chldNodeEL.getTextContent();
                                        String productCode = ProductMap.productLastTransaction.get(tagName);

                                        Product product = ProductMap.getProductByCode(productCode);
                                        if (product != null) {
                                            product.setProductBalanceQty(AppUtil.stringToDouble(tagValue, 0D));
                                            productBalanceList.add(product);
                                        }
                                    }


                                }

                            }

                        }


                        NodeList lastSaleNL = rcTransEL.getElementsByTagName("lastsaledetails");

                        for (int n = 0; n < lastSaleNL.getLength(); n++) {
                            Node lastSaleNode = lastSaleNL.item(n);
                            if (lastSaleNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element lastSaleEL = (Element) lastSaleNode;
                                NodeList ChldNL = lastSaleEL.getChildNodes();
                                for (int k = 0; k < ChldNL.getLength(); k++) {
                                    Node chldNode = ChldNL.item(k);
                                    if (chldNode.getNodeType() == Node.ELEMENT_NODE) {
                                        // Element chldNodeEL = (Element)
                                        // chldNode;

                                        Element chldNodeEL = (Element) chldNode;
                                        String tagName = chldNodeEL.getTagName();
                                        String tagValue = chldNodeEL.getTextContent();
                                        String productCode = ProductMap.productKeyMap.get(tagName);

                                        Product product = ProductMap.getProductByCode(productCode);
                                        // String tagValue =
                                        // element1.getTextContent();
                                        if (product != null) {
                                            product.setIssuedQuantity(AppUtil.stringToDouble(tagValue, 0D));
                                            productList.add(product);
                                        }

                                        if ("totalAmt".equals(tagName)) {
                                            Double totalAmnt = AppUtil.stringToDouble(tagValue, 0D);
                                            rcLastTransaction.setTotalAmt(totalAmnt);
                                        }

                                        if ("transDate".equals(tagName)) {
                                            rcLastTransaction.setTransDate(tagValue);
                                        }

                                        if ("transId".equals(tagName)) {
                                            rcLastTransaction.setTransactionId(tagValue);
                                        }

                                    }
                                }
                            }
                        }

                    }
                }

				/*
				 * rcLastTransaction.setDistCode(distCode);
				 * rcLastTransaction.setShopNo(shopNo);
				 * rcLastTransaction.setRationCard(rationCard);
				 * rcLastTransaction.setMemberId(memberId);
				 * rcLastTransaction.setUidNo(uidNo);
				 * rcLastTransaction.setTotalAmt(totalAmt);
				 * rcLastTransaction.setCurrYear(currYear);
				 * rcLastTransaction.setCurrMonth(currMonth);
				 * rcLastTransaction.setTransactionId(transactionId);
				 */
                for(int a=0;a<productList.size();a++){
                    for(int b=0;b<productBalanceList.size();b++){
                        if(productList.get(a).getCode().equalsIgnoreCase(productBalanceList.get(b).getCode())){
                            productList.get(a).setProductBalanceQty(productBalanceList.get(b).getProductBalanceQty());
                        }
                    }
                }

                rcLastTransaction.setProductList(productList);

            }

        }

        return rcLastTransaction;
    }

    /**
     * @param inputMap
     * @param itemDetails
     * @return
     * @throws Exception
     */
    public static GeneralResponse postRCSaleDetails(Map<String, Object> inputMap, String cardType,
                                                    List<Product> itemDetails) throws Exception {

        info("getFPDDetailsDetails-1");

        GeneralResponse generalResponse = new GeneralResponse();

		/*
		 * Build request XML
		 */
        String reqXML = buildPostRCSaleDetailsReq(cardType, inputMap, itemDetails);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~"+respXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("postringRespBean");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                String transactionID = getValue("transactionID", element1);
                transactionID = transactionID == null ? "" : transactionID.trim();

                generalResponse.setRespMessage(respMessage);
                generalResponse.setRespMsgCode(respMsgCode);
                generalResponse.setTransactionId(transactionID);

                info("respMsgCode " + respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

            }

        }

        return generalResponse;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static GeneralResponse mobileOTP(Map<String, Object> inputMap) throws Exception {
        GeneralResponse generalResponse = new GeneralResponse();

		/*
		 * Build request XML
		 */
        String reqXML = mobileOTPReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("mobileOTPResponse");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                generalResponse.setRespMessage(respMessage);
                generalResponse.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

            }

        }

        return generalResponse;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public static GeneralResponse postRODetails(Map<String, Object> inputMap, List<Product> itemDetails)
            throws Exception {
        GeneralResponse generalResponse = new GeneralResponse();

		/*
		 * Build request XML
		 */
        String reqXML = postRODetailsReq(inputMap, itemDetails);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("roDetailsPostringResp");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                generalResponse.setRespMessage(respMessage);
                generalResponse.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

            }

        }

        return generalResponse;
    }

    public static GeneralResponse postKeroseneRODetails(Map<String, Object> inputMap)
            throws Exception {
        GeneralResponse generalResponse = new GeneralResponse();

		/*
		 * Build request XML
		 */
        String reqXML = postKeroseneRODetailsReq(inputMap);

		/*
		 * Post and get response XML
		 */
        String respXML = postRequest(reqXML);
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::"+respXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("keroseneRODetailsResp");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                generalResponse.setRespMessage(respMessage);
                generalResponse.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

            }

        }

        return generalResponse;
    }

    /**
     * @param tag
     * @param element
     * @return
     */
    private static String getValue(String tag, Element element) {
        String rtnValue = null;
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList != null && nodeList.getLength() > 0) {
                nodeList = nodeList.item(0).getChildNodes();
                if (nodeList != null && nodeList.getLength() > 0) {
                    Node node = nodeList.item(0);
                    if (node != null) {
                        rtnValue = node.getNodeValue();
                    }

                    if (rtnValue == null) {
                        rtnValue = node.getTextContent();
                    }
                }
            }
        }

        return rtnValue;
    }

    private static String getText(String tag, Element element) {
        String rtnValue = null;
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList != null && nodeList.getLength() > 0) {
                nodeList = nodeList.item(0).getChildNodes();
                if (nodeList != null && nodeList.getLength() > 0) {
                    Node node = nodeList.item(0);
                    if (node != null) {
                        rtnValue = node.getTextContent();
                    }
                }
            }
        }

        return rtnValue;
    }

    /**
     * @param inputMap
     * @return Method to build getFPSDealerDetails request XML
     */
    public static String getFPSDealerDetailsReq(Map<String, Object> inputMap) {
        return buildRequest("getFPSDealerDetails", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String getRationCardDetailsReq(Map<String, Object> inputMap) {
        return buildRequest("getRationCardDetails", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String getFPSDealerAuthenticationReq(Map<String, Object> inputMap) {
        return buildRequest("getFPSDealerAuthentication", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String getFPSCardsPositionRptReq(Map<String, Object> inputMap) {
        return buildRequest("fpsCardsPositionRpt", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String getRODetsilsReq(Map<String, Object> inputMap) {
        return buildRequest("getRODetsils", inputMap);
    }

    public static String getKeroseneRODetsilsReq(Map<String, Object> inputMap) {
        return buildRequest("getRODetsils", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String getFPSAllotmentRptReq(Map<String, Object> inputMap) {
        return buildRequest("fpsAllotmentRpt", inputMap);
    }

    public static String getRCLastTransctionReq(Map<String, Object> inputMap) {
        return buildRequest("getRCLastTransction", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String getRCAuthenticationReq(Map<String, Object> inputMap) {
        return buildRequest("getRCAuthentication", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String getFPSCurrentClosingBalanceRepReq(Map<String, Object> inputMap) {
        return buildRequest("fpsCurrentClsBalancesRpt", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String getFPSMonthlyClosingBalanceRepReq(Map<String, Object> inputMap) {
        return buildRequest("fpsMonthlyClsBalancesRpt", inputMap);
    }

    public static String getFPSIssuesRptReq(Map<String, Object> inputMap) {
        return buildRequest("fpsissueDayWiseRpt", inputMap);
    }

    public static String getFPSSalesRptReq(Map<String, Object> inputMap) {
        return buildRequest("fpsIssuesRpt", inputMap);
    }

    public static String getFPSReceiptsRptReq(Map<String, Object> inputMap) {
        return buildRequest("fpsReceiptsRpt", inputMap);
    }

    public static String doShopLogoutReq(Map<String, Object> inputMap) {
        return buildRequest("shopLogout", inputMap);
    }

    public static String postMemberBioAuthVerifyReq(Map<String, Object> inputMap) {
        return buildRequest("postMemberBioAuthVerify", inputMap);
    }

    public static String postUidBestFingersReq(Map<String, Object> inputMap) {
        return buildRequest("postUidBestFingers", inputMap);
    }

    public static String getPortabilityDaywiseReq(Map<String, Object> inputMap){
        return buildRequest("portabilityIssuesDayWiseReport", inputMap);
    }

    public static String getPortabilityMonthwiseReq(Map<String, Object> inputMap){
        return buildRequest("allotmentRequest", inputMap);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String mobileUpdateReq(Map<String, Object> inputMap) {
        return buildRequest("MobileUpdate", inputMap);
    }

    /**
     * @param cardType
     * @param inputMap
     * @param itemDetails
     * @return
     */
    public static String buildPostRCSaleDetailsReq(String cardType, Map<String, Object> inputMap,
                                                   List<Product> itemDetails) {
        return buildSalesDetailsRequest("postRCSaleDetails", cardType, inputMap, itemDetails);
    }

    /**
     * @param inputMap
     * @return
     */
    public static String mobileOTPReq(Map<String, Object> inputMap) {
        return buildRequest("MobileOtp", inputMap);
    }

    public static String postRODetailsReq(Map<String, Object> inputMap, List<Product> itemProductList) {
        return buildRODetailsRequest("postRODetails", inputMap, itemProductList);
    }

    public static String postKeroseneRODetailsReq(Map<String, Object> inputMap) {
        return buildRequest("postKeroseneRODetails", inputMap);
    }

    public static String postPortabilityReq(Map<String, Object> inputMap, List<Product> itemProductList){
        return buildPortabilityRequest("allotmentPostRequest", inputMap, itemProductList);
    }

    /**
     * @param methodName
     * @param inputMap
     * @return
     */
    public static String buildRequest(String methodName, Map<String, Object> inputMap) {

        String requestXML = null;
        StringBuilder sb = null;
        try {
            sb = getMsgHeader();

            sb.append("<").append(methodName).append(" xmlns=\"http://service.epos.nfsc.nic.com/\">");
            sb.append("\n");
            Set<String> keys = inputMap.keySet();

            for (String key : keys) {
                sb.append("<").append(key).append(" xmlns=\"\">").append(inputMap.get(key));
                sb.append("</").append(key).append(">");
                sb.append("\n");
            }
            sb.append("</").append(methodName).append(">");
			/*
			 * Footer
			 */
            String msgFooter = getMsgFooter().toString();

            sb.append(msgFooter);

            requestXML = sb.toString();

        } catch (Exception e) {
            error("Exception at buildRequest(1) >>>> " + e.toString());
        }

        info("buildRequest-1 " + requestXML);

        return requestXML;

    }

    public static String buildSalesDetailsRequest(String methodName, String cardType, Map<String, Object> inputMap,
                                                  List<Product> itemDetails) {

        String requestXML = null;
        StringBuilder sb = null;
        try {
            sb = getMsgHeader();

            sb.append("<").append(methodName).append(" xmlns=\"http://service.epos.nfsc.nic.com/\">");
            sb.append("\n");
            Set<String> keys = inputMap.keySet();

            for (String key : keys) {
                sb.append("<").append(key).append(" xmlns=\"\">").append(inputMap.get(key));
                sb.append("</").append(key).append(">");
                sb.append("\n");
            }

            // String cardType=inputMap.get("cardType");

            List<String> riceTagNameList = getRiceTagNameList();

            for (Product item : itemDetails) {
                String qtyEntered = AppUtil.doubleToString(item.getQuantityEntered(), "0", false);
                String qtyTagName = item.getItemQtyTagName();
                String itemCode = item.getCode();
                if (itemCode.equals(ProductMap.RICE_CODE)) {
                    if (AFSC_CARD_TYPE.equals(cardType)) {
                        qtyTagName = "afscriceQty";
                        riceTagNameList.remove(qtyTagName);
                    } else if (FSC_CARD_TYPE.equals(cardType)) {
                        qtyTagName = "fscriceQty";
                        riceTagNameList.remove(qtyTagName);
                    } else if (AAP_CARD_TYPE.equals(cardType)) {
                        qtyTagName = "aapriceQty";
                        riceTagNameList.remove(qtyTagName);
                    }
                }
                if (!"riceQty".equals(qtyTagName)) {
                    sb.append("<").append(qtyTagName).append(" xmlns=\"\">").append(qtyEntered);
                    sb.append("</").append(qtyTagName).append(">");
                    sb.append("\n");
                }

            }

            for (String riceTag : riceTagNameList) {

                sb.append("<").append(riceTag).append(" xmlns=\"\">").append("0");
                sb.append("</").append(riceTag).append(">");
                sb.append("\n");

            }
            sb.append("</").append(methodName).append(">");
			/*
			 * Footer
			 */
            String msgFooter = getMsgFooter().toString();

            sb.append(msgFooter);

            requestXML = sb.toString();

        } catch (Exception e) {
            error("Exception at buildRequest(1) >>>> " + e.toString());
        }

        info("buildRequest-1 " + requestXML);

        return requestXML;

    }

    public static String buildRODetailsRequest(String methodName, Map<String, Object> inputMap,
                                               List<Product> itemDetails) {

        String requestXML = null;
        StringBuilder sb = null;
        try {
            sb = getMsgHeader();

            sb.append("<").append(methodName).append(" xmlns=\"http://service.epos.nfsc.nic.com/\">");
            sb.append("\n");
            Set<String> keys = inputMap.keySet();

            for (String key : keys) {
                sb.append("<").append(key).append(" xmlns=\"\">").append(inputMap.get(key));
                sb.append("</").append(key).append(">");
                sb.append("\n");
            }

            String qtyEntered;

            for (Product item : itemDetails) {
                qtyEntered = AppUtil.doubleToString(item.getReceivedQuantity(), "0", false);
                String qtyTagName = item.getItemQtyTagName();
                String itemCode = item.getCode();

                sb.append("<").append(qtyTagName).append(" xmlns=\"\">").append(qtyEntered);
                sb.append("</").append(qtyTagName).append(">");
                sb.append("\n");

            }

            sb.append("</").append(methodName).append(">");
			/*
			 * Footer
			 */
            String msgFooter = getMsgFooter().toString();

            sb.append(msgFooter);

            requestXML = sb.toString();

        } catch (Exception e) {
            error("Exception at buildRequest(1) >>>> " + e.toString());
        }

        info("buildRequest-1 " + requestXML);

        return requestXML;

    }

    public static String buildPortabilityRequest(String methodName, Map<String, Object> inputMap,
                                               List<Product> itemDetails) {

        String requestXML = null;
        StringBuilder sb = null;
        try {
            sb = getMsgHeader();

            sb.append("<").append(methodName).append(" xmlns=\"http://service.epos.nfsc.nic.com/\">");
            sb.append("\n");
            Set<String> keys = inputMap.keySet();

            for (String key : keys) {
                sb.append("<").append(key).append(" xmlns=\"\">").append(inputMap.get(key));
                sb.append("</").append(key).append(">");
                sb.append("\n");
            }

            String qtyEntered;

            for (Product item : itemDetails) {
                qtyEntered = AppUtil.doubleToString(item.getQuantityEntered(), "0", false);
                String qtyTagName = item.getItemQtyTagName();
                String itemCode = item.getCode();
                if(itemCode.equalsIgnoreCase("103")){
                    qtyTagName="keroseneQty";
                }

                sb.append("<").append(qtyTagName).append(" xmlns=\"\">").append(qtyEntered);
                sb.append("</").append(qtyTagName).append(">");
                sb.append("\n");

            }

            sb.append("</").append(methodName).append(">");
			/*
			 * Footer
			 */
            String msgFooter = getMsgFooter().toString();

            sb.append(msgFooter);

            requestXML = sb.toString();

        } catch (Exception e) {
            error("Exception at buildRequest(1) >>>> " + e.toString());
        }

        info("buildRequest-1 " + requestXML);

        return requestXML;

    }


    /**
     * @param requestXML
     * @return
     * @throws Exception
     */
    public static String postRequest(String requestXML) throws Exception {
        String respXML = null;
        boolean isServerError=false;
        int SelectedUrl = 0;
        String url;
        SelectedUrl = Integer.parseInt(MySharedPreference.readString(GlobalAppState.getInstance(),
                "SelectedUrl", "0"));

       /* if (SelectedUrl ==0) {
            url= "https://eposservices.telangana.gov.in/ePoSServicesUAT/epos";
            //http://epos.telangana.gov.in/ePoSServicesUAT/epos
        }else{
            url = "http://epos.telangana.gov.in/ePoSServicesTestURL/epos";
        }
        String twoDigits = LoginData.getInstance().getTwoDigitsShopID();
        if (twoDigits.equals("16") || twoDigits.equals("15") || twoDigits.equals("33")) {
            url = "http://epos.telangana.gov.in/ePoSServices/epos";
        }*/

      //  url= "https://eposservices.telangana.gov.in/ePoSServicesUAT/epos"; //testing
        if(Util.needAadhaarAuth2){
            url="https://epos.telangana.gov.in/ePoSServicesUAT99/epos?wsdl";
            //url="http://cscservices.telangana.gov.in/AUAePoSServicesUAT/epos?wsdl";
        }else{
            url= "https://eposservices.telangana.gov.in/ePoSServices/epos?wsdl";  //live
        }

            // url="https://eposservices.telangana.gov.in/ePoSServicesUAT99/epos?wsdl";

        try {
            URL obj = new URL(url);
            Log.e("XML UTIL", " url "+url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            Log.e("XML UTIL", "After HttpURLConnection "+requestXML);
            // add reuqest header
            con.setRequestMethod("POST");
         //   con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setConnectTimeout(CONNECTION_TIME);// 30 Secs
            con.setReadTimeout(CONNECTION_TIME);
            Log.e("XML UTIL", "After setReadTimeout");
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(requestXML);
            wr.flush();
            wr.close();
            Log.e("XML UTIL", " after close " );

            int responseCode = con.getResponseCode();

           //nfo("responseCode: " + responseCode);

            Log.e("responseCode", ""+responseCode);

            if (responseCode == 500) {
                isServerError=true;
                throw new FPSException("Internal Server Error. HTTP Response Code Received From Server: 500");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine.trim());
            }
            in.close();

            respXML = response.toString().replaceAll("&lt;", "<");
            respXML = respXML.replaceAll("&gt;", ">");
            Log.e("XML UTIL", "---------------------RESP-S-----------------------------");
            Log.e("XML UTIL", "response" + respXML);
            Log.e("XML UTIL", "---------------------RESP-E-----------------------------");
            respXML = respXML.trim();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new FPSException("Unable to receive response from ePOS server. Connection Timedout.");
        } catch (IOException e) {
            e.printStackTrace();
            /*String msg=e.getMessage();
            msg=msg==null?"":msg.trim();*/
            //throw e;
            throw new FPSException("Unable to receive response from ePOS server. Connection Timedout.");
        }catch (Exception e) {
            e.printStackTrace();
           /* String msg=e.getMessage();
            msg=msg==null?"":msg.trim();
            msg="";*/
            //throw e;
            if(!isServerError) {
                throw new FPSException("Unable to receive response from ePOS server. Connection Timedout.");
            }else{
                throw new FPSException("Internal Server Error. HTTP Response Code Received From Server: 500");
            }
        }
        return respXML;
    }

    private static StringBuilder getMsgHeader() {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<v:Envelope xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:d=\"http://www.w3.org/2001/XMLSchema\" xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        sb.append("\n");
        sb.append("<v:Header />");
        sb.append("\n");
        sb.append("<v:Body>");
        sb.append("\n");
        return sb;
    }

    /**
     * @return
     */
    private static List<String> getRiceTagNameList() {
        List<String> riceTagNameList = new ArrayList<String>();
        riceTagNameList.add("afscriceQty");
        riceTagNameList.add("fscriceQty");
        riceTagNameList.add("aapriceQty");

        return riceTagNameList;
    }

    private static StringBuilder getMsgFooter() {
        StringBuilder sb = new StringBuilder();
        sb.append("</v:Body></v:Envelope>");
        return sb;
    }

    private static void info(String infoMsg) {
        System.out.println(infoMsg);
    }

    private static void error(String errMsg) {
        System.err.println(errMsg);
    }

    private static void error(String tag, String errorMsg) {
        // Log.e(tag, errorMsg);
    }


    public static FPSPortablityDayWiseReport getFPSPortablilityDayWiseReport(Map<String, Object> inputMap) throws Exception {
        FPSPortablityDayWiseReport fpsReceiptReport = new FPSPortablityDayWiseReport();

        List<Product> productList = new ArrayList<Product>();


        String reqXML = getPortabilityDaywiseReq(inputMap);


        String respXML = postRequest(reqXML);

		/*
		 * Build Document
		 */

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("allotmentRequest");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsReceiptReport.setRespMessage(respMessage);
                fpsReceiptReport.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList fpsIssuesNL = element1.getElementsByTagName("allotmentbean");

                for (int j = 0; j < fpsIssuesNL.getLength(); j++) {
                    Node fpsIssuesNode = fpsIssuesNL.item(j);
                    if (fpsIssuesNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element fpsIssuesNodeEL = (Element) fpsIssuesNode;

                        NodeList fpsIssuesChldNL = fpsIssuesNodeEL.getChildNodes();
                        for (int k = 0; k < fpsIssuesChldNL.getLength(); k++) {
                            Node fpsIssuesChldNode = fpsIssuesChldNL.item(k);
                            if (fpsIssuesChldNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element fpsIssuesChldNodeEL = (Element) fpsIssuesChldNode;
                                String tagName = fpsIssuesChldNodeEL.getTagName();
                                String tagValue = fpsIssuesChldNodeEL.getTextContent();
                                // info("tagName: " + tagName);
                                String productCode = ProductMap.productIssueReportMap.get(tagName);
                                // info("productCode: " + productCode);
                                Product product = ProductMap.getProductByCode(productCode);
                                if (product != null) {
                                    //Log.e("XML UTIL",+"value");
                                    product.setIssuedQuantity(AppUtil.stringToDouble(tagValue, 0D));

                                    productList.add(product);
                                }

                                if ("transDate".equals(tagName)) {
                                    fpsReceiptReport.setTransDate(tagValue);
                                } else if ("totalAmt".equals(tagName)) {
                                    fpsReceiptReport.setTotalAmt(AppUtil.stringToDouble(tagValue, 0D));
                                } else if ("transRoNo".equals(tagName)) {
                                    fpsReceiptReport.setTransRoNo(tagValue);
                                }
                            }
                        }

                    }
                }

            }

        }
        fpsReceiptReport.setProductList(productList);
        return fpsReceiptReport;
    }

    public static FPSPortablityMonthWiseReport getFPSPortablilityMonthWiseReport(Map<String, Object> inputMap) throws Exception {
        FPSPortablityMonthWiseReport fpsReceiptReport = new FPSPortablityMonthWiseReport();

        List<Product> productList = new ArrayList<Product>();


        String reqXML = getPortabilityMonthwiseReq(inputMap);


        String respXML = postRequest(reqXML);

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("allotmentRequest");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                fpsReceiptReport.setRespMessage(respMessage);
                fpsReceiptReport.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

                NodeList fpsIssuesNL = element1.getElementsByTagName("allotmentbean");

                for (int j = 0; j < fpsIssuesNL.getLength(); j++) {
                    Node fpsIssuesNode = fpsIssuesNL.item(j);
                    if (fpsIssuesNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element fpsIssuesNodeEL = (Element) fpsIssuesNode;

                        NodeList fpsIssuesChldNL = fpsIssuesNodeEL.getChildNodes();
                        for (int k = 0; k < fpsIssuesChldNL.getLength(); k++) {
                            Node fpsIssuesChldNode = fpsIssuesChldNL.item(k);
                            if (fpsIssuesChldNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element fpsIssuesChldNodeEL = (Element) fpsIssuesChldNode;
                                String tagName = fpsIssuesChldNodeEL.getTagName();
                                String tagValue = fpsIssuesChldNodeEL.getTextContent();
                                // info("tagName: " + tagName);
                                String productCode = ProductMap.productIssueReportMap.get(tagName);
                                // info("productCode: " + productCode);
                                Product product = ProductMap.getProductByCode(productCode);
                                if (product != null) {
                                    product.setIssuedQuantity(AppUtil.stringToDouble(tagValue, 0D));
                                    product.setQuantityEntered(null);
                                    productList.add(product);
                                }

                                if ("transDate".equals(tagName)) {
                                    fpsReceiptReport.setTransDate(tagValue);
                                } else if ("totalAmt".equals(tagName)) {
                                    fpsReceiptReport.setTotalAmt(AppUtil.stringToDouble(tagValue, 0D));
                                } else if ("transRoNo".equals(tagName)) {
                                    fpsReceiptReport.setTransRoNo(tagValue);
                                }
                            }
                        }

                    }
                }

            }

        }
        fpsReceiptReport.setProductList(productList);
        return fpsReceiptReport;
    }

    public static GeneralResponse postPortabilityRequest(Map<String, Object> inputMap, List<Product> itemDetails)
            throws Exception {
        GeneralResponse generalResponse = new GeneralResponse();

        String reqXML = postPortabilityReq(inputMap, itemDetails);

        String respXML = postRequest(reqXML);

        Document doc = getDocument(respXML);

        NodeList nList = doc.getElementsByTagName("allotmentRequest");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) node;

                String respMessage = getValue("respMessage", element1);
                respMessage = respMessage == null ? "" : respMessage.trim();
                //
                String respMsgCode = getValue("respMsgCode", element1);
                respMsgCode = respMsgCode == null ? "" : respMsgCode.trim();

                generalResponse.setRespMessage(respMessage);
                generalResponse.setRespMsgCode(respMsgCode);

                if (!"0".equals(respMsgCode)) {
                    error("Error Code: " + respMsgCode);
                    throw new FPSException(respMsgCode, respMessage);
                }

            }

        }

        return generalResponse;
    }



}