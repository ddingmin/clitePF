package Program;

public class Test {

    public static void main(String[] args) {
        String filename = args[0];

    	System.out.println("Begin parsing... ");
    	Parser parser  = new Parser(new Lexer(filename));
        Program prog = parser.program();
        prog.display();
        
        // Static Type Check Section
        System.out.println("\n\nBegin type checking... \n");
        StaticTypeCheck.V(prog);
        System.out.println("No type errors\n");
        
        // Type Transformer Section
        Program out = TypeTransformer.T(prog);
        System.out.println("\nTransformed Abstract Syntax Tree");
        out.display();

        // Semantics section
        System.out.println("\n\n\nBegin interpreting...");
        Semantics semantics = new Semantics( );
        State state = semantics.M(out);
        System.out.println("Finish");
    }
}
