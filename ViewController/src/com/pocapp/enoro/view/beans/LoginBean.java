package com.pocapp.enoro.view.beans;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LoginBean {
    public LoginBean() {
    }
    
    private String user;
    private String pwd;

    public String doLogin() {
        
        System.out.println("do login!");

        String un = user;
        byte[] pw = pwd.getBytes();
        FacesContext fctx = FacesContext.getCurrentInstance();


        if (un == null || pw == null) {
            showError("Invalid credentials", "An incorrect username or password was specified.", null);
        } else {

            ExternalContext ectx = fctx.getExternalContext();
            HttpServletRequest request = (HttpServletRequest) fctx.getExternalContext().getRequest();


            try {
                request.login(user, pwd); // Servlet 3.0 login
                user = null;
                pwd = null;
                String loginUrl = ectx.getRequestContextPath() + "/adfAuthentication?success_url=/faces/untitled1.jsf";
                loginUrl = ectx.getRequestContextPath() + "/faces/untitled1.jsf";

                redirect(loginUrl);
                HttpSession userSession = (HttpSession) ectx.getSession(true);
                userSession.setAttribute("serverName", request.getServerName());
                userSession.setAttribute("serverPort", request.getServerPort());
                System.out.println("Host info is " + userSession.getAttribute("serverName") + ":" +
                                   userSession.getAttribute("serverPort"));
         

            } catch (ServletException fle) {
                fle.printStackTrace();
                showError("ServletException", "Login failed. Please verify the username and password and try again." //+ 
                                              //fle.getMessage() + " " + Arrays.toString(fle.getStackTrace())
                                              ,
                          null);
            }


        }
        return null;
    }
    
    public String doLogout() {
      FacesContext fctx = FacesContext.getCurrentInstance();
      ExternalContext ectx = fctx.getExternalContext();
      String url = ectx.getRequestContextPath() + 
                 //"/adfAuthentication?logout=true&end_url=/faces/login.jsf";     
                 "/adfAuthentication?logout=true&end_url=/faces/login.jsf";     
      //url =  ectx.getRequestContextPath() + "/faces/login.jsf";

      try {
        ectx.redirect(url);
      } catch (IOException e) {
        e.printStackTrace();
      }
      fctx.responseComplete();
      return null;
    }
    

    private void redirect(String forwardUrl) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = fctx.getExternalContext();
        try {
            ectx.redirect(forwardUrl);
        } catch (IOException ie) {
            showError("IOException", "An error occurred during redirecting. Please consult logs for more information.",
                      ie);
        }
    }

    private void showError(String errType, String message, Exception e) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, errType, message);
        FacesContext.getCurrentInstance().addMessage("d2:it35", msg);
        if (e != null) {
            e.printStackTrace();
        }
    }

    private void reportUnexpectedLoginError(String errType, Exception e) {
        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unexpected error during login",
                             "Unexpected error during login (" + errType + "), please consult logs for detail");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        e.printStackTrace();
    }

    public String logoff() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = ctx.getExternalContext();
        HttpServletRequest httpRequest = (HttpServletRequest) ectx.getRequest();
        try {
            httpRequest.logout(); // Servlet 3.0 logout
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            String logoutUrl = ectx.getRequestContextPath() + "/faces" + ctx.getViewRoot().getViewId();
            redirect(logoutUrl);
        } catch (ServletException e) {
            showError("ServletException", "An error occurred during logout. Please consult logs for more information.",
                      e);
        }
        return null;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return pwd;
    }
}
