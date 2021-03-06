package com.like.netty.protocol.custom.server.session;


import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

/**
 * Create By like On 2021-04-11 15:23
 * 聊天组会话 顶级接口
 */
public interface GroupSession {

    /**
     * 创建组
     *
     * @param name    group的名字
     *                {@link String}
     * @param members 成员
     *                {@link Set<String>}
     * @param creator 聊天室所有者
     *                {@link String}
     * @return {@link Group}
     * @Description: 创建聊天组, 如果不存在就创建成功。
     *         返回null: 创建成功
     *         否者: 创建失败
     */
    Group createGroup(String name, Set<String> members, String creator);

    /**
     * 判断是否存在该聊天室
     * @param name group的名字
     *             {@link String}
     * @return boolean
     */
    boolean hasGroup(String name);

    /**
     * 解散聊天组
     *
     * @param name group的名字
     * @return {@link Group}
     */
    Group dissolutionOfGroup(String name);

    /**
     * 加入到聊天组
     *
     * @param name   group的名字
     *               {@link String}
     * @param member 需要加入的成员
     *               {@link String}
     * @return {@link Group}
     */
    Group joinMember(String name, String member);

    /**
     * 移除出聊天组
     *
     * @param name   group的名字
     *               {@link String}
     * @param member 需要移除的成员
     *               {@link String}
     * @return {@link Group}
     */
    Group removeMember(String name, String member);

    /**
     * 获取聊天组的所有用户
     *
     * @param name group的名字
     * @return {@link Set<String>}
     */
    Set<String> getMembers(String name);


    /**
     * 获取聊天组内成员 对应的channel 在线才获取
     *
     * @param name group的名字
     * @return {@link List<Channel>}
     */
    List<Channel> getMembersChannels(String name);
}
