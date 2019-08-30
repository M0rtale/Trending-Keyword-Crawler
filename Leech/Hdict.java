/* Author: Allen Qiu
 * 
 * Dictionary Class for the leecher
 * will store everything as name, and popularity
 * 
 * In this case, Words will be the key, and Words will be the entry
 */

//I was going to re-write a unbounded array, but eh whatever
import java.util.ArrayList;
import java.util.List;

public class Hdict
{
  //2D array, where the first layer is hashed, and second layer is UBA or Linked List
  private ArrayList<Words>[] dict;
  private int size;
  
  public Hdict()
  {
    dict = new ArrayList[80]; // prolly more than this, but 80 just to be safe.
    //initialize the shish
    for(int i = 0; i < dict.length; i++)
    {
      dict[i] = new ArrayList<Words>();
    }
    size = 0;
  }
  
  // a simple hash function
  private int hash(Words n)
  {
    int out = 1;
    String name = n.getName();
    for(int i = 0; i < name.length(); i++)
    {
      out *= (int)name.charAt(i);
    }
    return out;
  }
  
  private boolean keyequiv(Words w1, Words w2)
  {
    return w1.getName().equals(w2.getName());
  }
  
  Words hdict_lookup(Words w)
  {
    //get which array it is in.
    int position = hash(w);
    position = Math.abs(position % dict.length);
    
    ArrayList<Words> a = dict[position];
    for(int i = 0 ;i < a.size(); i++)
    {
      //ArrayList lookup very slow
      Words w2 = a.get(i);
      if(keyequiv(w, w2)) return w2;
    }
    return null;
  }
  
  void hdict_insert(Words w)
  {
    int position = Math.abs(hash(w) % dict.length);
    for(int i = 0; i < dict[position].size(); i++)
    {
      //if there is already an instance, replace.
      if(dict[position].get(i).getName().equals(w.getName()))
      {
        dict[position].get(i).setPopularity(w.getPopularity());
        return;
      }
    }
    dict[position].add(w);
    size++;
  }
  
  public String toString()
  {
    for(ArrayList<Words> list : dict)
    {
      for(int i = 0; i < list.size(); i++)
      {
        System.out.println("word " + list.get(i).getName() + " has popularity " + list.get(i).getPopularity());
      }
    }
    return "";
  }
  
  static int partition(Words arr[], int low, int high) 
  { 
    double pivot = arr[high].getPopularity();  
    int i = (low-1); // index of smaller element 
    for (int j=low; j<high; j++) 
    { 
      // If current element is smaller than the pivot 
      if (arr[j].getPopularity() < pivot) 
      { 
        i++; 
        
        // swap arr[i] and arr[j] 
        Words temp = arr[i]; 
        arr[i] = arr[j]; 
        arr[j] = temp; 
      } 
    } 
    
    // swap arr[i+1] and arr[high] (or pivot) 
    Words temp = arr[i+1]; 
    arr[i+1] = arr[high]; 
    arr[high] = temp; 
    
    return i+1; 
  } 
  
  
  /* The main function that implements QuickSort() 
   arr[] --> Array to be sorted, 
   low  --> Starting index, 
   high  --> Ending index */
  static void sort(Words arr[], int low, int high) 
  { 
    
    if (low < high) 
    { 
      /* pi is partitioning index, arr[pi] is  
       now at right place */
      int pi = partition(arr, low, high); 
      
      // Recursively sort elements before 
      // partition and after partition 
      sort(arr, low, pi-1); 
      sort(arr, pi+1, high); 
    } 
  } 
  
  public Words[] toArr()
  {
    Words[] arr = new Words[size];
    int count = 0;
    for(ArrayList<Words> list : dict)
    {
      for(int i = 0; i < list.size(); i++)
      {
        arr[count] = list.get(i);
        count++;
      }
    }
    return arr;
  }
  
  public static void arrSort(Words[] in)
  {
    sort(in, 0, in.length-1);
  }
  
}