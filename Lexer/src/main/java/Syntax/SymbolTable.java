package Syntax;

import java.util.HashMap;
import java.util.Map;

class SymbolTable {
    private Map<String, SymbolEntry> table = new HashMap<>();

    // 添加符号
    public void addSymbol(String name, SymbolEntry entry) {
        table.put(name, entry);
    }

    // 查找符号
    public SymbolEntry lookup(String name) {
        return table.get(name);
    }

    // 检查符号是否已存在
    public boolean contains(String name) {
        return table.containsKey(name);
    }

    public void printAll(){
        for (Map.Entry<String, SymbolEntry> entry : table.entrySet()) {
            String name = entry.getKey();
            SymbolEntry symbolEntry = entry.getValue();
            System.out.println("Name: " + name + ", Type: " + symbolEntry.getType() +
                    ", ParamCount: " + symbolEntry.getParamCount());
        }
    }
}
