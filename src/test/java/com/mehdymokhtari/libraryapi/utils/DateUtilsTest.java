package com.mehdymokhtari.libraryapi.util;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void testPrivateConstructorThrowsException() throws Exception {
        Constructor<DateUtils> constructor = DateUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("This is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    void testFormatDate() {
        LocalDate date = LocalDate.of(2026, 7, 6);
        assertEquals("2026-07-06", DateUtils.formatDate(date));
        assertNull(DateUtils.formatDate(null));
    }

    @Test
    void testFormatDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 7, 6, 12, 0, 0);
        assertEquals("2026-07-06T12:00:00", DateUtils.formatDateTime(dateTime));
        assertNull(DateUtils.formatDateTime(null));
    }

    @Test
    void testParseDate() {
        assertEquals(LocalDate.of(2026, 7, 6), DateUtils.parseDate("2026-07-06"));
        assertNull(DateUtils.parseDate(null));
    }

    @Test
    void testParseDateTime() {
        assertEquals(LocalDateTime.of(2026, 7, 6, 12, 0, 0), DateUtils.parseDateTime("2026-07-06T12:00:00"));
        assertNull(DateUtils.parseDateTime(null));
    }

    @Test
    void testTodayAndNow() {
        assertNotNull(DateUtils.today());
        assertNotNull(DateUtils.now());
    }

    @Test
    void testIsPastDate() {
        assertTrue(DateUtils.isPastDate(LocalDate.now().minusDays(1)));
        falseFieldCheck(DateUtils.isPastDate(LocalDate.now().plusDays(1)));
        assertFalse(DateUtils.isPastDate(null));
    }

    @Test
    void testIsFutureDate() {
        assertTrue(DateUtils.isFutureDate(LocalDate.now().plusDays(1)));
        assertFalse(DateUtils.isFutureDate(LocalDate.now().minusDays(1)));
        assertFalse(DateUtils.isFutureDate(null));
    }

    @Test
    void testIsToday() {
        assertTrue(DateUtils.isToday(LocalDate.now()));
        assertFalse(DateUtils.isToday(LocalDate.now().minusDays(1)));
        assertFalse(DateUtils.isToday(null));
    }

    private void falseFieldCheck(boolean dynamicVal) {
        assertFalse(dynamicVal);
    }
}