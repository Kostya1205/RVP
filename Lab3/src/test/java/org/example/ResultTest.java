package org.example;
import org.junit.Test;
import static org.junit.Assert.*;

public class ResultTest {

    @Test
    public void testGetResult() {
        // Проверка для строки "123,45,abc,67.89,xyz"
        Result result = new Result("123,45,abc,67.89,xyz");

        // Проверка ожидаемых результатов
        assertEquals("Целые числа:[123, 45]Числа с плавающей точкой:[67.89]Оставшиеся слова[abc, xyz]", result.getResult());
    }
}
