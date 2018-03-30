public class Main {

    public static void main(String args[]) throws UnitMismatchException{
        UV a = new UV(3, U.M);
        UV b = new UV(2, U.S);

        UV c = new UV(10, U.KM.div(U.H));

        //U carl = new U(U.M.div(U.KG), 0.025, "carl", "carl");
        //U carl = new U(U.M, 1.93, "carl", "carl");
        //new UV(1, carl).convert(U.M.div(U.KG));
        //System.out.println(new UV(1.9, U.M).convert(carl));
    }
}
