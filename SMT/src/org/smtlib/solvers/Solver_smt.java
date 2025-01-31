/*
 * This file is part of the SMT project.
 * Copyright 2010 David R. Cok
 * Created August 2010
 */
package org.smtlib.solvers;

// Items not implemented:
//   attributed expressions
//   get-values get-assignment get-proof get-unsat-core
//   some error detection and handling

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.smtlib.*;
import org.smtlib.ICommand.Ideclare_fun;
import org.smtlib.ICommand.Ideclare_sort;
import org.smtlib.ICommand.Idefine_fun;
import org.smtlib.ICommand.Idefine_sort;
import org.smtlib.IExpr.IKeyword;
import org.smtlib.IExpr.INumeral;
import org.smtlib.IExpr.IStringLiteral;
import org.smtlib.IParser.ParserException;
import org.smtlib.impl.Pos;

/** This class is an adapter that takes the SMT-LIB ASTs and translates them into SMT commands */
public class Solver_smt extends AbstractSolver implements ISolver {
		
	/** A reference to the SMT configuration */
	protected SMT.Configuration smtConfig;

	/** A reference to the SMT configuration */
	@Override
	public SMT.Configuration smt() { return smtConfig; }
	
	/** The command-line arguments for launching the solver */
	String cmds[]; 

	/** The object that interacts with external processes */
	protected SolverProcess solverProcess;
	
	/** The parser that parses responses from the solver */
	protected org.smtlib.sexpr.Parser responseParser;
	
	/** The checkSatStatus returned by check-sat, if sufficiently recent, otherwise null */
	protected /*@Nullable*/ IResponse checkSatStatus = null;
	
	@Override
	public /*@Nullable*/IResponse checkSatStatus() { return checkSatStatus; }

//	// FIXME - get rid of this?
//	/** Map that keeps current values of options */
//	protected Map<String,IAttributeValue> options = new HashMap<String,IAttributeValue>();
//	{ 
//		options.putAll(Utils.defaults);
//	}
	
	/** Creates an instance of the solver */
	public Solver_smt(SMT.Configuration smtConfig, /*@NonNull*/ String executable) {
		this.smtConfig = smtConfig;
		solverProcess = new SolverProcess(cmd(executable),prompt(),smtConfig.logfile); // FIXME - what prompt?
		responseParser = new org.smtlib.sexpr.Parser(smt(),new Pos.Source("",null));
	}
	
	public Solver_smt(SMT.Configuration smtConfig, /*@NonNull*/ String[] args) {
		this.smtConfig = smtConfig;
		solverProcess = new SolverProcess(args,prompt(),smtConfig.logfile); // FIXME - what prompt?
		responseParser = new org.smtlib.sexpr.Parser(smt(),new Pos.Source("",null));
	}
	
	public String[] cmd(String exec) {
		return new String[] { exec };
	}
	
	public String prompt() {
		return "\n";
	}
	
	@Override
	public IResponse start() {
		try {
			solverProcess.start(true);
			if (smtConfig.solverVerbosity > 0) solverProcess.sendNoListen("(set-option :verbosity ",Integer.toString(smtConfig.solverVerbosity),")");
			//if (!smtConfig.batch) solverProcess.sendNoListen("(set-option :interactive-mode true)"); // FIXME - not sure we can do this - we'll lose the feedback
			// Can't turn off printing success, or we get no feedback
			//if (smtConfig.nosuccess) solverProcess.sendAndListen("(set-option :print-success false)");
			solverProcess.sendAndListen("(set-option :print-success true)");
			if (smtConfig.verbose != 0) smtConfig.log.logDiag("Started SMT ");
			return smtConfig.responseFactory.success();
		} catch (Exception e) {
			return smtConfig.responseFactory.error("Failed to start process " + cmds[0] + " : " + e.getMessage());
		}
	}
	
	/** Translates an S-expression into SMT syntax */
	protected String translate(IAccept sexpr) throws IVisitor.VisitorException {
		return translateSMT(sexpr);
	}
	
	/** Translates an S-expression into standard SMT syntax */
	protected String translateSMT(IAccept sexpr) throws IVisitor.VisitorException {
		StringWriter sw = new StringWriter();
		org.smtlib.solvers.Printer.write(sw,sexpr);
		return sw.toString();
	}
	
	public IResponse sendCommand(ICommand cmd) {
		String translatedCmd = null;
		try {
			translatedCmd = translate(cmd);
			return parseResponse(solverProcess.sendAndListen(translatedCmd,"\n"));
		} catch (IOException e) {
			return smtConfig.responseFactory.error("Error writing to solver: " + translatedCmd + " " + e);
		} catch (IVisitor.VisitorException e) {
			return smtConfig.responseFactory.error("Error writing to solver: " + translatedCmd + " " + e);
		}
	}
	
	public IResponse sendCommand(String cmd) {
		try {
			return parseResponse(solverProcess.sendAndListen(cmd,"\n"));
		} catch (IOException e) {
			return smtConfig.responseFactory.error("Error writing to solver: " + cmd + " " + e);
		}
	}
	
	protected IResponse parseResponse(String response) {
		try {
			//FIXME
			Pattern oldbv = Pattern.compile("bv([0-9]+)\\[([0-9]+)\\]");
			Matcher mm = oldbv.matcher(response);
			while (mm.find()) {
				long val = Long.parseLong(mm.group(1));
				int base = Integer.parseInt(mm.group(2));
				String bits = "";
				for (int i=0; i<base; i++) { bits = ((val&1)==0 ? "0" : "1") + bits; val = val >>> 1; }
				response = response.substring(0,mm.start()) + "#b" + bits + response.substring(mm.end(),response.length());
				mm = oldbv.matcher(response);
			}
			if (response.contains("error")) {
				// returns an s-expr (always?)
				// FIXME - (1) the {Print} also needs {Space}; (2) err_getValueTypes.tst returns a non-error s-expr and then an error s-expr - this fails for that case
				//Pattern p = Pattern.compile("\\p{Space}*\\(\\p{Blank}*error\\p{Blank}+\"(([\\p{Space}\\p{Print}^[\\\"\\\\]]|\\\\\")*)\"\\p{Blank}*\\)\\p{Space}*");
				Pattern p = Pattern.compile("\\p{Space}*\\(\\p{Blank}*error\\p{Blank}+\"(([\\p{Print}\\p{Space}&&[^\"\\\\]]|\\\\\")*)\"\\p{Blank}*\\)");
				Matcher m = p.matcher(response);
				String concat = "";
				while (m.lookingAt()) {
					if (!concat.isEmpty()) concat = concat + "; ";
					String matched = m.group(1);
					concat = concat + matched;
					m.region(m.end(0),m.regionEnd());
				}
				if (!concat.isEmpty()) response = concat;
				return smtConfig.responseFactory.error(response);
			}
			responseParser = new org.smtlib.sexpr.Parser(smt(),new Pos.Source(response,null));
			return responseParser.parseResponse(response);
		} catch (ParserException e) {
			return smtConfig.responseFactory.error("ParserException while parsing response: " + response + " " + e);
		}
	}

	@Override
	public IResponse exit() {
			IResponse response = sendCommand("(exit)");
			solverProcess.exit();
			if (smtConfig.verbose != 0) smtConfig.log.logDiag("Ended SMT ");
			solverProcess = null;
			return response;
	}
	
	public void forceExit() {
		if (solverProcess != null) solverProcess.exit();
		if (smtConfig.verbose != 0) smtConfig.log.logDiag("Ended Z3 forcibly");
	}



	@Override
	public IResponse assertExpr(IExpr sexpr) {
		try {
			return sendCommand("(assert " + translate(sexpr) + ")");
		} catch (IVisitor.VisitorException e) {
			return smtConfig.responseFactory.error("Failed to assert expression: " + e + " " + sexpr);
		} catch (Exception e) {
			return smtConfig.responseFactory.error("Failed to assert expression: " + e + " " + sexpr);
		}
	}
	
	@Override
	public IResponse get_assertions() {
		// FIXME - do we really want to call get-option here? it involves going to the solver?
		
		// FIXME - try sendCOmmand
		try {
			StringBuilder sb = new StringBuilder();
			String s;
			int parens = 0;
			do {
				s = solverProcess.sendAndListen("(get-assertions)\n");
				int p = -1;
				while (( p = s.indexOf('(',p+1)) != -1) parens++;
				p = -1;
				while (( p = s.indexOf(')',p+1)) != -1) parens--;
				sb.append(s.replace('\n',' ').replace("\r",""));
			} while (parens > 0);
			s = sb.toString();
			org.smtlib.sexpr.Parser p = new org.smtlib.sexpr.Parser(smtConfig,new org.smtlib.impl.Pos.Source(s,null));
			List<IExpr> exprs = new LinkedList<IExpr>();
			try {
				if (p.isLP()) {
					p.parseLP();
					while (!p.isRP() && !p.isEOD()) {
						IExpr e = p.parseExpr();
						exprs.add(e);
					}
					if (p.isRP()) {
						p.parseRP();
						if (p.isEOD()) return smtConfig.responseFactory.get_assertions_response(exprs); 
					}
				}
			} catch (Exception e ) {
				// continue - fall through
			}
			return smtConfig.responseFactory.error("Unexpected output from the solver: " + s);
		} catch (IOException e) {
			return smtConfig.responseFactory.error("IOException while reading solver's reponse");
		}
	}
	


	@Override
	public IResponse check_sat() {
		IResponse res;
		try {
			// Try sendCommand
			String s = solverProcess.sendAndListen("(check-sat)\n");
			//smtConfig.log.logDiag("HEARD: " + s);  // FIXME - detect errors - parseResponse?
			
			if (s.contains("unsat")) res = smtConfig.responseFactory.unsat();
			else if (s.contains("sat")) res = smtConfig.responseFactory.sat();
			else res = smtConfig.responseFactory.unknown();
			checkSatStatus = res;
		} catch (IOException e) {
			res = smtConfig.responseFactory.error("Failed to check-sat");
		}
		return res;
	}

	@Override
	public IResponse pop(int number) {
	    return sendCommand("(pop " + number + ")");
	}

	@Override
	public IResponse push(int number) {
		return sendCommand("(push " + number + ")");
	}

	@Override
	public IResponse set_logic(String logicName, /*@Nullable*/ IPos pos) {
		// FIXME - discrimninate among logics
		
		if (smtConfig.verbose != 0) smtConfig.log.logDiag("#set-logic " + logicName);
		return sendCommand("(set-logic " + logicName + ")");
	}

	@Override
	public IResponse set_option(IKeyword key, IAttributeValue value) {
		
		// FIXME - clarify all this
		String option = key.value();
		if (Utils.PRINT_SUCCESS.equals(option)) {
			if (!(Utils.TRUE.equals(value) || Utils.FALSE.equals(value))) {
				return smtConfig.responseFactory.error("The value of the " + option + " option must be 'true' or 'false'");
			}
		}
		if (Utils.VERBOSITY.equals(option)) {
			IAttributeValue v = value;
			smtConfig.verbose = (v instanceof INumeral) ? ((INumeral)v).intValue() : 0;
		} else if (Utils.DIAGNOSTIC_OUTPUT_CHANNEL.equals(option)) {
			// Actually, v should never be anything but IStringLiteral - that should
			// be checked during parsing
			String name = (value instanceof IStringLiteral)? ((IStringLiteral)value).value() : "stderr";
			if (name.equals("stdout")) {
				smtConfig.log.diag = System.out;
			} else if (name.equals("stderr")) {
				smtConfig.log.diag = System.err;
			} else {
				try {
					FileOutputStream f = new FileOutputStream(name,true); // true -> append
					smtConfig.log.diag = new PrintStream(f);
				} catch (java.io.IOException e) {
					return smtConfig.responseFactory.error("Failed to open or write to the diagnostic output " + e.getMessage(),value.pos());
				}
			}
		} else if (Utils.REGULAR_OUTPUT_CHANNEL.equals(option)) {
			// Actually, v should never be anything but IStringLiteral - that should
			// be checked during parsing
			String name = (value instanceof IStringLiteral)?((IStringLiteral)value).value() : "stdout";
			if (name.equals("stdout")) {
				smtConfig.log.out = System.out;
			} else if (name.equals("stderr")) {
				smtConfig.log.out = System.err;
			} else {
				try {
					FileOutputStream f = new FileOutputStream(name,true); // append
					smtConfig.log.out = new PrintStream(f);
				} catch (java.io.IOException e) {
					return smtConfig.responseFactory.error("Failed to open or write to the regular output " + e.getMessage(),value.pos());
				}
			}
		}

		if (!Utils.PRINT_SUCCESS.equals(option)) {
			return sendCommand(new org.smtlib.command.C_set_option(key,value));
		} else {
			return smtConfig.responseFactory.success();
		}
	}

	@Override
	public IResponse get_option(IKeyword key) {
		return sendCommand(new org.smtlib.command.C_get_option(key));
	}

	@Override
	public IResponse get_info(IKeyword key) {
		return sendCommand(new org.smtlib.command.C_get_info(key));
	}
	
	@Override
	public IResponse set_info(IKeyword key, IAttributeValue value) {
		return sendCommand(new org.smtlib.command.C_set_info(key,value));
	}


	@Override
	public IResponse declare_fun(Ideclare_fun cmd) {
		return sendCommand(cmd);
	}

	@Override
	public IResponse define_fun(Idefine_fun cmd) {
		return sendCommand(cmd);
	}

	@Override
	public IResponse declare_sort(Ideclare_sort cmd) {
		return sendCommand(cmd);
	}

	@Override
	public IResponse define_sort(Idefine_sort cmd) {
		return sendCommand(cmd);
	}
	
	@Override 
	public IResponse get_proof() {
		return sendCommand("(get-proof)");
	}

	@Override 
	public IResponse get_unsat_core() {
		return sendCommand("(get-unsat-core)");
	}

	@Override 
	public IResponse get_assignment() {
		return sendCommand("(get-assignment)");
	}

	@Override 
	public IResponse get_value(IExpr... terms) {
		try {
			solverProcess.sendNoListen("(get-value (");
			for (IExpr e: terms) {
				solverProcess.sendNoListen(" ",translate(e));
			}
			String r = solverProcess.sendAndListen("))\n");
			IResponse response = parseResponse(r);
			return response;
		} catch (IOException e) {
			return smtConfig.responseFactory.error("Error writing to solver: " + e);
		} catch (IVisitor.VisitorException e) {
			return smtConfig.responseFactory.error("Error writing to solver: " + e);
		}
	}


}
