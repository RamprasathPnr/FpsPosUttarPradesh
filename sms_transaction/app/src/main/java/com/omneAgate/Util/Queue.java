package com.omneagate.Util;

import com.omneagate.DTO.LoggingDto;

import java.util.LinkedList;

/**
 * Queue is for log management
 */
public class Queue {
    private final LinkedList<LoggingDto> list;

    // Queue constructor
    public Queue() {
        // Create a new LinkedList.
        list = new LinkedList<LoggingDto>();
    }

    // Post: Returns true if the queue is empty. Otherwise, false.
    public boolean isEmpty() {
        boolean empty = false;
        if (list.size() == 0)
            empty = true;
        return empty;
    }

    public void enqueue(LoggingDto item)
    // Post: An item is added to the back of the queue.
    {
        // Append the item to the end of our linked list.
        list.add(item);
    }

    //dequeue the log
    public LoggingDto dequeue() {
        // Store a reference to the item at the front of the queue
        //   so that it does not get garbage collected when we
        //   remove it from the list.
        // Note: list.get(...) returns null if item not found at
        //   specified index. See postCondition.
        LoggingDto item = list.get(0);
        // Remove the item from the list.
        // My implementation of the linked list is based on the
        //   J2SE API reference. In both, elements start at 1,
        //   unlike arrays which start at 0.
        list.remove(0);

        // Return the item
        return item;
    }

}