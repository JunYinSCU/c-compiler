package Syntax;

import java.util.ArrayList;
import java.util.List;

public class TreePrinter1 {

    // Get the depth of the AST
    public static int getTreeDepth(ASTNode root) {
        if (root == null) return 0;
        int maxChildDepth = 0;
        for (ASTNode child : root.getChildren()) {
            maxChildDepth = Math.max(maxChildDepth, getTreeDepth(child));
        }
        return 1 + maxChildDepth;
    }

    // Get the maximum number of nodes at any level to estimate width
    private static int getMaxWidth(ASTNode root, int maxDepth) {
        if (root == null) return 0;
        List<Integer> levelCounts = new ArrayList<>();
        for (int i = 0; i < maxDepth; i++) {
            levelCounts.add(0);
        }
        countNodesAtLevel(root, 0, levelCounts);
        int maxWidth = 0;
        for (int count : levelCounts) {
            maxWidth = Math.max(maxWidth, count);
        }
        return maxWidth * 4 + 1; // Each node needs ~4 chars for spacing
    }

    // Helper to count nodes at each level
    private static void countNodesAtLevel(ASTNode node, int level, List<Integer> levelCounts) {
        if (node == null) return;
        levelCounts.set(level, levelCounts.get(level) + 1);
        for (ASTNode child : node.getChildren()) {
            countNodesAtLevel(child, level + 1, levelCounts);
        }
    }

    // Write node and its children to the 2D array
    private static void writeArray(ASTNode currNode, int rowIndex, int columnIndex, String[][] res, int treeDepth, int[] childOffsets) {
        if (currNode == null) return;

        // Place current node's name in the array
        res[rowIndex][columnIndex] = currNode.getName();

        // Get children
        List<ASTNode> children = currNode.getChildren();
        if (children.isEmpty()) return;

        // Calculate current level (0-based, root is 0)
        int currLevel = rowIndex / 2;
        if (currLevel >= treeDepth - 1) return; // No more levels to process

        // Calculate gap for connectors (decreases with level)
        int gap = treeDepth - currLevel - 1;

        // Calculate child positions, centering them under the parent
        int totalChildWidth = children.size() * 2; // Each child needs ~2 columns
        int startColumn = columnIndex - totalChildWidth / 2;

        for (int i = 0; i < children.size(); i++) {
            ASTNode child = children.get(i);
            int childColumn = startColumn + i * 2 + 1; // Spread children evenly

            // Store offset for connector positioning
            childOffsets[i] = childColumn;

            // Place connector: "/" for first, "\" for last, "-" for middle
            String connector = i == 0 ? "/" : (i == children.size() - 1 ? "\\" : "-");
            res[rowIndex + 1][childColumn] = connector;

            // Recursively process child
            writeArray(child, rowIndex + 2, childColumn, res, treeDepth, new int[child.getChildren().size()]);
        }
    }

    public static void show(ASTNode root) {
        if (root == null) {
            System.out.println("EMPTY!");
            return;
        }

        // Get tree depth and width
        int treeDepth = getTreeDepth(root);
        int arrayHeight = treeDepth * 2 - 1; // Each node + connector row
        int arrayWidth = getMaxWidth(root, treeDepth);

        // Initialize 2D array with spaces
        String[][] res = new String[arrayHeight][arrayWidth];
        for (int i = 0; i < arrayHeight; i++) {
            for (int j = 0; j < arrayWidth; j++) {
                res[i][j] = " ";
            }
        }

        // Write tree to array starting from root
        writeArray(root, 0, arrayWidth / 2, res, treeDepth, new int[root.getChildren().size()]);

        // Print the 2D array
        for (String[] line : res) {
            StringBuilder sb = new StringBuilder();
            for (String s : line) {
                sb.append(s.length() > 1 ? s : " " + s); // Pad single chars for alignment
            }
            // Trim trailing spaces
            System.out.println(sb.toString().replaceAll("\\s+$", ""));
        }
    }
}