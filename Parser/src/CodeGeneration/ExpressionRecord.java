package CodeGeneration;

/*
    Expressions Records Class
    Data Class to hold type and loc.
    Used specially for expression methods. 
    Comparison between different ids.

    Class Consist of Basic Getters and Setters.
*/
public class ExpressionRecord {
    char type;
    int loc;
    
    public ExpressionRecord()
    {
        this.type = '\0';
        this.loc = 0;
    }
    public ExpressionRecord(char type, int loc)
    {
        this.type = type;
        this.loc = loc;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }
    
}
