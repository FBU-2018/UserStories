package com.example.lkimberly.userstories.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lkimberly.userstories.BitmapScaler;
import com.example.lkimberly.userstories.R;
import com.example.lkimberly.userstories.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.example.lkimberly.userstories.fragments.ProfileFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.example.lkimberly.userstories.fragments.ProfileFragment.GET_FROM_GALLERY;
import static com.parse.ParseUser.getCurrentUser;

public class EditProfileFragment extends Fragment {

    Button saveProfileBtn;
    ImageButton ib_profile_photo;
    String photoFileName = "photo.jpg";
    File photoFile;
    ImageView edit_profile_iv;
    ImageView profile_iv;

    private ViewPager viewPager;

    EditText et_name;
    EditText et_institution;
    EditText et_phoneNumber;

    TextView tv_name;
    TextView tv_institution;
    TextView tv_phoneNumber;

    ImageButton ib_facebook;
    ImageButton ib_linkedIn;
    ImageButton ib_twitter;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_edit_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        final User user = (User) ParseUser.getCurrentUser();

        // Grab a reference to our view pager.
        viewPager = getActivity().findViewById(R.id.pager);
        saveProfileBtn = view.findViewById(R.id.save_profile_btn);
        ib_profile_photo = view.findViewById(R.id.ib_profile_photo);
        edit_profile_iv = view.findViewById(R.id.edit_profile_iv);

        et_name = view.findViewById(R.id.profile_name);
        et_institution = view.findViewById(R.id.profile_institution);
        et_phoneNumber = view.findViewById(R.id.profile_phone_number);

        if (user.getName() != null) {
            et_name.setText(user.getName());
        }

        if (user.getInstitution() != null) {
            et_institution.setText(user.getInstitution());
        }

        if (user.getPhoneNumber() != null) {
            et_phoneNumber.setText(user.getPhoneNumber());
        }

        ib_facebook = view.findViewById(R.id.ib_facebook);
        ib_linkedIn = view.findViewById(R.id.ib_linkedIn);
        ib_twitter = view.findViewById(R.id.ib_twitter);

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view2 = inflater.inflate(R.layout.fragment_profile, null);

        tv_name = view2.findViewById(R.id.tv_profile_name);
        tv_institution = view2.findViewById(R.id.tv_profile_institution);
        tv_phoneNumber = view2.findViewById(R.id.tv_profile_phone_number);
        profile_iv = view2.findViewById(R.id.profile_iv);

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString();
                if (!name.equals("")) {
                    tv_name.setText(name);
                    user.setName(name);
                } else {
                    Toast.makeText(getContext(), "Please provide a name", Toast.LENGTH_LONG).show();
                }

                String institution = et_institution.getText().toString();
                if (!institution.equals("")) {
                    tv_institution.setText(institution);
                    user.setInstitution(institution);
                } else {
                    Toast.makeText(getContext(), "Please provide an institution", Toast.LENGTH_LONG).show();
                }

                String phoneNumber = et_phoneNumber.getText().toString();
                if (!phoneNumber.equals("")) {
                    tv_phoneNumber.setText(phoneNumber);
                    user.setPhoneNumber(phoneNumber);
                } else {
                    Toast.makeText(getContext(), "Please provide a phone number", Toast.LENGTH_LONG).show();
                }

                user.saveInBackground();
//                HomeActivity.adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(0);
            }
        });

        ib_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }
        });

        try {
            Glide.with(EditProfileFragment.this)
                    .load(ParseUser.getCurrentUser().getParseFile("profilePicture").getUrl())
                    .into(edit_profile_iv);

        } catch (NullPointerException e) {
            Log.d("EditProfileFragment", "profile picture does not exist!");
            Glide.with(EditProfileFragment.this)
                    .load(R.drawable.default_avatar)
                    .into(edit_profile_iv);
        }

        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_facebook);

        ib_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacebookDialogFragment facebookDialog = new FacebookDialogFragment();
                facebookDialog.show(getFragmentManager(), "FacebookDialog");
            }
        });

        ib_linkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinkedInDialogFragment linkedInDialog = new LinkedInDialogFragment();
                linkedInDialog.show(getFragmentManager(), "LinkedInDialog");
            }
        });

        ib_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterDialogFragment twitterDialog = new TwitterDialogFragment();
                twitterDialog.show(getFragmentManager(), "TwitterDialog");
            }
        });
    }

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("")
                    .setMessage("Take photo from:")
                    .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

                        }
                    })
                    .setNeutralButton("Go back to my profile", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "EditProfileFragment");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("EditProfileFragment", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                User user = (User) getCurrentUser();

                String imagePath = photoFile.getAbsolutePath();
                Bitmap rawTakenImage = BitmapFactory.decodeFile(imagePath);
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 400);
                Bitmap rotatedBitmap = rotate(resizedBitmap, imagePath);
                edit_profile_iv.setImageBitmap(rotatedBitmap);

                ParseFile parseFile = new ParseFile(new File(imagePath));

                user.put("profilePicture", parseFile);
                profile_iv.setImageBitmap(rotatedBitmap);

                user.saveInBackground();
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == GET_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                User user = (User) getCurrentUser();

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imagePath = cursor.getString(columnIndex);

                Bitmap rawTakenImage = BitmapFactory.decodeFile(imagePath);
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 400);

                Bitmap rotatedBitmap = rotate(resizedBitmap, imagePath);
                edit_profile_iv.setImageBitmap(rotatedBitmap);

                ParseFile parseFile = new ParseFile(new File(imagePath));

                user.put("profilePicture", parseFile);
                profile_iv.setImageBitmap(rotatedBitmap);

                user.saveInBackground();
                cursor.close();
            }
        }
    }

    public Bitmap rotate(Bitmap bitmap, String imagePath) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}