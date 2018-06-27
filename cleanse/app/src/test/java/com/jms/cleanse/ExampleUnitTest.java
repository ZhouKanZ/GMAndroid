package com.jms.cleanse;

import android.graphics.Matrix;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testRxJava() throws Exception {


        Observable
                .just(0, 2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("aaaa" + integer);
                    }
                });
    }

    @Test
    public void testJson() throws Exception {

        String str = "{feedback: {mode: 1, name: t1}, msg_type: disinfection_task_exec}";
        str = str.replaceAll("(\\{|,)([^:]+)", "$1\"$2\"").replaceAll("([^:,\\}]+)(\\}|,)", "\"$1\"$2");
        System.out.println(str);

    }

    @Test
    public void testMatrix() throws Exception{
//        Matrix matrix = new Matrix();
//        matrix.setTranslate(10,10);
//        matrix.setRotate(45);
//        matrix.setScale(1.0f,1.0f);
//
//        matrix.postConcat(matrix);
    }
}