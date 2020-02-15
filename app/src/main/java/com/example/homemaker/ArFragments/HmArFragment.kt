package com.example.homemaker.ArFragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.AudioManager
import android.media.MediaActionSound
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProviders
import com.example.homemaker.ActivityCallback
import com.example.homemaker.HmFragmentManager
import com.example.homemaker.MainActivity
import com.example.homemaker.Objects.GlideApp
import com.example.homemaker.Objects.Product
import com.example.homemaker.Objects.StateViewModel
import com.example.homemaker.R
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.*
import com.google.ar.sceneform.*
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.PlaneRenderer
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.hm_ar_fragment_layout.view.*
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap


class HmArFragment : ArFragment(){

    private val SMALL_SIZE = 0.7f
    private val REAL_SIZE = 1.0f
    private val LARGE_SIZE = 1.2f


    private lateinit var uri:Uri

    private lateinit var coaching_motion_view: TextView

    private lateinit var selected_model_view: View
    private lateinit var selected_product_view: View
    private lateinit var pnlFlash: View

    private lateinit var progressBar: View

    private var mCallback: ActivityCallback? = null
    private lateinit var viewModel: StateViewModel

    private lateinit var progressDialog:ProgressDialog

    private var productsInScene: HashMap<String, Product> = HashMap<String, Product>()
    private var selectedModel: Node? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val containerView = inflater.inflate(R.layout.hm_ar_fragment_layout, container, false)
        val arCoreFrameLayout = containerView.findViewById<FrameLayout>(R.id.hm_myarfragment_ar)

        val view = super.onCreateView(inflater, container, savedInstanceState)
        arCoreFrameLayout.addView(view)

        coaching_motion_view = containerView.coaching_motion_text
        pnlFlash = containerView.pnlFlash

        selected_model_view = containerView.selected_model_view
        selected_model_view.visibility = View.INVISIBLE

        selected_product_view = containerView.selected_product_view
        selected_product_view.visibility = View.INVISIBLE

//        arSceneView.scene.setOnTouchListener { hitTestResult, motionEvent ->
//            planeTapError()
//        }

        progressBar = containerView.progress_circular
        progressBar.visibility = View.GONE

        selected_model_view.object_delete_button.setOnClickListener {
            if( selectedModel != null) {
                arSceneView.scene.removeChild(selectedModel)
                selectedModel!!.setParent(null)
                productsInScene.remove(selectedModel!!.name)
                selectedModel = null
                selected_model_view.visibility = View.INVISIBLE
            }
        }

        selected_model_view.model_size_chipgroup.setOnCheckedChangeListener { group, checkedId ->
            //            Toast.makeText(this.context, group.findViewById<Chip>(checkedId).text.toString(),Toast.LENGTH_LONG).show()
            val chip = group.findViewById<Chip>(checkedId)
            if (chip != null){
                when (chip.text.toString()) {
                    "Small" -> selectedModel!!.localScale = Vector3(SMALL_SIZE, SMALL_SIZE, SMALL_SIZE)
                    "Real" -> selectedModel!!.localScale = Vector3(REAL_SIZE, REAL_SIZE, REAL_SIZE)
                    "Large" -> selectedModel!!.localScale = Vector3(LARGE_SIZE, LARGE_SIZE, LARGE_SIZE)
                }
            }
        }

        containerView.fab_take_photo.setOnClickListener {
            takePhoto()
        }

        return containerView
    }

    fun planeTapError(): Boolean{
        Toast.makeText(this.context, "Please select a Product and tap on detected plane only!!", Toast.LENGTH_SHORT).show()
        return true
    }


    private fun takePhoto() {
        val audio: AudioManager = this.context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when( audio.ringerMode){
            AudioManager.RINGER_MODE_NORMAL -> MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
            else -> Log.e("sound", "silent mode")
        }

        pnlFlash.visibility = View.VISIBLE
        val fade = AlphaAnimation(1.0f, 0.0f)
        fade.duration = 50
        fade.setAnimationListener( object: AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onAnimationEnd(anim: Animation) {
                pnlFlash.visibility = View.GONE;
            }

            override fun onAnimationStart(p0: Animation?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        pnlFlash.startAnimation(fade);

        val view = arSceneView
        // Create a bitmap the size of the scene view.
        val bitmap = Bitmap.createBitmap(view.width, view.height,
               Bitmap.Config.ARGB_8888)

        // Create a handler thread to offload the processing of the image.
        val handlerThread = HandlerThread("PixelCopier")
        handlerThread.start()
        // Make the request to copy.

        PixelCopy.request(view, bitmap, { copyResult ->
           if (copyResult == PixelCopy.SUCCESS) {
               try {
                   saveBitmapToDisk(bitmap)
               } catch(e: Exception){
                   Toast.makeText(this.context, e.toString(), Toast.LENGTH_LONG).show()
               }
           } else {
               Toast.makeText(this.context, "Failed to save Image", Toast.LENGTH_LONG).show()
           }
           handlerThread.quitSafely()
        }, Handler(handlerThread.looper))
    }


    private fun saveBitmapToDisk(bitmap: Bitmap) {
        val now = Date()
        android.text.format.DateFormat.format("yy-MM-dd_hh:mm", now)
        val title = "glimpase_"+ now + ".jpg"

        // Save image to gallery
        val contentResolver = context!!.contentResolver
        val savedImageURL = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                title,
                "Image of $title"
        )
        Toast.makeText(this.context, "Saved Image successfully as: $savedImageURL", Toast.LENGTH_LONG).show()
    }

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)

        if(coaching_motion_view.visibility == View.VISIBLE){
            val frame = arSceneView.arFrame
            if (frame!!.getUpdatedTrackables(Plane::class.java).isNotEmpty()){
//            for (plane in frame!!.getUpdatedTrackables(Plane::class.java)) {
                coaching_motion_view.visibility = View.INVISIBLE
//                break
            }
        }else{


//            try {
//                val prod = viewModel.state.productSelected
//                coaching_product_view.visibility = View.VISIBLE
//            }
//            catch (e: Exception){
//                coaching_product_view.visibility = View.INVISIBLE
//            }

            if (viewModel.state.productSelected == null){
                selected_product_view.visibility = View.INVISIBLE

            }else{
                selected_product_view.visibility = View.VISIBLE
                val storageReference = FirebaseStorage.getInstance().reference.child(viewModel.state.productSelected!!.getImgUrl())
                GlideApp.with(selected_product_view.context /* context */)
                        .load(storageReference)
                        .into(selected_product_view.selected_product_image)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(StateViewModel::class.java)
//        progressDialog = ProgressDialog(this.context)

        this.arSceneView.scene.addOnUpdateListener { frameTime ->
            this.onUpdate(frameTime)
            val planeRenderer = this.arSceneView.planeRenderer;
            planeRenderer.material.thenAccept {
                it.setFloat(PlaneRenderer.MATERIAL_SPOTLIGHT_RADIUS, 200f)
                it.setFloat3(PlaneRenderer.MATERIAL_COLOR, Color(254.0f/255.0f, 219.0f/255.0f, 208.0f/255.0f, 1.0f))
            }
        }

        val fabAddItems = view!!.findViewById(R.id.fab_additem) as FloatingActionButton
        fabAddItems.setOnClickListener{
            val parent = this.parentFragment as HmFragmentManager
            parent.changeView()
        }

        arSceneView.scene.addOnPeekTouchListener { hitTestResult, motionEvent ->
            touchEvent(hitTestResult, motionEvent)
        }


//        val layout = RelativeLayout(this.context)
//        val progressBar = ProgressBar(this.context, null, android.R.attr.progressBarStyleLarge)
//        progressBar.isIndeterminate = true
//        progressBar.visibility = View.VISIBLE
//        val params = RelativeLayout.LayoutParams(100, 100)
//        params.addRule(RelativeLayout.CENTER_IN_PARENT)
//        layout.addView(progressBar, params)
//
//        setContentView(layout)



//        progressDialog.setMessage("Loading your model....")
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)



        this.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            val anchor = hitResult.createAnchor()

            try {
                progressBar.visibility = View.VISIBLE
                val product = viewModel.state.productSelected
                val storageReference = FirebaseStorage.getInstance().reference.child(product!!.getModelUrl())

                storageReference.downloadUrl.addOnSuccessListener {
                    placeObject(this, anchor, it)

                }.addOnFailureListener {
                    Log.e("Url", it.toString())
                }
            } catch (e: Exception) {
                progressBar.visibility = View.INVISIBLE
//                    Toast.makeText(this.context, "Select a product first!!", Toast.LENGTH_SHORT).show()
                Toast.makeText(this.context, "Select a product first using the + button and then tap on the plane", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun getSessionConfiguration(session: Session?): Config {
        val config = super.getSessionConfiguration(session)
        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL)
        return config
    }


    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {

        progressBar.visibility = View.VISIBLE
        ModelRenderable.builder()
                .setSource(fragment.context, RenderableSource.builder().setSource(
                        fragment.context,
                        model,
                        RenderableSource.SourceType.GLB).build())
                .setRegistryId(model)
                .build()
                .thenAccept {
                    addNodeToScene(fragment, anchor, it)
//                    val arUri = mCallback!!.getArFragmentUri()
//                    progressDialog.dismiss()
                    progressBar.visibility = View.INVISIBLE
//                    Toast.makeText(this.context, model.toString(), Toast.LENGTH_SHORT).show()
                }
                .exceptionally {
//                    progressDialog.dismiss()
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this.context, "Could not fetch model from $model", Toast.LENGTH_SHORT).show()
                    return@exceptionally null
                }
    }


    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        val randomId = java.util.UUID.randomUUID().toString()
        productsInScene[randomId] = viewModel.state.productSelected!!
        // TransformableNode means the user to move, scale and rotate the model
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.scaleController.isEnabled = false
        transformableNode.localScale = Vector3(SMALL_SIZE, SMALL_SIZE, SMALL_SIZE)
        transformableNode.renderable = renderable
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.name = randomId
        transformableNode.select()
        selectedModel = transformableNode
        viewModel.state.productSelected = null
    }

    private fun touchEvent(hitTestResult: HitTestResult?, motionEvent: MotionEvent?): Boolean{
        val node = hitTestResult!!.node
        if (node == null){
            selected_model_view.visibility = View.INVISIBLE
            selectedModel = null
            return true
        }

        selectedModel = node!!
        val product = productsInScene[node!!.name]// ?: return true

        Log.e("Product", product.toString())
        Log.e("allProducts", productsInScene.toString())

        selected_model_view.selected_model_line1.text = product!!.name
        selected_model_view.selected_model_line2.text = this.context!!.resources.getString(R.string.Rs)+ product!!.price.toString()
        val storageReference = FirebaseStorage.getInstance().reference.child(product.getImgUrl())
        GlideApp.with(selected_model_view.context /* context */)
                .load(storageReference)
                .into(selected_model_view.selected_model_image)

        selected_model_view.model_size_chipgroup.clearCheck()
        when(selectedModel!!.localScale ){
            Vector3(SMALL_SIZE, SMALL_SIZE, SMALL_SIZE) -> selected_model_view.chip_small.isChecked = true
            Vector3(REAL_SIZE, REAL_SIZE, REAL_SIZE) -> selected_model_view.chip_real.isChecked = true
            Vector3(LARGE_SIZE, LARGE_SIZE, LARGE_SIZE) -> selected_model_view.chip_large.isChecked = true
        }
        selected_model_view.visibility = View.VISIBLE
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallback = context as ActivityCallback
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

}

