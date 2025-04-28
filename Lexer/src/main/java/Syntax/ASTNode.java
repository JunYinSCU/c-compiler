package Syntax;

import java.util.ArrayList;
import java.util.List;

public  class ASTNode {
    private String name;
    protected List<ASTNode> children = new ArrayList<>();

    public List<ASTNode> getChildren() {
        return children;
    }

    public void addChild(ASTNode child) {
        if (child != null) {
            children.add(child);
        }
    }

    public ASTNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void print(String indent) {
        // 打印当前节点的名字
        System.out.println(indent + "|-- " + name);
    
        // 遍历所有子节点并递归打印
        for (ASTNode child : children) {
            child.print(indent + "   "); // 每一层递归时增加更多的空格
        }
    }
}