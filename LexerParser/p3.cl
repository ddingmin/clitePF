// p3.cl
int main ( ) {
    float a, b, c, d;
    int x, y;
    a = 5;
    b = 4.5; c = 3.3; d = 2.2;
    x=5;
    y=6;
    a = (b * float(y)) + (c - d);
    if (a > 2){
       a = 3;
    }
    else
      b = 7;
}