
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
        return this.verb_en + " " + this.fullSentence_en + " >>" + getSpanBegin() + " - " + getSpanEnd();
    }

    public void setSpans() {
        int[] spanInfo;
        String verbNotSubstring = " " + this.verb_en.token.trim() + " ";
        int begin = this.fullSentence_en.trim().indexOf(verbNotSubstring);
        //System.out.println("B " + begin);
        int end = begin + this.verb_en.token.length();
        //System.out.println("E " + end);
        if ((begin < 0) || (end > fullSentence_en.length())) {
            throw new IndexOutOfBoundsException();
        }
        setSpanBegin(begin + 1);
        setSpanEnd(end);
    }


//    public  static void main(String[] args) {
//        String a = " This is an awesome sentence.";
//        String b = "awesome";
//        System.out.println(a.indexOf(b));
//        System.out.println(a.indexOf(b)+b.length());
//        System.out.println(a.substring(12, 19));
//
//    }
}
