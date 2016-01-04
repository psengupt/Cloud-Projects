import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
public class wordcountMapper {

	public static void main(String[] args) {
		
		try
		{	// TODO Auto-generated method stub
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			//String filename = new String();
			
			while((input=br.readLine())!= null)
			{
				StringTokenizer tokenizer = new StringTokenizer(input);
				while(tokenizer.hasMoreTokens())
				{
					String word = tokenizer.nextToken();
					System.out.println(System.getenv("Program_Files"));
					System.out.println(word+"\t"+"1");
				}
			}
		}catch(IOException io)
		{
			io.printStackTrace();
		}

	}

}
