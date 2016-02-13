package ru.flashsafe.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class HistoryObjectTest {

    private HistoryObject<String> historyObject;
    
    @Before
    public void setUp() {
        historyObject = new HistoryObject<String>();
    }
    
    @Test
    public void hasPrevious() {
        assertThat(historyObject.hasPrevious(), is(false));
    }
    
    @Test
    public void hasNext() {
        assertThat(historyObject.hasNext(), is(false));
    }
    
    @Test
    public void addObject() {
        historyObject.addObject("TEST_PATH");
        historyObject.addObject("TEST_PATH2");
        assertThat(historyObject.hasPrevious(), is(true));
    }
    
    @Test
    public void addObject_() {
        historyObject.addObject("TEST_PATH");
        historyObject.addObject("TEST_PATH2");
        String previousRecord = historyObject.previous();
        historyObject.addObject("TEST_PATH3");
        String previousRecord2 = historyObject.previous();
        assertThat(previousRecord, equalTo(previousRecord2));
    }
    
}
