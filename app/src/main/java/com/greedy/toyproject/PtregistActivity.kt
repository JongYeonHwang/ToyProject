package com.greedy.toyproject

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat
import com.greedy.toyproject.databinding.ActivityPtregistBinding



class PtregistActivity: AppCompatActivity()  {

    val binding by lazy { ActivityPtregistBinding.inflate(layoutInflater) }
    val helper = SqliteHelper(this, "post", 1)
    lateinit var btnSelect: ImageView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initImageViewProfile()

        val adapter = RecyclerAdapter()
        adapter.helper = helper

        adapter.listData.addAll(helper.selectPost())

        /* 메모 저장 버튼 이벤트 */
        binding.btnregi.setOnClickListener {
            /* 메모 내용이 입력 된 경우만 동작 */
            if (binding.editText.text.toString().isNotEmpty()) {
                val post = Post(
                    null, binding.editText.text.toString(),
                    System.currentTimeMillis()
                )
                helper.insertPost(post)

                /* DB가 변동 되었을 때 화면도 변동될 수 있도록 adapter의 data를 수정하고
                * 데이터가 변화 했음을 알린다. */
                adapter.listData.clear()
                adapter.listData.addAll(helper.selectPost())
                adapter.notifyDataSetChanged()

                /* 입력란 비우기 */
                binding.editText.setText("")

            }

            binding.btnregi.setOnClickListener {
                val intent = Intent(this, PostActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                this.startActivity(intent)

            }

        }
    }



    /* 권한이 승인 되었을 때 동작할 콜백 메소드[카메라]*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    navigateGallery()
                else
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //
            }
        }
    }

    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        // 가져올 컨텐츠들 중에서 Image 만을 가져온다.
        intent.type = "image/*"
        // 갤러리에서 이미지를 선택한 후, 프로필 이미지뷰를 수정하기 위해 갤러리에서 수행한 값을 받아오는 startActivityForeResult를 사용한다.
        startActivityForResult(intent, 2000)
    }

    //파일 권한
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initImageViewProfile() {
        btnSelect = findViewById(R.id.imgMain)

        btnSelect.setOnClickListener {
            when {
                // 갤러리 접근 권한이 있는 경우
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                -> {
                    navigateGallery()
                }

                // 갤러리 접근 권한이 없는 경우 & 교육용 팝업을 보여줘야 하는 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    showPermissionContextPopup()
                }

                // 권한 요청 하기(requestPermissions) -> 갤러리 접근(onRequestPermissionResult)
                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 예외처리
        if (resultCode != Activity.RESULT_OK)
            return

        when (requestCode) {
            // 2000: 이미지 컨텐츠를 가져오는 액티비티를 수행한 후 실행되는 Activity 일 때만 수행하기 위해서
            2000 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    btnSelect.setImageURI(selectedImageUri)
                } else{
                    Toast.makeText(this,"카메라를 실행합니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

}
