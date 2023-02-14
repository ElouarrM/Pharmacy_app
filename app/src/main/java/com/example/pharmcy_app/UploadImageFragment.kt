package com.example.pharmcy_app

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.example.pharmcy_app.databinding.FragmentUploadImageBinding
import java.io.ByteArrayOutputStream


class UploadImageFragment : Fragment() {
    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var imageUri: Uri? = null

    private var _binding: FragmentUploadImageBinding? = null
    private val binding get() = _binding!!

    private fun decode(imageString: String): Bitmap {
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) as Bitmap
        return bitmap
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUploadImageBinding.inflate(inflater, container, false)

        setFragmentResultListener("initImg") { key, bundle ->
            var imgUrl = bundle.getString("imgUrl")
            if("https" in imgUrl.toString()){
                Glide.with(binding.root).load(imgUrl.toString()).into(binding.image)
            }else{
                if(imgUrl.toString().isNotEmpty() && imgUrl.toString() != "null"){
                    binding.image?.setImageBitmap(decode(imgUrl.toString()))
                }
            }
        }
        return binding.root
    }

    private fun requestCameraPermission(): Boolean {
        var permissionGranted = false

        // If system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionNotGranted = ContextCompat.checkSelfPermission(
                activity as Context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionNotGranted){
                val permission = arrayOf(Manifest.permission.CAMERA)

                // Display permission dialog
                requestPermissions(permission, CAMERA_PERMISSION_CODE)
            }
            else{
                // Permission already granted
                permissionGranted = true
            }
        }
        else{
            // Android version earlier than M -> no need to request permission
            permissionGranted = true
        }

        return permissionGranted
    }

    // Handle Allow or Deny response from the permission dialog
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode === CAMERA_PERMISSION_CODE) {
            if (grantResults.size === 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission was granted
                openCameraInterface()
            }
            else{
                // Permission was denied
                showAlert("Camera permission was denied. Unable to take a picture.");
            }
        }
    }

    private fun openCameraInterface() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "take_picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "take_picture_description")
        val uri = MediaStore.Audio.Media.getContentUri("EXTERNAL_CONTENT_URI")
        Log.i("URL", values.toString())
        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        startActivityForResult(intent, IMAGE_CAPTURE_CODE)
    }


    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage(message)
        builder.setPositiveButton("k_button_title", null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.image.setOnClickListener {
            selectImage()
//            val permissionGranted = requestCameraPermission()
//            if (permissionGranted) {
//                openCameraInterface()
//            }
        }
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(context as Context)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                val permissionGranted = requestCameraPermission()
                if (permissionGranted) {
                    openCameraInterface()
                }
            } else if (options[item] == "Choose from Gallery") {
                imageChooser()

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun imageChooser() {
        val  SELECT_PICTURE = 200
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }


    fun encode(imageUri: String): String {

        val input = requireActivity().contentResolver.openInputStream(Uri.parse(imageUri))
        val image = BitmapFactory.decodeStream(input , null, null)

        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return imageString
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if(data?.data != null){
                imageUri = data?.data as Uri
                binding.image?.setImageURI(imageUri)
                setFragmentResult("sendMsg", bundleOf("imgUrl" to encode(imageUri.toString())))
            }else{
                binding.image?.setImageURI(imageUri)
            }
            setFragmentResult("sendMsg", bundleOf("imgUrl" to encode(imageUri.toString()).toString()))
        }
        else {
            showAlert("Failed to take camera picture")
        }
    }
}