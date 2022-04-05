const CHECK_IN = "var pvpinLoopTime0 = 0;\n";
const CHECK_OUT = "if(pvpinLoopTime0++ > 1000) throw \"Runtime Exception: Dead Loop\";\n";

function parse(code) {
    return new LoopParser0(code).parse0();
}

function parseInfo(code) {
    return new InfoParser0(code).parseInfo0();
}

class LoopParser0 {
    constructor(code) {
        this.ast = pvpincore_parse0(code);
        this.code = code;
        this.start = new Array();
        this.bodystart = new Array();
        this.bodyend = new Array();
    }
    parse0() {
        this.ast["body"].forEach(element => {
            this.objWalk0(element);
        });
        if (this.start.length == 0) {
            return this.code;
        }
        var codeBuilder = new Array();
        var cut = this.start.concat(this.bodystart.concat(this.bodyend));
        cut.sort((a, b) => a - b);
        codeBuilder.push(this.code.substring(0, cut[0]));
        for (var i = 0; i < cut.length; i++) {
            if (this.start.includes(cut[i])) {
                codeBuilder.push(CHECK_IN);
            } else if (this.bodystart.includes(cut[i])) {
                codeBuilder.push("{");
                codeBuilder.push(CHECK_OUT);
            } else {
                codeBuilder.push("}");
            }
            if (i < cut.length - 1) {
                codeBuilder.push(this.code.substring(cut[i], cut[i + 1]));
            }
        }
        codeBuilder.push(this.code.substring(cut[cut.length - 1]));
        return codeBuilder.join("");
    }
    objWalk0(obj) {
        var element;
        for (element in obj) {
            if (element == "type") {
                if (obj[element] == "WhileStatement" || obj[element] == "ForStatement" || obj[element] == "DoWhileStatement") {
                    this.start.push(obj["start"]);
                    this.bodystart.push(obj["body"]["start"]);
                    this.bodyend.push(obj["body"]["end"]);
                }
            }
            if (obj[element] instanceof Object) {
                this.objWalk0(obj[element]);
            } else if (obj[element] instanceof Array) {
                this.arrayWalk0(obj[element]);
            }
        }
    }
    arrayWalk0(array) {
        array.forEach(element => {
            if (element instanceof Object) {
                this.objWalk0(element);
            } else if (obj[element] instanceof Array) {
                this.arrayWalk0(element);
            }
        });
    }
}

class InfoParser0{
    constructor(code) {
        this.ast = pvpincore_parse0(code);
        this.code = code;
    }
    parseInfo0() {
        var info = {};
        this.ast["body"].forEach(element => {
            if (element["type"] == "ExportNamedDeclaration" || element["type"] == "VariableDeclaration") {
                if (element["declaration"]) {
                    if (element["declaration"]["type"] != "FunctionDeclaration") {
                        element["declaration"]["declarations"].forEach(declaration => {
                            var varName = declaration["id"]["name"];
                            if (varName == "name" || varName == "version" || varName == "author") {
                                if (!info[varName]) {
                                    if (declaration["init"]) {
                                        if (declaration["init"]["type"] == "Literal") {
                                            info[varName] = declaration["init"]["value"];
                                        }
                                    }
                                }
                            }
                        });
                    }
                } else if (element["declarations"]) {
                    element["declarations"].forEach(declaration => {
                        var varName = declaration["id"]["name"];
                        if (varName == "name" || varName == "version" || varName == "author") {
                            if (!info[varName]) {
                                if (declaration["init"]) {
                                    if (declaration["init"]["type"] == "Literal") {
                                        info[varName] = declaration["init"]["value"];
                                    }
                                }
                            }
                        }
                    });
                }
            }
            if (element["type"] == "ExpressionStatement") {
                if (element["expression"]["type"] == "AssignmentExpression") {
                    var varName = element["expression"]["left"]["name"];
                    if (varName == "name" || varName == "version" || varName == "author") {
                        if (!info[varName]) {
                            if (element["expression"]["right"]["type"] == "Literal") {
                                info[varName] = element["expression"]["right"]["value"];
                            }
                        }
                    }
                } else if (element["expression"]["type"] == "SequenceExpression") {
                    element["expression"]["expressions"].forEach(declaration => {
                        if (declaration["type"] == "AssignmentExpression") {
                            var varName = declaration["left"]["name"];
                            if (varName == "name" || varName == "version" || varName == "author") {
                                if (!info[varName]) {
                                    if (declaration["right"]["type"] == "Literal") {
                                        info[varName] = declaration["right"]["value"];
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        return [info["name"], info["version"], info["author"]];
    }
}