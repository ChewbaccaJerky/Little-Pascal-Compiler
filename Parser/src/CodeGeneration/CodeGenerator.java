

package CodeGeneration;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
    John Lugtu
    CS4110 COMPILER DESIGN
    
    Purpose of Code Generator Class:
            To create MIPS Assembly language to place into a file. Rather than
            having all of my code generation in the Parser it keeps it seperate.
*/
public class CodeGenerator {
    BufferedWriter bw;                      // Writer to MipsCode.s
    String filename = "src/MipsCode.s";     // Path
    
    
    
    /*
        CodeGenerator Constructor
        (i)Opens File for use.
        (ii)Writes Prolog immediately
    */
    public CodeGenerator()
    {
        try{
            bw = new BufferedWriter((new FileWriter(filename)));
            Prolog();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
    /*
        Prolog
        Writes the necessary Assembly code to begin the program.
    */
    public void Prolog()
    {
        try
        {
            bw.write("\t#TEXT SEGMENT\n");
            bw.write("\t.text\n");
            bw.write("\t.globl main\n");
            bw.write("main:\n"); 
            bw.write("\tmove\t $fp, $sp\n\n");
        }catch(Exception e){
            
        }
    }
    
    
    
    /*
        Postlog
        (i) Writes the .data segment.
                Placing all strings at the bottom.
        (ii)Closes Buffered Writer
    */
    public void Postlog(ArrayList<String> list)
    {
        try
        {
            bw.write("\t#EXIT SYSTEM\n");
            bw.write("\tli\t $v0, 10\n");    //exit code for syscall
            bw.write("\tsyscall\n\n");
            
            bw.write("\t#DATA SEGMENT\n");
            bw.write("\t.data\n");
            AddStrings(list);
            bw.close();
        }catch(Exception e){
            
        }
    }
    
    
    
    /*
        StoreImmediate
        (i) Stores a value into a register
        (ii) Places the value in the register into the stack. Starting from
             the frame pointer ($fp)
    */
    public void StoreImmediate(ExpressionRecord er, String num)
    {
        num = num.trim();
        try
        {
            bw.write("\t#Store Immediate\n");
            bw.write("\tli\t $t0, " + num+"\n");
            bw.write("\tsw\t $t0, "+er.getLoc()+"($fp)\n\n");
        }catch(IOException ioe)
        {
            
        }
    }
    
    
    
    /*
        MoveVariable
        (i) Loads value from frame pointer offset
        (ii) Stores it in variable location
    */
    public void MoveVariable(int erLoc, int strLoc)
    {
        try
        {
            //System.out.println("This is str ptr ="+Str_ptr.getName());
            bw.write("\t#Move to variable location.\n");
            bw.write("\tlw\t $t0, "+erLoc+"($fp)\n");
            bw.write("\tsw\t $t0, "+strLoc+"($fp)\n\n");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    /*
        AddOp
        Handles the addition, subtration, and or operations.
        (i) Loads from frame pointer to register $t0
        (ii)Loads from frame pointer to register $t1
        (iii) Does the operation (+ - or)
        (iv) Store into frame pointer offset
    */
    public void AddOp(int loc, int erLoc, String op, int curOff)
    {
        curOff = curOff + 4;
        
        try
        {
            bw.write("\t#AddOp\n");
            bw.write("\tlw\t $t0, "+loc+"($fp)\n");
            bw.write("\tlw\t $t1, "+erLoc+"($fp)\n");
            bw.write("\t"+op+"\t $t0, $t0, $t1\n");
            bw.write("\tsw\t $t0, "+curOff+"($fp)\n\n");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    /*
        MulOp
        Handles multiplication, division, and mod.
        (A)Mult
            (i) Moves the 2 values into register $t0 and $t1
            (ii) Does multiplication and store into frame pointer
        (B/C) Division and Mod
            (i) Moves values into register $t0 and $t1
            (ii) Does Division operation
            (ii) Quotient is stored in mfhi and remainer in mflo
            (iii) Retrieve it and store into frame pointer.
    */
    public void MulOp(int loc, int erLoc, String op, int curOff)
    {
        curOff = curOff + 4;
        
        try
        {
            if(op.equals("mult"))
            {
                bw.write("\t#MULTIPLY\n");
                bw.write("\tlw\t $t0, "+loc+"($fp)\n");
                bw.write("\tlw\t $t1, "+erLoc+"($fp)\n");
                bw.write("\tmult\t $t0, $t1\n");
                bw.write("\tsw\t $t1, "+curOff+"($fp)\n\n");
            }
            else if(op.equals("div"))
            {
                bw.write("\t#DIVIDE\n");
                bw.write("\tlw\t $t0, "+loc+"($fp)\n");
                bw.write("\tlw\t $t1, "+erLoc+"($fp)\n");
                bw.write("\tdiv\t $t0, $t1\n");
                bw.write("\tmfhi\t $t0\n");                       //gets quotient
                bw.write("\tsw\t $t0, "+curOff+"($fp)\n\n");
            }
            else if(op.equals("mod"))
            {
                bw.write("\t#MOD\n");
                bw.write("\tlw\t $t0, "+loc+"($fp)\n");
                bw.write("\tlw\t $t1, "+erLoc+"($fp)\n");
                bw.write("\tdiv\t $t0, $t1\n");
                bw.write("\tmflo\t $t0\n");                       //gets remainder
                bw.write("\tsw\t $t0, "+curOff+"($fp)\n\n");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
    /*
        RelOp
        Handles < > = Operations
        (A/B) < or >
        (i) Load Values into Register
        (ii)slt the values
        (iii) Store into framepointer
        (C) =
        (i)Load Values
        (ii)Create two labels equal and not equal
        (iii) If Equal store 1 if not 0
        (iv) Store int frame pointer
    */
    public void RelOp(int loc, int erLoc, String op, int curOff)
    {
        curOff = curOff + 4;
        
        try
        {
            if(op.equals("lessThan"))
            {
                bw.write("\t#LESS THAN\n");
                bw.write("\tlw\t $t0, "+loc+"($fp)\n");
                bw.write("\tlw\t $t1, "+erLoc+"($fp)\n");
                bw.write("\tslt\t $t0, $t0, $t1\n");
                bw.write("\tsw\t $t0, "+curOff+"($fp)\n\n");
            }
            else if(op.equals("greaterThan"))
            {
                bw.write("\t#GREATER THAN\n");
                bw.write("\tlw\t $t0, "+loc+"($fp)\n");
                bw.write("\tlw\t $t1, "+erLoc+"($fp)\n");
                bw.write("\tslt\t $t0, $t1, $t0\n");
                bw.write("\tsw\t $t0, "+curOff+"($fp)\n\n");
            }
            else if(op.equals("equalTo"))
            {
                bw.write("\t#GREATER THAN\n");
                bw.write("\tlw\t $t0, "+loc+"($fp)\n");
                bw.write("\tlw\t $t1, "+erLoc+"($fp)\n");
                bw.write("\tbne\t $t0, $t1, NOTEQUAL\n");
                bw.write("\tli\t $t0, 1\n");
                bw.write("\tsw\t $t0, "+curOff+"($fp)\n");
                bw.write("\tj EQUAL\n");
                bw.write("NotEQUAL:\n");
                bw.write("\tli\t $t0, 0\n");
                bw.write("\tsw\t $t0, "+curOff+"($fp)\n");
                bw.write("EQUAL:\n\n");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
    /*
        genIF
        Generates IF statement
        (i) Retrieve value.
        (ii) Branch Equal to the label.
    */
    public void genIF(int curOffset, int Labelnum)
    {
        curOffset = curOffset + 4;
        
        try
        {
            bw.write("\t#IF STATEMENT\n");
            bw.write("\tlw\t $t0, "+curOffset+"($fp)\n");
            bw.write("\tbeq\t $t0, $0, ELSE"+Labelnum+"\n\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
    /*
        genWHILE
        Generate WHILE statement
        (i) Load value
        (ii)Load value 1 for comparison
        (iii) Branch on Equal to label
    */
    public void genWHILE(int curOffset, int Labelnum)
    {
        try
        {
            bw.write("\t#WHILE STATEMENT\n");
            bw.write("\tlw\t $t0, "+curOffset+"($fp)\n");
            bw.write("\tbeq\t $t0, $0, ENDWHILE"+Labelnum+"\n\n");
            //bw.write("\tj\t WHILE"+Labelnum+"\n\n");
            //bw.write("ENDWHILE"+Labelnum+":\n\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /*
        genJUMP
        Generates JUMP statement
        (i) Design for the end of While statement
            to return to the top of while statment.
    */
    public void getJUMP(String label, int labelNum)
    {
        try
        {
            bw.write("\t#JUMP STATEMENT\n");
            bw.write("\tj\t"+label+labelNum+"\n\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    /*
        genLabel
        Generate LABEL
        (i)Creates a Label
    */
    public void genLabel(String type,int Labelnum)
    {
        // writes the label name
        try
        {
            bw.write("\t#GENERATE LABEL\n");
            bw.write(type+Labelnum+":\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    

    
    
    /*
        read
        Read values on input
        (i)Load 5 into $v0 5 represents reading int
        (ii)syscall to get input
        (iii) Store into frame pointer
    
        *******NOTE********
        Error in this function. Need to fix and add floats.
    */
    public void genRead(char type, int loc)
    {
        try
        {
            if(type == 'i')
            {
                bw.write("\t#READ AN INT\n");
                bw.write("\tli\t$v0, 5\n");
                bw.write("\tsyscall\n");
                bw.write("\tsw\t $v0 "+loc+"($fp)\n\n");
            }
        }catch(Exception e)
        {
            
        }
    }
    
    
    
    /*
        write
        Handles printing onto console.
        (i)Loads address of string
        (ii)Prints
    */
    public void genWriteString(int index)
    {
        try
        {
                bw.write("\t#Print String\n");
                bw.write("\tli\t $v0, 4\n");        //print string syscall code
                bw.write("\tla\t $a0, String"+index+"\n");
                bw.write("\tsyscall\n\n");         
               
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
    /*
        writeExp
        Handling writing expressions.
        (i)Loads value from framepointer
        (ii) Moves into $a0 register then print
    
        **************NOTE******************
        Can Handle arithmetic expressions like a + 4 * b,
        but needs work with single variables.
    */
    public void writeExp(int offSet)
    {
        offSet += 4;    //last offset that was added to stack.
        try
        {
            bw.write("\t#Print Expression\n");
            bw.write("\tlw\t $t0, "+offSet+"($fp)\n");
            bw.write("\tmove\t $a0, $t0\n");
            bw.write("\tli\t $v0, 1\n");
            bw.write("\tsyscall\n\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
    /*
        newline
        (i) Prints  new line
    
        Used for writeln in Parser class.
    */
    public void newline()
    {
        try
        {
            bw.write("\t#New Line\n");
            bw.write("\tla\t $a0, newline\n");
            bw.write("\tsyscall\n\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    /*
        AddStrings
        Handles writing all strings in the data segment at the bottom of the
        program.
        (i) Iterates through arraylist and writes strings.
        (ii) writes newline
    */
    private void AddStrings(ArrayList<String> stringList)
    {
        
            try
            {
                for(int i = 0; i < stringList.size(); i++)
                {
                    bw.write("String"+i+": .asciiz \""+stringList.get(i)+"\"\n");
                }
                bw.write("newline: .asciiz \"\\n\"");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        
    }
    
    

    

    
}
