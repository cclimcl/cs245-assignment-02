/**
 * This is the driver program. Structure:
 * (1) Reads in config.txt
 * (2) Reads in csv files and stores information from each service
 * (3) Interactive Program Begins: client can enter his shopping list, one item at a time
 * (4) Searches for item from each service and eliminates services accordingly
 * (5) When client is done, calculates cheapest grocery delivery service.
 *
 * @author Chiara Lim
 **/
import javax.sound.sampled.EnumControl;
import java.util.*;
import java.io.*;

public class ShoppingList {

    public static void main(String[] args) throws FileNotFoundException, NumberFormatException, NullPointerException, InputMismatchException {
        boolean done = false;
        int num_services = 3; // adjust this to accommodate more csv files
        ShoppingService[] services = new ShoppingService[num_services];
        int services_left = num_services;
        ShoppingService lastService = null;

        System.out.println("-----------------------------");
        System.out.println("\tWelcome to ALaCarte");
        System.out.println("-----------------------------");

        /* reads in the config.txt */
        File config_file = new File("config.txt");
        Scanner scanner = new Scanner(config_file);

        /* POPULATING FROM CONFIG */
        /* populates ShoppingService[] that holds basic information on each delivery service */
        int index = 0;
        while (scanner.hasNextLine()) {
            String[] config = scanner.nextLine().split("=");
            String[] tokens = config[1].split(",");
            ShoppingService service = new ShoppingService(tokens[0], tokens[1], Double.parseDouble(tokens[2]));
            services[index] = service;
            index++;
        }

        /* POPULATING FROM CSV */
        /* assign HashTables to each ShoppingService & populate by reading in respective their csv file */
        for (int i = 0; i < services.length; i++) {
            File csv_file = new File(services[i].csv_file);
            Scanner csv_scanner = new Scanner(csv_file);
            while (csv_scanner.hasNextLine()) {
                String[] csv = csv_scanner.nextLine().split(",");
                /* Add brand name */
                String brand = csv[0].toLowerCase();
                /* Add item name */
                String item = csv[1].toLowerCase();
                /* Add size */
                String[] size_str = csv[2].split(" ");
                double size = Double.parseDouble(size_str[0]);
                /* Add price */
                String price_str = csv[3].substring(1);
                double price = Double.parseDouble(price_str);
                /* Create new ShoppingItem */
                ShoppingItem new_item = new ShoppingItem(brand, item, size, price);

                /* Need to check which key to parse in hashtable.put().
                 * This is because a user can search via two options:
                 * (1) Brand name
                 * (2) Brand name + Item name
                 */
                String[] keys = new String[2];
                keys[0] = new_item.brand;
                keys[1] = new_item.brand_item;
                services[i].hashtable.put(keys, new_item);
            }
        }


        System.out.println("Let's start your shopping list.\n");
        while (!done) {
            Scanner shoppingScanner = new Scanner(System.in);
            System.out.print("Enter your next item or 'done': ");
            String key = shoppingScanner.nextLine().toLowerCase();

            if (key.equals("done")) {
                done = true;
            } else {
                System.out.print("Size: ");
                String[] str_size = shoppingScanner.nextLine().split(" ");
                double size;
                try {
                    size = Double.parseDouble(str_size[0]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Size. Try Again.");
                    System.out.print("Size: ");
                    str_size = shoppingScanner.nextLine().split(" ");
                    size = Double.parseDouble(str_size[0]);
                }

                /* SEARCHING */

                /* CASE: searching through all remaining services
                 * where there is more than one service remaining.
                 */

                ShoppingItem[] item = new ShoppingItem[num_services];
                for (int src = 0; src < num_services; src++) {
                    if (services[src].isEliminated == false) {
                        item[src] = services[src].searchItem(key, size);
                        /* This service does not have the requested item. This could be for 3 reasons:
                         * (1) The item is garbage/ no service has it.
                         * (2) This specific service does not have the item and thus, must be eliminated.
                         * (3) This is the last service left, and it does not have the item.
                         */
                        if (item[src] == null) {
                            services[src].hasItem = false;
                        }
                    }
                }

                /* CASE: RUBBISH KEY OR LAST SERVICE GOT AN INVALID ITEM */
                if (services[0].hasItem == false && services[1].hasItem == false && services[2].hasItem == false) {
                    /* CASE: last service got an invalid item */
                    if (services_left == 1) {
                        /* case where 'done' is parsed before lastService is initialized */
                        if (lastService == null) {
                            for (int i = 0; i < num_services; i++) {
                                if (services[i].isEliminated == false) {
                                    lastService = services[i];
                                    break;
                                }
                            }
                        }
                        lastService.hasItem = true;
                    } else {
                        /* reset all because item passed was not valid */
                        for (int i = 0; i < num_services; i++) {
                            if (services[i].isEliminated == false) {
                                services[i].hasItem = true;
                            }
                        }
                    }
                    System.out.println("No such item/ size.\n");
                } else {
                    /* CASE: more or one service does not have the item */
                    /* ELIMINATION ROUND */
                    for (int j = 0; j < num_services; j++) {
                        /* if the service doesn't have the item and hasn't been eliminated yet, eliminate it */
                        if (services[j].hasItem == false && services[j].isEliminated == false) {
                            services[j].isEliminated = true;
                            services_left -= 1;
                        } else {
                            /* initializes lastService if ShoppingList continues even if there is only one service left */
                            if (services_left == 1 && services[j].isEliminated == false) {
                                lastService = services[j];
                            }
                        }
                    }

                    /* POST ELIMINATION: ADD PRICES */
                    System.out.print("Quantity: ");
                    int quantity = shoppingScanner.nextInt();
                    for (int svc = 0; svc < num_services; svc++) {
                        /* if service has not been eliminated, it contains the requested item
                         * hence, we add it to the total price */
                        if (services[svc].isEliminated == false) {
                            services[svc].total_price += (quantity * item[svc].price);
                        }
                    }
                    System.out.println("Added to cart.\n");
                }
            }
        }
            /* CHECKING PRICES */

            /* check if there's an empty cart */
            boolean is_empty = true;
            for (int i = 0; i < num_services; i++) {
                if (services[i].total_price != 0.0) {
                    is_empty = false;
                }
            }

            /* these variables keep track of the cheapest service */
            double bestPrice = 0.0;
            ShoppingService bestService = null;
            if (is_empty == true) {
                System.out.println("\nYour cart was empty!");
            } else {
                /* ADD DELIVERY FEE */
                if (services_left == 1) {
                    /* If there is only one service in the running, the is no need for comparision */
                    if (lastService == null) {
                        for (int i = 0; i < num_services; i++) {
                            if (services[i].isEliminated == false) {
                                lastService = services[i];
                                break;
                            }
                        }
                    }
                    bestService = lastService;
                    bestPrice = lastService.total_price + lastService.delivery_charge;
                } else {
                    for (int i = 0; i < num_services; i++) {
                        /* if the service has not been eliminated, add delivery fee */
                        if (services[i].isEliminated == false) {
                            services[i].total_price += services[i].delivery_charge;
                            /* if bestService has not been initialized, do it */
                            if (bestService == null) {
                                bestService = services[i];
                                bestPrice = services[i].total_price;
                            }
                        }
                    }

                    for (int j = 0; j < num_services; j++) {
                        /* if the next service has a better price, update bestService */
                        if (!services[j].isEliminated && services[j].total_price < bestPrice) {
                            bestService = services[j];
                            bestPrice = services[j].total_price;
                        }
                    }
                }
                System.out.println("");
                System.out.print("Best price through " + bestService.service + ". Your total cost: $");
                System.out.printf("%.2f\n", bestPrice);
        }
        System.out.println("-------------------------------");
        System.out.println("Thank you for shopping with us!");
        System.out.println("-------------------------------");
    }
}