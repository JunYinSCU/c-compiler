package Syntax;

import java.util.ArrayList;
import java.util.List;

public  class ASTNode {
    private String name;    // 节点名称
    protected List<ASTNode> children = new ArrayList<>();   // 子节点列表

    // 获取子节点
    public List<ASTNode> getChildren() {
        return children;
    }
    //添加子节点
    public void addChild(ASTNode child) {
        if (child != null) {
            children.add(child);
        }
    }

    public ASTNode(String name) {
        this.name = name;
    }

    // 获取节点名称
    public String getName() {
        return name;
    }

    private void showHelper(String indent, boolean isLast) {
        System.out.print(indent);
        if (isLast) {
            System.out.print("└── ");
        } else {
            System.out.print("├── ");
        }
        System.out.println(name);

        int childrenCount = children.size();
        for (int i = 0; i < childrenCount; i++) {
            ASTNode child = children.get(i);
            String newIndent;
            if (isLast) {
                newIndent = indent + "    ";
            } else {
                newIndent = indent + "|   ";
            }
            child.showHelper(newIndent, i == childrenCount - 1);
        }
    }

    // 显示语法树到控制台
    public void show() {
        showHelper("", true);
    }

 
}