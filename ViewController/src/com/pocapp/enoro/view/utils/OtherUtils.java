package com.pocapp.enoro.view.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.data.RichListView;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.QueryEvent;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;

import oracle.binding.BindingContainer;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import oracle.adf.view.rich.component.rich.data.RichCarousel;

import oracle.binding.OperationBinding;

import oracle.jbo.AttributeDef;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;


import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaItem;
import oracle.jbo.ViewCriteriaManager;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.uicli.binding.JUCtrlListBinding;

import org.apache.myfaces.trinidad.component.UIXEditableValue;
import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;


import java.net.*;
import java.io.IOException;

import javax.faces.context.ExternalContext;

import oracle.adf.controller.ControllerContext;
import oracle.adf.share.logging.ADFLogger;

/**
 * @author andrei.ion@oracle.com
 * */

public class OtherUtils {
    public OtherUtils() {
        super();
    }
    
    public static void redirectTo(boolean externalPage, String page){      
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = fctx.getExternalContext();
        String url = page;
        /*
        if (externalPage){
            url = page;
        } else {
            url = ControllerContext.getInstance().
                        getGlobalViewActivityURL(page);
        }
        */
        try {
            ectx.redirect(url);
        } catch (IOException e) {
            System.out.println("EXC when REDIRECTING! " + 
                               e.getMessage());  
            e.printStackTrace();
        } finally {
            FacesContext.getCurrentInstance().responseComplete();
        }      
    }
    
    private static ADFLogger _logger = ADFLogger.createADFLogger(
        Package.getPackage("com.pocapp.enoro.view.utils"));    
    
    public static String showMessageInfo(String messageText) {
        /**
         * set the type of the message.
         * Valid types: error, fatal,info,warning
         */
        showMessage(messageText, FacesMessage.SEVERITY_INFO);
        return null;
    }    
    public static String showMessageFatal(String messageText) {
        /**
         * set the type of the message.
         * Valid types: error, fatal,info,warning
         */
        showMessage(messageText, FacesMessage.SEVERITY_FATAL);
        return null;
    }
    public static String showMessageWarn(String messageText) {
        /**
         * set the type of the message.
         * Valid types: error, fatal,info,warning
         */
        showMessage(messageText, FacesMessage.SEVERITY_WARN);
        return null;
    }    
    
    public static String showMessage(String messageText, FacesMessage.Severity severity) {
        FacesMessage fm = new FacesMessage(messageText);
        /**
         * set the type of the message.
         * Valid types: error, fatal,info,warning
         */
        fm.setSeverity(severity);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, fm);
        return null;
    }    
    
    
    public static  BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }
    

     public static DCBindingContainer getDCBindings(){
        return (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public static void print(String msg){
        System.out.println(msg);
    }
    
    public static void refreshTableIterator(String iteratorName){
        DCBindingContainer dcBindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();                  
        //FacesCtrlHierBinding treeData = (FacesCtrlHierBinding)dcBindings.getControlBinding("t2");  
        
        DCIteratorBinding iter = dcBindings.findIteratorBinding(iteratorName);        
        iter.executeQuery();
        iter.refresh(DCIteratorBinding.RANGESIZE_UNLIMITED);
    }
    
    public static Row[] getAllRows(String iteratorName){  
        DCIteratorBinding iterBind = getIterator(iteratorName);
        Row[] rows = iterBind.getAllRowsInRange();
        
        return rows;
    }
    
    public static void setCurrentRowCarousel(String iteratorName, RichCarousel carousel){
        print("setCurrentRowCarousel for iterator " + iteratorName);
        ViewObject vo = OtherUtils.getVO(iteratorName);
        Row row = vo.getCurrentRow();
        Key key = row.getKey();
        ArrayList alist = new ArrayList();
        alist.add(key);
        carousel.setCurrentItemKey(alist);
    }
            
    public static DCIteratorBinding getIterator(String iteratorName){
        DCIteratorBinding iterBind = (DCIteratorBinding)getDCBindings().get(iteratorName);
        return iterBind;
    }
    
    public static boolean setCurrentRow(String iteratorName, String index){

        boolean result = true;
        
        try {
            DCIteratorBinding iterBind = (DCIteratorBinding)getDCBindings().get(iteratorName);
            Row[] rows = iterBind.getAllRowsInRange();
            Row row = getRow(rows, index);
            RowSetIterator rsi = iterBind.getRowSetIterator();
            
            result = rsi.setCurrentRow(row);
            
        } catch (NullPointerException npe){
            
            print("ERROR - NPE - in setCurrentRow " + iteratorName + " index " + index + 
                " error: " + npe.getMessage());
            
            npe.printStackTrace();
            
            return false;
        }
        
        return result;
    }
    
    public static class RowAttribute{
        private String attrName;
        private String attrValue;
        
        public RowAttribute(String attrName, String attrValue){
            this.attrName = attrName;
            this.attrValue = attrValue;
        }
        
        public boolean equals(Object object2) {
            return object2 instanceof RowAttribute && 
                   attrName.equals(((RowAttribute)object2).getAttrName()) &&
                   attrValue.equals(((RowAttribute)object2).getAttrValue());
        }

        public String getAttrName() {
            return attrName;
        }

        public String getAttrValue() {
            return attrValue;
        }
    }
    
    /**
     * returns a list of lists of RowAttribute objects, 
     * given the iterator name and a list of attribute names to check for
     * It is scanning the iterator rows and returning requested information.
     * */
    public static List<List<RowAttribute>> getRowsAttributes(String iteratorName, String... attributeNames){
        
        List<List<RowAttribute>> rowsAttrs = new ArrayList<List<RowAttribute>>();        
        List<RowAttribute> rowAttrs;
        
        DCIteratorBinding iterBind = (DCIteratorBinding)getDCBindings().get(iteratorName);
        Row[] rows = iterBind.getAllRowsInRange();
        
        for (Row row : rows){
            rowAttrs = new ArrayList<RowAttribute>();
            
            for (String attributeName : attributeNames){
                
                String rowAttrVal = "" + row.getAttribute(attributeName);
                
                print(attributeName + " = " + rowAttrVal);
                
                rowAttrs.add(new RowAttribute(attributeName, rowAttrVal));                
            }
            
            rowsAttrs.add(rowAttrs);
        }
        
        return rowsAttrs;
    }
    
    public static long numberRowsIterator(String iteratorName){
        DCIteratorBinding iterBind = (DCIteratorBinding)getDCBindings().get(iteratorName);
        
        return iterBind.getEstimatedRowCount();
    }
    
    public static void removeAllRows(String iteratorName){
        
        try {
            DCIteratorBinding iterBind = (DCIteratorBinding)getDCBindings().get(iteratorName);
            RowSetIterator rsi = iterBind.getRowSetIterator();
            
            OtherUtils.print("removeAllRows for " + iteratorName);
            
            while (rsi.getCurrentRow() != null){
                rsi.removeCurrentRow();
                OtherUtils.print("removed 1 row");
            }
            
            while (rsi.hasNext()){
                rsi.next();
                rsi.removeCurrentRow();
                OtherUtils.print("removed 1 MORE row");
            }
            
        } catch (NullPointerException npe){
            
            print("ERROR - NPE - in removeAllRows " + iteratorName + " error: " + npe.getMessage());
            
        }
        
    }
    
    public static Row getRow(Row[] rows, String index){
            
        print("searching for row with idx " + index);
        
        for (Row row : rows) {
            
            String currentRowIdx = row.getAttribute(0).toString();            
            print("currentRowIdx=" + currentRowIdx);
            
            if (currentRowIdx.equals(index)){
                print ("inside row with index " + index);

                return row;
            }
            
        }
        
        return null;
    }
    
    public static boolean addressIsAvailable(String address) {                                                                                                                                                                                                 
        try {
               //make a URL to a known source
               URL url = new URL(address/*"http://www.google.com"*/);

               //open a connection to that source
               HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

               //trying to retrieve data from the source. If there
               //is no connection, this line will fail
               Object objData = urlConnect.getContent();

           } catch (UnknownHostException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
               return false;
           }
           catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
               return false;
           }
           return true;                                                                                                                                                                                                                           
    }    
        
    
    public static Object executeBoundMethod(String methodName){
        BindingContainer bindings = getBindings();
        OperationBinding method = bindings.getOperationBinding(methodName);
        return executeBoundMethod(method);
    }
    
    public static Object executeBoundMethod(String methodName, Map<String, Object> args){
        
        BindingContainer bindings = getBindings();
        OperationBinding method = bindings.getOperationBinding(methodName);  
    
        if (args != null)    {
            
                Map paramsMap = method.getParamsMap();  
                
                Set<String> keySet = args.keySet();
                
                for(String paramName: keySet) {
                    paramsMap.put(paramName, args.get(paramName));
                }
        }
            
        
        return executeBoundMethod(method);
    }    
    
    public static Object executeBoundMethod(OperationBinding method){
        Object result = method.execute();
        if (!method.getErrors().isEmpty()) {
            
             _logger.warning("statement executed with ERRORS !!");
            
            java.util.List errorList = method.getErrors();
            for (Object item : errorList){
                _logger.warning("error: " + item);
            }
            
        }
        return result;
    }
    
    public static ViewObject getVO(String iteratorName){
        DCBindingContainer bc = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding iter = bc.findIteratorBinding(iteratorName);
        return iter.getViewObject();
    }
    
    /**
     * filters a VO on one column
     * */
    public static void filterAndApplyWithVO(String iteratorName, String columnName, String columnValue){
                  
        ViewObject viewObj = getVO(iteratorName);        

        filterAndApplyWithVO(viewObj, columnName, columnValue);
        
        refreshTableIterator(iteratorName);        
    }
    
    public static void filterAndApplyWithVO(ViewObject viewObj, String columnName, String columnValue){
        
        String dummyQ = viewObj.getQuery();
        print("filterAndApplyWithVO Query before: " + dummyQ);
        
        ViewCriteria vc = viewObj.createViewCriteria();
        ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
        
        ViewCriteriaItem criteriatem = vcRow.ensureCriteriaItem(columnName);
        criteriatem.setOperator("=");
        criteriatem.getValues().get(0).setValue(columnValue);
        vc.add(vcRow);
        
        viewObj.applyViewCriteria(vc);
        viewObj.executeQuery();
        
        dummyQ = viewObj.getQuery();
        print("filterAndApplyWithVO Query after: " + dummyQ);    
    }
    
    
    /**
     * 
     * rough first implementation - clears everything
     * TODO - clear only what was set - give column name as arg
     * */
    public static void clearFilterWithVO(String iteratorName){        

        ViewObject viewObj = getVO(iteratorName);
        
        clearFilterWithVO(viewObj);
        
        // refresh table data
        refreshTableIterator(iteratorName);
    }
    
    public static void clearFilterWithVO(ViewObject viewObj){   
        
        // clear filter
        ViewCriteria vc = viewObj.getViewCriteria();
        
        if (vc == null){
            return;
        }
        
        if (vc.getCurrentRow() != null){
            vc.removeCurrentRow();
        }
        
        while (vc.hasNext()){
            vc.next();
            vc.removeCurrentRow();
        }
                
        // re-execute query      
        viewObj.executeQuery();        
    }
    
    
    public static Object getAttributeFromCurrentRowIterator(String iteratorName, String attrName){
        
        DCIteratorBinding iterBind = (DCIteratorBinding)getBindings().get(iteratorName);
        Row currentRow = iterBind.getCurrentRow();
        
        if (currentRow != null){
        
            Object attrValueObj = iterBind.getCurrentRow().getAttribute(attrName);
            
            if (attrValueObj != null){
                String attrValue = attrValueObj.toString();
                print(attrName + " = " + attrValue);
            }
            
            return attrValueObj;
            
        } else {
            // Or raise an exception - depending on the design.
            return null;
        }
    }    
    
    /**
     * @author original author -  Frank Nimphius
     * code changed by me
     * */
    public static long filterLOV(String LOV_id, String attrNameinLOV, String attrValinLOV){
        /*
          BindingContext bctx = BindingContext.getCurrent();
          BindingContainer bindings = bctx.getCurrentBindingsEntry();
          JUCtrlListBinding lov = 
               (JUCtrlListBinding)bindings.get(fieldName);
         */
          /*
          ViewCriteriaManager vcm = 
               lov.getListIterBinding().getViewObject().getViewCriteriaManager();          
          //make sure the view criteria is cleared
          vcm.removeViewCriteria(vcm.DFLT_VIEW_CRITERIA_NAME);
          //create a new view criteria
          ViewCriteria vc = 
                 new ViewCriteria(lov.getListIterBinding().getViewObject());
          //use the default view criteria name
          //"__DefaultViewCriteria__"
          vc.setName(vcm.DFLT_VIEW_CRITERIA_NAME);
          //create a view criteria row for all queryable attributes
          ViewCriteriaRow vcr = new ViewCriteriaRow(vc);
          //for this sample I set the query filter to DepartmentId 60. 
          //You may determine it at runtime by reading it from a managed bean
          //or binding layer 
          vcr.setAttribute(attrNameinLOV, attrValinLOV);
          //also note that the view criteria row consists of all attributes 
          //that belong to the LOV list view object, which means that you can
          //filter on multiple attributes
          vc.addRow(vcr);           
          lov.getListIterBinding().getViewObject().applyViewCriteria(vc);    
          */
          JUCtrlListBinding lov = getLOV(LOV_id);
          ViewObject vo = getVOFromLOV(lov);
          
          print("row count before: " + vo.getRowSet().getEstimatedRowCount());
          
          clearFilterWithVO(vo);
          filterAndApplyWithVO(vo, attrNameinLOV, attrValinLOV);
          
          print("filterLOV vo name: " + vo.getName());
          printVOAttributelist(vo);
          
          long rowCountAfter = vo.getRowSet().getEstimatedRowCount();
          print("row count after: " + rowCountAfter);
          
          // must
          DCIteratorBinding iter = lov.getListIterBinding();  
          refreshIterator(iter);
          
          return rowCountAfter;
    }
    
    public static JUCtrlListBinding getLOV(String LOV_id){
        BindingContext bctx = BindingContext.getCurrent();
        BindingContainer bindings = bctx.getCurrentBindingsEntry();
        JUCtrlListBinding lov = 
             (JUCtrlListBinding)bindings.get(LOV_id);
        
        return lov;
    }
    
    public static ViewObject getVOFromLOV(String LOV_id){
        
        ViewObject vo = getLOV(LOV_id).getListIterBinding().getViewObject();
        
        return vo;        
    }
    
    public static String getAttrValFromCurrentRowLOV(String LOV_id, String attrName){
        ViewObject vo = getVOFromLOV(LOV_id);
        String attrVal = "" + vo.getCurrentRow().getAttribute(attrName);
        
        print("attrName=" + attrVal);
        
        return attrVal;
    }
    
    /**
     * from a list view - get the selected key
     * */
    public static Row[] getSelectedRowsFromListViewSL(String iteratorName, SelectionEvent selectionEvent){
        
        RichListView src = (RichListView) selectionEvent.getSource();
        
        RowKeySet rks = src.getSelectedRowKeys();
        
        print(rks.getClass().getName());
        
        Object o = rks.iterator().next();
        
        Object e = ((java.util.List) o).get(0);
        
        // OtherUtils.print(e.getClass().getName());
        
        Key currentKey = (Key) e;
        
        ViewObject vo = getVO(iteratorName);
        Row[] rows = vo.findByKey(currentKey, -1);
        
        return rows;
    }
    
    public static ViewObject getVOFromLOV(JUCtrlListBinding lov){
        
        ViewObject vo = lov.getListIterBinding().getViewObject();
        
        return vo;        
    }    
    
    
    public static void refreshIterator(DCIteratorBinding iter){        
        iter.executeQuery();
        iter.refresh(DCIteratorBinding.RANGESIZE_UNLIMITED);
    }
    
    public static void printVOAttributelist(ViewObject vo){
        AttributeDef[] attrDefs = vo.getAttributeDefs();
        print("atribute names");
        for (AttributeDef attrdef : attrDefs){
            print("aatribute name: " + attrdef.getName());
        }
    }
    
    public static void refreshComponent(javax.faces.component.UIComponent newTarget){
        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
        adfFacesContext.addPartialTarget(newTarget);
    }
    
    public static Row getCurrentRow(String iteratorName){
        DCIteratorBinding iterBind = (DCIteratorBinding)getDCBindings().get(iteratorName);
        Row row = iterBind.getCurrentRow();
        
        return row;
    }    
    
    public static void refreshPage() {
        System.out.println("refresh page");
        FacesContext fctx = FacesContext.getCurrentInstance();
        String refreshpage = fctx.getViewRoot().getViewId();
        System.out.println("view id" + refreshpage);
        ViewHandler ViewH = fctx.getApplication().getViewHandler();
        UIViewRoot UIV = ViewH.createView(fctx, refreshpage);
        UIV.setViewId(refreshpage);
        fctx.setViewRoot(UIV);
    }
    
    public static void resetStaleValues(UIComponent rootComponent){
        Iterator iter = rootComponent.getFacetsAndChildren();
        while (iter.hasNext()) {
            UIComponent component = (UIComponent)iter.next();
            if (component instanceof UIXEditableValue) {
               UIXEditableValue uiField = (UIXEditableValue)component;
               uiField.resetValue();
            }
            resetStaleValues(component);
        }    
    }
    
    public static String getValueFromUIBinding(ValueHolder vh){
        if(vh == null){
            print("the rich list is null");
            return null;
        }
        
        Object obj = vh.getValue();
        if (obj == null || obj.equals("")){
            print("the rich list has no value");
            return null;
        }
        return obj.toString();
    }
    
}
