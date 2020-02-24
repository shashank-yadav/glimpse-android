package com.example.homemaker.ArFragments

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
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
import androidx.lifecycle.ViewModelProviders
import com.example.homemaker.ActivityCallback
import com.example.homemaker.Helpers.AnalyticsHelper
import com.example.homemaker.Helpers.LaggedVariable
import com.example.homemaker.HmFragmentManager
import com.example.homemaker.Objects.GlideApp
import com.example.homemaker.Objects.Product
import com.example.homemaker.Objects.StateViewModel
import com.example.homemaker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.hm_ar_fragment_layout.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt


class HmArFragment : ArFragment(){

    private val SMALL_SIZE = 0.7f
    private val REAL_SIZE = 1.0f
    private val LARGE_SIZE = 1.2f


    private lateinit var uri:Uri

    private lateinit var coachingMotionView: TextView
    private lateinit var selectedModelView: View
    private lateinit var selectedProductView: View
    private lateinit var pnlFlash: View

    private lateinit var progressBar: View

    private var mCallback: ActivityCallback? = null
    private lateinit var viewModel: StateViewModel

    private var productsInScene: HashMap<String, Product> = HashMap<String, Product>()
    private var selectedModel: Node? = null

    private lateinit var analyticsHelper: AnalyticsHelper


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val containerView = inflater.inflate(R.layout.hm_ar_fragment_layout, container, false)
        val arCoreFrameLayout = containerView.findViewById<FrameLayout>(R.id.hm_myarfragment_ar)

        val view = super.onCreateView(inflater, container, savedInstanceState)
        arCoreFrameLayout.addView(view)

        analyticsHelper = AnalyticsHelper(this.context!!)

        coachingMotionView = containerView.coaching_motion_text
        pnlFlash = containerView.pnlFlash

        selectedModelView = containerView.selected_model_view
        selectedModelView.visibility = View.INVISIBLE

        selectedProductView = containerView.selected_product_view
        selectedProductView.visibility = View.INVISIBLE

//        arSceneView.scene.setOnTouchListener { hitTestResult, motionEvent ->
//            planeTapError()
//        }

        progressBar = containerView.progress_circular
        progressBar.visibility = View.GONE

        selectedModelView.object_delete_button.setOnClickListener {
            if( selectedModel != null) {
                arSceneView.scene.removeChild(selectedModel)
                selectedModel!!.setParent(null)
                productsInScene.remove(selectedModel!!.name)
                selectedModel = null
                selectedModelView.visibility = View.INVISIBLE
            }
        }

        val selecteModelScaleSeekBar = selectedModelView.selected_model_scale_seekbar
        selecteModelScaleSeekBar.setIndicatorTextFormat("\${PROGRESS}%")
        selecteModelScaleSeekBar.setProgress(100.0f)
        selecteModelScaleSeekBar.onSeekChangeListener = object: OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams){
                val progressFloat = seekParams.progressFloat
                analyticsHelper.logSelectContent("changedScale", "$progressFloat", "Seekbar")
                selectedModel!!.localScale = Vector3(progressFloat/100.0f, progressFloat/100.0f, progressFloat/100.0f)
            }
            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        val trueSizeButton = selectedModelView.selected_model_truesize_button
        trueSizeButton.setOnClickListener {
            analyticsHelper.logSelectContent("trueSizeModel", "", "Button")
            selecteModelScaleSeekBar.setProgress(100.0f)
        }

        val showFloorButton = containerView.show_floor_button
        showFloorButton.setOnClickListener {
            if(arSceneView.planeRenderer.isVisible){
                analyticsHelper.logSelectContent("floorVisibility", "Made invisible", "Button")
                arSceneView.planeRenderer.isVisible = false
                showFloorButton.imageAlpha = 50
            }else{
                analyticsHelper.logSelectContent("floorVisibility", "Made visible", "Button")
                arSceneView.planeRenderer.isVisible = true
                showFloorButton.imageAlpha = 255
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
        analyticsHelper.logSelectContent("takePhoto", "", "Button")
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

        if(coachingMotionView.visibility == View.VISIBLE){
            val frame = arSceneView.arFrame
            if (frame!!.getUpdatedTrackables(Plane::class.java).isNotEmpty()){
                coachingMotionView.visibility = View.INVISIBLE
                analyticsHelper.logSelectContent("planeDetectionComplete", "", "Action")
            }
        }else{
            if (viewModel.state.productSelected == null){
                selectedProductView.visibility = View.INVISIBLE
            }else{
                if( selectedModelView.visibility == View.INVISIBLE && selectedProductView.visibility == View.INVISIBLE){
                    val storageReference = FirebaseStorage.getInstance().reference.child(viewModel.state.productSelected!!.getImgUrl())
                    GlideApp.with(selectedProductView.context /* context */)
                            .load(storageReference)
                            .into(selectedProductView.selected_product_image)
                    selectedProductView.selected_product_line1.text = viewModel.state.productSelected!!.name
                    selectedProductView.visibility = View.VISIBLE
                }
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
            analyticsHelper.logSelectContent("addProduct", "", "Button")
            val parent = this.parentFragment as HmFragmentManager
            selectedModelView.visibility = View.INVISIBLE
            selectedModel = null
            parent.changeView()
        }

        arSceneView.scene.addOnPeekTouchListener { hitTestResult, motionEvent ->
            touchEvent(hitTestResult, motionEvent)
        }

        this.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            analyticsHelper.logSelectContent("tappedOnPlane", "", "Action")
            if(selectedProductView.visibility == View.VISIBLE || (selectedProductView.visibility == View.INVISIBLE&& selectedModelView.visibility == View.INVISIBLE)) {
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
                    analyticsHelper.logSelectContent("tappedOnPlaneResult", "No Object selected", "Action")
                    Toast.makeText(this.context, "Select a product first using the + button and then tap on the plane", Toast.LENGTH_SHORT).show()
                }
            }else{
                analyticsHelper.logSelectContent("tappedOnPlaneResult", "Tapped with model selected", "Action")
                Toast.makeText(this.context, "Select a product first using the + button and then tap on the plane", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun getSessionConfiguration(session: Session?): Config {
        val config = super.getSessionConfiguration(session)
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
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
                    analyticsHelper.logSelectContent("tappedOnPlaneResult", "Placed Model", "Action")
//                    Toast.makeText(this.context, model.toString(), Toast.LENGTH_SHORT).show()
                }
                .exceptionally {
//                    progressDialog.dismiss()
                    progressBar.visibility = View.INVISIBLE
                    analyticsHelper.logSelectContent("tappedOnPlaneResult", "Unable to place model", "Action")
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
//        transformableNode.localScale = Vector3(SMALL_SIZE, SMALL_SIZE, SMALL_SIZE)
        transformableNode.renderable = renderable
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.name = randomId
        transformableNode.select()
        selectedModel = transformableNode
        viewModel.state.productSelected = null
    }

    private fun touchEvent(hitTestResult: HitTestResult?, motionEvent: MotionEvent?): Boolean{

        analyticsHelper.logSelectContent("ARTap", "", "Action")
        if(motionEvent!!.actionMasked == MotionEvent.ACTION_UP) {

            val node = hitTestResult!!.node
            if (node == null) {
                if( selectedModel != null){
                    selectedModelView.visibility = View.INVISIBLE
                    selectedModel = null
                    analyticsHelper.logSelectContent("ARTapResult", "Un-selected a model", "Action")
                }
                return true
            }

            selectedProductView.visibility = View.INVISIBLE
            selectedModel = node!!
            val product = productsInScene[node!!.name]// ?: return true

            Log.e("Product", product.toString())
            Log.e("allProducts", productsInScene.toString())

            selectedModelView.selected_model_line1.text = product!!.name
//        selected_model_view.selected_model_line2.text = this.context!!.resources.getString(R.string.Rs)+ product!!.price.toString()
            val storageReference = FirebaseStorage.getInstance().reference.child(product.getImgUrl())
            GlideApp.with(selectedModelView.context /* context */)
                    .load(storageReference)
                    .into(selectedModelView.selected_model_image)
            selectedModelView.visibility = View.VISIBLE
            analyticsHelper.logSelectContent("ARTapResult", "Selected a model", "Action")

            return true
        }else{
            return false
        }
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

