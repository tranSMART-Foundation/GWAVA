/*
 * Collection of utilities for parsing and calling Transmart webservices
 */
package com.pfizer.mrbt.genomics.TransmartClient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author henstockpv
 */
public class TransmartUtil {

    /**
     * Joins list by delimiter into a string.
     *
     * @param delimiter
     * @param list
     * @return
     */
    public static String join(String delimiter, List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Integer i : list) {
            if (index > 0) {
                sb.append(delimiter);
            }
            sb.append(i);
            index++;
        }
        return sb.toString();
    }

    public static String joinLong(String delimiter, List<Long> list) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Long l : list) {
            if (index > 0) {
                sb.append(delimiter);
            }
            sb.append(l);
            index++;
        }
        return sb.toString();
    }

    /**
     * Converts the params into a string with the url for query. It excludes the
     * dbSrcOption and geneSrcOption from the list of params
     *
     * @param url
     * @param params
     * @param excludeKeyList list of keys from params to exclude from list
     * @return
     */
    public static String addParametersToUrl(String url,
                                            Map<String, String> params,
                                            ArrayList<String> excludeKeyList) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int numParams = params.keySet().size();
        int paramIndex = 0;
        for (Map.Entry<String, String> pairs : params.entrySet()) {
            String pname = (String) pairs.getKey();
            if (!excludeKeyList.contains(pname)) {
                if (paramIndex == 0) {
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                String pvalue = (String) pairs.getValue();
                sb.append(pname + "=" + pvalue);
                paramIndex++;
            }
        }
        //System.out.println("AddParamtoURL [" + sb.toString() + "]");
        return sb.toString();
    }

    /**
     * Converts the params into a string with the url for query
     * @param url
     * @param params
     * @return 
     */
    public static String addParametersToUrl(String url,Map<String,String> params){
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int numParams = params.keySet().size();
        int paramIndex = 0;
        for(Map.Entry<String,String> pairs : params.entrySet()){
            if(paramIndex == 0){
               sb.append("?");
            } else {
               sb.append("&");
            }
            String pname = (String)pairs.getKey();
            String pvalue = (String) pairs.getValue();
            sb.append(pname+"="+pvalue);
            paramIndex++;
        }
        //System.out.println("AddParamtoURL [" + sb.toString() + "]");
        return sb.toString();
    }

    
    /**
     * Prints the XML result from parsing in a outline type format.
     * @param xmlStr 
     */
    public static void printXmlParsing(String xmlStr) {
        XMLDigester digester = new XMLDigester();
        try {
            Rows rows = digester.digest(xmlStr);
            for (int i = 0; i < rows.getNumRows(); i++) {
                Row row = rows.getRow(i);
                System.out.println("Row " + i);
                for (int rowi = 0; rowi < row.getNumValues(); rowi++) {
                    System.out.println("\t" + row.getValue(rowi));
                }
            }
        } catch (Exception ex) {
            System.out.println("Failed in digesting xml string " + xmlStr);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Takes the xml data structure that has 
     * <rows><row><data>val1</data>...<data>valn</data></row>...</rows>
     * and converts it into a list of lists where where each row is a list within the 
     * parent structure.
     * @param xmlStr
     * @throws Exception if anything in the parsing fails
     * @return 
     */
    public static ArrayList<ArrayList<String>> parseXml(String xmlStr) throws Exception {
        XMLDigester digester = new XMLDigester();
        ArrayList<ArrayList<String>> outputList = new ArrayList<ArrayList<String>>();
        try {
            Rows rows = digester.digest(xmlStr);
            for (int rowi = 0; rowi < rows.getNumRows(); rowi++) {
                Row row = rows.getRow(rowi);
                ArrayList<String> outputRow = new ArrayList<String>();
                for (int index = 0; index < row.getNumValues(); index++) {
                    outputRow.add(row.getValue(index));
                }
                outputList.add(outputRow);
            }
        } catch (Exception ex) {
            System.out.println("Failed in digesting xml string " + xmlStr);
            System.out.println(ex.getMessage());
            throw ex;
        }
        return outputList;
    }
    
    
    /**
     * Performs the main service call that is general to all calls and throws the two exceptions
     * that are likely to come back
     * @param path
     * @return
     * @throws UniformInterfaceException
     * @throws ClientHandlerException 
     */
    public static String fetchResult(String path) throws UniformInterfaceException, ClientHandlerException {
        WebResource webRes = Client.create().resource(path);
        Builder clientReqBuilder = webRes.accept(MediaType.APPLICATION_XML);
        String xmlResult = clientReqBuilder.get(String.class);
        return (String) xmlResult;
    }
    

}
