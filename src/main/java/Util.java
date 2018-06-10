public class Util {

    public static double epsilon = 0.000000000000001;

    public static int compareDouble(double d1, double d2){
        if (d1 - epsilon > d2) return 1;
        if ( d1 + epsilon < d2) return -1;
        return 0;
    }

}
