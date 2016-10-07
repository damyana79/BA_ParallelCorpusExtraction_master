
public class Verb {
    String token;
    String infinitiv;
    String aspect;

    Verb(String token, String infinitiv) {
        this.token = token;
        this.infinitiv = infinitiv;
        this.aspect = "";
    }

    public void setAspect(String aspect){
        this.aspect = aspect;
    }

    @Override
    public String toString() {
        return this.token + " " + this.infinitiv + " " + this.aspect;
    }

    /*
    public static void main(String[] args) {
        Verb test = new Verb("eat", "perfective");
        System.out.println(test);

    }
    */

}
