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
import com.oracle.js.parser.ir.*;
import org.graalvm.collections.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author William_Shi
 */
@Deprecated
public class LoopParser {
    protected String code;
    protected static final String CHECK_IN = "var pvpinLoopTime0 = 0;\n";
    protected static final String CHECK_OUT = "if(pvpinLoopTime0++ > 1000) throw \"Runtime Exception: Dead Loop\";\n";


    public LoopParser(String code) {
        this.code = code;
    }

    public String parse() {
        String forSrc = new ForVisitor(code).parse();
        String whileSrc = new WhileVisitor(forSrc).parse();
        return whileSrc;
    }

}

@Deprecated
class ForVisitor {
    protected String code;
    protected FunctionNode node;

    public ForVisitor(String code) {
        this.code = code;
        Source src0 = Source.sourceFor("js", code);
        Parser parser0 = new Parser(ScriptEnvironment.builder().build(), src0, new ErrorManager.ThrowErrorManager());
        this.node = parser0.parse();
    }

    public String parse() {
        StringBuilder codeBuilder = new StringBuilder();
        List<Pair<Integer, Integer>> cutList = new ArrayList<>(32);
        List<String> loopBodyList = new ArrayList<>(32);
        var ref = new Object() {
            int first = -1;
            int last = -1;
        };
        node.getBody().getStatements()
                .stream().filter(element -> element.isTokenType(TokenType.FOR))
                .filter(element -> ((BlockStatement) element).getBlock().getStatements().get(0) instanceof ForNode)
                .filter(element -> !((ForNode) ((BlockStatement) element).getBlock().getStatements().get(0)).isForEach())
                .forEach(element -> {
                    ref.first = ref.first == -1 ? element.getStart() : Math.min(ref.first, element.getStart());
                    ref.last = ref.last == -1 ? element.getFinish() : Math.max(ref.last, element.getFinish());
                    ForNode stmt = ((ForNode) ((BlockStatement) element).getBlock().getStatements().get(0));
                    ref.first = ref.first == -1 ? element.getStart() : Math.min(ref.first, element.getStart());
                    ref.last = ref.last == -1 ? element.getFinish() : Math.max(ref.last, element.getFinish());
                    cutList.add(Pair.create(element.getStart(), element.getFinish()));
                    StringBuilder tempCodeBuilder = new StringBuilder();
                    tempCodeBuilder.append(LoopParser.CHECK_IN);
                    tempCodeBuilder.append(code, stmt.getStart(), stmt.getBody().getStart() + 1);
                    tempCodeBuilder.append("{");
                    tempCodeBuilder.append(LoopParser.CHECK_OUT);
                    stmt.getBody().getStatements().forEach(action -> {
                        String str = code.substring(action.getStart(), action.getFinish());
                        tempCodeBuilder.append(ParserManager.parse(str));
                    });
                    tempCodeBuilder.append(code, stmt.getBody().getFinish() - 1, stmt.getFinish());
                    tempCodeBuilder.append("}");
                    loopBodyList.add(tempCodeBuilder.toString());
                });
        if (ref.first == -1 || ref.last == -1) {
            return code;
        }
        codeBuilder.append(code, 0, ref.first);
        for (int i = 0; i < cutList.size(); i++) {
            codeBuilder.append(loopBodyList.get(i));
            if (i < cutList.size() - 1) {
                codeBuilder.append(code, cutList.get(i).getRight(), cutList.get(i + 1).getLeft());
            }
        }
        codeBuilder.append(code.substring(ref.last));
        return codeBuilder.toString();
    }
}

@Deprecated
class WhileVisitor {
    protected String code;
    protected FunctionNode node;

    public WhileVisitor(String code) {
        this.code = code;
        Source src0 = Source.sourceFor("js", code);
        Parser parser0 = new Parser(ScriptEnvironment.builder().build(), src0, new ErrorManager.ThrowErrorManager());
        this.node = parser0.parse();
    }

    public String parse() {
        StringBuilder codeBuilder = new StringBuilder();
        List<Pair<Integer, Integer>> cutList = new ArrayList<>(32);
        List<String> loopBodyList = new ArrayList<>(32);
        var ref = new Object() {
            int first = -1;
            int last = -1;
        };
        node.getBody().getStatements().stream()
                .filter(element -> element.isTokenType(TokenType.WHILE) || element.isTokenType(TokenType.DO))
                .forEach(element -> {
                    ref.first = ref.first == -1 ? element.getStart() : Math.min(ref.first, element.getStart());
                    ref.last = ref.last == -1 ? element.getFinish() : Math.max(ref.last, element.getFinish());

                    WhileNode stmt = (WhileNode) element;
                    cutList.add(Pair.create(element.getStart(), element.getFinish()));
                    StringBuilder tempCodeBuilder = new StringBuilder();
                    if (stmt.isDoWhile()) {
                        tempCodeBuilder.append(LoopParser.CHECK_IN);
                        tempCodeBuilder.append("do{\n");
                        tempCodeBuilder.append(LoopParser.CHECK_OUT);
                        tempCodeBuilder.append(
                                new LoopParser(code.substring(stmt.getBody().getStart(), stmt.getBody().getFinish())).parse()
                        );
                        tempCodeBuilder.append("}while(");
                        tempCodeBuilder.append(code, stmt.getTest().getStart(), stmt.getTest().getFinish());
                        tempCodeBuilder.append(");\n");
                    } else {
                        tempCodeBuilder.append(LoopParser.CHECK_IN);
                        tempCodeBuilder.append("while(");
                        tempCodeBuilder.append(code, stmt.getTest().getStart(), stmt.getTest().getFinish());
                        tempCodeBuilder.append("){\n");
                        tempCodeBuilder.append(LoopParser.CHECK_OUT);
                        tempCodeBuilder.append(
                                ParserManager.parse(code.substring(stmt.getBody().getStart(), stmt.getBody().getFinish()))
                        );
                        tempCodeBuilder.append("}\n");
                    }
                    loopBodyList.add(tempCodeBuilder.toString());

                });
        if (ref.first == -1 || ref.last == -1) {
            return code;
        }
        codeBuilder.append(code, 0, ref.first);
        for (int i = 0; i < cutList.size(); i++) {
            codeBuilder.append(loopBodyList.get(i));
            if (i < cutList.size() - 1) {
                codeBuilder.append(code, cutList.get(i).getRight(), cutList.get(i + 1).getLeft());
            }
        }
        codeBuilder.append(code.substring(ref.last));
        return codeBuilder.toString();
    }
}
