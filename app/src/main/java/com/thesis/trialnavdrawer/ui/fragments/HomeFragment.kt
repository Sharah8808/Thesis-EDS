package com.thesis.trialnavdrawer.ui.fragments

//import com.thesis.trialnavdrawer.MainActivity.ActionBarTitleSetter
import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.thesis.trialnavdrawer.databinding.FragmentHomeBinding
import com.thesis.trialnavdrawer.ui.viewModels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDate
//        binding.buttonDiagnostic.setOnClickListener(View.OnClickListener {
//            // Code here executes on main thread after user presses button
//            Toast.makeText(requireActivity(), "Halo", Toast.LENGTH_SHORT).show()
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(com.thesis.trialnavdrawer.R.id.action_nav_home_to_nav_diagnostic, DiagnosticFragment())
//            transaction?.disallowAddToBackStack()
//            transaction?.commit()
//
//        })

//        onClick(root)
        dateApplicator(textView)


        return root

    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val btnCategory: Button = view.findViewById(binding.buttonDiagnostic.id)
            val textHistoryAll : TextView = view.findViewById(binding.textHistorySeeAll.id)
            val textDiseaseListAll : TextView = view.findViewById(binding.textDiseaseListSeeAll.id)
            btnCategory.setOnClickListener(this)
            textHistoryAll.setOnClickListener(this)
            textDiseaseListAll.setOnClickListener(this)

        }

        override fun onClick(v: View) {
            Toast.makeText(requireActivity(), "teessst", Toast.LENGTH_SHORT).show()
            if (v.id == binding.buttonDiagnostic.id) {
                Toast.makeText(requireActivity(), "test pindah ke diagnostik", Toast.LENGTH_SHORT).show()
                val mCategoryFragment = DiagnosticFragment()
                val mFragmentManager = parentFragmentManager
                mFragmentManager.beginTransaction().apply {
//                    Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show()
                    replace(binding.frameHomeFragment.id, mCategoryFragment, DiagnosticFragment::class.java.simpleName)
//                    binding.appBarLayout.setExpanded(true, true)
                    addToBackStack(null)
                    commit()

//                    val fragmentInstance = parentFragmentManager.findFragmentById(binding.frameHomeFragment.id)
//                    if(fragmentInstance is HomeFragment){
//                        Toast.makeText(requireActivity(), "home fragment", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(requireActivity(), "moved diag success", Toast.LENGTH_SHORT).show()
//                    }
                }
            }

            if (v.id == binding.textHistorySeeAll.id){
                Toast.makeText(requireActivity(), "test pindah ke riwayat", Toast.LENGTH_SHORT).show()
                val mCategoryFragment = HistoryFragment()
                val mFragmentManager = parentFragmentManager
                mFragmentManager.beginTransaction().apply {
//                    Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show()
                    replace(binding.frameHomeFragment.id, mCategoryFragment, HistoryFragment::class.java.simpleName)
//                    binding.appBarLayout.setExpanded(true, true)
                    addToBackStack(null)
                    commit()

//                    val fragmentInstance = parentFragmentManager.findFragmentById(binding.frameHomeFragment.id)
//                    if(fragmentInstance is HomeFragment){
//                        Toast.makeText(requireActivity(), "home fragment", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(requireActivity(), "moved diag success", Toast.LENGTH_SHORT).show()
//                    }
                }
            }

            if (v.id == binding.textDiseaseListSeeAll.id){
                Toast.makeText(requireActivity(), "test pindah ke daftar penyakit", Toast.LENGTH_SHORT).show()
                val mCategoryFragment = DiseaseListFragment()
                val mFragmentManager = parentFragmentManager
                mFragmentManager.beginTransaction().apply {
//                    Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show()
                    replace(binding.frameHomeFragment.id, mCategoryFragment, DiseaseListFragment::class.java.simpleName)
//                    binding.appBarLayout.setExpanded(true, true)
                    addToBackStack(null)
                    commit()

//                    val fragmentInstance = parentFragmentManager.findFragmentById(binding.frameHomeFragment.id)
//                    if(fragmentInstance is HomeFragment){
//                        Toast.makeText(requireActivity(), "home fragment", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(requireActivity(), "moved diag success", Toast.LENGTH_SHORT).show()
//                    }
                }
            }
        }



    @SuppressLint("SimpleDateFormat")
    fun dateApplicator(textView: TextView){
        val dateTime : String
        val calendar : Calendar
        val simpleDateFormat : SimpleDateFormat

        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("EEEE, dd LLLL yyyy")
        dateTime = simpleDateFormat.format(calendar.time).toString()
        textView.text = dateTime


    }


//    fun onDestinationChanged(
//        controller: NavController, destination: NavDestination,
//        arguments: Bundle?
//    ) {
//        if (destination.id == R.id.helpFragment) {
//            Timber.e("App Bar Hide")
//            binding.appBarLayout.setExpanded(false, true) //This never hides toolbar
//        } else {
//            Timber.e("App Bar Show")
//            binding.appBarLayout.setExpanded(true, true)
//        }
//    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}