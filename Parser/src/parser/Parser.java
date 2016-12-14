/*
    John Lugtu
    COMPILER DESIGN
    LITTLE PASCAL COMPILER
*/
package parser;

import CodeGeneration.CodeGenerator;
import CodeGeneration.ExpressionRecord;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import SymbolTable.*;
import java.util.ArrayList;

/*
    PARSER
        Within Little Pascal's grammar the program checks whether or not
        if the source code has a parse tree. And within the routines it also
        generates code.

TODO:
    []Incorporate Floats and Booleans
    []NOT
    
    BUGS:
        []There is an issue with my WHILE LOOP GENERATION
        []Read statement needs to pause to get info.
*/
public class Parser {
    
/*******************Constants Class Variables**********************************/
    
    final int IDTOK = 1;                //Identifier
    final int LITTOK = 2;               //Number, True, or False
    final int STRLITTOK = 3;            //String
    final int ASGNTOK = 4;              //Assignment                :=
    
    final int LPARENTOK = 5;            //Left Parenthesis          (
    final int RPARENTOK = 6;            //Right Parenthesis         )
    final int COLONTOK = 7;             //Colon                     :
    final int SEMICOLONTOK = 8;         //Semicolon                 ;
    
    final int DOTTOK = 9;               //Dot                       .
    final int ADDOPTOK = 10;            //Add Operation             + -
    final int MULOPTOK = 11;            //Mul Operation             * /
    final int RELOPTOK = 12;            //Rel Operation             < > =
    //KEYWORDS
    //********
    final int PROGTOK = 13;             //Program
    final int BEGINTOK = 14;            //Begin
    final int ENDTOK = 15;              //end
    final int CONSTOK = 16;             //const
    final int VARTOK = 17;              //var
    final int WHILETOK = 18;            //while
    
    final int DOTOK = 19;               //do
    final int IFTOK = 20;               //if
    final int THENTOK = 21;             //then
    final int BASETYPETOK = 22;         //integer boolean float
    //        ADDTOK = 10               //or
    //        MULTOK   11               //mod 
    
    final int NOTTOK = 23;              //not
    final int WRITETOK = 24;            //writeln write
    final int READTOK = 25;             //read
    //        NUMTOK  = 2;              //true false
    final int EOFTOK = -1;              //EOF
    /*************************************************************************/
    
    //STATIC OBJECTS
    static Parser parser;
    static SymbolTable ST;
    static CodeGenerator CG;
    static ExpressionRecord er;
    static ArrayList<String> stringList;
    static BufferedReader br;           //read from file
    
    //CLASS VARIABLES
    int currScope = 0;
    int curOffset = 0;
    int writeIndex = 0;
    int labelNum = 0;
    char value;
    boolean ErrorFlag = false;
    token tempTok;
    record Str_Ptr;
    
    
    
    
    
    /*
        MAIN
        (i)Main opens and closes the file for reading and closes the file for
        writing.
        (ii) This where compiler begins.
        (iii) Parser starts which consist of the Scanner, Parser, and Code Gen.
    */
    public static void main(String[] args) {
        
        String filename = "src/SourceCode.txt";
        String fileOutput = "src/MipsCode.s";
        parser = new Parser();
        CG = new CodeGenerator();
        stringList = new ArrayList();
        ST = new SymbolTable();
        er = new ExpressionRecord();
        
        try{
            
            br = new BufferedReader((new FileReader(filename)));
            parser.program();
            CG.Postlog(stringList);
            System.out.print("\n\n");
            ST.display();
            br.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
    
    /*
        Match
        (i)Handles Comparison of tokens.
        (ii)Retrieves the next token.
        (iii) Checks if there is an error and sets flag if it does.
    */
    private boolean match(int tTok, int tokNum)
    {
        if(tTok == tokNum)
        {
            //System.out.println(tempTok.getLexeme());
            tempTok = FindToken();
            return true;
        }
        else
            System.out.println("ERROR HERE! at " + tempTok.getLexeme());
            ErrorFlag = true;
        return false;
    }
    
    
    
    
    
     /*
        Checks string type to see whether it is 
        an integer, float, or boolean and return 
        a char corresponding to the type.
    */
    public char CheckType(String type)
    {
        
        if(type.contains("integer"))
            return 'i';
        else if(type.contains("float"))
            return 'f';
        else if(type.contains("boolean"))
            return 'b';
        else
            return '\0';
    }
    
    
    
    
    
    
    /*
        CheckIntOrFloat
        Checks if it is a int or float and returns a correspoinding char
    */
    public char CheckIntOrFloat(String num)
    {
        char type = '\0'; //error
 
        if(String.valueOf(num).trim().matches("[0-9]+"))
        {
            type = 'i';   //integer
        }
        else if(String.valueOf(num).trim().matches("[0-9]*.[0-9]*"))
        {
            type = 'f';   //float
        }
        //System.out.println(type);
        return type;
    }
    
    
    
    /*************************************************************************/
    /***************************GRAMMAR**************************************/
    
    
    
    /*
        program
        The beginning of the parser. Follows a strict grammar if there's an 
        error, ErrorFlag is set.
    
        (1)IDTOK ‘(‘‘)’CONSTPART VARPART BEGTOK stat morestats  ENDTOK IDTOK'.'    
    */
    public void program()
    {
        //start program and get first character
        tempTok = FindToken();
        
        System.out.print("1 ");
        
        match(tempTok.getNum(), PROGTOK);
        match(tempTok.getNum(), IDTOK);
        match(tempTok.getNum(), LPARENTOK);
        match(tempTok.getNum(), RPARENTOK);
        constpart();
        varpart();
        match(tempTok.getNum(), BEGINTOK);
        statmt();
        morestats();
        match(tempTok.getNum(), ENDTOK);
        match(tempTok.getNum(), DOTTOK);
    }
    
    
    
    
    
    
    /*
        constpart
        Recognizes there are constants in the code.
        Begins preparing to store constants into Symbol Table
    
        (2)CONSTPART:  CONSTTOK  constdecl moreconsdecls     |  <empty>
    */
    private void constpart()
    {
        
        if(tempTok.getNum() == CONSTOK)
        {
            System.out.print("2 ");
            match(tempTok.getNum(), CONSTOK);
            constdecl();
            moreconstdecls();
        }
        //else do nothing
    }
    
    
    
    
    
    
    /*
        varpart
        Handles variables
        Begins preparing to store variables into Symbol Table.
        (3)VARPART:  VARTOK vardecl morevardecls  |  <empty>
    */
    private void varpart()
    {
        if(tempTok.getNum() == VARTOK)
        {
            System.out.print("3 ");
            match(tempTok.getNum(), VARTOK);
            vardecl();
            morevardecls();
        }
        //else do nothing
    }
    
    
    
    
    
    /*
        moreconstdecls
        If there are more constants. It will declare more.
    
        (4)moreconstdecls:  constdecl moreconsdecls       |    <empty>
    */
    private void moreconstdecls()
    {
        if(tempTok.getNum() == IDTOK)
        {
            System.out.print("4 ");
            constdecl();
            moreconstdecls();
        }
        // else do nothing
    }
    
    
    
    
    
    
    /*
        morevardecls
        If there are more variables, it will begin declaring more.
        (5)morevardecls: vardecl morevardecls  |    <empty>
    */
    private void morevardecls()
    {
        if(tempTok.getNum() == IDTOK)
        {
            System.out.print("5 ");
            vardecl();
            morevardecls();
        }
        //else do nothing
    }
    
    
    
    
    
    
    /*
        vardecl
        Declaration of vardecls placing them into Symbol Table and checking the
        different scopes if the variable exsists.
    
        (6)vardecl:   IDTOK ':' BASTYPETOK  ';'
    */
    private void vardecl()
    {
        System.out.print("6 ");
        
        String name = tempTok.getLexeme();
        match(tempTok.getNum(), IDTOK);
        
        match(tempTok.getNum(), COLONTOK);
    
        char type = CheckType(tempTok.getLexeme());
        //System.out.println(type);
        match(tempTok.getNum(), BASETYPETOK);
        match(tempTok.getNum(), SEMICOLONTOK); 
        
        if(ST.find_current_scope(name, currScope) == null)
        {
            ST.insert(name, currScope, 'v', type, curOffset);
            curOffset = curOffset - 4;
        }
        else
        {
            System.out.printf("ERROR: Id %s already exists in scope %d %n",name,currScope);
            ErrorFlag = true;
        }
    }
    
    
    
    
    
    
    /*
        constdecl
        Declares constants and checks Symbol Table if they exists.
        (7)constdecl:  IDTOK '=' LITTOK ';'
    */
    private void constdecl()
    {
        System.out.print("7 ");
        String name = tempTok.getLexeme();
        match(tempTok.getNum(), IDTOK);
        match(tempTok.getNum(), RELOPTOK);
        
        String num = tempTok.getLexeme();
        char type = CheckIntOrFloat(num);
        match(tempTok.getNum(), LITTOK);
        match(tempTok.getNum(), SEMICOLONTOK);
        
        if(ST.find_current_scope(name, currScope) == null)
        {
            ST.insert(name, currScope, 'c', type,curOffset);
            curOffset = curOffset - 4;
        }
        else
        {
            System.out.printf("ERROR: Id %s already exists in scope %d %n"
                              ,name
                              ,currScope);
            ErrorFlag = true;
        }
    }
    
    
    
    
    
    
    /*  
         morestats
         Additional statements if needed.
    
        (8)morestats:   ‘;” statmt   morestats    |    <empty>
    */
    private void morestats()
    {
 
        if(tempTok.getNum() == SEMICOLONTOK)
        {
            System.out.print("8 ");
            match(tempTok.getNum(),SEMICOLONTOK);
            statmt();
            //System.out.println(tempTok.getLexeme());
            morestats();
        }
    }
    
    
    
    
    
    
    /*
        statmt
        Differentiates which stat is being used.
    
        (9)statmt:  assignstat  |  ifstat   |  readstat   |  writestat
                  |  blockst   | whilest  

    */
    private void statmt()
    {
        System.out.print("9 ");
        //System.out.println(tempTok.getLexeme());
        switch(tempTok.getNum())
        {
            case IDTOK:     assignstat();
                            break;
                        
            case IFTOK:     ifstat();
                            break;
                        
            case READTOK:   readstat();
                            break;
                          
            case WRITETOK:  writestat();
                            break;
                            
                
            case VARTOK:    
            case BEGINTOK:  blockst();
                            break;
                           
            case WHILETOK:  whilest();
                            break;
            default: System.out.println("\nFAILS HERE AT: " + tempTok.getLexeme());
                     ErrorFlag = true;
            
        }
    }
    
    
    
    
    
    
    /*
        assignstat
        Assigns IDs an expression.
        Beginning of code generations. Placing variables into individual slots
        in the frame pointer.
    
        (10)assignstat:  idnonterm  ASTOK express
    */
    private void  assignstat()
    {
        System.out.print("10 ");

        
        idnonterm();
        if(Str_Ptr != null)
        {
            //System.out.println(Str_Ptr.getName());
            int strLoc = Str_Ptr.getLoc();
            match(tempTok.getNum(), ASGNTOK);
            express();
            
                CG.MoveVariable(er.getLoc(), strLoc);
        }
        
    }
    
    
    
    
    
    /*
        ifstat
        Begins and generates if statments.
    
        (11)ifstat:  IFTOK express THENTOK  stat 
    */
    private void ifstat()
    {
        System.out.print("11 ");
        
        match(tempTok.getNum(), IFTOK);
        express();
        CG.genIF(curOffset,labelNum);
        match(tempTok.getNum(), THENTOK);
        statmt();
        CG.genLabel("ELSE",labelNum);
        labelNum++;
    }
    
    
    
    
    
    
    /*
        ********************NOTE***********************************
        NEED TO IMPLEMENT FLOAT AND BOOLEANS.
    
        (12)readstat:READTOK '(' idnonterm ')' 
    */
    private void readstat()
    {
        System.out.print("12 ");
        
        match(tempTok.getNum(), READTOK);
        match(tempTok.getNum(), LPARENTOK);
        
        idnonterm();
        if(er.getType() == 'i')
        {
            //System.out.println(Str_Ptr.getName());
            CG.genRead('i', er.getLoc());
        }
        match(tempTok.getNum(), RPARENTOK);
    }
    
    
    
    
    
    /*
        writestat
        Generates code so assembly language can write. If it's writeln generates
        newline in assembly code.
    
        (13)writestat: WRITETOK '('  writeexp ')'
    */
    private void writestat()
    {
        System.out.print("13 ");
        
        //checks if writeln, if so adds a newline
        if(tempTok.getLexeme().trim().equals("writeln"))
            CG.newline();
        
        match(tempTok.getNum(), WRITETOK);
        match(tempTok.getNum(), LPARENTOK);
        writeexp();
        match(tempTok.getNum(), RPARENTOK);
    }
    
    
    
    
    
    
    /*
        whilest
        Generates while statement with the label at the top of the block.
    
        (14)whilest:WHILETOK express DOTOK 
    */
    private void whilest()
    {
        System.out.print("14 ");
        
        match(tempTok.getNum(), WHILETOK);
        CG.genLabel("WHILE",labelNum);
        express();
        CG.genWHILE(curOffset, labelNum);
        match(tempTok.getNum(), DOTOK);
        statmt();
        //CG.genWHILE(curOffset, labelNum);
        CG.getJUMP("WHILE",labelNum);       //jumps to while
        CG.genLabel("ENDWHILE", labelNum);
    }
    
    
    
    
    
    /*
        blockst
        Equivalent to brackets in c++. Results in nesting scopes.
    
        (15)blockst:varpart BEGINTOK   stats   ENDTOK    // revised to add decls
    */
    private void blockst()
    {
        System.out.print("15 ");
        currScope++;
        constpart();
        varpart();
        match(tempTok.getNum(), BEGINTOK);
        statmt();
        morestats();
        match(tempTok.getNum(), ENDTOK);
        //currScope--;
    }
    
    
    
    
    
    /*
        writeexp
        Either writing a String Litteral or an Expression
    
        (16)writeexp: STRLITTOK  |  express
    */
    private void writeexp()
    {
        
        System.out.print("16 ");
        if(tempTok.getNum() == STRLITTOK)
        {
            stringList.add(tempTok.getLexeme());
            match(tempTok.getNum(),STRLITTOK);
            CG.genWriteString(writeIndex);               //print string lit
            writeIndex++;
        }
        else
        {
            express();
            // NEED TO FIX. Only handles arthmetic expressions like
            // a + b * 3 * 5 but has problem with simple write(a)
            CG.writeExp(curOffset);         //points to value recently stored from
                                            //expression
        }
    }
    
    
    
    
    
    /*
        (17)express: term expprime       
    */
    private void express()
    {
        System.out.print("17 ");
        term();
        expprime();        
    }
    
    
    
    
    
    
    /*
        expprime
        Generates AddOperation Assembly Code.
    
        (18)expprime:ADDOPTOK  term expprime   |  <empty>  
    */
    private void expprime()
    {
        
        if(tempTok.getNum() == ADDOPTOK)
        {
    
            System.out.println("18 ");
            
            //ExpressionRecord lop = er;
            int loc = er.getLoc();
            char ch = tempTok.getLexeme().charAt(0);
            String st = "";
            switch(ch)
            {
                case '+': st = "add";
                          break;
                case '-': st = "sub";
                          break;
                case 'o': st = "or";
                          break;
            }
            match(tempTok.getNum(),ADDOPTOK);
            term();
            CG.AddOp(loc, er.getLoc(), st, curOffset);
            //curOffset = curOffset + 4;
            expprime();
            
            
        }
    }
    
    
    
    
    
    
    /*
        (19)term:relfactor termprime
    */
    private void term()
    {
        System.out.print("19 ");
        relfactor();
        termprime();
    }
    
    
    
    
    
    
    /*
        termprime
        Generates MulOperation Assembly code
    
        (20)termprime: MULOPTOK  relfactor termprime  |  <empty> 
    */
    private void termprime()
    {
        
        if(tempTok.getNum() == MULOPTOK)
        {
            System.out.println("20 ");
            int loc = er.getLoc();
            char ch = tempTok.getLexeme().charAt(0);
            String st = "";
            switch(ch)
            {
                case '*':   st = "mult";
                            break;
                case '/':   st = "div";
                            break;
                case 'm':   st = "mod";
            }
            match(tempTok.getNum(),MULOPTOK);
            relfactor();
            CG.MulOp(loc, er.getLoc(), st, curOffset);
            termprime();
        }
        //else do nothing
    }
    
    
    
    
    
    
    /*
        (21)relfactor: factor factorprime
    */
    private void relfactor()
    {
        System.out.print("21 ");
        factor();
        factorprime();
    }
    
    
    
    
    
    
    
    /*
        factorprime
        Generates RELOPTOK code.
    
        (22)factorprime: RELOPTOK  factor          |  <empty>
    */
    private void factorprime()
    {
        
        if(tempTok.getNum() == RELOPTOK)
        {
            System.out.print("22 ");
            int loc = er.getLoc();
            char ch = tempTok.getLexeme().charAt(0);
            String st = "";
            switch(ch)
            {
                case '<':   st = "lessThan";
                            break;
                case '>':   st = "greaterThan";
                            break;
                case '=':   st = "equalTo";
                            break;
            }
            match(tempTok.getNum(),RELOPTOK);
            factor();
            CG.RelOp(loc, er.getLoc(), st, curOffset);
        }
    }
    
    
    
    
    
    
    /*
    *******************NOTES************************
    Need to add not Code generation to negate factor
    
        (23)factor:NOTTOK   factor             
                  |  idnonterm        
                  |  LITTOK
                  |  '('  express  ')' 

    */
    private void factor()
    {
        System.out.print("23 ");

        switch(tempTok.getNum())
        {
            case NOTTOK:    match(tempTok.getNum(),NOTTOK);
                            factor();
                            break;
                            
            case IDTOK:     idnonterm();
                            //SETTING ER HERE
                            er.setType(Str_Ptr.getType());
                            er.setLoc(Str_Ptr.getLoc());
                            break;
                            
            case LITTOK:    //SETTING ER HERE
                            er.setType(CheckIntOrFloat(tempTok.getLexeme()));
                            er.setLoc(curOffset);
                            CG.StoreImmediate(er, tempTok.getLexeme());
                            curOffset = curOffset - 4;
                            
                            match(tempTok.getNum(),LITTOK);
                            break;
                            
            case LPARENTOK: match(tempTok.getNum(),LPARENTOK);
                            express();
                            match(tempTok.getNum(),RPARENTOK);
                            break;
                            
            default: System.out.println("FAILS HERE");
        }
        
    }
    
    /*
        idnonterm
        Retrieval of an id.
    
        (24)idnonterm: IDTOK
    */
    private void idnonterm()
    {
        System.out.print("24 ");
        record re;
        String name = tempTok.getLexeme();
        match(tempTok.getNum(),IDTOK);
        
        //check if Symbol resides in current scope.
        if((re = ST.find_current_scope(name, currScope)) == null)
        {
                if((re = ST.find_all_scope(name, currScope)) == null)
                    System.out.printf("ERROR: %s does not exist %n",name);
                else
                    Str_Ptr = re; //checking other scopes.
        }
        else
        {
            Str_Ptr = re;           //Sets Str_Ptr to the record that was found
                                    //in current record.
            
        }

    }
    
/******************************************************************************/
/*******************************SCANNER****************************************/    
    
    /*
        GETS CHAR
    */
    private void getChar()
    {
        try
        {
            value = (char)br.read();
            //currPosition++;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
   
/*
        FindToken
        PURPOSE: Using LPAS( little PASCALS) DFA check for tokens
                 return token with lexemene and tokenNumber
        *Corresponding Token Numbers guide can be found in token class.*
    */
    private token FindToken()
    {
        int index;   // To keep track of length of digit or identifier.
        String temp = ""; //Use to convert lexeme to string.                                      
        char[] lexeme = new char[32];// 32 because most names won't past that.
                                                  
        
        try{
            
            getChar();
            
            
            //skip white space
            while(value == ' ' || value == '\n' || value == '\t')
            {
                getChar();
            }
            
                        //skip comments
            if(value == '(')
            {
                br.mark(1);
                //System.out.println(value);
                getChar();
                
                switch(value)
                {

                    case '*':   getChar();
                                while(value != ')')
                                {

                                    getChar();

                                }
                                getChar();
                                FindToken();
                                break;

                    default: 
                        br.reset();
                        return new token("(", LPARENTOK);

                }
                           //System.out.println(value);
            }
            
            //STRLIKTOK
            if(value == '"' || value == '\'' || value == '\"')
            {
                //System.out.println("here");
                getChar();
                while(value != '\"')
                {
                    temp = temp.concat(String.valueOf(value));  
                    getChar();
                }
                //System.out.println(temp);
                return new token(temp, STRLITTOK);
            }
            
            
            
            switch(value)
            {
		case ':':
                        br.mark(0);          //sets check point
                        getChar();
                        
                        if(value == '=')                
                            return new token(":=", ASGNTOK);
                        else
                        {
                            //currPosition++;
                            br.reset();                 // return to check point
                            return new token(":", COLONTOK);
                        }
				
                case '(':   return new token("(",LPARENTOK);
				
		case ')':   return new token(")",RPARENTOK);
            
		case ';':   return new token(";",SEMICOLONTOK);
			
		case '.':   return new token(".",DOTTOK);
						
		case '+':
		case '-':   return new token(String.valueOf(value),ADDOPTOK);
						 
		case '*':
		case '/':   return new token(String.valueOf(value),MULOPTOK);
				
		case '<':
		case '>':   
		case '=':   return new token(String.valueOf(value), RELOPTOK);
            
            }
            
            // checks identifier, string and true/false
            if(((String.valueOf(value)).matches("[a-zA-Z]")))
            {
                lexeme[0] = value;              // inserts first value to char[]
                int tNumber;
                br.mark(1);          //sets checkPoint
                value = (char)br.read();
                
                // Checks if the next String is valid if not return to CheckPoint
                // and returns
                if((String.valueOf(value).matches("[^a-zA-Z0-9_]")))
                {
                    br.reset();
                    temp = new String(lexeme);
                    return new token(temp.replace(" ", ""),IDTOK);
                }
                
                
                //Loop until the end of identifier/string
                for(index = 1; String.valueOf(value).matches("[a-zA-Z0-9_]"); index++)
                {
                    br.mark(1);
                    lexeme[index] = value;
                    value = (char)br.read();
                }
                
                // once at the end of identifier/string resets to checkPoint
                // skips forward to the end of identifer/string.
                br.reset();
                //currPosition--;  //update current positin in file
                temp = new String(lexeme);
                
                //CheckKeyword and return tokenNumber and remove whitespace in 
                // char Array
                tNumber = CheckKeyword(temp.replace(" ", ""));
                return new token(temp, tNumber);
                
            }
            
            // collect consecutive digits
            if((String.valueOf(value)).matches("[0-9]"))
            {
                lexeme[0] = value;          //sets first value
                br.mark(1);      // sets Check Point
                value = (char)br.read();
                
                
                //if next value does not match number or '.'
                //reset to checkpoint and return token
                if((String.valueOf(value).matches("[^0-9.]")))
                {
                    br.reset();
                    temp = new String(lexeme);
                    return new token(temp.replace(" ", ""),LITTOK);
                }
                
                
                
                // Loops until end of digit
                for(index = 1; String.valueOf(value).matches("[0-9.]"); index++)
                {
                    br.mark(1);
                    lexeme[index] = value;
                    value = (char)br.read();
                }
                
                // resets, move to end of digit and update current position.
                br.reset();
                //currPosition--;
                temp = new String(lexeme);
                
                //return token with white space removed
                return new token(temp.trim().replace(" ",""), LITTOK);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }

        
        return new token("EOF",EOFTOK);
    }
    
    
    
    
    
    /*
    CheckKeyword
    PURPOSE: Check keywords and return corresponding token number
    */
    private int CheckKeyword(String lexeme)
    {
        //cleans any trailing or leading white space.
        lexeme = lexeme.trim();
        
        switch(lexeme)
        {
            case "Program": return PROGTOK;
            case "begin":   return BEGINTOK;
            case "end":     return ENDTOK;
            case "const":   return CONSTOK;
            case "var":     return VARTOK;
            case "while":   return WHILETOK;
            case "do":      return DOTOK;
            case "if":      return IFTOK;
            case "then":    return THENTOK;
            
            case "integer":
            case "real":
            case "float":
            case "Boolean": return BASETYPETOK;
            
            case "mod":
            case "div":
            case "and":     return MULOPTOK;
            
            case "or":      return ADDOPTOK;
            
            case "not":     return NOTTOK;
            
            case "write":
            case "writeln": return  WRITETOK;
            
            case "read":    return READTOK;
            
            case "true":
            case "false":   return LITTOK;
            
        }
        return IDTOK;       //represents identifier
    }
    
   
}

