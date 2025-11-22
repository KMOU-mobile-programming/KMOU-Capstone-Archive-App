package com.kmou.capstondesignarchive.Upload

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kmou.capstondesignarchive.R
import com.kmou.capstondesignarchive.model.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var uploaderUid: String? = null

    private lateinit var titleEditText: EditText
    private lateinit var teamEditText: EditText
    private lateinit var departmentEditText: EditText
    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var placeholderView: View
    private lateinit var progressOverlay: FrameLayout

    private lateinit var contentAdapter: UploadContentAdapter
    private val contentList = mutableListOf<Any>()

    private var tempImageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> handleSelectedUri(uri) }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success) { handleSelectedUri(tempImageUri) } }
    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) { launchCamera() } else { Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        uploaderUid = intent.getStringExtra("userId")

        if (uploaderUid.isNullOrEmpty()) {
            Toast.makeText(this, "로그인 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupListeners()
        setupRecyclerView()
    }

    private fun initViews() {
        titleEditText = findViewById(R.id.edit_project_title)
        teamEditText = findViewById(R.id.edit_project_team)
        departmentEditText = findViewById(R.id.edit_project_department)
        contentRecyclerView = findViewById(R.id.recycler_view_content)
        placeholderView = findViewById(R.id.placeholder_view)
        progressOverlay = findViewById(R.id.progress_overlay)
    }

    private fun setupRecyclerView() {
        contentAdapter = UploadContentAdapter(contentList)
        contentRecyclerView.layoutManager = LinearLayoutManager(this)
        contentRecyclerView.adapter = contentAdapter
        updatePlaceholderVisibility()
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.btn_close).setOnClickListener { finish() }
        findViewById<TextView>(R.id.btn_next).setOnClickListener { uploadProject() }
        findViewById<ImageButton>(R.id.btn_add_image).setOnClickListener { launchPhotoPicker() }
        findViewById<ImageButton>(R.id.btn_add_camera).setOnClickListener { requestCameraPermission.launch(Manifest.permission.CAMERA) }
        findViewById<ImageButton>(R.id.btn_add_text).setOnClickListener { addTextItem() }
        findViewById<ImageButton>(R.id.btn_add_html).setOnClickListener { showAddLinkDialog() }
    }

    private fun launchPhotoPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun launchCamera() {
        createImageUri()?.let { uri ->
            tempImageUri = uri
            takePicture.launch(uri)
        } ?: run {
            Toast.makeText(this, "사진 파일을 생성하는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageUri(): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(".")
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
    }

    private fun addTextItem() {
        contentList.add("")
        contentAdapter.notifyItemInserted(contentList.size - 1)
        updatePlaceholderVisibility()
    }

    private fun showAddLinkDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("링크 추가")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
        builder.setView(input)
        builder.setPositiveButton("추가") { _, _ ->
            var url = input.text.toString().trim()
            if (url.isNotEmpty()) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://$url"
                }
                if (Patterns.WEB_URL.matcher(url).matches()) {
                    contentList.add(url)
                    contentAdapter.notifyItemInserted(contentList.size - 1)
                    updatePlaceholderVisibility()
                } else {
                    Toast.makeText(this, "올바른 URL 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "URL을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun handleSelectedUri(uri: Uri?) {
        if (uri != null) {
            contentList.add(uri)
            contentAdapter.notifyItemInserted(contentList.size - 1)
            updatePlaceholderVisibility()
        } else {
            Toast.makeText(this, "파일을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePlaceholderVisibility() {
        placeholderView.isVisible = contentList.isEmpty()
        contentRecyclerView.isVisible = contentList.isNotEmpty()
    }

    private fun uploadProject() {
        val title = titleEditText.text.toString().trim()
        val team = teamEditText.text.toString().trim()
        val department = departmentEditText.text.toString().trim()

        if (title.isEmpty() || team.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "프로젝트 제목, 팀, 학부를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (contentList.isEmpty()) {
            Toast.makeText(this, "프로젝트 내용을 1개 이상 추가해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val processedContent = processContent()
                val project = Project(
                    title = title,
                    team = team,
                    department = department,
                    content = processedContent,
                    createdAt = System.currentTimeMillis(),
                    uploaderUid = uploaderUid!!
                )
                db.collection("projects").add(project).await()
                Toast.makeText(this@UploadActivity, "프로젝트가 성공적으로 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@UploadActivity, "업로드 실패: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun processContent(): List<Map<String, String>> = withContext(Dispatchers.IO) {
        val processedList = mutableListOf<Map<String, String>>()
        contentList.forEach { item ->
            when (item) {
                is Uri -> {
                    val downloadUrl = uploadFileToStorage(item)
                    val type = if (contentResolver.getType(item)?.startsWith("video/") == true) "video" else "image"
                    processedList.add(mapOf("type" to type, "value" to downloadUrl))
                }
                is String -> {
                    val type = if (item.startsWith("http")) "link" else "text"
                    processedList.add(mapOf("type" to type, "value" to item))
                }
            }
        }
        processedList
    }

    private suspend fun uploadFileToStorage(uri: Uri): String {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("uploads/${fileName}")
        val uploadTask = ref.putFile(uri)
        return uploadTask.await().storage.downloadUrl.await().toString()
    }

    private fun setLoading(isLoading: Boolean) {
        progressOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.btn_next).isEnabled = !isLoading
        findViewById<TextView>(R.id.btn_close).isEnabled = !isLoading
        findViewById<ImageButton>(R.id.btn_add_image).isEnabled = !isLoading
        findViewById<ImageButton>(R.id.btn_add_camera).isEnabled = !isLoading
        findViewById<ImageButton>(R.id.btn_add_text).isEnabled = !isLoading
        findViewById<ImageButton>(R.id.btn_add_html).isEnabled = !isLoading
    }
}
