
package SymbolTable;

/*
    Data class

    Holds information of a record from
    name, block, type, variable or constant, and location.

    Class Basic Getters and Setters and Constructors
*/
public class record {
    String name;
    int block;
    char type;   // i:integer , f:float, b:boolean
    char vORc;
    int loc;
    

    record(String name, int block)
    {
        this.name = name;
        this.block = block;
    }

    public record(String name, int block, char VorC, char type,int loc) {
        this.name = name;
        this.block = block;
        this.vORc = VorC;
        this.type = type;
        this.loc = loc;
    }

    public char getvORc() {
        return vORc;
    }

    public void setvORc(char vORc) {
        this.vORc = vORc;
    }
    
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }
    
    
    
    
}
