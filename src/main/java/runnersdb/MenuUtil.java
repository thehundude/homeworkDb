package runnersdb;

import java.util.Scanner;

/**
 * Created by tamas on 2017. 06. 28..
 */
public class MenuUtil {
    public static void mainMenu (boolean canEdit) {
        System.out.print("\n");
        System.out.println("Menüpontok");
        System.out.println("[1] A klub összes futója");
        System.out.println("[2] Legjobb futók listája");
        System.out.println("[3] Versenyeredmények");
        System.out.println("[4] Trénerek eredményei");

        // ha nincs szerkesztési jogosultság, akkor ez nem is fog látszani
        if (canEdit == true) {
            System.out.println("[5] Versenyeredmény felvétele");
            System.out.println("[6] Meglévő futó adatainak módosítása");
            System.out.println("[7] Új futó hozzáadása");
        }

        System.out.print("\nVálassz menüpontot:");

        Scanner reader = new Scanner(System.in);


        int chosenMenu = reader.nextInt();

        switch (chosenMenu) {
            case 1:
                Athlete.clubAthletes();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                if (canEdit == false) {
                    System.out.println("Érvénytelen menüpont!");
                }
                break;
            case 6:
                if (canEdit == false) {
                    System.out.println("Érvénytelen menüpont!");
                }
                break;
            case 7:
                if (canEdit == false) {
                    System.out.println("Érvénytelen menüpont!");
                }
                else {
                    Athlete.addAthlete();
                }
                break;
            default:
                System.out.println("Érvénytelen menüpont!");
        }
    }
}
