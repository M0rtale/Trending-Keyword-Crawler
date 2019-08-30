/* Author: Allen Qiu
 * 
 * Entry point for a trending leecher
 */

/* List of websites:
 * https://www.yahoo.com/
 * https://www.bloomberg.com/
 * https://www.reddit.com/
 * https://www.nytimes.com/
 * https://www.nbcnews.com/
 * https://abcnews.go.com/
 */

//test
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import org.openqa.selenium.chrome.ChromeDriver;
//
//import java.net.URL;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.InputStream;
//import java.io.BufferedReader;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

//import javax.swing.*;

public class Leecher
{
  
  
  //keyword by priority:
  //https://news.yahoo.com/    usually breaking news
  //https://www.yahoo.com/gma/   because we produce articles for people to read while they are eating their breakfast right?
  //https://sports.yahoo.com/  better than entertainment
  //skip this: https://sports.yahoo.com/mlb
  //https://www.yahoo.com/entertainment/
  //https://www.yahoo.com/everything/
  //https://www.yahoo.com/lifestyle/     because fuck lifestyle, 996 ftw
  
  //no one cares about https://www.yahoo.com/now/
  
  final static String[] JUNK_WORDS = {"I", "a", "about", "after", "all"
    , "also", "an", "and", "any", "as", "at", "be", "because", "but", "by", "can", "come", "could", "do", "for", "from", "get", "give", "go", "have", 
    "he", "her", "him", "his", "if", "in", "into", "it", "its", "just", "know", "last", "make", "may", "me", "move", "my", "new", "no", "not", "now", "of", "on", "only", "or", "other", "out", "say", "see", "she", 
    "should", "so", "some", "such", "take", "than", "the", "their", "them", "then", "there", "these", "they", "think", "this", "time", "to", "up", "use", "very", "way", "we", "well", "what", "when", 
    "which", "who", "will", "with", "would", "year", "you", "your"};
  
  final static String[] YAHOO_WEB_LINKS = {"https://news.yahoo.com/", "https://www.yahoo.com/gma/", "https://sports.yahoo.com/", "https://www.yahoo.com/entertainment/", "https://www.yahoo.com/everything/"};
  final static String[] FOX_WEB_LINKS = {"https://www.foxnews.com/us/", "https://www.foxnews.com/world/", "https://www.foxnews.com/politics/", /*fuck opinions*/};
  
  // (500 - overall rank) / 500, because 500+ websites are more sketchy and may be more subjective to orwellianism. So rip bloomberg haha
  final static double YAHOO_WORTH = .98; //Done
  final static double REDDIT_WORTH = .97; // contains many skewed opinion, so keep at back of mind.
  final static double BBC_WORTH = .838; // TODO: see if you can distinguish directories
  final static double NYTIME_WORTH = .776; // Done.
  final static double CNN_WORTH = .796; // Done
  final static double FOX_WORTH = .462; // TODO: see if you can find conversation element.
  final static double WASHINGTON_WORTH = .582; // this site is quite hard to categorise, so on backup list
  
 // final static double HUFF_WORTH = 27; // super sketchy, have to reconsider.
  //final static double WSJ_WORTH = 0; // Nice
  //final static double NBC_WORTH = -246; // nice
  
  final static int NYTIME_AVG = 3000; // since nytimes is not skewed, each front page post gets 3000 points for just being there.
  
  
  static int year = 0;
  static int month = 0;
  static int day = 0;
  
  public static void leechFOX(Hdict dict)
  {
    WebDriver driver = new ChromeDriver();
    
    String baseUrl = "https://www.foxnews.com/";
    // launch Fire fox and direct it to the Base URL
    driver.get(baseUrl);
    
    //get all the links on the page
    List<WebElement> links = driver.findElements(By.tagName("a"));
    
    System.out.println("There is a total of " + links.size() + " front page news detected on Yahoo.com");
    
    Hdict titleDicts = new Hdict();
//    for(int i = 0; i < links.size(); i++)
//    {
//      System.out.println(links.get(i).getAttribute("href"));
//    }
//    
//    Scanner s = new Scanner(System.in);
//    s.next(); // pause to analyse
    
    for (int i = 0; i < links.size(); i++)
    {
      //get href link
      String url = links.get(i).getAttribute("href");
      
      Words hUrl = new Words(url, 0);
      if(titleDicts.hdict_lookup(hUrl) != null) // if this page is already parsed
      {
        continue;
      }
      
      //if the page is not parsed
      titleDicts.hdict_insert(hUrl);
      
      for(int p = 0; p < FOX_WEB_LINKS.length; p++)
      {
        String[] news = url.split(FOX_WEB_LINKS[p]);
        if(news.length > 1 && news[1].length() > 10) // news has to have a nice, big title to attract people, so 10 char is fine.
        {
          WebDriver driver1 = new ChromeDriver();
          driver1.get(url);
          
          // Conversation won't load until it scrolled to bottom
          JavascriptExecutor js = (JavascriptExecutor) driver1;
          js.executeScript("window.scrollBy(0,2147483647)");
          
          try // wait a bit for the element to load
          {
            Thread.sleep(1000);
          }
          catch(InterruptedException e){};
          
          String title = driver1.getTitle();
          
          List<WebElement> reaction = driver1.findElements(By.tagName("span")); // view reactions
          
          System.out.println("There is a total of " + reaction.size() + " reaction elements on " + url);
          
          boolean isConvers = false;
          
          for(int j = 0; j < reaction.size(); j++)
          {
            String test = reaction.get(j).getText();
            if(test.contains("Conversation ")) isConvers = true;
            if(isConvers) System.out.println(test);
          }
          
           Scanner s = new Scanner(System.in);
           s.next(); // pause to analyse
          
          for(int j = 0; j < reaction.size(); j++)
          {
            //View reactions (4,591) 
            String viewReaction = reaction.get(j).getText();
            
            String[] spliced = viewReaction.split("View reactions \\(");
            if(spliced.length <= 1) continue; // if the element is not reaction
            String[] spliced2 = spliced[1].split("\\)");
            
            //cannot parse commas, so splice and combine
            String[] sReaction = spliced2[0].split(",");
            String newReaction = "";
            for(int o = 0; o < sReaction.length; o++)
            {
              newReaction += sReaction[o];
            }
            
            int reactions = Integer.parseInt(newReaction);
            
            String directory = news[1];
            String[] temp_dir = directory.split("/");
            directory = temp_dir[temp_dir.length-1];
            
            String[] keys = directory.split("-");
            for(String key : keys)
            {
              if(key.contains(".html")) continue; // useless stuff
              
              boolean integercheck = true;
              try{
                Integer.parseInt(key);
              } catch(Exception e) 
              {
                integercheck = false;
              };
              if(integercheck) continue; // useless numbers
              
              if(Arrays.binarySearch(JUNK_WORDS, key) >= 0) continue; // useless words
              
              Words wkey = new Words(key, reactions * NYTIME_WORTH);
              Words wkey_old = dict.hdict_lookup(wkey);
              if(wkey_old == null) dict.hdict_insert(wkey);
              else
              {
                wkey.setPopularity(wkey.getPopularity() + wkey_old.getPopularity());
                dict.hdict_insert(wkey);
              }
            }
          }
          driver1.close();
        }
      }
    }
    //close Chrome
    driver.close();
  }
  
  //bbc is very easy to navigate, however, there is no such conversations on the thing, so 2500 it is
  public static void leechBBC(Hdict dict)
  {
    WebDriver driver = new ChromeDriver();
    
    String baseUrl = "https://www.bbc.com/";
    driver.get(baseUrl);
    
    List<WebElement> links = driver.findElements(By.tagName("a"));
    
    System.out.println("There is a total of " + links.size() + " frong page news detected on BBC");
    
    Hdict titleDicts = new Hdict();
    
    for(int i = 0; i < links.size(); i++)
    {
      //get href links
      String url = links.get(i).getAttribute("href");
      
      Words hUrl = new Words(url, 0);
      if(titleDicts.hdict_lookup(hUrl) != null) // if this page is already parsed
      {
        continue;
      }
      
      //if the page is not parsed
      titleDicts.hdict_insert(hUrl);
      
      String urlname = "https://www.bbc.com/news/";
      
      String[] news = url.split(urlname);
      if(news.length > 1 && news[1].length() > 10) // if it is .html and has some other shish, shit solution but works
      {
        //WebDriver driver1 = new ChromeDriver();
        //driver1.get(url);
        
        String[] temp1 = news[1].split("-");
        
        try{ // if the serial is not attached
        Integer.parseInt(temp1[temp1.length-1]);
        }
        catch(Exception e){ continue; };
        
        if(temp1.length < 2) // if it is not even a topic
          continue;
        
        //nytimes doesn't have viewers count, so default 2500 effectiveness on all posts
        
        int reactions = NYTIME_AVG;
        
        String directory = news[1];
        
        directory = directory.split(".html")[0]; // removes file name and directory
        String[] temp = directory.split("/");
        String title = temp[temp.length-2]; // get file name, more concise, skip last for CNN, last is index
        String[] keys = title.split("-");
        
        for(String key : keys)
        {
          if(key.contains(".html")) continue; // useless stuff
          
          boolean integercheck = true;
          try{
            Integer.parseInt(key);
          } catch(Exception e) 
          {
            integercheck = false;
          };
          if(integercheck) continue; // useless numbers
          
          if(Arrays.binarySearch(JUNK_WORDS, key) >= 0) continue; // useless words
          
          Words wkey = new Words(key, reactions * BBC_WORTH);
          Words wkey_old = dict.hdict_lookup(wkey);
          if(wkey_old == null) dict.hdict_insert(wkey);
          else
          {
            wkey.setPopularity(wkey.getPopularity() + wkey_old.getPopularity());
            dict.hdict_insert(wkey);
          }
        }
      }
    }
    //close Chrome
    driver.close();
  }
  
  //CNN is basically nytimes, same structure, so just reuse.
  //we can skip live news, those will probably have some sort of articles that backs it up.
  public static void leechCNN(Hdict dict)
  {
    WebDriver driver = new ChromeDriver();
    
    String baseUrl = "https://www.cnn.com/";
    driver.get(baseUrl);
    
    List<WebElement> links = driver.findElements(By.tagName("a"));
    
    System.out.println("There is a total of " + links.size() + " frong page news detected on CNN");
    
    Hdict titleDicts = new Hdict();
    
    for(int i = 0; i < links.size(); i++)
    {
      //get href links
      String url = links.get(i).getAttribute("href");
      
      Words hUrl = new Words(url, 0);
      if(titleDicts.hdict_lookup(hUrl) != null) // if this page is already parsed
      {
        continue;
      }
      
      //if the page is not parsed
      titleDicts.hdict_insert(hUrl);
      
      for(int o = 0; o < 4; o++) // goes back 4 days
      {
        String urlname = "";
        if(month >= 10)
          urlname = "https://www.cnn.com/" + year + "/" + month;
        else
          urlname = "https://www.cnn.com/" + year + "/0" + month;
        
        if(day >= 10)
          urlname += "/" + (day - o) + "/";
        else
          urlname += "/0" + (day - o) + "/";
        
        String[] news = url.split(urlname);
        if(news.length > 1 && news[1].length() > 10) // if it is .html and has some other shish, shit solution but works
        {
          //WebDriver driver1 = new ChromeDriver();
          //driver1.get(url);
          
          //nytimes doesn't have viewers count, so default 2500 effectiveness on all posts
          
          int reactions = NYTIME_AVG;
          
          String directory = news[1];
          
          directory = directory.split(".html")[0]; // removes file name and directory
          String[] temp = directory.split("/");
          String title = temp[temp.length-2]; // get file name, more concise, skip last for CNN, last is index
          String[] keys = title.split("-");
          
          for(String key : keys)
          {
            if(key.contains(".html")) continue; // useless stuff
            
            boolean integercheck = true;
            try{
              Integer.parseInt(key);
            } catch(Exception e) 
            {
              integercheck = false;
            };
            if(integercheck) continue; // useless numbers
            
            if(Arrays.binarySearch(JUNK_WORDS, key) >= 0) continue; // useless words
            
            Words wkey = new Words(key, reactions * CNN_WORTH);
            Words wkey_old = dict.hdict_lookup(wkey);
            if(wkey_old == null) dict.hdict_insert(wkey);
            else
            {
              wkey.setPopularity(wkey.getPopularity() + wkey_old.getPopularity());
              dict.hdict_insert(wkey);
            }
          }
        }
      }
    }
    //close Chrome
    driver.close();
  }
  
  
  
  public static void leechNytimes(Hdict dict)
  {
    WebDriver driver = new ChromeDriver();
    
    String baseUrl = "https://www.nytimes.com/";
    driver.get(baseUrl);
    
    List<WebElement> links = driver.findElements(By.tagName("a"));
    
    System.out.println("There is a total of " + links.size() + " frong page news detected on nytimes");
    
    Hdict titleDicts = new Hdict();
    
    for(int i = 0; i < links.size(); i++)
    {
      //get href links
      String url = links.get(i).getAttribute("href");
      
      Words hUrl = new Words(url, 0);
      if(titleDicts.hdict_lookup(hUrl) != null) // if this page is already parsed
      {
        continue;
      }
      
      //if the page is not parsed
      titleDicts.hdict_insert(hUrl);
      
      for(int o = 0; o < 4; o++) // goes back 4 days
      {
        String urlname = "";
        if(month >= 10)
          urlname = "https://www.nytimes.com/" + year + "/" + month;
        else
          urlname = "https://www.nytimes.com/" + year + "/0" + month;
        
        if(day >= 10)
          urlname += "/" + (day - o) + "/";
        else
          urlname += "/0" + (day - o) + "/";
        
        //System.out.println(urlname + " vs. " + url);
        
        String[] news = url.split(urlname);
        if(news.length > 1 && news[1].length() > 10) // if it is .html and has some other shish, shit solution but works
        {
          //WebDriver driver1 = new ChromeDriver();
          //driver1.get(url);
          
          //nytimes doesn't have viewers count, so default 2500 effectiveness on all posts
          
          int reactions = NYTIME_AVG;
          
          String directory = news[1];
          
          directory = directory.split(".html")[0]; // removes file name and directory
          String[] temp = directory.split("/");
          String title = temp[temp.length-1]; // get file name, more concise
          String[] keys = title.split("-");
          
          System.out.print("There is " + keys.length + " keywords on this link ");
          
          for(String key : keys)
          {
            if(key.contains(".html")) continue; // useless stuff
            
            boolean integercheck = true;
            try{
              Integer.parseInt(key);
            } catch(Exception e) 
            {
              integercheck = false;
            };
            if(integercheck) continue; // useless numbers
            
            if(Arrays.binarySearch(JUNK_WORDS, key) >= 0) continue; // useless words
            
            System.out.println("And it will be added");
            
            Words wkey = new Words(key, reactions * YAHOO_WORTH);
            Words wkey_old = dict.hdict_lookup(wkey);
            if(wkey_old == null) dict.hdict_insert(wkey);
            else
            {
              wkey.setPopularity(wkey.getPopularity() + wkey_old.getPopularity());
              dict.hdict_insert(wkey);
            }
          }
        }
      }
    }
    //close Chrome
    driver.close();
  }
  
  public static void leechYahoo(Hdict dict)
  {
    WebDriver driver = new ChromeDriver();
    
    String baseUrl = "https://www.yahoo.com/";
    // launch Fire fox and direct it to the Base URL
    driver.get(baseUrl);
    
    //get all the links on the page
    List<WebElement> links = driver.findElements(By.tagName("a"));
    
    System.out.println("There is a total of " + links.size() + " front page news detected on Yahoo.com");
    
    Hdict titleDicts = new Hdict();
    
    for (int i = 0; i < links.size(); i++)
    {
      //get href link
      String url = links.get(i).getAttribute("href");
      
      Words hUrl = new Words(url, 0);
      if(titleDicts.hdict_lookup(hUrl) != null) // if this page is already parsed
      {
        continue;
      }
      
      //if the page is not parsed
      titleDicts.hdict_insert(hUrl);
      
      if(url.contains("https://sports.yahoo.com/mlb"))continue;
      
      for(int p = 0; p < YAHOO_WEB_LINKS.length; p++)
      {
        String[] news = url.split(YAHOO_WEB_LINKS[p]);
        if(news.length > 1 && news[1].length() > 10) // if it is .html and has some other shish, shit solution but works
        {
          WebDriver driver1 = new ChromeDriver();
          driver1.get(url);
          
          String title = driver1.getTitle();
          
          List<WebElement> reaction = driver1.findElements(By.tagName("button")); // view reactions
          
          System.out.println("There is a total of " + reaction.size() + " reaction elements on " + url);
          
          for(int j = 0; j < reaction.size(); j++)
          {
            //View reactions (4,591) 
            String viewReaction = reaction.get(j).getText();
            
            String[] spliced = viewReaction.split("View reactions \\(");
            if(spliced.length <= 1) continue; // if the element is not reaction
            String[] spliced2 = spliced[1].split("\\)");
            
            //cannot parse commas, so splice and combine
            String[] sReaction = spliced2[0].split(",");
            String newReaction = "";
            for(int o = 0; o < sReaction.length; o++)
            {
              newReaction += sReaction[o];
            }
            
            int reactions = Integer.parseInt(newReaction);
            
            String directory = news[1];
            String[] temp_dir = directory.split("/");
            directory = temp_dir[temp_dir.length-1];
            
            String[] keys = directory.split("-");
            for(String key : keys)
            {
              if(key.contains(".html")) continue; // useless stuff
              
              boolean integercheck = true;
              try{
                Integer.parseInt(key);
              } catch(Exception e) 
              {
                integercheck = false;
              };
              if(integercheck) continue; // useless numbers
              
              if(Arrays.binarySearch(JUNK_WORDS, key) >= 0) continue; // useless words
              
              Words wkey = new Words(key, reactions * NYTIME_WORTH);
              Words wkey_old = dict.hdict_lookup(wkey);
              if(wkey_old == null) dict.hdict_insert(wkey);
              else
              {
                wkey.setPopularity(wkey.getPopularity() + wkey_old.getPopularity());
                dict.hdict_insert(wkey);
              }
            }
          }
          driver1.close();
        }
      }
    }
    //close Chrome
    driver.close();
  }
  
  public static void main(String[] args)
  {
    //nytime urls uses date stamp
    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    
    year = calendar.get(Calendar.YEAR);
    month = calendar.get(Calendar.MONTH) + 1; // Januarry is Month 0 ???
    day = calendar.get(Calendar.DAY_OF_MONTH);
    
    //initialize a dictionary.
    Hdict dict = new Hdict();
    leechYahoo(dict);
    leechNytimes(dict);
    leechCNN(dict);
    //fox doesn't work, have to find the conversation element.
    //leechFOX(dict);
    
    Words[] output = dict.toArr();
    Hdict.arrSort(output);
    
    for(int i = 0; i < output.length; i++)
    {
      System.out.println("word " + output[i].getName() + " has popularity " + output[i].getPopularity());
    }
  }
}