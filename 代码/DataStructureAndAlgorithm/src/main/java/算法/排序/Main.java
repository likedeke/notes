package 算法.排序;

import 算法.排序.sort.Sort;
import 算法.排序.sort.cmp.HeapSort;
import 算法.排序.sort.cmp.MergeSort;
import 算法.排序.sort.cmp.QuickSort;
import 算法.排序.sort.nocmp.CountingSort;
import 算法.排序.tools.Asserts;

import java.util.Arrays;
import java.util.Random;

/**
 * @author like
 * @date 2020-12-28 11:23
 * @contactMe 980650920@qq.com
 * @description
 */
public class Main {
    public static final Integer ARRAY_SIZE = 500000;
    public static Integer[] ascSort;

    public static void main(String[] args) {
        Integer[] array = loadData();
        ascSort = copy(array);
        Arrays.sort(ascSort);

        sort(array,
                //                new BubbleSort<>(),
                //                new SelectionSort<>(),
                new HeapSort<>(),
                //                new InsertionSort<>(),
                new MergeSort<>(),
                new QuickSort<>(),
                //                new ShellSort<>(),
                new CountingSort()
        );
    }

    @SafeVarargs
    public static void sort(Integer[] array, Sort<Integer>... sorts) {
        for (Sort<Integer> sort : sorts) {
            equals(ascSort, sort.sort(copy(array)));
        }
        Arrays.sort(sorts);
        for (Sort<Integer> sort : sorts) {
            System.out.println(sort);
        }
    }

    private static void equals(Integer[] array, Integer[] sort) {
        Asserts.test(Arrays.equals(array, sort));
    }

    private static Integer[] copy(Integer[] array) {
        return Arrays.copyOf(array, array.length);
    }

    private static Integer[] loadData() {
        Integer[] array = new Integer[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = new Random().nextInt(ARRAY_SIZE);
        }
        return array;
    }

}
