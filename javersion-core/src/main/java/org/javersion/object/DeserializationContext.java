package org.javersion.object;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.javersion.path.PropertyPath;
import org.javersion.path.PropertyTree;

import com.google.common.collect.Maps;

public class DeserializationContext {

    private final Map<PropertyPath, Object> properties;
    
    private final RootMapping rootMapping;
    
    private final PropertyTree rootNode;

    private final Deque<PropertyTree> queue = new ArrayDeque<>();
    
    private final Map<PropertyPath, Object> objects = Maps.newHashMap();
    
//    private QueueItem<PropertyPath, Object> currentItem;
    
    protected DeserializationContext(RootMapping rootMapping, Map<PropertyPath, Object> properties) {
        this.properties = properties;
        this.rootMapping = rootMapping;
        this.rootNode = PropertyTree.build(properties.keySet());
    }
    
    public Object getObject() {
        try {
            Object value = properties.get(rootNode.path);
            Object result = rootMapping.valueType.instantiate(rootNode, value, this);
            objects.put(rootNode.path, result);
            if (result != null && rootNode.hasChildren()) {
                rootMapping.valueType.bind(rootNode, result, this);
                while (!queue.isEmpty()) {
                    PropertyTree propertyTree = queue.pop();
                    ValueMapping valueMapping = rootMapping.get(propertyTree.path);
                    Object target = objects.get(propertyTree.path);
                    valueMapping.valueType.bind(propertyTree, target, this);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getObject(PropertyPath path) {
        PropertyTree propertyTree = rootNode.get(path);
        return propertyTree != null ? getObject(propertyTree) : null;
    }

    public Object getObject(PropertyTree propertyTree) {
        if (objects.containsKey(propertyTree.path)) {
            return objects.get(propertyTree.path);
        } else {
            ValueMapping valueMapping = rootMapping.get(propertyTree.path);
            Object value = properties.get(propertyTree.path);
            try {
                Object result = valueMapping.valueType.instantiate(propertyTree, value, this);
                objects.put(propertyTree.path, result);
                if (result != null && valueMapping.hasChildren()) {
                    queue.add(propertyTree);
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}