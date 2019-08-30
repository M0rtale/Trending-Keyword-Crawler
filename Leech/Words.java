/* Author: Allen Qiu
 * 
 * Words
 */

public class Words
{
  private String name;
  private double popularity;
  
  public Words(String name, double popularity)
  {
    this.name = name;
    this.popularity = popularity;
  }
  
  public String getName() {return name;}
  public void setName(String n) {name = n;}
  public double getPopularity() {return popularity;}
  public void setPopularity(double p) {popularity = p;}
}