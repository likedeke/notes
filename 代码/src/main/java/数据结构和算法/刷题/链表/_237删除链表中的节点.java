package 数据结构和算法.刷题.链表;

/**
 * @author likeLove
 * @since 2020-10-01  18:41
 * <p>
 * https://leetcode-cn.com/problems/delete-node-in-a-linked-list
 * 输入：head = [4,5,1,9], node = 5
 * 输出：[4,1,9]
 * 解释：给定你链表中值为5的第二个节点，那么在调用了你的函数之后，该链表应变为 4 -> 1 -> 9.
 * 示例 2：
 * <p>
 * 输入：head = [4,5,1,9], node = 1
 * 输出：[4,5,9]
 * 解释：给定你链表中值为1的第三个节点，那么在调用了你的函数之后，该链表应变为 4 -> 5 -> 9.
 * <p>
 * 提示：
 * <p>
 * 链表至少包含两个节点。
 * 链表中所有节点的值都是唯一的。
 * 给定的节点为非末尾节点并且一定是链表中的一个有效节点。
 * 不要从你的函数中返回任何结果。
 * head = [4,5,1,9], node = 5
 *         4,1,1,9
 */

public class _237删除链表中的节点 {

    /**
     * 删除传入的节点
     * 1.把下一个节点的值付给当前节点
     * 2.当前节点的下一个指向下一个的下一个
     * @param node 需要删除的节点
     */
    public void deleteNode(ListNode node) {
        node.val = node.next.val;
        node.next = node.next.next;
    }

}
