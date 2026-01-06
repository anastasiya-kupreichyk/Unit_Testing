package com.example.unittesting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LineSegmentAnalyzerTest {

    private LineSegmentAnalyzer analyzer;
    private static final double DELTA = 0.01; // Точность для сравнения double

    @BeforeEach
    void setUp() {
        analyzer = new LineSegmentAnalyzer();
    }

    // ========== Тесты для isPointOnLine() ==========

    @Test
    @DisplayName("Точка (1, 1) лежит на прямой -3x + 5y - 2 = 0")
    void testPointOnLine() {
        assertTrue(analyzer.isPointOnLine(1, 1));
    }

    @Test
    @DisplayName("Точка (4, 2.8) лежит на прямой")
    void testAnotherPointOnLine() {
        assertTrue(analyzer.isPointOnLine(4, 2.8));
    }

    @Test
    @DisplayName("Точка (0, 0) не лежит на прямой")
    void testPointNotOnLine() {
        assertFalse(analyzer.isPointOnLine(0, 0));
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, true",      // на прямой
            "4, 2.8, true",    // на прямой
            "0, 0, false",     // не на прямой
            "5, 5, false",     // не на прямой
            "-1, -0.2, true"   // на прямой (отрицательные)
    })
    @DisplayName("Параметризованный тест: проверка точек на прямой")
    void testIsPointOnLineParameterized(double x, double y, boolean expected) {
        assertEquals(expected, analyzer.isPointOnLine(x, y));
    }

    // ========== Тесты для lineValue() ==========

    @Test
    @DisplayName("Значение функции для точки (0, 0)")
    void testLineValueAtOrigin() {
        assertEquals(-2, analyzer.lineValue(0, 0), DELTA);
    }

    @Test
    @DisplayName("Значение функции для точки (1, 1)")
    void testLineValueAtPointOnLine() {
        assertEquals(0, analyzer.lineValue(1, 1), DELTA);
    }

    // ========== Тесты для doesLineIntersectSegment() ==========

    @Test
    @DisplayName("TC1: Отрезок не пересекает прямую (обе точки с одной стороны)")
    void testNoIntersectionSameSide() {
        assertFalse(analyzer.doesLineIntersectSegment(0, 0, 1, 0));
    }

    @Test
    @DisplayName("TC2: Отрезок пересекает прямую (точки по разные стороны)")
    void testIntersectionDifferentSides() {
        assertTrue(analyzer.doesLineIntersectSegment(0, 0, 4, 4));
    }

    @Test
    @DisplayName("TC3: Конец отрезка на прямой")
    void testEndpointOnLine() {
        assertTrue(analyzer.doesLineIntersectSegment(1, 1, 5, 5));
    }

    @Test
    @DisplayName("TC5: Весь отрезок на прямой")
    void testSegmentOnLine() {
        assertTrue(analyzer.doesLineIntersectSegment(1, 1, 4, 2.8));
    }

    @ParameterizedTest
    @MethodSource("provideSegmentsForIntersection")
    @DisplayName("Параметризованный тест: пересечение прямой и отрезка")
    void testDoesLineIntersectSegmentParameterized(double x1, double y1, double x2, double y2, boolean expected) {
        assertEquals(expected, analyzer.doesLineIntersectSegment(x1, y1, x2, y2));
    }

    static Stream<Arguments> provideSegmentsForIntersection() {
        return Stream.of(
                Arguments.of(0, 0, 1, 0, false),      // TC1: нет пересечения
                Arguments.of(0, 0, 4, 4, true),       // TC2: пересечение
                Arguments.of(1, 1, 5, 5, true),       // TC3: конец на прямой
                Arguments.of(0, 0, 4, 2.8, true),     // TC4: конец на прямой
                Arguments.of(1, 1, 4, 2.8, true),     // TC5: весь отрезок на прямой
                Arguments.of(-5, -5, 5, 5, true),     // TC11: отрицательные координаты
                Arguments.of(100, 50, 200, 150, true) // TC12: большие значения
        );
    }

    // ========== Тесты для getIntersectionPoint() ==========

    @Test
    @DisplayName("TC1: Нет точки пересечения")
    void testGetIntersectionPointNoIntersection() {
        assertNull(analyzer.getIntersectionPoint(0, 0, 1, 0));
    }

    @Test
    @DisplayName("TC2: Точка пересечения в середине отрезка")
    void testGetIntersectionPointInMiddle() {
        double[] point = analyzer.getIntersectionPoint(0, 0, 4, 4);
        assertNotNull(point);
        assertEquals(1.54, point[0], DELTA);
        assertEquals(1.54, point[1], DELTA);
    }

    @Test
    @DisplayName("TC3: Точка пересечения в начале отрезка")
    void testGetIntersectionPointAtStart() {
        double[] point = analyzer.getIntersectionPoint(1, 1, 5, 5);
        assertNotNull(point);
        assertEquals(1, point[0], DELTA);
        assertEquals(1, point[1], DELTA);
    }

    @Test
    @DisplayName("TC4: Точка пересечения в конце отрезка")
    void testGetIntersectionPointAtEnd() {
        double[] point = analyzer.getIntersectionPoint(0, 0, 4, 2.8);
        assertNotNull(point);
        assertEquals(4, point[0], DELTA);
        assertEquals(2.8, point[1], DELTA);
    }

    @Test
    @DisplayName("TC8: Вертикальный отрезок")
    void testGetIntersectionPointVerticalSegment() {
        double[] point = analyzer.getIntersectionPoint(2, 0, 2, 5);
        assertNotNull(point);
        assertEquals(2, point[0], DELTA);
        assertEquals(1.6, point[1], DELTA);
    }

    @Test
    @DisplayName("TC9: Горизонтальный отрезок")
    void testGetIntersectionPointHorizontalSegment() {
        double[] point = analyzer.getIntersectionPoint(0, 2, 5, 2);
        assertNotNull(point);
        assertEquals(2.67, point[0], DELTA);
        assertEquals(2, point[1], DELTA);
    }

    // ========== Тесты для arePerpendicular() ==========

    @Test
    @DisplayName("TC6: Прямая и отрезок перпендикулярны")
    void testArePerpendicular() {
        // Направление прямой: (5, 3)
        // Перпендикулярное направление: (-3, 5)
        assertTrue(analyzer.arePerpendicular(2, 1, -1, 6));
    }

    @Test
    @DisplayName("Прямая и отрезок НЕ перпендикулярны")
    void testAreNotPerpendicular() {
        assertFalse(analyzer.arePerpendicular(0, 0, 4, 4));
    }

    @Test
    @DisplayName("Горизонтальный отрезок не перпендикулярен")
    void testHorizontalSegmentNotPerpendicular() {
        assertFalse(analyzer.arePerpendicular(0, 2, 5, 2));
    }

    @ParameterizedTest
    @CsvSource({
            "2, 1, -1, 6, true",      // перпендикулярны
            "0, 0, 4, 4, false",      // не перпендикулярны
            "1, 1, 5, 5, false",      // не перпендикулярны
            "0, 0, -3, 5, true"       // перпендикулярны
    })
    @DisplayName("Параметризованный тест: проверка перпендикулярности")
    void testArePerpendicularParameterized(double x1, double y1, double x2, double y2, boolean expected) {
        assertEquals(expected, analyzer.arePerpendicular(x1, y1, x2, y2));
    }

    // ========== Тесты для isEndPoint() ==========

    @Test
    @DisplayName("Точка является первым концом отрезка")
    void testIsFirstEndpoint() {
        assertTrue(analyzer.isEndPoint(1, 1, 1, 1, 5, 5));
    }

    @Test
    @DisplayName("Точка является вторым концом отрезка")
    void testIsSecondEndpoint() {
        assertTrue(analyzer.isEndPoint(5, 5, 1, 1, 5, 5));
    }

    @Test
    @DisplayName("Точка НЕ является концом отрезка")
    void testIsNotEndpoint() {
        assertFalse(analyzer.isEndPoint(3, 3, 1, 1, 5, 5));
    }

    // ========== Тесты для analyzePosition() ==========

    @Test
    @DisplayName("TC1: Анализ - нет пересечения")
    void testAnalyzePositionNoIntersection() {
        String result = analyzer.analyzePosition(0, 0, 1, 0);
        assertTrue(result.contains("do not intersect"));
    }

    @Test
    @DisplayName("TC2: Анализ - есть пересечение")
    void testAnalyzePositionWithIntersection() {
        String result = analyzer.analyzePosition(0, 0, 4, 4);
        assertTrue(result.contains("ONE common point"));
    }

    @Test
    @DisplayName("TC3: Анализ - конец отрезка на прямой")
    void testAnalyzePositionEndpointOnLine() {
        String result = analyzer.analyzePosition(1, 1, 5, 5);
        assertTrue(result.contains("ONE common point"));
        assertTrue(result.contains("END of the segment"));
    }

    @Test
    @DisplayName("TC5: Анализ - весь отрезок на прямой")
    void testAnalyzePositionSegmentOnLine() {
        String result = analyzer.analyzePosition(1, 1, 4, 2.8);
        assertTrue(result.contains("lies entirely on"));
    }

    @Test
    @DisplayName("TC6: Анализ - перпендикулярное пересечение")
    void testAnalyzePositionPerpendicular() {
        String result = analyzer.analyzePosition(2, 1, -1, 6);
        assertTrue(result.contains("ONE common point"));
        assertTrue(result.contains("mutually perpendicular"));
    }

    @Test
    @DisplayName("Анализ - НЕ перпендикулярное пересечение")
    void testAnalyzePositionNotPerpendicular() {
        String result = analyzer.analyzePosition(0, 0, 4, 4);
        assertTrue(result.contains("NOT mutually perpendicular"));
    }

    @Test
    @DisplayName("TC10: Вырожденный отрезок (точка)")
    void testAnalyzePositionDegenerateSegment() {
        String result = analyzer.analyzePosition(1, 1, 1, 1);
        assertTrue(result.contains("lies entirely on") || result.contains("ONE common point"));
    }

    @ParameterizedTest
    @MethodSource("provideSegmentsForAnalysis")
    @DisplayName("Параметризованный тест: комплексный анализ позиции")
    void testAnalyzePositionParameterized(double x1, double y1, double x2, double y2, String expectedKeyword) {
        String result = analyzer.analyzePosition(x1, y1, x2, y2);
        assertTrue(result.contains(expectedKeyword),
                "Expected result to contain: " + expectedKeyword + ", but got: " + result);
    }

    static Stream<Arguments> provideSegmentsForAnalysis() {
        return Stream.of(
                Arguments.of(0, 0, 1, 0, "do not intersect"),           // TC1
                Arguments.of(0, 0, 4, 4, "ONE common point"),           // TC2
                Arguments.of(1, 1, 5, 5, "END of the segment"),         // TC3
                Arguments.of(1, 1, 4, 2.8, "lies entirely on"),         // TC5
                Arguments.of(2, 1, -1, 6, "mutually perpendicular"),    // TC6
                Arguments.of(-5, -5, 5, 5, "ONE common point"),         // TC11
                Arguments.of(100, 50, 200, 150, "ONE common point")     // TC12
        );
    }

    // ========== Граничные случаи ==========

    @Test
    @DisplayName("Граничный случай: нулевые координаты")
    void testEdgeCaseZeroCoordinates() {
        assertFalse(analyzer.isPointOnLine(0, 0));
    }

    @Test
    @DisplayName("Граничный случай: большие числа")
    void testEdgeCaseLargeNumbers() {
        double[] point = analyzer.getIntersectionPoint(100, 50, 200, 150);
        assertNotNull(point);
    }

    @Test
    @DisplayName("Граничный случай: отрицательные координаты")
    void testEdgeCaseNegativeCoordinates() {
        double[] point = analyzer.getIntersectionPoint(-5, -5, 5, 5);
        assertNotNull(point);
    }
}