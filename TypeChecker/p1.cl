// p1.cl

int main() {
			
	int n,k, count;
	int lower, upper;
	bool prime;
			
	lower = 1;
	upper = 100;
	prime = false;
			
	n = lower;
	count = 0;
			
	while (n >= lower && n <= upper) {
		if (n < 4) 
			prime = true;	 
		else 
			if (n > 4) {
				// check if n has a factor.
				prime = true;
				k = 2;
			   
				while (k <= (n+1)/2 ) { 
					if (n == (n/k) * k) {   
						prime = false;
					}
					
					k = k + 1;			// try next number
				}
			}
			else
				prime = false;
			 
		if (prime) {
				count = count + 1; 
				if (count >=5) {
					count = 0;
				}
		}
			  	 
		n = n + 1;
	} // end while
}