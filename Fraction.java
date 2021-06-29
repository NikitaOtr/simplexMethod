package example;

public class Fraction {
    private long numerator;
    private long denominator;

    public Fraction(long num) {
        this.numerator = num;
        this.denominator = 1;
    }

    public Fraction(long numerator, long denominator) {
        if (denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }
        this.numerator = numerator;
        this.denominator = denominator;
        this.reduce();
    }

    public Fraction (String num, String den){
        this.numerator = (long) (Integer.parseInt(num));
        this.denominator = (long) (Integer.parseInt(den));
    }

    public Fraction(String s) {
        long t = 1000000000;
        this.numerator = (long) (Double.parseDouble(s) * t);
        this.denominator = t;
        this.reduce();
    }

    private void reduce() {
        long nod = this.nod(this.numerator, this.denominator);
        this.numerator /= nod;
        this.denominator /= nod;
    }

    private long nod(long n, long m) {
        long f;
        if (n < 0)
            n *= -1;
        if (m < 0)
            m *= -1;
        while (n > 0) {
            f = n;
            n = m % n;
            m = f;
        }
        return m;
    }

    public Fraction add(Fraction f) {
        return new Fraction(this.numerator * f.denominator + f.numerator * this.denominator,
                this.denominator * f.denominator);
    }

    public Fraction sub(Fraction f) {
        return new Fraction(this.numerator * f.denominator - f.numerator * this.denominator,
                this.denominator * f.denominator);
    }

    public Fraction mul(Fraction f) {
        return new Fraction(this.numerator * f.numerator, this.denominator * f.denominator);
    }

    public Fraction div(Fraction f) {
        return new Fraction(this.numerator * f.denominator, this.denominator * f.numerator);
    }

    public Fraction changeSign() {
        return new Fraction(this.numerator * (-1), this.denominator);
    }

    public double getDouble() {
        double num = (double) this.numerator;
        return num / this.denominator;
    }

    @Override
    public String toString() {
        if (this.denominator == 1)
            return Long.toString(this.numerator);
        return this.numerator + "\\" + this.denominator;
    }
}