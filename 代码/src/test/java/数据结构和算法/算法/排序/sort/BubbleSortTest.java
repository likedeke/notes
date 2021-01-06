package 数据结构和算法.算法.排序.sort;

import java.util.Arrays;

class BubbleSortTest {

    static Integer[] leftArray;
    static Integer[] array = {
            4, 2, 1, 5, 3, 7, 6, 8
    };
    static Integer[] stepArray = {
            4, 2, 1
    };

    public static void main(String[] args) {
        for (Integer step : stepArray) {
            shell(step);
        }
        System.out.println(Arrays.toString(array));
    }

    private static void shell(Integer step) {
        for (int col = 0; col < step; col++) {
            for (int i = col + step; i < array.length; i += step) {
                int cur = i;
                while (cur > col && array[i] < array[i - step]) {
                    int temp = array[i];
                    array[i] = array[i -step];
                    array[i - step] = temp;
                    cur -= step;
                }
            }
        }
    }


}