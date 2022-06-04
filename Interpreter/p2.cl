// p2.cl
int main () {
  int iNum, iAny, count;
  iNum = 1000;
  iAny = 1;
  count = 0;
  while (iNum<=2000) 
  {
    while(iNum>= iAny)
    {
      if(iNum%iAny == 0)
      {
        count = count + 1;
      }
      iAny = iAny + 1;
    }
    if (count == 2)
    {
      print iNum;
      printCh '\n';
    }
    iAny = 1;
    count = 0;
    iNum = iNum+1;
  }
}
