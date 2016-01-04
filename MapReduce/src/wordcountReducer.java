import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class wordcountReducer {
	
	public static void main(String[] args){
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			String word = null;
			String currentWord = null;
			int currentCount = 0;
			while((input = br.readLine())!=null)
			{
				try
				{
					String[] parts = input.split("\t");
					word = parts[0];
					int count = Integer.parseInt(parts[1]);
					
					if((currentWord!=null)&&(currentWord.equals(word)))
					{
						currentCount++;
					}
					else
					{
						if (currentWord!=null)
							System.out.println(currentWord+"\t"+currentCount);
						currentWord = word;
						currentCount = count;
					}
					
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
