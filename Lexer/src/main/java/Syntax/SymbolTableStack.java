package Syntax;

import java.util.LinkedList;

// 符号表栈类
class SymbolTableStack {
    private LinkedList<SymbolTable> stack;
    private SymbolTable globalTable = new SymbolTable();
    private int currentLevel;

    public SymbolTableStack() {
        stack = new LinkedList<>();
        currentLevel = 0;
    }

    // 推入新的符号表（例如函数开始时）
    public void push() {
        stack.push(new SymbolTable());
        currentLevel++;
    }

    // 弹出符号表（例如函数结束时）
    public void pop() {
        if (stack.size() > 1) {
            stack.pop();
            currentLevel--;
        }
        
    }

    public SymbolTable getGlobalTable(){
        return globalTable;
    }

    // 向当前符号表添加符号
    public void addSymbol(String name, String type) {
        stack.peek().addSymbol(name, type);
    }

    //向全局符号表添加符号
    public void addSymbolToGlobal(String name, String type) {
        getGlobalTable().addSymbol(name, type);
    }

    // 查找当前符号表中是否有符号
    public String lookupAtThis(String name) {
        return stack.peek().lookup(name);
    }

    // 检查当前符号表是否包含某符号
    public boolean containsAtThis(String name) {
        return stack.peek().contains(name);
    }

    // 查找当前符号表及其以下的符号表中是否有符号
    public boolean containsInBellow(String name) {
        for (int i = currentLevel; i >= 0; i--) {
            if (stack.get(i).contains(name)) {
                return true;
            }        
        }
        return false;
    }
    // 查找全局符号表中是否有符号
    public boolean containsInGlobal(String name) {
        return getGlobalTable().contains(name);
    }

    // 查找全局符号表及当前符号表中是否有符号
    public boolean containsInGlobalAndThis(String name) {
        return containsInGlobal(name) || containsAtThis(name);
    }
}