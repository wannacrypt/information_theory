import java.io.*;
import java.util.*;

public class Main
{
    public static void main(String [] args)
    {
        System.out.println("[Debug] Program has started\n");
        // List to store Symbols
        List<Symbol> symbolList = new ArrayList<>();

        // Symbol counter
        int symbolCnt = 0;

        //To save text
        String text = "";

        // Scanner to read from file
        Scanner scan = null;
        try {
            scan = new Scanner(new File("text.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("[Error] Ups... File not found :(");
            e.printStackTrace();
        }
        System.out.println("[Debug] Text was read form \"text.txt\"\n");
        // Read each word in the file
        while (scan.hasNextLine())
        {
            // Copy a line from the text
            String word = scan.nextLine();
            // In case, we need to count '\n' symbols as well
            if(scan.hasNextLine())
                word += '\n';

            text += word;
            // Split string into individual symbols
            char [] array = word.toCharArray();
            // Count, save, calculate
            for (char c : array) {
                // Increment symbols quantity
                symbolCnt++;
                // Check if current character in the array
                Symbol s = contains(symbolList, c);
                if (s != null)
                {
                    // If it contains, just increment quantity, and alter probability
                    s.incQuantity();
                } else
                {
                    // If it does not contain, then add character to the list
                    symbolList.add(new Symbol(c));
                }
            }
        }
        System.out.println("[Debug] Symbols was written into List<Symbol>\n");

        // Calculate probability
        int finalSymbolCnt = symbolCnt;
        symbolList.forEach(s -> s.setProbability((double)s.getQuantity() / finalSymbolCnt));
        System.out.println("[Debug] Probability calculated for each symbol in List<Symbol>. The result is:");

        // Sort in reversed order
        symbolList.sort(Comparator.comparingDouble(Symbol::getProbability).reversed());
        // Print out the Result
        symbolList.forEach(s -> System.out.println(s.getCharacter() + " - " + s.getProbability()));
        System.out.println();
        codeSymbols(symbolList, 0.5f);

        // Write code to file (generate Dictionary)
        try {
            PrintWriter writer = new PrintWriter("dictionary.txt", "UTF-8");
            symbolList.forEach(s -> writer.println(s.getCharacter() + s.getCode()));
            System.out.println("[Debug] Dictionary created for symbols from List<Symbol>. The result is: \"dictionary.txt\"\n");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         *  Part 2
         *  Encode text
         */
        try {
            PrintWriter writer = new PrintWriter("encodedText.txt", "UTF-8");
            String tmp = encode(text, symbolList);
            System.out.println("[Debug] Text encoded. The result is: \n" + tmp + '\n');
            writer.print(tmp);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encodedText = encode(text, symbolList);
        // Print decoded text
        System.out.println("[Debug] Text decoded. The result is: \n" + decode(encodedText, symbolList) + "\n");

        String encodedHuffmanText = encodeHuffman(encodedText);
        // Print Huffman encoded text
        System.out.println("[Debug] Text encoded using Huffman coding. The result is: \n" + encodedHuffmanText + "\n");

        String noisyText = addNoise(encodedHuffmanText);
        // Print Text with mistake
        System.out.println("[Debug] Some mistake was added in every 7 bit. The result is: \n" + noisyText + "\n");

        // Print Huffman decoded text
        String decodedHuffmanText = decodeHuffman(noisyText);
        System.out.println("[Debug] Text decoded using Huffman coding. The result is: \n" + decodedHuffmanText + "\n");

        // Restored Text
        String restoredText = decode(decodedHuffmanText, symbolList);
        System.out.println("[Debug] Text was restored after mistakes made. The result is: \n" + restoredText + "\n");
    }

    /**
     * Function to encode text
     * @param text
     * @param symbolList
     */
    private static String encode(String text, List<Symbol> symbolList)
    {
        // Create scanner to run through text's characters one by one
        Scanner scanner = new Scanner(text);
        String encodedText = "";

        while (scanner.hasNextLine())
        {
            // Copy a line from the text
            String word = scanner.nextLine();
            // In case, we need to count '\n' symbols as well
            if(scanner.hasNextLine())
                word += '\n';

            // Split string into individual symbols
            char [] array = word.toCharArray();
            for (char c : array) {
                Symbol s = contains(symbolList, c);
                encodedText += s.getCode();
            }
        }

        return encodedText;
    }

    /**
     * Function to decode the encoded text
     * @param text
     * @param symbolList
     * @return
     */
    private static String  decode(String text, List<Symbol> symbolList)
    {
        // Split string into individual symbols
        char [] array = text.toCharArray();
        int size = array.length;

        String code = "";
        String decodedText = "";

        for (int i=0; i<size; i++)
        {
            code += array[i];
            Character ch = getCharacter(symbolList, code);

            if (ch != null)
            {
                decodedText += ch;
                code = "";
            }
        }

        return decodedText;
    }

    /**
     * Returns symbol from the list
     * @param list
     * @param character
     * @return
     */
    private static Symbol contains(List<Symbol> list, Character character)
    {
        for (Symbol s : list)
        {
            if (s.getCharacter() == character)
                return s;
        }

        return null;
    }

    /**
     * Get the character with corresponding code
     * @param list
     * @param code
     * @return
     */
    private static Character getCharacter(List<Symbol> list, String code)
    {
        for (Symbol s : list)
        {
            if (s.getCode().equals(code))
                return s.getCharacter();
        }
        return null;
    }

    /**
     * Code symbols using Shannon-Fano coding
     * @param list
     * @param milestone
     */
    private static void codeSymbols(List<Symbol> list, double milestone)
    {
        if (list.size() == 1) {
            list.get(0).setCoded(true);
            return;
        }
        double sum = 0.0;
        for (int i = 0; i < list.size(); i++)
        {
            if (!list.get(i).isCoded()) {
                if (sum < milestone) {
                    sum += list.get(i).getProbability();
                } else {
                    List<Symbol> list1 = new ArrayList<>();
                    List<Symbol> list2 = new ArrayList<>();
                    for (int x = 0; x < i; x++) {
                        list.get(x).setCode(list.get(x).getCode() + "1");
                        list1.add(list.get(x));
                    }

                    for (int y = i; y < list.size(); y++) {
                        list.get(y).setCode(list.get(y).getCode() + "0");
                        list2.add(list.get(y));
                    }

                    delegate(list1);
                    delegate(list2);
                }
            }
        }
    }

    /**
     * @param list
     * Divide list into two sublist to be processed recursively
     * Takes sublist, recursive call codeSymbols() with new parameters
     */
    private static void delegate(List<Symbol> list)
    {
        if (list.size() != 0)
        {
            double sum = 0f;
            for (Symbol s : list)
            {
                sum += s.getProbability();
            }
            codeSymbols(list, sum / 2);
        }
    }

    /**
     * Encode using Huffman coding
     * @param word
     * @return
     */
    private static String encodeHuffman(String word){

        //number of zeros needed to add to the end
        int x = word.length() % 4;

        switch (x) {
            case 1:
                word = new StringBuilder(word).append("000").toString();
                break;
            case 2:
                word = new StringBuilder(word).append("00").toString();
                break;
            case 3:
                word = new StringBuilder(word).append("0").toString();
                break;
        }

        //write parity bits at the end of every 4 bits
        char r1, r2, r3;
        for (int i=4; i<=word.length(); i = i + 7){
            r1 = (char) (word.charAt(i-4) ^ word.charAt(i-3) ^ word.charAt(i-2));
            r2 = (char) (word.charAt(i-3) ^ word.charAt(i-2) ^ word.charAt(i-1));
            r3 = (char) (word.charAt(i-4) ^ word.charAt(i-3) ^ word.charAt(i-1));

            word = new StringBuilder(word).insert(i, r3).toString();
            word = new StringBuilder(word).insert(i, r2).toString();
            word = new StringBuilder(word).insert(i, r1).toString();
        }

        return word;
    }

    /**
     * Change bit at random position in every 7 bit
     * @param word
     * @return
     */
    private static String addNoise(String word){

        //replace one random bit out of 7 bits
        int changePosition;
        StringBuilder str = new StringBuilder(word);
        for (int i = 0; i<word.length(); i = i+7){
            changePosition = new Random().nextInt(7) + i;

            // if position is out of range, it will not make a mistake
            if (changePosition % 7 != 0 || changePosition == i){
                str.setCharAt(changePosition, Character.forDigit((word.charAt(changePosition) ^ '1'), 10));
            }

        }

        return str.toString();

    }

    /**
     * Decode text using Huffman coding
     * @param word
     * @return
     */
    private static String decodeHuffman(String word)
    {
        String decodedText = "";
        byte[] keys = new byte[8];
        //  i1  i2  i3  i4  r1  r2  r3
        //  0   1   2   3   4   5   6
        keys[0] = -1; // no error
        keys[1] = -1; // r3
        keys[2] = -1; // r2
        keys[3] = 3; // i4
        keys[4] = -1; // r1
        keys[5] = 0; // i1
        keys[6] = 2; // i3
        keys[7] = 1; // i2

        int size = word.length();
        for (int i=0; i<size; i += 7){
            byte s = 0;
            s += (word.charAt(i+4) ^ word.charAt(i) ^ word.charAt(i+1) ^ word.charAt(i+2)) << 2;
            s += (word.charAt(i+5) ^ word.charAt(i+1) ^ word.charAt(i+2) ^ word.charAt(i+3)) << 1;
            s += (word.charAt(i+6) ^ word.charAt(i) ^ word.charAt(i+1) ^ word.charAt(i+3));

            StringBuilder str = new StringBuilder(word.substring(i, i+4));
            if (keys[s] != -1)
            {
                str.setCharAt(keys[s], Character.forDigit((str.charAt(keys[s]) ^ '1'), 10));
            }
            decodedText += str.toString();

        }

        //number of zeros needed to be deleted from the end
        int x = word.length() % 4;

        switch (x) {
            case 1:
                decodedText = new StringBuilder(decodedText).delete(decodedText.length()-3, decodedText.length()).toString();
                break;
            case 2:
                decodedText = new StringBuilder(decodedText).delete(decodedText.length()-2, decodedText.length()).toString();
                break;
            case 3:
                decodedText = new StringBuilder(decodedText).deleteCharAt(decodedText.length()).toString();
                break;
        }

        return decodedText;
    }
}
