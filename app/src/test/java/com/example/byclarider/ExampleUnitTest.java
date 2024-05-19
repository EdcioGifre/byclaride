package com.example.byclarider;

import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Intent;

import com.example.byclarider.presentador.MainActivity;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    // Prueba unitaria para verificar que la autenticación con Google se inicia correctamente
    @Test
    public void testGoogleSignInIntentStarted() {
        MainActivity activity = new MainActivity();
        activity.onCreate(null);
        activity.signIn();
        Intent signInIntent = activity.mGoogleSignInClient.getSignInIntent();
        assertNotNull(signInIntent);
    }


}

