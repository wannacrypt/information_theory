public class Symbol {
    private char character;
    private double probability;
    private int quantity;
    private String code;
    private boolean coded;

    public Symbol(char character) {
        this.character = character;
        this.quantity = 1;
        this.code = "";
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void incQuantity() {
        this.quantity++;
    }

    public boolean isCoded() {
        return coded;
    }

    public void setCoded(boolean coded) {
        this.coded = coded;
    }
}
