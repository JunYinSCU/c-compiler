package Syntax;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TreePrinter {
    private static BufferedWriter output = null;

    // 打印语法树到指定文件
    public static void print(String outputFile,ASTNode root) {
        try {
            // 打开输出文件
            output = new BufferedWriter(new FileWriter(outputFile));
            // 调用打印帮助函数
            printHelper(root, "", true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();  // 确保写完后关闭流
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // 显式语法树到控制台
    public static void show(ASTNode root) {
        if (root == null) {
            return;
        }
        showHelper(root, "", true);
    }
    
    // 获取语法树的深度
    public static int getTreeDepth(ASTNode root) {
        if (root == null) return 0;
        int maxChildDepth = 0;
        for (ASTNode child : root.getChildren()) {
            maxChildDepth = Math.max(maxChildDepth, getTreeDepth(child));
        }
        return 1 + maxChildDepth;
    }
    
    
    private static void printHelper(ASTNode root, String indent, boolean isLast) {
        // 打印当前节点的名称
        try {
            output.write(indent);
            if (isLast) {
                output.write("└── ");
            } else {
                output.write("├── ");
            }
            output.write(root.getName() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 递归打印所有子节点
        List<ASTNode> children = root.getChildren();
        int childrenCount = children.size();
        for (int i = 0; i < childrenCount; i++) {
            ASTNode child = children.get(i);
            String newIndent;
            if (isLast) {
                newIndent = indent + "    ";  // 如果是最后一个子节点，缩进不变
            } else {
                newIndent = indent + "|   ";  // 如果不是最后一个子节点，显示竖线符号
            }
            // 递归调用，判断是否是最后一个子节点
            printHelper(child, newIndent, i == childrenCount - 1);
        }
    }

    private static void showHelper(ASTNode root, String indent, boolean isLast) {
        // 打印当前节点的名称
        System.out.print(indent);
        if (isLast) {
            System.out.print("└── ");
        } else {
            System.out.print("├── ");
        }
        System.out.println(root.getName());

        // 递归打印所有子节点
        int childrenCount = root.getChildren().size();
        for (int i = 0; i < childrenCount; i++) {
            ASTNode child = root.getChildren().get(i);
            String newIndent;
            if (isLast) {
                newIndent = indent + "    ";  // 如果是最后一个子节点，缩进不变
            } else {
                newIndent = indent + "|   ";  // 如果不是最后一个子节点，显示竖线符号
            }
            // 递归调用，判断是否是最后一个子节点
            showHelper(child, newIndent, i == childrenCount - 1);
        }
    }
}
