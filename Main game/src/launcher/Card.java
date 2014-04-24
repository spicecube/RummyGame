package launcher;
public class Card
{
	private int rank, suit;//General arrays to store suits and ranks
	private static String[] suits = { "hearts", "spades", "diamonds", "clubs" };
	private static String[] ranks  = { "2", "3", "4", "5", "6", "7", "8", "9","10", "Jack", "Queen", "King", "Ace" };

	//This refers to a certain suit and rank
	Card(int suit, int rank)
	{
		this.rank=rank;
		this.suit=suit;
	}
	//Helps represent suit and rank of a card as a string
	public @Override String toString()
	{
		  return ranks[rank] + " of " + suits[suit];
	}
	//Get functions in order to access the specific rank and suit from 
	//the suits and ranks arrays

	public int getRank() {
		 return rank;
	}

	public int getSuit() {
		return suit;
	}

}
