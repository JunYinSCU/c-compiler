package Syntax;


public class TreePrinter2 {
    public static void print(ASTNode root) {
        printHelper(root, "\t");
    }
    
    public static int getTreeDepth(ASTNode root) {
        if (root == null) return 0;
        int maxChildDepth = 0;
        for (ASTNode child : root.getChildren()) {
            maxChildDepth = Math.max(maxChildDepth, getTreeDepth(child));
        }
        return 1 + maxChildDepth;
    }
    
    private static void printHelper(ASTNode root, String start) {
        if (root == null) {
            return;
        }
        String mid = start.substring(0, start.lastIndexOf("\t")) + "└---";
        System.out.println(mid + root.getName());
        if (root.children == null) {
            return;
        }
        for (ASTNode node : root.getChildren()) {
            printHelper(node, start + "\t");
        }
    }
}
