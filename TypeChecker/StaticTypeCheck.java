import java.util.*;



public class StaticTypeCheck {
    public static TypeMap typing(Declarations d){
        TypeMap map = new TypeMap();
        for (Declaration di : d){ //선언부의 내용을 하나씩 for문
            // TypeMap에 선언된 변수, 타입 put
            map.put(di.v, di.t);
        }
        return map;
    }

    public static void check(boolean test, String msg){
        // 체크하는 함수 참이면 return 오류면 오류를 출력한 후 프로그램 종료
        if (test) return;
        System.err.println(msg);
        System.exit(1);
    }


    public static void V(Declarations d){
        // 선언부에 중복된 변수가 선언되었는지 확인하는 함수
        for (int i = 0; i < d.size() - 1; i++){
            Declaration di = d.get(i);
            for (int j = i+ 1; j < d.size(); j++){
                Declaration dj = d.get(j);
                check(!(di.v.equals(dj.v)),
                        "dup: " + dj.v);
            }
        }
    }

    public static void V(Program p){
        V(p.decpart);
        V(p.body, typing(p.decpart));
    }

    public static void V(Statement s, TypeMap tm) {
        // null stmt는 오류
        if (s == null) throw new IllegalArgumentException("AST error: null stmt");
            // skip은 항상 Valid
        else if (s instanceof Skip) return;
            // 대상 변수와 expr의 타입이 일치해야함.
        else if (s instanceof Assignment) {
            Assignment a = (Assignment) s;
            // 변수가 선언됐는지 확인
            check(tm.containsKey(a.target), "undefined target in assignment: " + a.target);
            V(a.source, tm);
            Type ttype = (Type) tm.get(a.target); // 대상 변수의 타입을 가져옴
            Type srctype = typeOf(a.source, tm); // 계산식 expr의 타입을 가져옴
            if (ttype != srctype) {
                // Float 타입 변수에는 Int형 타입 가능
                if (ttype == Type.FLOAT) check(srctype == Type.INT
                        , "type error.." + a.target);
                    // Int 타입 변수에는 Char형 타입 가능
                else if (ttype == Type.INT) check(srctype == Type.CHAR
                        , "type error " + a.target);
                    // 나머지는 오류
                else check(false, "mixed mode assignment to " + a.target);
            }
            return;
        }
        // 조건식은 조건식의 expr이 bool형 타입이여야함.
        else if (s instanceof Conditional){
            Conditional c = (Conditional) s;
            V(c.test, tm);
            Type ttype = typeOf(c.test, tm);
            // 조건식이 bool타입이 아니면 오류
            if (ttype == Type.BOOL){
                V(c.elsebranch, tm);
                V(c.thenbranch, tm);
                return;
            }
            else
                check(false, "Conditional must have bool type: " + c.test);
        }
        // 반복문은 expr(조건식)의 타입이 bool 형식이면 Valid
        else if (s instanceof Loop){
            Loop l = (Loop) s;
            // 조건식 자체가 Valid한지 체크
            V(l.test, tm);
            // 조건식 (expr)의 타입을 불러와서
            Type ttype = typeOf(l.test, tm);
            // 해당 타입이 bool 형식이 아니면 오류
            if (ttype == Type.BOOL){
                V(l.body, tm);
                return;
            }
            else
                check(false, "Expr in loop must have bool type: " + l.test);
        }
        // Blcok 안의 모든 stmt의 형식이 Valid 해야함
        else if (s instanceof Block){
            Block b = (Block) s;
            for(Statement i: b.members){
                V(i, tm);
            }
        }
        else
            throw new IllegalArgumentException("Should never come here");
    }

    public static void V(Expression e, TypeMap tm){
        // 타입을 확인하여 올바른 타입끼리의 연산인지 확인하는 함수

        // 값이라면 타입을 확인할 필요가 없어 항상 참
        if (e instanceof Value) return;

            // 변수라면 해당 변수가 typeMap에 존재하는지 확인 후 참 거짓 판별
        else if (e instanceof Variable){
            Variable v = (Variable) e;
            check(tm.containsKey(v), "undeclared variable: " + v);
            return;
        }

        // binary 라면 term1과 term2의 타입이 같아야함.
        else if (e instanceof Binary){

            Binary b = (Binary) e;
            Type type_Term1 = typeOf(b.term1, tm);
            Type type_Term2 = typeOf(b.term2, tm);
            // term1과 term2가 정상적인 type을 갖는지 확인
            V(b.term1, tm);
            V(b.term2, tm);

            // 산술 연산자라면 term1과 term2의 타입이 같고 Int or Float 형식이여야함.
            if (b.op.ArithmeticOp())
                check((type_Term1 == type_Term2) &&
                                (type_Term1 == Type.INT || type_Term1 == Type.FLOAT)
                        , "ArithmeticOp must have same type (int or float) " + b.op);
                // 비교 연산자(부등호) 라면 term1 과 term2의 타입이 같아야함.
            else if (b.op.RelationalOp())
                check(type_Term1 == type_Term2, "RelationalOp must have same type " + b.op);
                // 논리형 연산자라면 term1과 term2의 타입이 bool형 타입이여야함.
            else if (b.op.BooleanOp())
                check(type_Term1 == Type.BOOL && type_Term2 == Type.BOOL
                        , "BooleanOp must have Bool type " + b.op);
            else
                throw new IllegalArgumentException("Binary error");
            return;
        }

        else if (e instanceof Unary){
            // Unary은 하나의 term과 계산됨.
            Unary u = (Unary) e;
            Type type = typeOf(u.term, tm);
            V(u.term, tm);

            // Not 연산자(!) 이라면 Bool 형식을 가져야만 함.
            if (u.op.NotOp())
                check(type == Type.BOOL, "NotOp must have Bool type " + u.op);
                // 음수 연산자(-) 라면 숫자(int or float) 형식을 가져야 함.
            else if (u.op.NegateOp())
                check(type == Type.INT || type == Type.FLOAT
                        , "NegateOp must have Int or Float type " + u.op);
            else if (u.op.intOp())
                check(type == Type.FLOAT || type == Type.CHAR
                        , "2i must have Float or Char Type " + u.op);
            else if (u.op.floatOp())
                check(type == Type.INT
                        , "2f must have Int Type " + u.op);
            else if (u.op.charOp())
                check(type == Type.INT
                        , "2c must have Int Type " + u.op);
            else
                throw new IllegalArgumentException("Unary error");
            return;
        }
        throw new IllegalArgumentException("Should never come here");
    }



    public static Type typeOf(Expression e, TypeMap tm){
        // 타입을 반환하기 위한 typeOf 함수

        // 값이라면 해당 값의 타입 반환
        if (e instanceof Value) return ((Value)e).type;

        else if (e instanceof Variable){
            // 변수라면 해당 변수를 타입맵에서 찾아 반환
            Variable v = (Variable)e;
            // 없다면 오류
            check(tm.containsKey(v), "undefined variable: " + v);
            return (Type) tm.get(v); // key값 v를 입력하면 타입이 반환됨
        }
        else if (e instanceof Binary){
            Binary b = (Binary) e;
            // 연산기호라면 첫번째 Term의 타입을 따름 (Int or Float)
            if (b.op.ArithmeticOp()){
                if (typeOf(b.term1, tm) == Type.INT) return (Type.INT);
                else return (Type.FLOAT);
            }
            // 논리형, 비교 연산자(and, or, 부등호)라면 BOOL 타입
            else if (b.op.RelationalOp() || b.op.BooleanOp()) return (Type.BOOL);
        }
        else if (e instanceof Unary){
            Unary u = (Unary) e;
            // Unary Type: Not, Negate, int, float, char

            // NotOp (!)일때 BOOL 타입
            if (u.op.NotOp()) return (Type.BOOL);
            // Negate (-)일때 해당 operand의 타입
            else if (u.op.NegateOp()) return typeOf(u.term, tm);
            // 나머지는 해당하는 타입
            else if (u.op.intOp()) return (Type.INT);
            else if (u.op.floatOp()) return (Type.FLOAT);
            else if (u.op.charOp()) return (Type.INT);
        }
        // 정해진 형식이 오지않은 오류
        throw new IllegalArgumentException("should never come here");
    }




    public static void main(String args[]){
        Parser parser = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        TypeMap map = typing(prog.decpart);
        V(prog);
        System.out.println(map);
    }

}
