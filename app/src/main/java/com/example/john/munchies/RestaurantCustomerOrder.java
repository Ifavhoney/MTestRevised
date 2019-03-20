package com.example.john.munchies;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RestaurantCustomerOrder extends AppCompatActivity implements View.OnClickListener {

    ListView order;
    TextView price;
    Button checkout_Btn;
    Button remove_btn;
    List<String> sample;
    String userEmail;
    SimpleDateFormat day;
    SimpleDateFormat hour;

    String currentDay;
    String orderitem;
    String currentHour;

    String orderPrice;
    String restaurantName;

    private ArrayAdapter<String> orderItemsAdapter;

    FirebaseDatabase myFB;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_customer_order);
        // Shared Preferences

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> setOrder = pref.getStringSet("order",null);
       orderPrice = pref.getString("orderPrice", null);
         userEmail = pref.getString("userEmail", null);
        restaurantName = pref.getString("RestaurantName", null);

        sample=new ArrayList<String>(setOrder);

        //Adapter - connects to the arraylist
        orderItemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, sample);

        //Firebase
        myFB = FirebaseDatabase.getInstance();

        myRef = myFB.getReference("MunchiesDB").child("RestaurantOrders").child(restaurantName).child("Purchases").child("AuthUser: " + userEmail);


        //Views
        order = (ListView)findViewById(R.id.order);
        price = (TextView) findViewById(R.id.orderPrice);
        checkout_Btn = (Button) findViewById(R.id.checkout_Btn);
        remove_btn = (Button) findViewById(R.id.remove_Btn);

        //Modify Views
        order.setAdapter(orderItemsAdapter);
        price.setText("TOTAL: " + orderPrice);

        //Listeners
        checkout_Btn.setOnClickListener(this);

        //Other

         day = new SimpleDateFormat("yyyy-MM-dd");
         hour = new SimpleDateFormat("HH:mm");



    }


@Override

public void onClick(View view){
        if(view==checkout_Btn){
            placeOrder();

        }
        else if ( view == remove_btn)
        {
            RemoveItem();
            recreate();
        }
}




public void placeOrder(){
    currentDay = day.format(new Date());
    currentHour = hour.format(new Date());
//Replace Num with KEY
    Random r = new Random();
    int n = r.nextInt(99999);



    String num = "Order: " + n;
//TEST FOR DUPLICATION IF THERE IS TIME (Although rare)

    myRef.child(currentDay).child(num).child("Order").setValue(sample);
    myRef.child(currentDay).child(num).child("HourCreated").setValue(currentHour);

    Toast.makeText(this, num, Toast.LENGTH_SHORT).show();

    myRef.child(currentDay).child(num).child("price").setValue(orderPrice);

    if (checkout_Btn.isEnabled() && !userEmail.isEmpty()){
        checkout_Btn.setEnabled(false);

    }


}
    public void RemoveItem() {


        order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                orderitem = String.valueOf(adapterView.getItemAtPosition(position));

                if (order.isItemChecked(position)) {
                    sample.remove(orderitem);

                    Toast.makeText(RestaurantCustomerOrder.this, "Removed " + orderitem, Toast.LENGTH_SHORT).show();


                }


            }

        });

//    public void deleteMenuItem(){
//        final RestaurantItemClass restaurant = new RestaurantItemClass();
//        restaurantMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                restaurant.setItemID(restaurantMenuArrayList.get(position));
//                editRestaurantMenuName.setText(restaurantMenuArrayList.get(position));
//            }
//        });
//
//        btnDeleteMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String name = restaurant.getItemID();
//                Toast.makeText(getApplicationContext(), "Deleted " +  name  + " item menu", Toast.LENGTH_SHORT).show();
//                if(name.equals("")){
//                    Toast.makeText( RestaurantMenuCRUD.this, "Please Select item before delete!", Toast.LENGTH_LONG).show();
//                } else {
//                    myRef.child(restaurantName).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            myRef.child(name).removeValue();
//                            editRestaurantMenuName.setText("");
//                            editRestaurantMenuPrice.setText("");
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                        }
//                    });
//                }
//            }
//        });
//    }

//Prevents user from moving back
//    @Override
//    public void onBackPressed() {
//
//    }


    }}