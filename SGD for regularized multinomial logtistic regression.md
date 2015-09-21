1. Let $k = 0$, and let $A$ and $B$ be empty hashtables. $A$ will record the value of k last time $B[j]$ was updated.
2. For $t=1,...,T$
- For each exampel $x_i, y_i$:
- Let $k=k+1$
	 - For each not-zero feature of $x_i$ with index $j$ and value $x_j$:
		 - If j is not in $B$, set $B[j]=0$
		 - If j is not in $A$, set $A[j]=0$
		 - Simulate the "regularization" updates that would have been performed for the $k-A[j]$ examples since the last time a non-zero $x_j$ was encountered by setting $$B[j] = B[j]*(1-2\lambda\mu)^{k-A[j]}$$
		 - set $B[j] = B[j]+\lambda(y-p)x_i$
		 - set $A[j]=k$
3. For each parameter $\beta_1,...,\beta_d$, set $$B[j] = B[j]*(1-2\lambda\mu)^{k-A[j]}$$
4. Output the parameters $\beta_1,...,\beta_d$
