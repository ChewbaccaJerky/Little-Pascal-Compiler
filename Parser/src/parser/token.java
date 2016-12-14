
package parser;

/*
POJO token
With simple getters and setters.
*/
public class token {
    private String lexeme;
    private int num;

    public token(String lexeme, int tokenNumber) {
        this.lexeme = lexeme;
        this.num = tokenNumber;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int tokenNumber) {
        this.num = tokenNumber;
    }
    
    public String toString()
    {
        return  this.num + " " + this.lexeme;
    }
    
    
}
