package misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class main_misc {

	public static class aux{
		
		public int sum = 0;
		public int start = -1;
		public int end = -1;
		
		aux(int sum,int start,int end){
			this.sum = sum;
			this.start = start;
			this.end = end;
		}
		
		public String toString(){
			return "("+sum+","+start+","+end+")";
		}
		
	}
	
	public static class matrix{
		
		//NxM
		public final int N;
		public final int M;
		
		public List<List<Integer>> elements;
		
		matrix(List<List<Integer>> elements,int N,int M){
			this.N = N;
			this.M = M;
			this.elements = elements;
		}
		
		matrix(int N,int M){
			this.N = N;
			this.M = M;
			elements = new ArrayList<>();
		}
		
		public void print(){
			for(int i = 0;i < N;i++){
				System.out.println(elements.get(i));
			}
		}
		
	}

	
	public static void main(String[] args) {
	
	
	List<Integer> l0 = new ArrayList<>(Arrays.asList(-1,2,3,0));	
	List<Integer> l1 = new ArrayList<>(Arrays.asList(1,-5,3,2));
	List<Integer> l2 = new ArrayList<>(Arrays.asList(0,10,-30,-1));
	List<Integer> l3 = new ArrayList<>(Arrays.asList(-10,0,20,2));	
	
	List<List<Integer>> matrix = new ArrayList<>(Arrays.asList(l0,l1,l2,l3));
	
	
	max_submatrix(matrix).print();;
	
	}
	
	//solving for 1D in O(n)
	private static int max_sub_array(List<Integer> arr,int[] info){
	
		//info = {start,end}
		
		int l = arr.size();
		
		List<aux> tmp = new ArrayList<>();
		
		int a = 0;
		int b = 0;
		int curr = arr.get(0);
		boolean pos = true;
		if(curr < 0){
			pos = false;
		}
		//only the positives
		for(int i = 1;i <= l;i++){
			
			if(i == l){
				b = i-1;
				tmp.add(new aux(curr,a,b));
				continue;
			}
			
			int x = arr.get(i);
			if(x < 0 && !pos) {
				curr += x;
			}
			else if(x < 0 && pos){
			
				b = i-1;
				tmp.add(new aux(curr,a,b));
				a = i;
				curr = x;
				pos = false;
			}
			else if(x >= 0 && !pos){
	
				b = i-1;
				tmp.add(new aux(curr,a,b));
				a = i;
				curr = x;
				pos = true;
			}
			else if(x >= 0 && pos) {
				curr += x;
			}
			
			
		}
		
		int max = tmp.get(0).sum;
		curr = 0;
		
		l = tmp.size();
		
		boolean same_seq = false;
		
		for(int i = 0;i < tmp.size();i++){
			
			int x = tmp.get(i).sum;
			
			if(x < 0 && i == 0) {
				aux z = tmp.get(i+1);
				info[0] = z.start;
				info[1] = z.end;
				curr += z.sum;
			}
			
			if(x < 0 && i != 0 && i != l-1){
				
				aux y = null;
				if(i != 0) {
					y = tmp.get(i-1);
				}
				aux z = tmp.get(i+1);
				
				if(y.sum + x > 0 && z.sum + x > 0){
					
					if(!same_seq) {
						info[0] = y.start;
					}
					
					info[1] = z.end;
					
					curr += (y.sum+z.sum)+x;
					
					same_seq = true;
					
				}
				else {
					
					if(curr > max){
						max = curr;
					}
					curr = 0;
					same_seq = false;
					
				}
				
			}
			else if(i+1 == l){
				
				if(curr > max) {
					max = curr;
				}
				
			}
			
			
			
		}
		
		//System.out.println(info[0] + "," + info[1]);
		return max;
		
	}
		
	
	private static List<Integer> add_lists(List<Integer> a,List<Integer> b){
		
		List<Integer> c = new ArrayList<>();
		
		for(int i = 0;i < a.size();i++){
			
			c.add(a.get(i)+b.get(i));
			
		}
		return c;
	}

	//generalizing for 2D in O(n^3)
	private static matrix max_submatrix(List<List<Integer>> elements){
		
		List<matrix> cumulative = new ArrayList<>();
		
		int n = elements.size();//N == M
		
		//1) build the cumulative sum matrix for each starting index
		
		
		for(int i = 0;i < n;i++){
			
			matrix s = new matrix(n,n);
			List<Integer> line = elements.get(i);
			
			for(int j = 0;j <= i;j++){
				s.elements.add(elements.get(j));
			}
			
			for(int j = i+1;j < n;j++){
				List<Integer> ref = add_lists(line,elements.get(j));
				s.elements.add(ref);
				line = ref;
			}
			cumulative.add(s);
		}
		
		//cumulative.get(n-1).print();
		
		//2) for each cumulative matrix find the biggest subline,this subline represents a submatrix
		
		int[] info = {0,0};
		int a = -1;
		int b = -1;
		boolean max_set = false;
		int max = 0;
		for(int i = 0;i < n;i++){
			
			matrix  m = cumulative.get(i);
			for(int j = i;j < n;j++){
				int curr = max_sub_array(m.elements.get(j),info);
				if(!max_set) {
					max = curr;
					a = i;
					b = j;
					max_set = true;
				}
				else {
					if(curr > max){
						a = i;
						b = j;
						max = curr;
					}
				}
			}
			
		}
		
		//System.out.println(max);
		
		List<List<Integer>> ger = new ArrayList<>();
		
		int c = 0;
		for(int i = a;i <= b;i++){
			ger.add(new ArrayList<>());
			c++;
			for(int j = info[0];j <= info[1];j++){
				ger.get(c-1).add(elements.get(i).get(j));
			}
			
		}
		
		return new matrix(ger,b-a+1,info[1]-info[0]+1);
	}	
}
