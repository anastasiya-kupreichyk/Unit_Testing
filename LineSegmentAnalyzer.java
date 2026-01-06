package com.example.unittesting;

public class LineSegmentAnalyzer {

    // Коэффициенты прямой (I): -3x + 5y - 2 = 0
    private static final int A = -3;
    private static final int B = 5;
    private static final int C = -2;

    /**
     * Проверяет, лежит ли точка на прямой
     * @param x координата x точки
     * @param y координата y точки
     * @return true если точка на прямой, иначе false
     */
    public boolean isPointOnLine(double x, double y) {
        return Math.abs(A * x + B * y + C) < 0.0001;
    }

    /**
     * Вычисляет значение прямой для данной точки
     * @param x координата x
     * @param y координата y
     * @return значение A*x + B*y + C
     */
    public double lineValue(double x, double y) {
        return A * x + B * y + C;
    }

    /**
     * Проверяет, пересекает ли прямая отрезок
     * @param x1 координата x первой точки отрезка
     * @param y1 координата y первой точки отрезка
     * @param x2 координата x второй точки отрезка
     * @param y2 координата y второй точки отрезка
     * @return true если прямая пересекает отрезок
     */
    public boolean doesLineIntersectSegment(double x1, double y1, double x2, double y2) {
        double val1 = lineValue(x1, y1);
        double val2 = lineValue(x2, y2);

        // Если концы отрезка по разные стороны от прямой (разные знаки)
        return val1 * val2 <= 0;
    }

    /**
     * Находит точку пересечения прямой и отрезка (если существует)
     * @param x1 координата x первой точки отрезка
     * @param y1 координата y первой точки отрезка
     * @param x2 координата x второй точки отрезка
     * @param y2 координата y второй точки отрезка
     * @return массив [x, y] точки пересечения или null если нет пересечения
     */
    public double[] getIntersectionPoint(double x1, double y1, double x2, double y2) {
        if (!doesLineIntersectSegment(x1, y1, x2, y2)) {
            return null;
        }

        // Параметрическое представление отрезка: P(t) = P1 + t*(P2-P1), где t ∈ [0,1]
        // Подставляем в уравнение прямой: A*(x1 + t*(x2-x1)) + B*(y1 + t*(y2-y1)) + C = 0

        double dx = x2 - x1;
        double dy = y2 - y1;

        double denominator = A * dx + B * dy;

        if (Math.abs(denominator) < 0.0001) {
            // Отрезок параллелен прямой
            if (isPointOnLine(x1, y1)) {
                return new double[]{x1, y1}; // Весь отрезок на прямой
            }
            return null;
        }

        double t = -(A * x1 + B * y1 + C) / denominator;

        if (t < -0.0001 || t > 1.0001) {
            return null; // Пересечение за пределами отрезка
        }

        double x = x1 + t * dx;
        double y = y1 + t * dy;

        return new double[]{x, y};
    }

    /**
     * Проверяет, перпендикулярны ли прямая и отрезок
     * @param x1 координата x первой точки отрезка
     * @param y1 координата y первой точки отрезка
     * @param x2 координата x второй точки отрезка
     * @param y2 координата y второй точки отрезка
     * @return true если перпендикулярны
     */
    public boolean arePerpendicular(double x1, double y1, double x2, double y2) {
        // Направляющий вектор прямой (I): (B, -A) = (5, 3)
        // Направляющий вектор отрезка (II): (x2-x1, y2-y1)
        // Перпендикулярны если скалярное произведение = 0

        double segmentDx = x2 - x1;
        double segmentDy = y2 - y1;

        double dotProduct = B * segmentDx + (-A) * segmentDy;

        return Math.abs(dotProduct) < 0.0001;
    }

    /**
     * Проверяет, является ли точка концом отрезка
     * @param px координата x проверяемой точки
     * @param py координата y проверяемой точки
     * @param x1 координата x первой точки отрезка
     * @param y1 координата y первой точки отрезка
     * @param x2 координата x второй точки отрезка
     * @param y2 координата y второй точки отрезка
     * @return true если точка является концом отрезка
     */
    public boolean isEndPoint(double px, double py, double x1, double y1, double x2, double y2) {
        return (Math.abs(px - x1) < 0.0001 && Math.abs(py - y1) < 0.0001) ||
                (Math.abs(px - x2) < 0.0001 && Math.abs(py - y2) < 0.0001);
    }

    /**
     * Определяет взаимное положение прямой и отрезка
     * @param x1 координата x первой точки отрезка
     * @param y1 координата y первой точки отрезка
     * @param x2 координата x второй точки отрезка
     * @param y2 координата y второй точки отрезка
     * @return текстовое описание взаимного положения
     */
    public String analyzePosition(double x1, double y1, double x2, double y2) {
        StringBuilder result = new StringBuilder();

        boolean point1OnLine = isPointOnLine(x1, y1);
        boolean point2OnLine = isPointOnLine(x2, y2);

        if (point1OnLine && point2OnLine) {
            result.append("The line segment lies entirely on the straight line.");
            return result.toString();
        }

        double[] intersection = getIntersectionPoint(x1, y1, x2, y2);

        if (intersection == null) {
            result.append("The straight line and the line segment do not intersect.");
        } else {
            result.append("The straight line and the line segment have ONE common point: ");
            result.append(String.format("(%.2f, %.2f)", intersection[0], intersection[1]));
            result.append("\n");

            // Проверка перпендикулярности
            if (arePerpendicular(x1, y1, x2, y2)) {
                result.append("The straight line and the line segment are mutually perpendicular.");
            } else {
                result.append("The straight line and the line segment are NOT mutually perpendicular.");
            }
            result.append("\n");

            // Проверка, является ли точка пересечения концом отрезка
            if (isEndPoint(intersection[0], intersection[1], x1, y1, x2, y2)) {
                result.append("The intersection point is an END of the segment.");
            } else {
                result.append("The intersection point is NOT an end of the segment.");
            }
        }

        return result.toString();
    }
}