/**
 * This class uses the data structure HashTables to organize a service's inventory.
 *
 * @author Chiara Lim
 **/
public class HashTable {
    LinkedList[] hash; /* array of the LinkedList of the values */
    int listLength;

    String[] brands; /* array of String of brand names */
    int brand_index;

    /**
     * Hashtable constructor. This initializes hash, a LinkedList[],
     * and brands, a String array[].
     * @param
     */
    public HashTable(){
        listLength = 10;
        hash = new LinkedList[listLength];

        brands = new String[1000];
        brand_index = 0;
    }

    /**
     * Calculates the hashCode index of the hash table based on the key.
     * @param key
     * @return int
     */
    int getHashCode(String key){
        /* calculate sum of ascii values */
        int len = key.length();
        int sum = 0;
        for( int i = 0; i < len; i++ ){
            sum += key.charAt(i);
        }
        /* calculate hashCode */
        int hashCode = sum % listLength;

        return hashCode;
    }

    /**
     * Inserts a key into the hash table.
     * @param keys
     * @param value
     */
    void put(String[] keys, ShoppingItem value){
        int index = getHashCode(keys[0]);

        brands[brand_index++] = keys[0];
        /* check the LinkedList for collisions */
        if( hash[index] == null ){
            LinkedList list = new LinkedList();
            hash[index] = list;
            list.add(keys, value);
        } else {
            hash[index].add(keys, value);
        }
    }

    /**
     * Finds ShopppingItem in hash[] based on the given key.
     * @param key
     * @return ShoppingItem
     * @throws NullPointerException
     */
    ShoppingItem getItem(String key, double size) throws NullPointerException {
        /* finds appropriate key based on brand name to find correct index */
        String brand = findBrandName(key);
        if( brand == null ){
            return null;
        }

        int index = getHashCode(brand);
        /* retrieves item based on index in hashtable */
        ShoppingItem item = hash[index].get(key, size);

        return item;
    }

    /**
     * Finds appropiate hashtable index based on key. Since the user can find an item
     * based on brand name or brand + item name and the ShoppingItem is only stored once,
     * this function returns brand name since both possibilities include brand name.
     * @param key
     * @return String
     * @throws NullPointerException
     */
    public String findBrandName(String key) throws NullPointerException {
        for( int i = 0; i < brands.length; i++ ){
            /* if we've searched through all brand names */
            if( brands[i] ==  null){
                return null;
            }
            /* if the key is only brand name */
            if( brands[i].equals(key) ) {
                return key;
            }
            /* if key is brand + item name, we want to return brand name */
            if( key.contains(brands[i]) ){
                return brands[i];
            }
        }
        return null;
    }
}
