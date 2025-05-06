package Syntax;

import java.util.LinkedList;

// 符号表栈类
class SymbolTableStack {
    private LinkedList<SymbolTable> stack;
    private SymbolTable globalTable = new SymbolTable();
    private int currentLevel;

    public SymbolTableStack() {
        stack = new LinkedList<>();
        currentLevel = -1;

        // 初始化全局符号表,添加内置input和output函数
        SymbolEntry inputEntry = new SymbolEntry("input","int",0);
        globalTable.addSymbol("input", inputEntry);
        SymbolEntry outputEntry = new SymbolEntry("output","void",1);
        globalTable.addSymbol("output", outputEntry);
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

    //获得全局符号表
    public SymbolTable getGlobalTable(){
        return globalTable;
    }

    //获取当前符号表
    public SymbolTable getCurrentTable(){
        return stack.peek();
    }

    // 向当前符号表添加符号
    public void addSymbol(String name, SymbolEntry entry) {
        getCurrentTable().addSymbol(name, entry);
    }

    //向全局符号表添加符号
    public void addSymbolToGlobal(String name, SymbolEntry entry) {
        getGlobalTable().addSymbol(name, entry);
    }

    // 查找当前符号表中是否有符号
    public SymbolEntry lookupAtThis(String name) {
        return getCurrentTable().lookup(name);
    }

    //查找全局符号表中是否有符号
    public SymbolEntry lookupAtGlobal(String name) {
        return getGlobalTable().lookup(name);
    }

    // 检查当前符号表是否包含某符号
    public boolean containsInThis(String name) {
        return getCurrentTable().contains(name);
    }

    // 查找当前符号表及其以下层次的符号表中是否有符号
    public boolean containsBellow(String name) {
        //先检查全局变量表中是否有符号
        if(containsInGlobal(name)){
            return true;
        }
        //再依次检查当前符号表及其以下的符号表中是否有符号
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
        return containsInGlobal(name) || containsInThis(name);
    }
}