package com.my.storetree.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.my.storetree.bean.Node;
import com.my.storetree.bean.NodeRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClosureTableService {

    @Autowired
    private NodeRelationService nodeRelationService;
    @Autowired
    private NodeService nodeService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    // A -> B -> C -> D
    // 1 -> 2 -> 3 -> 4
//    @Transactional(rollbackFor = Exception.class)
    public boolean closureInsert(Node node) {
        if (node != null) {
            // 开启事务
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            // 保存当前节点
            boolean saveNodeSuccess = nodeService.save(node);

            if (saveNodeSuccess) {
                Long currentNodeId = node.getId();

                // 创建本节点与本节点的关系
                NodeRelation self = new NodeRelation()
                        .setAncestor(currentNodeId)
                        .setDescendant(currentNodeId)
                        .setDistance(0L);

                boolean saveRelationSuccess = false;

                // 判断是否是顶级节点
                if (node.getFatherId() == null || node.getFatherId() == 0) {
                    saveRelationSuccess = nodeRelationService.save(self);
                } else {
                    List<NodeRelation> descendantList = nodeRelationService.list(new LambdaQueryWrapper<NodeRelation>()
                            .eq(NodeRelation::getDescendant, node.getFatherId()));

                    if (CollUtil.isNotEmpty(descendantList)) {
                        List<NodeRelation> saveRelationList = new ArrayList<>();
                        saveRelationList.add(self);

                        for (NodeRelation descendant : descendantList) {
                            NodeRelation saveRelation = new NodeRelation()
                                    .setAncestor(descendant.getAncestor())
                                    .setDescendant(currentNodeId)
                                    .setDistance(descendant.getDistance() + 1);
                            saveRelationList.add(saveRelation);
                        }

                        saveRelationSuccess = nodeRelationService.saveBatch(saveRelationList);
                    }
                }

                if (saveRelationSuccess) {
                    // 提交事务
                    transactionManager.commit(status);
                    return true;
                }
            }
            // 回滚事务
            transactionManager.rollback(status);
        }
        return false;
    }

    public List<Node> getTree(Long nodeId) {
        return nodeRelationService.getBaseMapper().getTree(nodeId);
    }

    public List<Node> getTree() {
        List<Node> nodeList = nodeService.list();
        if (CollUtil.isNotEmpty(nodeList)) {
            Map<Long, Node> nodeMap = nodeList.stream().collect(Collectors.toMap(Node::getId, Function.identity(), (k1, k2) -> k1));

            List<NodeRelation> nodeRelationList = nodeRelationService.list();

            if (CollUtil.isNotEmpty(nodeRelationList)) {
                for (NodeRelation nodeRelation : nodeRelationList) {
                    if (nodeRelation.getDistance() == 1) {
                        // 祖先
                        Node ancestorNode = nodeMap.get(nodeRelation.getAncestor());
                        List<Node> children = Optional.ofNullable(ancestorNode.getChildren()).orElseGet(() -> {
                            List<Node> newList;
                            ancestorNode.setChildren(newList = new ArrayList<>());
                            return newList;
                        });
                        // 自己
                        Node descendantNode = nodeMap.get(nodeRelation.getDescendant());
                        descendantNode.setFatherId(nodeRelation.getAncestor());
                        children.add(descendantNode);
                    }
                }
                // 去除非顶级目录
                nodeList = nodeList.stream().filter(t -> {
                    boolean needNode = t.getFatherId() == null;
                    if (needNode) {
                        t.setFatherId(0L);
                    }
                    return needNode;
                }).collect(Collectors.toList());
            }
        }
        return nodeList;
    }
}
