package com.example.lib;

import java.util.ArrayList;

public class JavaSocket {
    static class TreeNode
    {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val, TreeNode left, TreeNode right)
        {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public static void main(String[] args)
    {
        TreeNode node7 = new TreeNode(7, null, null);
        TreeNode node6 = new TreeNode(6, null, null);
        TreeNode node5 = new TreeNode(5, node6, node7);
        TreeNode node4 = new TreeNode(4, null, null);
        TreeNode node3 = new TreeNode(3, null, null);
        TreeNode node2 = new TreeNode(2, node4, node5);
        TreeNode node1 = new TreeNode(1, node2, node3);
        ArrayList<TreeNode> list  = new ArrayList<>();
        levelOrder(node1, 1, list);
        System.out.println(list.toArray().toString());
    }

    public static void levelOrder(TreeNode node, int index, ArrayList list)
    {
        if(null == node)
        {
            return;
        }
        //为了防止当前数组size小于index
        int length = list.size();
        if(length <= index)
        {
            //开始填充i-length到i之间的数
            for(int j = length; j < index; j++)
            {
                list.add(j, null);
            }
        }
        list.set(index, node.val);
        levelOrder(node.left, 2 * index, list);
        levelOrder(node.right, 2 * index + 1, list);
    }
}