/**
 * the Verb class : token, infinitiv, aspect
 */
public class Verb {
    String token;
    String infinitiv;
    String aspect;

    /**
     *
     * @param token
     * @param infinitiv
     */
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

}
