package controler;

import annotation.Controller;
import annotation.WebMapping;

@Controller
public class HomeContoller {

    @WebMapping(url = "/accueil")
    public String afficherAccueil() {
        return "Bienvenue sur la page d'accueil !";
    }

    @WebMapping(url = "/liste")
    public String afficherListe() {
        return "Voici la liste des éléments.";
    }

    @WebMapping(url = "/contact")
    public String afficherContact() {
        return "Page de contact : email@example.com";
    }
}
