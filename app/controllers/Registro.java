package controllers;

import play.data.validation.Constraints;

// Usamos los constraints para que se validen automáticamente
// en el formulario: https://www.playframework.com/documentation/2.5.x/JavaForms
public class Registro {
    @Constraints.Required
    @Constraints.MaxLength(20)
    public String login;
    @Constraints.Required
    @Constraints.MaxLength(30)
    @Constraints.Email
    public String email;
    @Constraints.Required
    @Constraints.MaxLength(20)
    public String password;
    @Constraints.Required
    public String confirmacion;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(String confirmacion) {
        this.confirmacion = confirmacion;
    }
}
