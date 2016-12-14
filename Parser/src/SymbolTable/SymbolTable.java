/**
 *  John Lugtu
 *  CS4110
 *  Symbol Table Assignment
 */
package SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    SymbolTable

    The Datastructure that I've choosen for mini pascal is the HashMap. Working
    with a HashMap allows quick access to tokens when searching. Relies on key-
    value pairs to search. Advantages to using HashMap is speed. To hold the 
    symbols will be the Java's Collection ArrayList. Built similiar to a 
    LinkedList also allows dynamic appending and deleting.

    Using a hashmap I can set or seach by a key. With the key I can use to
    access a location in the HashMap that will either be empty or has an
    ArrayList that either has a value you are searching for or ArrayList you are
    inserting to.
*/
public class SymbolTable {
    
    private static HashMap<Integer, ArrayList<record>> ST = new HashMap();
    /*
        hash
    
        The hash function gets it's value by each character in the string and 
        getting it's numerical value and returning the sum mod the hashSize.
        That will be the hash value.
    */
    public int hash(String record)
    {
        final int hashSize = 13;
        char[] tokens = record.toCharArray();
        int value = 0;
        
        for(char symbol : tokens)
        {
            value += ((int)symbol);
        }
        return value % hashSize;
    }
    
    /*
        find_current_scope
    
        This routine uses the hash function to get the ArrayList within the 
        HashMap. Once the ArrayList is found, it iterates the 
        arraylist and checks if the symbol if found with the same activeBlock.
        Returns a record of the symbol that contains name, type, block, Variable
        Constant, and location.
    */
    public record find_current_scope(String symbol,int activeBlock)
    {
        ArrayList<record> tempList;

        //Checks if token exists in the HashMap
        if((tempList = ST.get(hash(symbol))) != null)
        {
                    for(record rec:tempList)
                    {
                        if((rec.getName().equals(symbol))&&(rec.getBlock()==activeBlock))
                        {
                            return rec;
                        }
                    }
        }
        return null;
    }
    
    /*
        find_all_scope
    
        Uses te hash function to return value to search for the an ArrayList
        that potentially has the symbol. Once the ArrayList is found it starts
        it iterate through the list searching for the symbol to see if it 
        is contained in the symbol table.
        Returns a record of the symbol that was found containing name, type, 
        block, Variable and Constant, and location.
    */
    public record find_all_scope(String symbol, int activeBlock)
    {
        ArrayList<record> tempList;
        record tempToken;
        
        for(Map.Entry<Integer,ArrayList<record>> entry : ST.entrySet())
        {
            tempList = entry.getValue();
            
                for(int i = 0; i< tempList.size(); i++)
                {
                    tempToken = tempList.get(i);
                    if(tempToken.getName().equals(symbol))
                    {
                        return new record(symbol,activeBlock);
                    }
                    
                }
            
        }
        return null;
    }
    
    /*
        @name: insert
        @purpose: Creates necessary arrayList when there is an existing array list,
                  copies and overwrite existing one. If there is no arrayList hashMap
                  slot, creates a new one.
    */
    public void insert(String symbol, int activeBlock, char vORc, char type, int loc)
    {
        ArrayList tempList;
        //Add to an exisiting ArrayList
        if(ST.containsKey(hash(symbol)))
        {
            tempList = (ArrayList)ST.get(hash(symbol));
            tempList.add(new record(symbol,activeBlock,vORc,type, loc));
            ST.put(hash(symbol), tempList);
        }
        //Create a new ArrayList
        else
        {
            tempList = new ArrayList();
            tempList.add(new record(symbol,activeBlock,vORc,type, loc));
            ST.put(hash(symbol), tempList);
        }
    }
    
    /*
        Display
    
        Iterates through each filled spot in the HashMap that has an ArrayList.
        If an array list is found print and iterate until the end of the Array
        List. If it goes to the end iterate to the next filled spot in the Hash
        Map, repeat until end of HashMap.
    
    */
    public void display()
    { 
        ArrayList<record> tempList;
        record temp;
        
        System.out.println("------------Symbol Table----------------------------------------------");
        System.out.printf("%s %15s %15s %15s %15s \n",
                                          "Name",
                                           "Scope",
                                           "V or C",
                                           "Type",
                                           "Location");
        System.out.println("----------------------------------------------------------------------");
        for(Map.Entry<Integer,ArrayList<record>> entry : ST.entrySet())
        {
            tempList = entry.getValue();
            
                for(int i = 0; i< tempList.size(); i++)
                {
                    temp = tempList.get(i);
                    if(!temp.getName().equals(""))
                    {
                        System.out.printf("%s %15d %15c %15c %15d %n",
                                          temp.getName(),
                                           temp.getBlock(),
                                           temp.getvORc(),
                                           temp.getType(),
                                           temp.getLoc()
                                           );
                    }
                    
                }
            
        }
    }
    
}
