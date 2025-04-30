package Syntax;

public class SymbolEntry {
    private String name;    // 变量名
    private String type;  // "int" 或 "void"
    private boolean isFunction;
    private int paramCount; // 如果是函数，记录参数个数

    public SymbolEntry(String name, String type) {
        this.name = name;
        this.type = type;
        this.isFunction = false;
    }

    public SymbolEntry(String name, String type, int paramCount) {
        this.name = name;
        this.type = type;
        this.paramCount = paramCount;
        this.isFunction = true;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isFunction() { return isFunction; }
    public int getParamCount() { return paramCount; }
}

