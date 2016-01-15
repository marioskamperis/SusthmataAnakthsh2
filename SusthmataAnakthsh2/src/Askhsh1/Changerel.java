package Askhsh1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Created by Marios on 1/15/2016.
 */
public class Changerel {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(new FileReader(new File("C:\\Users\\Marios\\Desktop\\Susthmata Anakthshs\\final_project\\TrecEval\\cranqrel.txt")));
        while (input.hasNext()) {
            String line = input.nextLine();

            System.out.println(line.substring(0, line.indexOf(' ')) + " 0" + line.substring(line.indexOf(' '), line.length()));
        }
    }
}
