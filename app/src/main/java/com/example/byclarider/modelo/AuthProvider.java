package com.example.byclarider.modelo;

import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {

    FirebaseAuth mAuth;

    public AuthProvider() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void logout(){
        mAuth.signOut();
    }
}