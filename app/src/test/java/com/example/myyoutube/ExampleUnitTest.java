package com.example.myyoutube;

import org.junit.Test;

import static org.junit.Assert.*;

import com.youtube_v.domain.myyoutube.SkipAd;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void setSkipaAdd(){

        SkipAd skipaAdd = new SkipAd(null);

       // int result = skipaAdd.skipVideo();

       // assertEquals(5, result);
    }
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}