package Syntax;

public class SymbolEntry {
    //用于记录变量或者函数,name是变量或者函数名;对于变量,type为变量类型,对于函数,type则为函数返回值类型
    private String name;    // 变量名
    private String type;  // "int" 或 "void"
    private int paramCount; // 如果是函数，记录参数个数

    public SymbolEntry(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public SymbolEntry(String name, String type, int paramCount) {
        this.name = name;
        this.type = type;
        this.paramCount = paramCount;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getParamCount() { return paramCount; }
}

