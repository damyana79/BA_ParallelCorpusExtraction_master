
public class OutputVerbData {
    Verb verb_en;
    String fullSentence_en;
    private int spanBegin;
    private int spanEnd;

    String infoStringCzVerb;

    OutputVerbData(Verb verb_en, String fullSentence_en, String infoStringCzVerb) {
        this.verb_en = verb_en;
        this.fullSentence_en = fullSentence_en;
        this.infoStringCzVerb = infoStringCzVerb;

    }

    public void setSpanBegin(int begin) {
        this.spanBegin = begin;
    }

    public int getSpanBegin() {
        return this.spanBegin;
    }

    public void setSpanEnd(int end) {
        this.spanEnd = end;
    }

    public int getSpanEnd() {
        return this.spanEnd;
    }

    @Override
    public String toString() {
        return ">>>" + this.verb_en + ">>" + this.fullSentence_en + " >>" + getSpanBegin() + " - " + getSpanEnd() + "<<<";
    }

    //TODO: deprecated, moved to final processing
    public void setSpans() {
        int[] spanInfo;
        //String verbNotSubstring = " " + this.verb_en.token.trim();
        int begin = this.fullSentence_en.indexOf(this.verb_en.token); //untrimmed
        //System.out.println("B " + begin);
        int end = begin + this.verb_en.token.length() - 1; //untrimmed
        //System.out.println("E " + end);
        if ((begin < 0) || (end > fullSentence_en.length())) {
            System.err.println("begin " + begin + " end " + end + this.verb_en.token);
            throw new IndexOutOfBoundsException();
        }
        //make up for untrimmed verb and untrimmed sentence
        setSpanBegin(begin);
        setSpanEnd(end);
        //System.out.println(">>>>>>>>>>>>>>>>>>>>" + this.verb_en.token + ">>" + this.fullSentence_en.substring(begin, end) + "<<");
    }


//    public  static void main(String[] args) {
//        String a = " This is an awesome sentence. ";
//        String b = " awesome ";
//        System.out.println(a.indexOf(b));
//        System.out.println(a.indexOf(b)+b.length());
//        System.out.println(a.substring(12, 19));
//
//    }
}
