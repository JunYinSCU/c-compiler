package Syntax;

import java.util.HashMap;
import java.util.Map;

class SymbolTable {
    private Map<String, String> table = new HashMap<>();

    // 添加符号
    public void addSymbol(String name, String note) {
        table.put(name, note);
    }

    // 查找符号
    public String lookup(String name) {
        return table.get(name);
    }

    // 检查符号是否已存在
    public boolean contains(String name) {
        return table.containsKey(name);
    }

    public void printAll(){
        for (Map.Entry<String, String> entry : table.entrySet()) {
            System.out.println("Name: " + entry.getKey() + ", Note: " + entry.getValue());
        }
    }
}
