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
import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;

import static com.pvpin.pvpincore.modules.js.parser.ParserManager.CXT;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class InfoParser {
    protected String code;
    protected FunctionNode node;

    protected String name = null;
    protected String version = null;
    protected String author = null;

    public InfoParser(String code) {
        this.code = code;
        Source src = Source.sourceFor("js", code);
        Parser parser = new Parser(ScriptEnvironment.builder().build(), src, new ErrorManager.ThrowErrorManager());
        this.node = parser.parse();
        node.getBody().getStatements().forEach(element -> {
            if (element.isTokenType(TokenType.VAR) || element.isTokenType(TokenType.LET) || element.isTokenType(TokenType.CONST)) {
                VarNode varNode = (VarNode) element;
                if (varNode.isAssignment()) {
                    switch (varNode.getName().getName()) {
                        case "name": {
                            this.name = CXT.eval("js", varNode.getAssignmentSource().toString()).asString();
                            break;
                        }
                        case "version": {
                            this.version = CXT.eval("js", varNode.getAssignmentSource().toString()).asString();
                            break;
                        }
                        case "author": {
                            this.author = CXT.eval("js", varNode.getAssignmentSource().toString()).asString();
                            break;
                        }
                    }
                }
            }
        });
        parseName();
        parseVersion();
        parseAuthor();
    }

    public String parseName() {
        if (name != null) {
            return name;
        }
        throw new RuntimeException();
    }

    public String parseVersion() {
        if (version != null) {
            return version;
        }
        throw new RuntimeException();
    }

    public String parseAuthor() {
        if (author != null) {
            return author;
        }
        throw new RuntimeException();
    }
}
