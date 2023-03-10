package com.thesis.trialnavdrawer.ui.fragments

//import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.thesis.trialnavdrawer.R
import com.thesis.trialnavdrawer.data.History
import com.thesis.trialnavdrawer.databinding.FragmentDiagnosticDetailBinding
import com.thesis.trialnavdrawer.interfaces.ActionBarTitleSetter
import com.thesis.trialnavdrawer.ui.viewModels.DiagnosticDetailViewModel

class DiagnosticDetailFragment : Fragment() {

    private var _binding : FragmentDiagnosticDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DiagnosticDetailViewModel

    companion object{
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDiagnosticDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar: Toolbar? = root.findViewById(R.id.toolbar)
        toolbar?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        toolbar?.setNavigationOnClickListener { requireActivity().onBackPressed() }

        Toast.makeText(requireActivity(), "haaaloooooooooooooooooo", Toast.LENGTH_SHORT).show()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[DiagnosticDetailViewModel::class.java]
        fragmentTransactionProcess()

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        val btnCategory: Button = view.findViewById(binding.buttonDiagnostic.id)
//        btnCategory.setOnClickListener(this)
    }

    private fun fragmentTransactionProcess(){
        if(arguments != null){
            val extra = arguments?.getInt(EXTRA_DATA)
            extra?.let { viewModel.setSelectedEntityHistory(it) }
            showDetailHistory(viewModel.selectHistory())
        }
    }

    private fun showDetailHistory(history : History){
        binding.textDetailName.text = history.name_history
        binding.textDetailResult.text = history.result_history
        binding.textDetailDate.text = history.date_history
        Glide.with(this)
            .load(history.image_history)
            .transform(RoundedCorners(20))
            .into(binding.imgPhotoDetail)
    }

    override fun onAttach(context: Context) {
        Toast.makeText(requireActivity(), "is kierooo heerre? from detail detail frag", Toast.LENGTH_SHORT).show()
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_detail))


    //        (activity as MenuItemHighlighter).setMenuHighlight(2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}