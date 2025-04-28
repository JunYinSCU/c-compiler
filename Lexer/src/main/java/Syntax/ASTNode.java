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

    private void printHelper(String indent, boolean isLast) {
        System.out.print(indent);
        if (isLast) {
            System.out.print("`-- ");
        } else {
            System.out.print("|-- ");
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
            child.printHelper(newIndent, i == childrenCount - 1);
        }
    }

    public void print() {
        printHelper("", true);
    }

 
}