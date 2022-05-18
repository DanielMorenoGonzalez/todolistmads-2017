package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.acercaDe;

public class AcercaDeController extends Controller {
    public Result acercaDe() {
        return ok(acercaDe.render());
    }
}
