/**
 * File $CISC367HOME/example-progs/randomSentences/RandomSentences.java
 *
 * This program mimics the corresponding C++ program found in the file
 * RandomSentences.cc in the same directory.  Although this program is 
 * written in the "pure" object-oriented language Java, it is not object
 * oriented any more than the C++ is object oriented.  It does not make
 * use of any natural objects.
 *
 * For contrast, see the file RandomSent.java, in the same directory, which
 * defines an object RandomSent for generating random sentences that is
 * modeled on the class java.util.Random for generating random numbers.
 *
 * The class RandomSent is then used in the file RSClassTester.java to 
 * generate the random sentences.  The programs in the two files
 * RandomSent.java and RSClassTester.java reflect much better oo design
 * than the program found in RandomSentences.java.  Also note that the
 * public interface of the class RandomSent is similar to that of 
 * java.util.Random thereby making it easier to use for someone already
 * familiar with the "standard" class Random.
 */

import java.util.Random;

public class RandomSentences{

    final static int NO_WORDS = 10;	// These constants must be static
    final static int NO_SENTS = 20;	// if they are going to be used in
    final static String SPACE = " ";	// a static method like main().
    final static String PERIOD = ".";

    static Random r = new Random();

    public static void main( String args[] ){

        String article[] = { "the", "a", "one", "some", "any", "every", "none", "noone", "idk", "idc" };
        String noun[] = { "boy", "girl", "dog", "town", "car", "person", "cat", "computer", "laptop", "phone" };
        String verb[] = { "drove", "jumped", "ran", "walked", "skipped", "", "", "", "", "", "" };
        String preposition[] = { "to", "from", "over", "under", "on", "with", "as if", "as tho", "the way", "or" };

        String sentence;
        for (int i = 0; i < NO_SENTS; i++){
            sentence = article[rand()];
            char c = sentence.charAt(0);
            sentence = sentence.replace( c, Character.toUpperCase(c) );
            sentence += SPACE + noun[rand()] + SPACE;
            sentence += (verb[rand()] + SPACE + preposition[rand()]);
            sentence += (SPACE + article[rand()] + SPACE + noun[rand()]);
            sentence += PERIOD;
            System.out.println(sentence);
            sentence = "";
        }
    }

    static int rand(){
        int ri = r.nextInt() % NO_WORDS;
        if ( ri < 0 )
            ri += NO_WORDS;
        return ri;
    }
}

/**
 * Further commentary on the above code
 *
 * Note that the method used to capitalize the first article is not a 
 * general method.  If the article were "that", for example, the code
 * would change it into "ThaT".
 *
 * A better solution would be to provide a method
 *   String capitalizeCharAt( int index )
 * that would return a new String with the char at the specified location
 * capitalized.
 */