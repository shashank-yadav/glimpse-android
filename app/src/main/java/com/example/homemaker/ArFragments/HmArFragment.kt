package com.example.homemaker.ArFragments

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.example.homemaker.ActivityCallback
import com.example.homemaker.Objects.StateViewModel
import com.example.homemaker.R
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.PlaneRenderer
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.hm_ar_fragment_layout.view.*
import java.lang.Exception


class HmArFragment : ArFragment(){

    private lateinit var uri:Uri
    private lateinit var coaching_motion_view: TextView
    private lateinit var coaching_product_view: TextView
    private var mCallback: ActivityCallback? = null
    private lateinit var viewModel: StateViewModel

    private lateinit var progressDialog:ProgressDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val containerView = inflater.inflate(R.layout.hm_ar_fragment_layout, container, false)
        val arCoreFrameLayout = containerView.findViewById<FrameLayout>(R.id.hm_myarfragment_ar)

        val view = super.onCreateView(inflater, container, savedInstanceState)
        arCoreFrameLayout.addView(view)

        coaching_motion_view = containerView.coaching_motion_text
        coaching_product_view = containerView.coaching_product_text
        coaching_product_view.visibility = View.INVISIBLE

        return containerView
    }


    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)

        if(coaching_motion_view.visibility == View.VISIBLE){
            val frame = arSceneView.arFrame
            if (frame!!.getUpdatedTrackables(Plane::class.java).size > 0){
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
                coaching_product_view.visibility = View.INVISIBLE
            }else{
                coaching_product_view.visibility = View.VISIBLE

            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(StateViewModel::class.java)
        progressDialog = ProgressDialog(this.context)

        this.arSceneView.scene.addOnUpdateListener { frameTime ->
            this.onUpdate(frameTime)
            val planeRenderer = this.arSceneView.planeRenderer;
            planeRenderer.material.thenAccept {
                it.setFloat(PlaneRenderer.MATERIAL_SPOTLIGHT_RADIUS, 200f)
                it.setFloat3(PlaneRenderer.MATERIAL_COLOR, Color(0.0f, 0.0f, 1.0f, 1.0f))
            }
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



        progressDialog.setMessage("Loading your model....")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)



        this.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            val anchor = hitResult.createAnchor()

//            uri = Uri.parse("https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf")
//            Log.d("viewSelected", viewModel.state.productSelected.name)
//            uri = Uri.parse(viewModel.state.productSelected.getModelUrl())

            try {
                progressDialog.show()
                val product = viewModel.state.productSelected
                val storageReference = FirebaseStorage.getInstance().reference.child(product!!.getModelUrl())

                storageReference.downloadUrl.addOnSuccessListener {
                    placeObject(this, anchor, it)
                    viewModel.state.productSelected = null
                }.addOnFailureListener {
                    // Handle any errors
                    Log.e("Url", it.toString())
                }
            }
            catch (e: Exception){
                progressDialog.hide()
                Toast.makeText(this.context, "Select a product first!!", Toast.LENGTH_SHORT).show()
//                Toast.makeText(this.context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getSessionConfiguration(session: Session?): Config {
        val config = super.getSessionConfiguration(session)
        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL)
        return config
    }


    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {
        progressDialog.show()
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
                    progressDialog.dismiss()
//                    Toast.makeText(this.context, model.toString(), Toast.LENGTH_SHORT).show()
                }
                .exceptionally {
                    progressDialog.dismiss()
                    Toast.makeText(this.context, "Could not fetch model from $model", Toast.LENGTH_SHORT).show()
                    return@exceptionally null
                }
    }


    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        // TransformableNode means the user to move, scale and rotate the model
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderable
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
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

