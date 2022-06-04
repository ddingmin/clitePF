import javax.management.relation.Relation;
import javax.swing.*;
import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
        // System.out.println(token.type() + " " + token.value()); // debug
    }
  
    private String match (TokenType t) { // * return the string of a token if it matches with t *
        String value = token.value();
        // System.out.println(token.type() + " " + token.value()); //debug
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
        match(TokenType.LeftBrace);
        Declarations dcrts = declarations();// student exercise
        Block blk = progstatements();
        match(TokenType.RightBrace);
        return new Program(dcrts, blk);  // student exercise
    }
  
    private Declarations declarations () {
        // Declarations --> { Declaration }
        Declarations dcrts = new Declarations();
        while (isType()){
            declaration(dcrts);
        }
        return dcrts;  // student exercise
    }
  
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
        // student exercise
        Type t = type();
        Variable var = new Variable(match(TokenType.Identifier));
        Declaration d = new Declaration(var, t);
        ds.add(d);

        // check more , Identifier
        while (isComma()){
            token = lexer.next();
            var = new Variable(match(TokenType.Identifier));
            d = new Declaration(var, t);
            ds.add(d);
        }
        match(TokenType.Semicolon);
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char 
        Type t = null;
        // student exercise
        if (token.type().equals(TokenType.Int)){ t = Type.INT; }
        else if (token.type().equals(TokenType.Bool)) { t = Type.BOOL; }
        else if (token.type().equals(TokenType.Float)) { t = Type.FLOAT; }
        else if (token.type().equals(TokenType.Char)) { t = Type.CHAR; }
        else { error(token.type()); }
        token = lexer.next();
        return t;
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        Statement s = new Skip();
        // student exercise
        if (token.type().equals(TokenType.Semicolon)){ token = lexer.next(); }
        else if (token.type().equals(TokenType.LeftBrace)) { s = statements(); }
        else if (token.type().equals(TokenType.Identifier)) { s = assignment(); }
        else if (token.type().equals(TokenType.If)) { s = ifStatement(); }
        else if (token.type().equals(TokenType.While)) { s = whileStatement(); }
        else error("error stmt");
        return s;
    }

    private Block progstatements( ) {
        // Block --> Statements
        Block b = new Block();
        Statement s;
        while (isStatement()) {
            s = statement();
            b.members.add(s);
        }
        return b;
    }
    private Block statements () {
        // Block --> '{' Statements '}'
        Block b = new Block();
        // student exercise
        match(TokenType.LeftBrace);
        Statement s;
        // check more Statement
        while (isStatement()){
            s = statement();
            b.members.add(s);
        }
        match(TokenType.RightBrace);
        return b;
    }
  
    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
        Variable var = new Variable(match(TokenType.Identifier));
        match(TokenType.Assign);
        Expression exp = expression();
        match(TokenType.Semicolon);
        return new Assignment(var, exp);  // student exercise
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
        match(TokenType.If);
        match(TokenType.LeftParen);
        Expression exp = expression();
        match(TokenType.RightParen);
        Statement stmt = statement();
        Conditional cdtn;
        // check [ else Statement ]
        if (isElse()){
            token = lexer.next();
            Statement elsestmt = statement();
            cdtn = new Conditional(exp, stmt, elsestmt);
        }
        else{
            cdtn = new Conditional(exp, stmt);
        }
        return cdtn;  // student exercise
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
        match(TokenType.While);
        match(TokenType.LeftParen);
        Expression exp = expression();
        match(TokenType.RightParen);
        Statement stmt = statement();

        return new Loop(exp, stmt);  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
        Expression cj = conjunction();

        // check ||
        while (isOr()){
            Operator op = new Operator(match(token.type()));
            Expression cj2 = conjunction();
            cj = new Binary(op, cj, cj2);
        }
        return cj;  // student exercise
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
        Expression eq = equality();

        // check &&
        while (isAnd()){
            Operator op = new Operator(match(token.type()));
            Expression eq2 = equality();
            eq = new Binary(op, eq, eq2);
        }
        return eq;  // student exercise
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
        Expression rel = relation();

        // Optional EquOp
        if (isEqualityOp()){
            Operator op = new Operator(match(token.type()));
            Expression rel2 = relation();
            rel = new Binary(op, rel, rel2);
        }
        return rel;  // student exercise
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition]
        Expression adit = addition();
        if (isRelationalOp()){
            Operator op = new Operator(match(token.type()));
            Expression adit2 = addition();
            adit = new Binary(op, adit, adit2);
        }
        return adit;  // student exercise
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
        Value val = null;
        String tempVal = token.value();
        if (isIntLit()){
            val = new IntValue(Integer.parseInt(tempVal));
            token = lexer.next();
        }
        else if (isfloatLit()){
            val = new FloatValue(Float.parseFloat(tempVal));
            token = lexer.next();
        }
        else if (isCharLit()){
            val = new CharValue(tempVal.charAt(0));
            token = lexer.next();
        }
        else if (isTrueLit()){
            val = new BoolValue(true);
            token = lexer.next();
        }
        else if (isFalseLit()){
            val = new BoolValue(false);
            token = lexer.next();
        }
        else error("error literal");
        return val;  // student exercise
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }

    // isFuntion
    private boolean isComma( ){ return token.type().equals(TokenType.Comma);}
    private boolean isElse( ) { return token.type().equals(TokenType.Else);}
    private boolean isOr( ) { return token.type().equals(TokenType.Or);}
    private boolean isAnd( ) { return token.type().equals(TokenType.And);}
    private boolean isIntLit( ) { return token.type().equals(TokenType.IntLiteral);}
    private boolean isfloatLit( ) { return token.type().equals(TokenType.FloatLiteral);}
    private boolean isCharLit( ) { return token.type().equals(TokenType.CharLiteral);}
    private boolean isTrueLit( ) { return token.type().equals(TokenType.True);}
    private boolean isFalseLit( ) { return token.type().equals(TokenType.False);}

    private boolean isStatement( ){
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        return token.type().equals(TokenType.Semicolon) ||
                token.type().equals(TokenType.LeftBrace) ||
                token.type().equals(TokenType.Identifier) ||
                token.type().equals(TokenType.If) ||
                token.type().equals(TokenType.While);
    }


    
    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display(0);           // display abstract syntax tree
    } //main

} // Parser
