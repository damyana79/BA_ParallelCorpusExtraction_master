# 1. Initial raw files:

## A. initial Intercorp files (not in the project structure)
	intercorp_en2cs
	intercorp_en
	intercorp_cs

### A1. Intercorp files: broken down into smaller files, so that one file corresponds to just one book/named collection. 
a. The output files from the books (151), which were used in the corpus extraction are in:
correspondences_intercorp_en2cs
intercorp_cs
intercorp_en

b. big Corpus:
Some of the named collections _ACQUIS, _EUROPARL, _PRESSEUROP, _SUBTITLES, _SYNDICATE 
were too big to be processed on my computer (8GB and 12GB working memory were not enough) and were processed on a later stage. 
It turned out they contained just 555 unique verb occurrences and of these only 5 that did not occur in the books so they weren't taken into account further.


The classes used for breaking down the initial Intercorp files are:
- **RepairXml**: splits intercorp_en2cs 
- **SplitCorpusXmlData**: splits intercorp_en and intercorp_cs

The files from the "big Corpus" were manually extracted; formatting mistakes in them were also manually corrected


## B. Vallex files
	vallex-2.8.3-work.xml
	get_aspects.xslt -> resulting file with all verbs (5098) and all 4 aspect labels (pf, impf, biasp, iter)

	
# 2. Getting a dictionary: cs; aspect value -> [en translations]
- **VallexGlosbeDictionary**: 
Class containing methods that create a VallexGlosbeDictionary by looking up czech Vallex Verbs in Glosbe and finding List of english translations
Once created, an instance of the VallexGlosbeDictionary can be used for further processing.

* input:"vallex\\vallex_aspectOutput.txt"
* output: "vallex/dictionary.csv" -> czech verbs with aspect value and englisch translations

The dictionary has 4221 entries (after removing 2 aspectual values and homographs from vallex);
of these only 2915 have a matching translation


# 3. Container Classes for the corpus Processing
- **Verb**
- **Sentence**


# 4. Processing the parallel texts
- **CorpusParser** - parses (pre-split).xml files from intercorp_en, intercorp_cs 
- **CorrespondenceParser** - parses .xml files from correspondences_intercorp_en2cs


- **SentenceProcessor**(filename_en2cs, filename_en, filename_cs) 
Initialises a CorrespondenceParser(filename_en2cs) and CorpusParser(filename_en), CorpusParser(filename_cs);
gets/initializes corresponding sentencePairs

- **SentencePair**(sentence_en, sentence_cs, outputFile) 
Checks verb correspondences (if the GlosbeVallexDictionary translation of a verb from the czech verblist corresponds to a verb from the english Verblist)


- **Mainapp**:
..1. instantiates a **VallexGlosbeDictionary**
* input: vallex/dictionary.csv

..2. initializes instance of **SentenceProcessor**
inside it instantiates a corpus parser and a correspondence parser
processes corpora and writes corresponding sentence pairs: sentenceProcessor.getSentencePairs(output_filename) and writes files in output_sentences

* input: correspondences_intercorp_en2cs, intercorp_en, intercorp_cs

* output: output_sentences -> collection of sentence pairs, a file per book, a sentence pair per verb
of the form:
token_en, inf_en, token_cs, inf_cs, aspect, English sentence, Czech sentence
"come","come","prišel","prijít","pf"," Zarquon has come again! ”"," Zarquon znovu prišel! """


# 5. Ordering the processed output (from 5.) and extracting sample sentences for annotation

- **OutputVerbDataDictionary**: 
Processes the <verb: verbdata, sentence occurance> data from all output files (folder output_sentences) from the processed corpus
and builds a dictionary for each verb: <verb : [data, all sentence occurances]>

- **WriteProcessedOutput**
Initializes OutputVerbDataDictionary;
uses instance of OutputVerbDataDictionary to select for each verb occuring in the corpus data the first two(in this case) sentences of occurrence

* input: folder with processed output files
* output: in folder processed_output: 
selected_1.csv (2774 verbs/sentences), selected_2.csv (2374) -> first sentence (first sentence value for each verb key in the OutputVerbDataDictionary) - used for the annotation, second sentence(-"-) - additional sentences, not used further
full_output.txt - all entries of OutputVerbDataDictionary
verb_keys.txt - unique verb occurrences
verb_keys_occurrenceNumber.txt -> verb - #occurrences

- **PrepareFinalFiles**:
Reads selected_n.csv file (in processed_output) -> write .txt and .json; writes only 200 sentences per .txt (better processing for the annotation tool)

* input: in folder processed input
* output: in folder final_files