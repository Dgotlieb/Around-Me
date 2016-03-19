package com.example.daniel.aroundme.tablet;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.activities_and_fragments.MainActivity;
import com.example.daniel.aroundme.database.DBFabHandler;
import com.example.daniel.aroundme.database.DBHandler;
import com.example.daniel.aroundme.database.Place;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Daniel on 12/20/2015.
 */
public class DialogFavouritesTablet extends android.support.v4.app.DialogFragment {


    private static final int FAVOURITES_FLAG = 100;
    private ListView fabList;
    private ArrayAdapter<Place> adapter;
    private ArrayList<Place> arrayList;
    private Place place;
    private Toolbar toolbar;
    private Comunnicator comm;
    private ImageButton deleteAll;
//    private ImageButton recommendedToolBar;
    private ImageButton shortcutToolBar;
    private SharedPreferences intro;
    private View v;
    private  SharedPreferences.Editor edit;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourites, container, false);


        fabList = (ListView) v.findViewById(R.id.fabList);
        deleteAll = (ImageButton)v.findViewById(R.id.toolBarAllDelete);
//        recommendedToolBar = (ImageButton)v.findViewById(R.id.toolBarRecommended);
        shortcutToolBar = (ImageButton)v.findViewById(R.id.toolBarShortcut);
        setHasOptionsMenu(true);


        final DBFabHandler dbFabHandler = new DBFabHandler(getActivity());
        arrayList = dbFabHandler.showAllLocationsArrayListAndSort();
        adapter = new ArrayAdapter<Place>(getActivity(),android.R.layout.simple_list_item_1, arrayList);


        fabList.setAdapter(adapter);


        shortcutToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());

                alertDialog.setTitle("Favourites shortcut");

                alertDialog.setMessage("Press ok, to create a shortcut to your favourites, \n on your home screen");

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addShortcut();
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();

            }
        });

//        recommendedToolBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //// TODO: 2/2/2016
////                comm.recommended();
//                Toast.makeText(getActivity(), "Recommended places, will be available soon", Toast.LENGTH_SHORT).show();            }
//        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbFabHandler.removeAllLocations();
                adapter.notifyDataSetChanged();
            }
        });

        fabList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    DBFabHandler dbFabHandler1 = new DBFabHandler(getActivity());
                    dbFabHandler1.showAllLocationsArrayListAndSort();
                    comm.locationPicker(place.getLatitude(), place.getLongitude(), place.getName(), place.getVicinity(), place.getPhotos());
                } catch (Exception e) {
                    Log.d("fav press", "null");
                }

            }
        });



        fabList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                place = arrayList.get(position);

                new BottomSheet.Builder(getActivity()).title("options").sheet(R.menu.bottom_menu).listener(new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.menu_cancel:
                                break;

                            //send place by email
                            case R.id.menu_send_by_email:
                                String emailMessage = arrayList.get(position).toString();
                                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                String[] recipients = new String[]{"", "",};
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New PLACE ");
                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Title: "+ arrayList.get(position).toString());
                                emailIntent.setType("message/rfc822");
                                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                                break;
                            //send place by whatsapp
                            case R.id.menu_send_by_whatsapp:
                                String whatsAppMessage = arrayList.get(position).toString();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "" + "My place: " + whatsAppMessage);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                break;
                            //send place by message
                            case R.id.menu_send_by_message:
                                AlertDialog.Builder sms = new AlertDialog.Builder(getActivity());
                                final EditText numToSend = new EditText(getActivity());
                                sms.setTitle("send Place by sms");
                                sms.setMessage("please enter a number to send to");
                                sms.setView(numToSend);
                                sms.setPositiveButton("send", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String message = arrayList.get(position).toString();
                                        SmsManager manager = SmsManager.getDefault();
                                        manager.sendTextMessage(numToSend.getText().toString(), null, message, null, null);
//                                        SnackbarManager.show(Snackbar.with(getActivity()).text("sucessfullySent"));
                                    }
                                });
                                sms.setNeutralButton("cancel", null);
                                sms.show();

                                break;

                            //delete place from list
                            case R.id.menu_delete:


                                DBFabHandler handler = new DBFabHandler(getActivity());
                                handler.removeLocation(position);
                                adapter.notifyDataSetChanged();

                        }
                    }
                }).show();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(" Operation ");
                builder.setMessage("what would you like to do?");
                builder.setNeutralButton("cancel", null);
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                        b.setTitle("warning");
                        b.setMessage("by pressing ok data will be deleted!");
                        b.setNeutralButton("cancel", null);
                        b.setPositiveButton("delete anyways", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHandler handler = new DBHandler(getActivity());
//                                int selected_id = arrayList.get(position).getId();
//                                handler.removeMovie(selected_id);
//                                moviesList.remove(position);
//                                moviesArrayAdapter.notifyDataSetChanged();
//                                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.deletedSucess)).actionLabel("undo").actionColor(Color.RED).actionListener(new ActionClickListener() {
//                                    @Override
//                                    public void onActionClicked(Snackbar snackbar) {
//
//                                    }
//                                }));
                            }
                        });
                    }

                });


                return false;
            }
        });





        return v;
    }


    private void addShortcut() {


        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getActivity(), MainActivity.class);


        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.putExtra("a", 100);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "My favourites");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getActivity(),
                        R.drawable.favourites_places));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getActivity().sendBroadcast(addIntent);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.help_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.help) {
            edit = intro.edit();

            edit.putBoolean("First_fav", true);
            edit.apply();


//            introduction();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }




}