// Generated from parser-generator/src/main/resources/grammar/activity/ActivityLexer.g4 by ANTLR 4.12.0
package org.sudu.experiments.parser.activity.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class ActivityLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		AND=1, OR=2, XOR=3, NOT=4, EQ=5, COMMA=6, SEMI=7, LPAREN=8, RPAREN=9, 
		LCURLY=10, RCURLY=11, LESSER=12, GREATER=13, CONS=14, ACTIVITY=15, SELECT=16, 
		REPEAT=17, SCHEDULE=18, SEQUENCE=19, RANDOM=20, DEFAULT=21, IF=22, ELSE=23, 
		INT=24, ID=25, WS=26, JAVADOC=27, COMMENT=28, LINE_COMMENT=29, NEW_LINE=30, 
		ERROR=31;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"AND", "OR", "XOR", "NOT", "EQ", "COMMA", "SEMI", "LPAREN", "RPAREN", 
			"LCURLY", "RCURLY", "LESSER", "GREATER", "CONS", "ACTIVITY", "SELECT", 
			"REPEAT", "SCHEDULE", "SEQUENCE", "RANDOM", "DEFAULT", "IF", "ELSE", 
			"INT", "ID", "WS", "JAVADOC", "COMMENT", "LINE_COMMENT", "NEW_LINE", 
			"ERROR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'and'", "'or'", "'xor'", "'!'", "'='", "','", "';'", "'('", "')'", 
			"'{'", "'}'", "'<'", "'>'", "'->'", "'activity'", "'select'", "'repeat'", 
			"'schedule'", "'sequence'", "'random'", "'default'", "'if'", "'else'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "AND", "OR", "XOR", "NOT", "EQ", "COMMA", "SEMI", "LPAREN", "RPAREN", 
			"LCURLY", "RCURLY", "LESSER", "GREATER", "CONS", "ACTIVITY", "SELECT", 
			"REPEAT", "SCHEDULE", "SEQUENCE", "RANDOM", "DEFAULT", "IF", "ELSE", 
			"INT", "ID", "WS", "JAVADOC", "COMMENT", "LINE_COMMENT", "NEW_LINE", 
			"ERROR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ActivityLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ActivityLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u001f\u00e7\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017"+
		"\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a"+
		"\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d"+
		"\u0002\u001e\u0007\u001e\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0017\u0004\u0017\u00a3\b\u0017\u000b\u0017\f\u0017\u00a4\u0001"+
		"\u0018\u0001\u0018\u0005\u0018\u00a9\b\u0018\n\u0018\f\u0018\u00ac\t\u0018"+
		"\u0001\u0019\u0004\u0019\u00af\b\u0019\u000b\u0019\f\u0019\u00b0\u0001"+
		"\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0005\u001a\u00ba\b\u001a\n\u001a\f\u001a\u00bd\t\u001a\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0005\u001b\u00c8\b\u001b\n\u001b\f\u001b\u00cb"+
		"\t\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u00d6\b\u001c\n"+
		"\u001c\f\u001c\u00d9\t\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0003"+
		"\u001d\u00de\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0002\u00bb\u00c9\u0000\u001f"+
		"\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r"+
		"\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e"+
		"\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017"+
		"/\u00181\u00193\u001a5\u001b7\u001c9\u001d;\u001e=\u001f\u0001\u0000\u0005"+
		"\u0001\u000009\u0003\u0000AZ__az\u0004\u000009AZ__az\u0003\u0000\t\t\f"+
		"\f  \u0002\u0000\n\n\r\r\u00ed\u0000\u0001\u0001\u0000\u0000\u0000\u0000"+
		"\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000"+
		"\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b"+
		"\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001"+
		"\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001"+
		"\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001"+
		"\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001"+
		"\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001"+
		"\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000"+
		"\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000\u0000"+
		"\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0000-"+
		"\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000\u0000\u00001\u0001\u0000"+
		"\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u00005\u0001\u0000\u0000\u0000"+
		"\u00007\u0001\u0000\u0000\u0000\u00009\u0001\u0000\u0000\u0000\u0000;"+
		"\u0001\u0000\u0000\u0000\u0000=\u0001\u0000\u0000\u0000\u0001?\u0001\u0000"+
		"\u0000\u0000\u0003C\u0001\u0000\u0000\u0000\u0005F\u0001\u0000\u0000\u0000"+
		"\u0007J\u0001\u0000\u0000\u0000\tL\u0001\u0000\u0000\u0000\u000bN\u0001"+
		"\u0000\u0000\u0000\rP\u0001\u0000\u0000\u0000\u000fR\u0001\u0000\u0000"+
		"\u0000\u0011T\u0001\u0000\u0000\u0000\u0013V\u0001\u0000\u0000\u0000\u0015"+
		"X\u0001\u0000\u0000\u0000\u0017Z\u0001\u0000\u0000\u0000\u0019\\\u0001"+
		"\u0000\u0000\u0000\u001b^\u0001\u0000\u0000\u0000\u001da\u0001\u0000\u0000"+
		"\u0000\u001fj\u0001\u0000\u0000\u0000!q\u0001\u0000\u0000\u0000#x\u0001"+
		"\u0000\u0000\u0000%\u0081\u0001\u0000\u0000\u0000\'\u008a\u0001\u0000"+
		"\u0000\u0000)\u0091\u0001\u0000\u0000\u0000+\u0099\u0001\u0000\u0000\u0000"+
		"-\u009c\u0001\u0000\u0000\u0000/\u00a2\u0001\u0000\u0000\u00001\u00a6"+
		"\u0001\u0000\u0000\u00003\u00ae\u0001\u0000\u0000\u00005\u00b4\u0001\u0000"+
		"\u0000\u00007\u00c3\u0001\u0000\u0000\u00009\u00d1\u0001\u0000\u0000\u0000"+
		";\u00dd\u0001\u0000\u0000\u0000=\u00e3\u0001\u0000\u0000\u0000?@\u0005"+
		"a\u0000\u0000@A\u0005n\u0000\u0000AB\u0005d\u0000\u0000B\u0002\u0001\u0000"+
		"\u0000\u0000CD\u0005o\u0000\u0000DE\u0005r\u0000\u0000E\u0004\u0001\u0000"+
		"\u0000\u0000FG\u0005x\u0000\u0000GH\u0005o\u0000\u0000HI\u0005r\u0000"+
		"\u0000I\u0006\u0001\u0000\u0000\u0000JK\u0005!\u0000\u0000K\b\u0001\u0000"+
		"\u0000\u0000LM\u0005=\u0000\u0000M\n\u0001\u0000\u0000\u0000NO\u0005,"+
		"\u0000\u0000O\f\u0001\u0000\u0000\u0000PQ\u0005;\u0000\u0000Q\u000e\u0001"+
		"\u0000\u0000\u0000RS\u0005(\u0000\u0000S\u0010\u0001\u0000\u0000\u0000"+
		"TU\u0005)\u0000\u0000U\u0012\u0001\u0000\u0000\u0000VW\u0005{\u0000\u0000"+
		"W\u0014\u0001\u0000\u0000\u0000XY\u0005}\u0000\u0000Y\u0016\u0001\u0000"+
		"\u0000\u0000Z[\u0005<\u0000\u0000[\u0018\u0001\u0000\u0000\u0000\\]\u0005"+
		">\u0000\u0000]\u001a\u0001\u0000\u0000\u0000^_\u0005-\u0000\u0000_`\u0005"+
		">\u0000\u0000`\u001c\u0001\u0000\u0000\u0000ab\u0005a\u0000\u0000bc\u0005"+
		"c\u0000\u0000cd\u0005t\u0000\u0000de\u0005i\u0000\u0000ef\u0005v\u0000"+
		"\u0000fg\u0005i\u0000\u0000gh\u0005t\u0000\u0000hi\u0005y\u0000\u0000"+
		"i\u001e\u0001\u0000\u0000\u0000jk\u0005s\u0000\u0000kl\u0005e\u0000\u0000"+
		"lm\u0005l\u0000\u0000mn\u0005e\u0000\u0000no\u0005c\u0000\u0000op\u0005"+
		"t\u0000\u0000p \u0001\u0000\u0000\u0000qr\u0005r\u0000\u0000rs\u0005e"+
		"\u0000\u0000st\u0005p\u0000\u0000tu\u0005e\u0000\u0000uv\u0005a\u0000"+
		"\u0000vw\u0005t\u0000\u0000w\"\u0001\u0000\u0000\u0000xy\u0005s\u0000"+
		"\u0000yz\u0005c\u0000\u0000z{\u0005h\u0000\u0000{|\u0005e\u0000\u0000"+
		"|}\u0005d\u0000\u0000}~\u0005u\u0000\u0000~\u007f\u0005l\u0000\u0000\u007f"+
		"\u0080\u0005e\u0000\u0000\u0080$\u0001\u0000\u0000\u0000\u0081\u0082\u0005"+
		"s\u0000\u0000\u0082\u0083\u0005e\u0000\u0000\u0083\u0084\u0005q\u0000"+
		"\u0000\u0084\u0085\u0005u\u0000\u0000\u0085\u0086\u0005e\u0000\u0000\u0086"+
		"\u0087\u0005n\u0000\u0000\u0087\u0088\u0005c\u0000\u0000\u0088\u0089\u0005"+
		"e\u0000\u0000\u0089&\u0001\u0000\u0000\u0000\u008a\u008b\u0005r\u0000"+
		"\u0000\u008b\u008c\u0005a\u0000\u0000\u008c\u008d\u0005n\u0000\u0000\u008d"+
		"\u008e\u0005d\u0000\u0000\u008e\u008f\u0005o\u0000\u0000\u008f\u0090\u0005"+
		"m\u0000\u0000\u0090(\u0001\u0000\u0000\u0000\u0091\u0092\u0005d\u0000"+
		"\u0000\u0092\u0093\u0005e\u0000\u0000\u0093\u0094\u0005f\u0000\u0000\u0094"+
		"\u0095\u0005a\u0000\u0000\u0095\u0096\u0005u\u0000\u0000\u0096\u0097\u0005"+
		"l\u0000\u0000\u0097\u0098\u0005t\u0000\u0000\u0098*\u0001\u0000\u0000"+
		"\u0000\u0099\u009a\u0005i\u0000\u0000\u009a\u009b\u0005f\u0000\u0000\u009b"+
		",\u0001\u0000\u0000\u0000\u009c\u009d\u0005e\u0000\u0000\u009d\u009e\u0005"+
		"l\u0000\u0000\u009e\u009f\u0005s\u0000\u0000\u009f\u00a0\u0005e\u0000"+
		"\u0000\u00a0.\u0001\u0000\u0000\u0000\u00a1\u00a3\u0007\u0000\u0000\u0000"+
		"\u00a2\u00a1\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000"+
		"\u00a4\u00a2\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000"+
		"\u00a50\u0001\u0000\u0000\u0000\u00a6\u00aa\u0007\u0001\u0000\u0000\u00a7"+
		"\u00a9\u0007\u0002\u0000\u0000\u00a8\u00a7\u0001\u0000\u0000\u0000\u00a9"+
		"\u00ac\u0001\u0000\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00aa"+
		"\u00ab\u0001\u0000\u0000\u0000\u00ab2\u0001\u0000\u0000\u0000\u00ac\u00aa"+
		"\u0001\u0000\u0000\u0000\u00ad\u00af\u0007\u0003\u0000\u0000\u00ae\u00ad"+
		"\u0001\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0\u00ae"+
		"\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b2"+
		"\u0001\u0000\u0000\u0000\u00b2\u00b3\u0006\u0019\u0000\u0000\u00b34\u0001"+
		"\u0000\u0000\u0000\u00b4\u00b5\u0005/\u0000\u0000\u00b5\u00b6\u0005*\u0000"+
		"\u0000\u00b6\u00b7\u0005*\u0000\u0000\u00b7\u00bb\u0001\u0000\u0000\u0000"+
		"\u00b8\u00ba\t\u0000\u0000\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000\u00ba"+
		"\u00bd\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bb"+
		"\u00b9\u0001\u0000\u0000\u0000\u00bc\u00be\u0001\u0000\u0000\u0000\u00bd"+
		"\u00bb\u0001\u0000\u0000\u0000\u00be\u00bf\u0005*\u0000\u0000\u00bf\u00c0"+
		"\u0005/\u0000\u0000\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1\u00c2\u0006"+
		"\u001a\u0000\u0000\u00c26\u0001\u0000\u0000\u0000\u00c3\u00c4\u0005/\u0000"+
		"\u0000\u00c4\u00c5\u0005*\u0000\u0000\u00c5\u00c9\u0001\u0000\u0000\u0000"+
		"\u00c6\u00c8\t\u0000\u0000\u0000\u00c7\u00c6\u0001\u0000\u0000\u0000\u00c8"+
		"\u00cb\u0001\u0000\u0000\u0000\u00c9\u00ca\u0001\u0000\u0000\u0000\u00c9"+
		"\u00c7\u0001\u0000\u0000\u0000\u00ca\u00cc\u0001\u0000\u0000\u0000\u00cb"+
		"\u00c9\u0001\u0000\u0000\u0000\u00cc\u00cd\u0005*\u0000\u0000\u00cd\u00ce"+
		"\u0005/\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000\u00cf\u00d0\u0006"+
		"\u001b\u0000\u0000\u00d08\u0001\u0000\u0000\u0000\u00d1\u00d2\u0005/\u0000"+
		"\u0000\u00d2\u00d3\u0005/\u0000\u0000\u00d3\u00d7\u0001\u0000\u0000\u0000"+
		"\u00d4\u00d6\b\u0004\u0000\u0000\u00d5\u00d4\u0001\u0000\u0000\u0000\u00d6"+
		"\u00d9\u0001\u0000\u0000\u0000\u00d7\u00d5\u0001\u0000\u0000\u0000\u00d7"+
		"\u00d8\u0001\u0000\u0000\u0000\u00d8\u00da\u0001\u0000\u0000\u0000\u00d9"+
		"\u00d7\u0001\u0000\u0000\u0000\u00da\u00db\u0006\u001c\u0000\u0000\u00db"+
		":\u0001\u0000\u0000\u0000\u00dc\u00de\u0005\r\u0000\u0000\u00dd\u00dc"+
		"\u0001\u0000\u0000\u0000\u00dd\u00de\u0001\u0000\u0000\u0000\u00de\u00df"+
		"\u0001\u0000\u0000\u0000\u00df\u00e0\u0005\n\u0000\u0000\u00e0\u00e1\u0001"+
		"\u0000\u0000\u0000\u00e1\u00e2\u0006\u001d\u0000\u0000\u00e2<\u0001\u0000"+
		"\u0000\u0000\u00e3\u00e4\t\u0000\u0000\u0000\u00e4\u00e5\u0001\u0000\u0000"+
		"\u0000\u00e5\u00e6\u0006\u001e\u0000\u0000\u00e6>\u0001\u0000\u0000\u0000"+
		"\b\u0000\u00a4\u00aa\u00b0\u00bb\u00c9\u00d7\u00dd\u0001\u0000\u0001\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}