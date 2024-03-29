/*
 * The MIT License
 * Copyright © 2020-2021 PVPINStudio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pvpin.pvpincore.modules.js.parser;

import com.oracle.js.parser.*;
import com.oracle.js.parser.ir.FunctionNode;
import com.oracle.js.parser.ir.VarNode;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.TypeLiteral;

import java.io.IOException;
import java.util.List;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class ParserManager {

    protected static final Context INTERNAL_CONTEXT;

    static {
        ClassLoader appCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
        INTERNAL_CONTEXT = Context.newBuilder("js").allowAllAccess(true).build();
        try {
            INTERNAL_CONTEXT.getPolyglotBindings().putMember("internal", true);
            INTERNAL_CONTEXT.eval(org.graalvm.polyglot.Source.newBuilder("js", PVPINCore.class.getResource("/espree.js")).build());
            INTERNAL_CONTEXT.eval(org.graalvm.polyglot.Source.newBuilder("js", PVPINCore.class.getResource("/parser.js")).build());
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        } finally {
            Thread.currentThread().setContextClassLoader(appCl);
        }
    }

    public static String parse(String code) {
        ClassLoader appCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
        String result;
        INTERNAL_CONTEXT.getBindings("js").putMember("pvpin_src0", code);
        result = INTERNAL_CONTEXT.eval("js", "parse(pvpin_src0)").asString();
        Thread.currentThread().setContextClassLoader(appCl);
        return result;
    }

    public static List<String> parseInfo(String code) {
        ClassLoader appCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
        List<String> result;
        INTERNAL_CONTEXT.getBindings("js").putMember("pvpin_src0", code);
        result = INTERNAL_CONTEXT.eval("js", "parseInfo(pvpin_src0)").as(List.class);
        Thread.currentThread().setContextClassLoader(appCl);
        return result;

    }

    /**
     * This method was used to parse some code and prevent dead loops.
     * But now it is abandoned.
     *
     * @param code the code to be parsed
     * @return the parsed code, adding some controls to prevent dead loops
     */
    @Deprecated
    public static String parse0(String code) {
        Source src0 = Source.sourceFor("js", code);
        Parser parser0 = new Parser(ScriptEnvironment.builder().build(), src0, new ErrorManager.ThrowErrorManager());
        FunctionNode node = parser0.parse();
        StringBuilder codeBuilder = new StringBuilder();
        node.getBody().getStatements().forEach(element -> {
            if (element.isTokenType(TokenType.FUNCTION)) {
                FunctionNode stmt = (FunctionNode) ((VarNode) element).getAssignmentSource();
                codeBuilder.append(code, stmt.getStart(), stmt.getBody().getStart() + 1);
                stmt.getBody().getStatements().forEach(action -> {
                    String str = code.substring(action.getStart(), action.getFinish());
                    codeBuilder.append(ParserManager.parse0(str));
                });
                codeBuilder.append(code, stmt.getBody().getFinish() - 1, stmt.getFinish());
            } else if (element.isAssignment() || element.isTokenType(TokenType.CONST) || element.isTokenType(TokenType.LET) || element.isTokenType(TokenType.VAR)) {
                codeBuilder.append(element.toString(false)).append(";");
            } else {
                String stmtCode = code.substring(element.getStart(), element.getFinish());
                String ret = stmtCode;
                try {
                    ret = new LoopParser(stmtCode).parse();
                } catch (ParserException ex) {
                    ex.printStackTrace();
                    // Do nothing.
                } finally {
                    codeBuilder.append(ret);
                }
            }
        });
        return codeBuilder.toString();
    }
}
