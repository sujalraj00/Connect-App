 package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.connect.Adapters.FragmentsAdapter;
import com.example.connect.databinding.ActivityFirstScreenBinding;
import com.example.connect.databinding.ActivityLoginBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

 public class FirstScreen extends AppCompatActivity {

     NavigationView navigationView;
     TabLayout tabLayout;
     Toolbar toolbar;
     DrawerLayout drawerLayout;
FirebaseAuth auth;
ViewPager viewPager;
ActivityFirstScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        viewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar);
       // tabLayout = findViewById(R.id.tablayout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");
        auth = FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tablayout.setupWithViewPager(binding.viewPager);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // this method is called once with the initial value and again
                // whatever data at this location is updated
                String value = dataSnapshot.getValue(String.class);
                Toast.makeText(FirstScreen.this, ""+value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // setupDrawerContent(navigationView);
        // For Navigation Drawer
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
               this,drawerLayout,R.string.OpenDrawer,R.string.CloseDrawer
       );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.draw_profile);
//        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.logout){
                    auth.signOut();
                    Intent intent = new Intent(FirstScreen.this, Login.class);
                    startActivity(intent);
                } else if (id == R.id.draw_setting) {
                    Toast.makeText(FirstScreen.this, "setting opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FirstScreen.this, SettingsActivity.class);
                    startActivity(intent);
                 }  else if(id == R.id.profileFragment) {
                    Toast.makeText(FirstScreen.this, "profile opened", Toast.LENGTH_SHORT).show();
                    drawFrag(new ProfileFragment());
                } else if( id == R.id.groupChat){
                    Toast.makeText(FirstScreen.this, "group chat opened", Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(FirstScreen.this, GroupChatActivity.class);
                     startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        // For Bottom Navigation
//        bnView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int id = item.getItemId();
//               if(id == R.id.nav_Chats){
//                   loadFrag(new ChatsFragment(), true);
//               } else if (id == R.id.nav_Status) {
//                   loadFrag(new StatusFragment(),false);
//               }  else {
//                   loadFrag(new CallsFragment(),false);
//               }
//
//                return  true;
//            }
//        });
//        bnView.setSelectedItemId(R.id.nav_Chats);
//
//
//
//    }






//    @Override
//    public  void  onBackPressed(){
//        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else{
//            super.onBackPressed();
//        }
//    }

//    public void loadFrag(Fragment fragment, boolean flag){
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        if(flag) {
//            ft.add(R.id.container, fragment);
//        } else {
//            ft.replace(R.id.container, fragment);
//        }
//        ft.commit();
//    }
//
}
     @Override
     public void onBackPressed() {
         DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

         if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
             // Close the navigation drawer if it's open
             drawerLayout.closeDrawer(GravityCompat.START);
         } else {
             // Check if there are fragments in the back stack
             FragmentManager fragmentManager = getSupportFragmentManager();
             if (fragmentManager.getBackStackEntryCount() > 0) {
                 // Pop the fragment from the back stack
                 fragmentManager.popBackStack();
             } else {
                 // If no fragments in the back stack, proceed with default behavior
                 super.onBackPressed();
             }
         }
     }

//     @Override
//     public void onBackPressed() {
//         if(drawerLayout.isDrawerOpen(GravityCompat.START)){
//             drawerLayout.closeDrawer(GravityCompat.START);
//         } else{
//             super.onBackPressed();
//         }
//     }

     private void drawFrag(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container,fragment);
         ft.addToBackStack(null);
        ft.commit();
     }


 }
