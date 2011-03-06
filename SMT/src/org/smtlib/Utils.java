/*
 * This file is part of the SMT project.
 * Copyright 2010 David R. Cok
 * Created August 2010
 */
package org.smtlib;

// TODO can we arrange Utils to be extensible?
// FIXME - needs more separation of concrete syntax

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smtlib.IExpr.IAttributeValue;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.impl.SMTExpr;

/** A class of static utility methods and constants for the SMT-LIB package. */
public class Utils {

	static final public String PROPS_FILE = "jsmtlib.properties";
	static final public String PROPS_SOLVER_EXEC_PREFIX = "org.smtlib.solver_";
	static final public String PROPS_LOGIC_PATH = "org.smtlib.logic_path";

	/** A reference to the configuration being used for this instance of SMT. */
	protected SMT.Configuration smtConfig;
	
	/** Creates a Utils instance for the given configuration */
	public Utils(SMT.Configuration smtConfig) {
		this.smtConfig = smtConfig;
	}
	
	/** The name of the test solver, implemented by this SMT app. */
	final static public String TEST_SOLVER = "test";
	
	/** The ID of the core functionality plug-in */
	final static public String PLUGIN_ID = "org.smtlib.SMT";
	
	/** The suffix used for SMT-LIB files */
	final static public String SUFFIX = ".smt2";
	
	/** Name of the Core theory */
	public static final String CORE = "Core";

	/** The string designating an option item */
	public static final String PRINT_SUCCESS = ":print-success";
	
	/** The string designating an option item */
	public static final String INTERACTIVE_MODE = ":interactive-mode";
	
	/** The string designating an option item */
	public static final String RANDOM_SEED = ":random-seed";
	
	/** The string designating an option item */
	public static final String VERBOSITY = ":verbosity";
	
	/** The string designating an option item */
	public static final String EXPAND_DEFINITIONS = ":expand-definitions";
	
	/** The string designating an option item */
	public static final String REGULAR_OUTPUT_CHANNEL = ":regular-output-channel";
	
	/** The string designating an option item */
	public static final String DIAGNOSTIC_OUTPUT_CHANNEL = ":diagnostic-output-channel";
	
	/** The string designating an option item */
	public static final String PRODUCE_PROOFS = ":produce-proofs";
	
	/** The string designating an option item */
	public static final String PRODUCE_ASSIGNMENTS = ":produce-assignments";
	
	/** The string designating an option item */
	public static final String PRODUCE_UNSAT_CORES = ":produce-unsat-cores";
	
	/** The string designating an option item */
	public static final String PRODUCE_MODELS = ":produce-models";
	
	/** The string designating an info item */
	public static final String ERROR_BEHAVIOR = ":error-behavior"; 
	
	/** The string designating an info item */
	public static final String NAME = ":name";
	
	/** The string designating an info item */
	public static final String AUTHORS = ":authors";
	
	/** The string designating an info item */
	public static final String VERSION = ":version";
	
	/** The string designating an info item */
	public static final String STATUS = ":status";
	
	/** The string designating an info item */
	public static final String REASON_UNKNOWN = ":reason-unknown";
	
	/** The string designating an info item */
	public static final String ALL_STATISTICS = ":all-statistics";

	/** The response to the :authors info item */
	public static final String AUTHORS_VALUE = "David R. Cok";
	
	/** The response to the :name info item */
	public static final String NAME_VALUE = "SMT-LIB adapter";
	
	/** The response to the :version info item */ // FIXME - must this be a string; what is the relationship to SW_VERSION?
	public static final String VERSION_VALUE = "0.0";
	
	/** The string designating the smtlib attribute within a logic or theory */
	public static final String SMTLIB_VERSION = ":smt-lib-version";
	
	/** The version of SMT-LIB that is expected of theory and logic definitions */
	public static final String SMTLIB_VERSION_20 = "2.0";
	
	/** The attribute tag for defining sorts in a theory */
	public static final String SORTS = ":sorts";
	
	/** The attribute tag for defining functions in a theory */
	public static final String FUNS = ":funs";
	
	/** The attribute tag for defining theories in a logic */
	public static final String THEORIES = ":theories";
	
	/** An ERROR_BEHAVIOR return value */
	public static final String CONTINUED_EXECUTION = "continued-execution";
	
	/** An ERROR_BEHAVIOR return value */
	public static final String IMMEDIATE_EXIT = "immediate-exit";

	/** A REASON_UNKNOWN return value */
	public static final String MEMOUT = "memout";
	
	/** A REASON_UNKNOWN return value */
	public static final String INCOMPLETE = "incomplete";
	
	/** The String for the logic symbol */
	public static final String LOGIC = "logic";
	
	/** The String for the theory symbol */
	public static final String THEORY = "theory";
	
	/** The String for the par reserved word */
	public static final String PAR = "par";
	
	/** The String for the as reserved word */
	public static final String AS = "as";
	
	/** The String for the as reserved word */
	public static final String LET = "let";
	
	/** The String for the as reserved word */
	public static final String FORALL = "forall";
	
	/** The String for the as reserved word */
	public static final String EXISTS = "exists";
	
	/** The String for the ! reserved word */
	public static final String ATTRIBUTE = "!";
	
	/** The String for the _ reserved word */
	public static final String PARAM = "_";
	
	/** The String for the stdout predefined string */
	public static final String STDOUT = "stdout";
	
	/** The String for the stderr predefined string */
	public static final String STDERR = "stderr";
	
	
	/** The set of standard options with boolean values */
	static final public Set<String> boolOptions = new HashSet<String>();
	
	/** The set of standard options with numeric values */
	static final public Set<String> numericOptions = new HashSet<String>();
	
	/** The set of standard options with string values */
	static final public Set<String> stringOptions = new HashSet<String>();
	
	/** The set of default values for all standard options */
	static final public Map<String,IAttributeValue> defaults = new HashMap<String,IAttributeValue>();
	
	/** String constant for boolean true. */
	static public final ISymbol TRUE = new SMTExpr.Symbol("true".intern());
	
	/** String constant for boolean false. */
	static public final ISymbol FALSE = new SMTExpr.Symbol("false".intern());
	
	static {
		// Initializing all the standard smtConfig
		boolOptions.add(PRINT_SUCCESS);
		boolOptions.add(EXPAND_DEFINITIONS);
		boolOptions.add(INTERACTIVE_MODE);
		boolOptions.add(PRODUCE_PROOFS);
		boolOptions.add(PRODUCE_UNSAT_CORES);
		boolOptions.add(PRODUCE_MODELS);
		boolOptions.add(PRODUCE_ASSIGNMENTS);
		numericOptions.add(RANDOM_SEED);
		numericOptions.add(VERBOSITY);
		stringOptions.add(REGULAR_OUTPUT_CHANNEL);
		stringOptions.add(DIAGNOSTIC_OUTPUT_CHANNEL);
		defaults.put(PRINT_SUCCESS, TRUE);
		defaults.put(EXPAND_DEFINITIONS, FALSE);
		defaults.put(INTERACTIVE_MODE, FALSE);
		defaults.put(PRODUCE_PROOFS, FALSE);
		defaults.put(PRODUCE_UNSAT_CORES, FALSE);
		defaults.put(PRODUCE_MODELS, FALSE);
		defaults.put(PRODUCE_ASSIGNMENTS, FALSE);
		defaults.put(RANDOM_SEED, new SMTExpr.Numeral(0));
		defaults.put(VERBOSITY, new SMTExpr.Numeral(0));
		defaults.put(REGULAR_OUTPUT_CHANNEL,new SMTExpr.StringLiteral(STDOUT,false));
		defaults.put(DIAGNOSTIC_OUTPUT_CHANNEL,new SMTExpr.StringLiteral(STDERR,false));
	}
	
	static final public HashSet<String> infoKeywords = new HashSet<String>();
	static {
		for (String k: new String[]{ NAME, AUTHORS, VERSION, ERROR_BEHAVIOR, REASON_UNKNOWN, ALL_STATISTICS}) {
			infoKeywords.add(k);
		}

	}

//	/** The values for info characteristics as used for the test solver */
//	public final static Map<String,IAttributeValue> stringInfo = new HashMap<String,IAttributeValue>();
//	
//	static {
//		// The values for standard info quantities 
//		stringInfo.put(VERSION, VERSION_VALUE);
//		stringInfo.put(AUTHORS, AUTHORS_VALUE);
//		stringInfo.put(NAME, NAME_VALUE);
//	}

	/** Quotes a string, adding enclosing quotes and putting in SMT-LIB escapes as needed 
	 * @param msg String to quote
	 * @return the quoted string
	 */
	public static String quote(String msg) {
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		for (char c: msg.toCharArray()) {
			// In SMT-LIB v2.0, the only escapes within strings are for " and \ 
			// which are represented as \" and \\
			if (c == '"') sb.append("\\\"");
			else if (c == '\\') sb.append("\\\\");
			else sb.append(c);

// Use something like the following if we ever implement C-like escapes
// Will need to add UNICODE escapes
//			if (c >= '!' && c <= '~') sb.append(c);
//			else if (c == ' ') sb.append(c);
//			else if (c == '\"') sb.append("\\\"");
//			else if (c == '\\') sb.append("\\\\");
//			else if (c == '\n') sb.append("\\n");
//			else if (c == '\t') sb.append("\\t");
//			else if (c == '\r') sb.append("\\r");
//			else if (c == '\b') sb.append("\\b");
//			else if (c == '\f') sb.append("\\f");
//			else {
//				sb.append('\\');
//				sb.append((char)('0' + ((int)c)/64));
//				sb.append((char)('0' + ((int)c)%64)/8);
//				sb.append((char)('0' + ((int)c)%8));
//			}
		}
		sb.append('"');
		return sb.toString();
	}
	
	/** Converts a quoted string (which has enclosing double quotes)
	 * to a raw sequence of ASCII characters, undoing any escape sequences,
	 * and without the enclosing quotes */
	public static String unescape(String msg) {
		StringBuilder sb = new StringBuilder();
		int k = 1;
		int endPos = msg.length()-1; 
		while (k < endPos) {
			int kk = msg.indexOf('\\',k);
			if (kk == -1) {
				sb.append(msg.substring(k,endPos));
				break;
			} else {
				if (k < kk) sb.append(msg.substring(k,kk));
				char c = msg.charAt(kk+1);
				// In SMT-LIB v2, \\ is \ , \" is "
				// and \x for some other x is \x (the \ is not special)
				if (c == '\\' || c == '"') {
					sb.append(c);
				} else {
					sb.append('\\');
					sb.append(c);
				}
				k = kk+2;
				
				// Use something like the following if we need to undo C-like escapes
//				if (Character.isDigit(c)) {
//					// octal escape
//					int v = (c-'0') * 64 + (msg.charAt(kk+2)-'0') * 8 + (msg.charAt(kk+3)-'0');
//					sb.append((char)v);
//					k = kk+4;
//				} else if (c == 'x') {
//					// hex escape
//					c = msg.charAt(kk+2);
//					int v = c < 'a' ? (c - 'A') : ( c - 'a');
//					c = msg.charAt(kk+3);
//					v = (v*16) + (c < 'a' ? (c - 'A') : ( c - 'a'));
//					sb.append((char)v);
//					k = kk+4;
//				} else {
//					switch (c) {
//					default: 
//					case '\\': 
//					case '"': 
//					case '\'': break;
//					case 'r': c = '\r'; break;
//					case 'n': c = '\n'; break;
//					case 't': c = '\t'; break;
//					case 'b': c = '\b'; break;
//					case 'f': c = '\f'; break;
//					case 'v': c = 11; break; // the \v C escape is not valid Java
//					case 'a': c = 7; break; // the \a C escape is not valid Java
//					}
//					sb.append(c);
//					k = kk+2;
//				}
			}
		}
		return sb.toString();
	}

	/** Reads a logic file.
	 * @param name the name of the logic
	 * @param path the directory in which logic files are stored
	 * @return either an ISexpr that holds a logic definition or a IResponse containing an error
	 */
	public /*@Nullable*/ILogic findLogic(String name, /*@Nullable*/String path, /*@Nullable*/IPos pos) throws Utils.SMTLIBException {
		IPos.ISource source;
		InputStream input = null;
		try {
			SMT.Configuration config = smtConfig.clone();
			config.interactive = false;
			input = SMT.logicFinder.find(smtConfig,name,pos);
			// All errors should result in thrown exceptions
			if (input== null) return null;
			source = config.smtFactory.createSource(config,input,null);
			IParser p = config.smtFactory.createParser(config,source);
			return p.parseLogic();
		} catch (IParser.ParserException e) {
			throw new Utils.SMTLIBException(smtConfig.responseFactory.error("Failed to parse the logic file for " + name + " in " + path + " : " + e,e.pos()));
		} catch (Utils.SMTLIBException e) {
			throw e;
		} catch (Exception e) {
			throw new Utils.SMTLIBException(smtConfig.responseFactory.error("Failed to read the logic file for " + name + " in " + path + " : " + e,null));
		} finally {
			try {
				if (input != null) input.close();
			} catch (java.io.IOException e) {
				throw new Utils.SMTLIBException(smtConfig.responseFactory.error("Failed to close a stream while parsing " + name + " in " + path + " : " + e,null));
			}
		}
	}
	
	// FIXME - use the log or exceptions in findLogic and findTheory - why the difference
	
	/** Reads a theory file, returning the S-expression that it holds.
	 * @param name the name of the theory
	 * @param path the directory in which theory files are stored
	 * @return either an ISexpr that holds a theory definition or an IResponse containing an error
	 */
	public /*@Nullable*/ITheory findTheory(String name, /*@Nullable*/String path) {
		IPos.ISource source;
		InputStream input = null;
		try {
			SMT.Configuration config = smtConfig.clone();
			config.interactive = false;
			input = SMT.logicFinder.find(smtConfig,name,null);
			source = config.smtFactory.createSource(config,input,null);
			IParser p = config.smtFactory.createParser(config, source);
			return p.parseTheory();
		} catch (IParser.ParserException e) {
			smtConfig.log.logError(smtConfig.responseFactory.error("Failed to parse the theory file " + name + " in " + path + ": " + e,e.pos()));
		} catch (Exception e) {
			smtConfig.log.logError(smtConfig.responseFactory.error("Failed to read the theory file " + name + " in " + path + ": " + e,null));
		} finally {
			try {
				if (input != null) input.close();
			} catch (java.io.IOException e) {
				smtConfig.log.logError(smtConfig.responseFactory.error("Failed to close a stream while parsing " + name + " in " + path + " : " + e,null));
			}
		}
		return null;
	}
	
	
	/** Finds and loads a logic into the given symbol table
	 * 
	 * @param logicName name of the logic to load
	 * @param symTable the symbol table into which to load it
	 * @return null if read OK, an error if a problem happened
	 */
	public /*@Nullable*/IResponse loadLogic(String logicName, SymbolTable symTable, /*@Nullable*/ IPos pos) {
		ILogic sx = null; // = findLogic(logicName, smtConfig.logicPath, pos);
		{
			String name = logicName;
			IPos.ISource source;
			InputStream input = null;
			try {
				SMT.Configuration config = smtConfig.clone();
				config.interactive = false;
				input = SMT.logicFinder.find(smtConfig,name,pos);
				if (input== null) return smtConfig.responseFactory.error("No logic loaded " + name, pos);
					// The above error should not happen, because an exception
					// ought to be thrown for any problems in find().
				source = config.smtFactory.createSource(config,input,null);
				IParser p = config.smtFactory.createParser(config,source);
				sx = p.parseLogic();
			} catch (IParser.ParserException e) {
				return smtConfig.responseFactory.error("Failed to parse the logic file " + name + ": " + e,e.pos());
			} catch (Utils.SMTLIBException e) {
				return e.errorResponse;
			} catch (Exception e) {
				return smtConfig.responseFactory.error("Failed to read the logic file for " + name + ": " + e,null);
			} finally {
				try {
					if (input != null) input.close();
				} catch (java.io.IOException e) {
					return smtConfig.responseFactory.error("Failed to close a stream while parsing " + name + " : " + e,null);
				}
			}
		}
		if (sx == null) {
			return smtConfig.responseFactory.error("Failed to load logic",pos);
		}
		
		// The second element should be the name of the logic, if specified
		if (!logicName.equals(sx.logicName().value())) {
			return smtConfig.responseFactory.error("Definition of logic " + logicName + " is mal-formed (internal name does not match file name): " + sx.logicName().value(),sx.logicName().pos());
		}
		
		return loadLogic(sx, symTable);
	}

	/** Loads a theory into the given symbol table.
	 * 
	 * @param theoryName the theory to load
	 * @param symTable the symbol table into which to put the theory
	 * @return null if OK, otherwise an error as a CommandResult
	 */
	public /*@Nullable */IResponse loadTheory(String theoryName, SymbolTable symTable) {
		ITheory th = findTheory(theoryName, smtConfig.logicPath);
		if (th == null) {
			// FIXME - this is a repetitive error
			return smtConfig.responseFactory.error("Failed to load theory");
		}

		// The second element should be the name of the logic, if specified
		if (theoryName != null && !theoryName.equals(th.theoryName().value())) {
			return smtConfig.responseFactory.error("Definition of logic " + theoryName + " is mal-formed (internal name does not match file name): " + th.theoryName().value(),th.theoryName().pos());
		}
		
		if (smtConfig.verbose != 0) smtConfig.log.logDiag("#Installing theory " + theoryName);
		
		/*@Nullable*/IResponse response = loadTheory(th,symTable);
		if (response == null) {
			if (theoryName.equals("ArraysEx")) symTable.arrayTheorySet = true;
			if (theoryName.equals("Fixed_Size_BitVectors")) symTable.bitVectorTheorySet = true;
		}
		return response;
	}
	
	/** This method must be overridden by a subclass to interpret the ILogic object according to the concrete syntax */
	public /*@Nullable*/IResponse loadLogic(ILogic logic, SymbolTable symTable) { 
		throw new UnsupportedOperationException("org.smtlib.Utils.loadLogic must be overridden");
	}

	/** This method must be overridden by a subclass to interpret the ITheory object according to the concrete syntax */
	public /*@Nullable*/IResponse loadTheory(ITheory theory, SymbolTable symTable) { 
		throw new UnsupportedOperationException("org.smtlib.Utils.loadTheory must be overridden");
	}
	
	static public class SMTLIBException extends Exception {
		private static final long serialVersionUID = 1L;
		public IResponse.IError errorResponse;
		public SMTLIBException(IResponse.IError err) {
			this.errorResponse = err;
		}
	}

}
