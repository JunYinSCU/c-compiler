package Syntax;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {
    protected List<ASTNode> children = new ArrayList<>();

    public List<ASTNode> getChildren() {
        return children;
    }

    public void addChild(ASTNode child) {
        if (child != null) {
            children.add(child);
        }
    }

    protected int lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


    public abstract void print(String indent);
}