package com.like.netty.protocol.custom.server.session.impl;

import cn.hutool.core.util.ObjectUtil;
import com.like.netty.protocol.custom.server.session.Group;
import com.like.netty.protocol.custom.server.session.GroupSession;
import com.like.netty.protocol.custom.server.session.factory.SessionFactory;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GroupSessionMemoryImpl implements GroupSession {
    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();

    @Override
    public Group createGroup(String name, Set<String> members,String creator) {
        Group group = new Group(name, members,creator);
        return groupMap.putIfAbsent(name, group); // 存在key:value 不替换
    }

    @Override
    public boolean hasGroup(String name) {
        return ObjectUtil.isNotNull(groupMap.get(name));
    }

    @Override
    public Group joinMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().add(member);
            return value;
        });
    }

    @Override
    public Group removeMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().remove(member);
            return value;
        });
    }

    @Override
    public Group dissolutionOfGroup(String name) {
        return groupMap.remove(name);
    }

    @Override
    public Set<String> getMembers(String name) {
        return groupMap.getOrDefault(name, Group.EMPTY_GROUP).getMembers();
    }


    @Override
    public List<Channel> getMembersChannels(String name) {
        return getMembers(name).stream()
                .map(member -> SessionFactory.getSession().getChannel(member))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
