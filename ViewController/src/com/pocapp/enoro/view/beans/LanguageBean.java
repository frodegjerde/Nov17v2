package com.pocapp.enoro.view.beans;

import com.pocapp.enoro.view.utils.OtherUtils;

import java.io.Serializable;

import javax.faces.event.ValueChangeEvent;

import java.util.Locale;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class LanguageBean implements Serializable {
    @SuppressWarnings("compatibility:2092157638886270800")
    private static final long serialVersionUID = 1L;

    private String language = "en";
    private Locale locale = Locale.getDefault();

    public String getLanguage() {
        return language;
    }

    public Locale getLocale() {
        return locale;
    }

    public LanguageBean() {
        language = Locale.getDefault().getLanguage(); 
        System.out.println("Constructor for LanguageBean; language = " + language);
    }

    public void languageSelectionChanged(ValueChangeEvent valueChangeEvent) {
        String newLang = "" + valueChangeEvent.getNewValue();
        
        System.out.println("new value: " + newLang);
        
        this.language = newLang;
        
        changeLocale(newLang);
        
        //ResourceBundle.clearCache();
        OtherUtils.refreshPage();
    }
    
    private void changeLocale(String newL){
        locale = new Locale(newL);  
        FacesContext context = FacesContext.getCurrentInstance();  
        context.getViewRoot().setLocale(locale);   
        
        //OtherUtils.refreshComponent(context.getViewRoot());
        
        //OtherUtils.resetStaleValues();
    }
    
    public String getCurrentLanguage(){
        if (language.equals("no")){
            return "Norska";
        }
        if (language.equals("en")){
            return "English";
        }
        return "";
    }
}
