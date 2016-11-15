package com.pocapp.enoro.view.beans;

import com.pocapp.enoro.view.utils.OtherUtils;

import java.io.Serializable;

import javax.faces.event.ValueChangeEvent;

import java.util.Locale;

import javax.faces.context.FacesContext;

public class LanguageBean implements Serializable {
    
    private String language;
    
    public LanguageBean() {
        language = Locale.getDefault().getLanguage(); 
        System.out.println("Constructor for LanguageBean; language = " + language);
    }

    public void languageSelectionChanged(ValueChangeEvent valueChangeEvent) {
        String newLang = "" + valueChangeEvent.getNewValue();
        
        System.out.println("new value: " + newLang);
        
        this.language = newLang;
        
        changeLocale(newLang);
        
        //OtherUtils.refreshPage();
    }
    
    private void changeLocale(String newL){
        Locale locale = new Locale(newL);  
        FacesContext context = FacesContext.getCurrentInstance();  
        context.getViewRoot().setLocale(locale);   
    }
}
