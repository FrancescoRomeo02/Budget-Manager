package com.example.budgetmanager.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/* ------------------------------------------------------------------
Custom Controller per gestire gli errori HTTP
Ritorna una pagina personalizzata in caso di errore 
con un messaggio diverso a seconda del codice di stato.
------------------------------------------------------------------ */

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            String errorMessage = statusCode == 404 ? "Oops! Page not found" : "Oops! An error occurred";
            model.addAttribute("statusCode", statusCode);
            model.addAttribute("errorMessage", errorMessage);

        }

        return "error"; 
    }

}