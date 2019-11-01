package com.rzn.gargi.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.UserInfo;
import com.rzn.gargi.helper.images;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity {
    String image_name;

    private static final int camera_request =200;
    private static final int gallery_request =400;
    private static final int image_pick_request =600;
    private static final int camera_pick_request =800;
    String cameraPermission[];
    String storagePermission[];
    Uri image;
    ProgressDialog progressDialog;
    private StorageReference imageStorage;
    EditText about,school,job,insta_id,twitter_id,facebook_id,snachat_id;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Map<String , Object> info;
    Map<String , Object> social;
    String gender;
    ProgressBar prg1,prg2,prg3,prg4,prg5,prg6;
    CircleImageView image1; ImageView image2,image3,image4,image5,image6;
    ImageButton upload1,upload2,upload3,upload4,upload5,upload6,delete_1,delete_2,delete_3,delete_4,delete_5,delete_6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        gender=getIntent().getStringExtra("gender");
        imageStorage= FirebaseStorage.getInstance().getReference();
        cameraPermission= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission= new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        prg1=(ProgressBar)findViewById(R.id.prg1);
        prg2=(ProgressBar)findViewById(R.id.prg2);
        prg3=(ProgressBar)findViewById(R.id.prg3);
        prg4=(ProgressBar)findViewById(R.id.prg4);
        prg5=(ProgressBar)findViewById(R.id.prg5);
        prg6=(ProgressBar)findViewById(R.id.prg6);
        about =(EditText) findViewById(R.id.about);
        school =(EditText) findViewById(R.id.school);
        job =(EditText) findViewById(R.id.job);
        insta_id =(EditText) findViewById(R.id.instagram);
        twitter_id =(EditText) findViewById(R.id.twitter);
        facebook_id =(EditText) findViewById(R.id.facebook);
        snachat_id =(EditText) findViewById(R.id.snapchat);
        auth= FirebaseAuth.getInstance();
        image1=(CircleImageView)findViewById(R.id.image_1);
        image2=(ImageView) findViewById(R.id.image_2);
        image3=(ImageView)findViewById(R.id.image_3);
        image4=(ImageView)findViewById(R.id.image_4);
        image5=(ImageView)findViewById(R.id.image_5);
        image6=(ImageView)findViewById(R.id.image_6);
        upload1 = (ImageButton)findViewById(R.id.upload_1);
        upload2 = (ImageButton)findViewById(R.id.upload_2);
        upload3 = (ImageButton)findViewById(R.id.upload_3);
        upload4 = (ImageButton)findViewById(R.id.upload_4);
        upload5 = (ImageButton)findViewById(R.id.upload_5);
        upload6 = (ImageButton)findViewById(R.id.upload_6);
        delete_1=(ImageButton)findViewById(R.id.delete_1);
        delete_2=(ImageButton)findViewById(R.id.delete_2);
        delete_3=(ImageButton)findViewById(R.id.delete_3);
        delete_4=(ImageButton)findViewById(R.id.delete_4);
        delete_5=(ImageButton)findViewById(R.id.delete_5);
        delete_6=(ImageButton)findViewById(R.id.delete_6);
        upload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="bir";
                uploadProfileImage();

            }
        });
        upload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="iki";
               uploadProfileImage();

            }
        });
        upload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="uc";
                uploadProfileImage();
            }
        });
        upload4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="dort";
                uploadProfileImage();
            }
        });
        upload5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="bes";
                uploadProfileImage();
            }
        });
        upload6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="alti";
                uploadProfileImage();
            }
        });
        delete_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="bir";
                deleteImage(image_name);

            }
        });
        delete_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="iki";
                deleteImage(image_name);

            }
        });
        delete_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="uc";
                deleteImage(image_name);

            }
        });
        delete_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="dort";
                deleteImage(image_name);

            }
        });
        delete_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="bes";
                deleteImage(image_name);

            }
        });
        delete_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name="alti";
                deleteImage(image_name);

            }
        });
    }
    Task<Void> taskDeleteImage;
    private void deleteImage(String path){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String,Object> delete = new HashMap<>();
        delete.put(path, FieldValue.delete());
       taskDeleteImage = db.collection("images").document(auth.getUid())
               .update(delete).addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()) {
                           Log.d("delete", "onComplete: " + "succes");
                           taskDeleteImage.isComplete();
                       }
                   }
               }).addOnFailureListener(EditProfileActivity.this, new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Crashlytics.logException(e);
                   }
               });
       if (path.equals("bir")){
           final Map<String,Object> profile = new HashMap<>();
           profile.put("profileImage","");
           profile.put("thumb_image","");
           db.collection(gender)
                   .document(auth.getUid())
                   .update(profile)
                   .addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){

                               db.collection("allUser").document(auth.getUid())
                                       .update(profile)
                                       .addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                upload1.setVisibility(View.GONE);
                                                delete_1.setVisibility(View.VISIBLE);
                                                image1.setImageResource(R.drawable.upload_place_holder);
                                                prg1.setVisibility(View.GONE);
                                               }
                                           }
                                       });
                           }
                       }
                   }).addOnFailureListener(EditProfileActivity.this, new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Crashlytics.logException(e);
               }
           });
       }

   }
    private void uploadProfileImage() {
        if (!checkGalleryPermissions()){
            requestStoragePermission();
        }
        else{ pickGallery();}
    }
    private void pickGallery()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(intent.CATEGORY_OPENABLE);
        //  Intent intent = new Intent(Intent.ACTION_PICK);
        // intent.setType("*/*");
        startActivityForResult(intent,image_pick_request);
    }
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,gallery_request);

    }
    private boolean checkGalleryPermissions()
    {
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    StorageTask<UploadTask.TaskSnapshot> storageTask;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if(requestCode==image_pick_request){
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .setMinCropWindowSize(500 ,500)
                        .start(this);
            }
            if (requestCode==camera_pick_request){
                CropImage.activity(image)

                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .setMinCropWindowSize(500, 500)
                        .start(this);

            }
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result =CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                progressDialog = new ProgressDialog(EditProfileActivity.this);
                progressDialog.setTitle(getResources().getString(R.string.resim_yukleniyor));
                progressDialog.setMessage(getResources().getString(R.string.lutfen_bekleniyiz));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                Uri resultUri = result.getUri();
                File thumb_filePath = new File(resultUri.getPath());
                try {
                    Bitmap thumb_bitmap= new Compressor(this)
                            .setMaxHeight(256)
                            .setMaxWidth(256)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();
                    final StorageReference filePath = imageStorage.child(auth.getUid()).child("images").child(image_name+".jpg");
                    final StorageReference filePath_thumb = imageStorage.child(auth.getUid()).child("images").child("thumb").child(image_name+".jpg");
                     storageTask = filePath.putFile(resultUri).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageTask.isComplete();
                                imageStorage.child(auth.getUid())
                                        .child("images").child(image_name + ".jpg")
                                        .getDownloadUrl()
                                        .addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String downloadUri = uri.toString();
                                                //  Map<String ,Object> imageMap = new HashMap<>();
                                                final Map<String, Object> imageName = new HashMap<>();
                                                imageName.put(image_name, downloadUri);
                                                // imageMap.put(image_name,imageName);
                                                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("images").document(auth.getUid())
                                                        .set(imageName, SetOptions.merge()).addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    if (image_name == "bir") {
                                                                        final Map<String, Object> map = new HashMap<>();
                                                                        map.put("profileImage", downloadUri);

                                                                        db.collection(gender).document(auth.getUid())
                                                                                .update(map).addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    db.collection("allUser").document(auth.getUid())
                                                                                            .update(map).addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                UploadTask uploadTask = filePath_thumb.putBytes(thumb_byte);
                                                                                                uploadTask.addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                        if (taskSnapshot.getMetadata() != null) {
                                                                                                            if (taskSnapshot.getMetadata().getReference() != null) {
                                                                                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                                                                                result.addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<Uri>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(Uri uri) {
                                                                                                                        String imageUrl = uri.toString();
                                                                                                                        final Map<String, Object> thumb = new
                                                                                                                                HashMap<>();
                                                                                                                        thumb.put("thumb_image", imageUrl);
                                                                                                                        db.collection("allUser").document(auth.getUid())
                                                                                                                                .update(thumb).addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    db.collection(gender).document(auth.getUid())
                                                                                                                                            .update(thumb);

                                                                                                                                }
                                                                                                                            }
                                                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                                                            @Override
                                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                                Crashlytics.logException(e);
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Crashlytics.logException(e);
                                                                                        }
                                                                                    });


                                                                                }
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Crashlytics.logException(e);
                                                                            }
                                                                        });


                                                                    }
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Crashlytics.logException(e);
                                                            }
                                                        });


                                            }
                                        });

                            } else {
                                Log.d("hata", "onFailure: " + task.getResult());

                            }

                        }
                    }).addOnProgressListener(EditProfileActivity.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progresSize = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setProgress((int) progresSize);
                            String progressText = taskSnapshot.getBytesTransferred() / 1024 + "KB/" + taskSnapshot.getTotalByteCount() / 1024 + "KB";

                            progressDialog.setMessage(progressText + "      " + (int) progresSize + "%");
                        }
                    });

                }catch (Exception e){
                    Crashlytics.logException(e);
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
    public void back(View view){
        //  setUserInfo(about,school,job);
        // setUserSocial(twitter_id,insta_id,facebook_id,snachat_id);
        Intent i = new Intent(EditProfileActivity.this, EditActivity.class);
        setUserInfo(about,school,job,twitter_id,insta_id,facebook_id,snachat_id);
        // setUserSocial(twitter_id,insta_id,facebook_id,snachat_id);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.putExtra("gender",gender);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadImages();
        loadUserInfo(about,school,job,twitter_id,insta_id,facebook_id,snachat_id);
    }


    ListenerRegistration listenerRegistration;
    private void loadImages(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.document("images"+"/"+auth.getUid());
         listenerRegistration = user.addSnapshotListener(EditProfileActivity.this, MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Log.d("Edit", "onEvent: " + documentSnapshot);
                images images = documentSnapshot.toObject(com.rzn.gargi.helper.images.class);
                if (images!=null){
                    if (images.getBir() != null) {

                        Picasso.get().load(images.getBir()).noFade().placeholder(R.drawable.upload_place_holder).config(Bitmap.Config.RGB_565).resize(256,256).into(image1, new Callback() {
                            @Override
                            public void onSuccess() {
                                prg1.setVisibility(View.GONE);
                                upload1.setVisibility(View.GONE);
                                delete_1.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else{
                        prg1.setVisibility(View.GONE);
                        upload1.setVisibility(View.VISIBLE);
                        delete_1.setVisibility(View.GONE);
                        image1.setImageResource(R.drawable.upload_place_holder);
                    }
                    if (images.getIki() != null) {
                        Picasso.get().load(images.getIki()).noFade().placeholder(R.drawable.upload_place_holder).config(Bitmap.Config.RGB_565).resize(256,256).into(image2, new Callback() {
                            @Override
                            public void onSuccess() {
                                prg2.setVisibility(View.GONE);
                                upload2.setVisibility(View.GONE);
                                delete_2.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else{
                        prg2.setVisibility(View.GONE);
                        upload2.setVisibility(View.VISIBLE);
                        delete_2.setVisibility(View.GONE);
                        image2.setImageResource(R.drawable.upload_place_holder);
                    }
                    if (images.getUc() != null) {
                        Picasso.get().load(images.getUc()).noFade().placeholder(R.drawable.upload_place_holder).config(Bitmap.Config.RGB_565).resize(256,256).into(image3, new Callback() {
                            @Override
                            public void onSuccess() {
                                prg3.setVisibility(View.GONE);
                                upload3.setVisibility(View.GONE);
                                delete_3.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else{
                        prg3.setVisibility(View.GONE);
                        upload3.setVisibility(View.VISIBLE);
                        delete_3.setVisibility(View.GONE);
                        image3.setImageResource(R.drawable.upload_place_holder);
                    }
                    if (images.getDort()!=null){
                        Picasso.get().load(images.getDort()).noFade().placeholder(R.drawable.upload_place_holder).config(Bitmap.Config.RGB_565).resize(256,256)
                                .into(image4, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        prg4.setVisibility(View.GONE);
                                        upload4.setVisibility(View.GONE);
                                        delete_4.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }else
                    {
                        prg4.setVisibility(View.GONE);
                        upload4.setVisibility(View.VISIBLE);
                        delete_4.setVisibility(View.GONE);
                        image4.setImageResource(R.drawable.upload_place_holder);
                    }
                    if (images.getBes()!=null){
                        Picasso.get().load(images.getBes()).noFade().placeholder(R.drawable.upload_place_holder).config(Bitmap.Config.RGB_565).resize(256,256)
                                .into(image5, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        prg5.setVisibility(View.GONE);
                                        upload5.setVisibility(View.GONE);
                                        delete_5.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }else
                    {
                        prg5.setVisibility(View.GONE);
                        delete_5.setVisibility(View.GONE);
                        upload5.setVisibility(View.VISIBLE);
                        image5.setImageResource(R.drawable.upload_place_holder);
                    }
                    if (images.getAlti()!=null){
                        Picasso.get().load(images.getAlti()).noFade().placeholder(R.drawable.upload_place_holder).config(Bitmap.Config.RGB_565).centerInside().resize(256,256)
                                .into(image6, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        prg6.setVisibility(View.GONE);
                                        upload6.setVisibility(View.GONE);
                                        delete_6.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }else{
                        prg6.setVisibility(View.GONE);
                        upload6.setVisibility(View.VISIBLE);
                        delete_6.setVisibility(View.GONE);
                        image6.setImageResource(R.drawable.upload_place_holder);
                    }
                }else {
                    prg1.setVisibility(View.GONE);
                    prg2.setVisibility(View.GONE);
                    prg3.setVisibility(View.GONE);
                    prg4.setVisibility(View.GONE);
                    prg5.setVisibility(View.GONE);
                    prg6.setVisibility(View.GONE);
                }

            }
        });





    }
    Task<Void> taskUserInfo;
    private void setUserInfo(EditText _about, EditText _school, EditText _job, EditText _twitter_id, EditText _insta_id, EditText _facebook_id, EditText _snachat_id) {
        final Map<String,Object> map = new HashMap<>();

        String job =_job.getText().toString();
        String about =_about.getText().toString();
        String school =_school.getText().toString();
        String twitter =_twitter_id.getText().toString();
        String insta =_insta_id.getText().toString();
        String face =_facebook_id.getText().toString();
        String snap =_snachat_id.getText().toString();
        if (!job.isEmpty()){
            map.put("job",job);
        }
        if (!about.isEmpty())
        map.put("about",about);
        if (!school.isEmpty())
            map.put("school",school);
        if (!twitter.isEmpty())
            map.put("twitter",twitter);
        if (!insta.isEmpty())
            map.put("insta",insta);
        if (!face.isEmpty())
            map.put("face",face);
        if (!snap.isEmpty())
            map.put("snap",snap);
        final FirebaseFirestore db  =FirebaseFirestore.getInstance();
        taskUserInfo = db.collection("allUser").document(auth.getUid())
                .set(map, SetOptions.merge())
                .addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            db.collection(gender).document(auth.getUid())
                                    .set(map, SetOptions.merge())
                                    .addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("Bio", "onComplete: " + "succes");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Crashlytics.logException(e);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Crashlytics.logException(e);
                    }
                });

    }
    private void loadUserInfo(final EditText _about, final EditText _school, final EditText _job, final EditText _twitter_id, final EditText _insta_id, final EditText _facebook_id, final EditText _snachat_id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.document(gender+"/"+auth.getUid());
        Task<DocumentSnapshot> task = user.get().addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task != null) {

                    UserInfo info = task.getResult().toObject(UserInfo.class);
                    if (info.getAbout() != null) {
                        _about.setText(info.getAbout());
                    }
                    if (info.getSnap() != null) {
                        _snachat_id.setText(info.getSnap());
                    }
                    if (info.getFace() != null) {
                        _facebook_id.setText(info.getFace());
                    }
                    if (info.getJob() != null) {
                        _job.setText(info.getJob());
                    }
                    if (info.getSchool() != null) {
                        _school.setText(info.getSchool());
                    }
                    if (info.getTwitter() != null) {
                        _twitter_id.setText(info.getTwitter());
                    }
                    if (info.getInsta() != null) {
                        _insta_id.setText(info.getInsta());
                    }
                    task.isComplete();
                }
            }
        }).addOnFailureListener(EditProfileActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
        taskUserInfo.isComplete();
    }
}
