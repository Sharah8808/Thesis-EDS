package com.thesis.eds.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.thesis.eds.R
import com.thesis.eds.databinding.FragmentSettingBinding
import com.thesis.eds.ui.viewModels.SettingViewModel
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter

class SettingFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentSettingBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel by viewModels<SettingViewModel>()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        viewModel.loadUserData()

        val logout : TextView = view.findViewById(binding.textLogout.id)
        val editProfile : Button = view.findViewById(binding.buttonEditProfile.id)
        val textUpdate : TextView = view.findViewById(binding.descUpdate.id)
        val textAbout : TextView = view.findViewById(binding.descAbout.id)

        logout.setOnClickListener(this)
        editProfile.setOnClickListener(this)
        textAbout.setOnClickListener(this)
        textUpdate.setOnClickListener(this)

        showProfileWidgetData()
    }

    private fun showProfileWidgetData(){
        viewModel.user.observe(viewLifecycleOwner){ user ->
            binding.textFullname.text = user?.fullname
            binding.textEmail.text = user?.email
            binding.textPhoneNumber.text = user?.phoneNumber
            val profilePictureUrl = user?.img

            if (profilePictureUrl != null) {
                Glide.with(this)
                    .load(profilePictureUrl)
                    .into(binding.imgAvatar)
            } else {
                // Use a default image if the user doesn't have a profile picture
                Glide.with(this)
                    .load(R.drawable.chawieputh)
                    .into(binding.imgAvatar)
            }
            Log.d("EDSThesis_Setting", "Variables check -->\n name = ${user?.fullname}\n email = ${user?.email}\n phone = ${user?.phoneNumber}\n img = ${user?.img}")

        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.textLogout.id -> {
                showLogoutAlertDialog()
            }
            binding.buttonEditProfile.id -> {
                Log.d("EDSThesis_Setting", "Move to edit profile page...")
                val action = SettingFragmentDirections.actionNavSettingToEditProfileFragment()
                findNavController().navigate(action)
            }
            binding.descUpdate.id -> {
                showUpdateAlertDialog()
            }
            binding.descAbout.id -> {
                showAboutAlertDialog()
            }
        }
    }

    private fun showUpdateAlertDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Info Update Sistem")
        builder.setMessage("Sistem masih dalam tahap pengembangan. Tunggu info update sistem selanjutnya.")
            .setCancelable(false)
            .setNeutralButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showAboutAlertDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tentang Aplikasi")
        builder.setMessage("Aplikasi ini ditujukan untuk mendeteksi penyakit telinga menggunakan kamera endoskopi yang disambungkan ke USB smartphone pengguna.")
            .setCancelable(false)
            .setNeutralButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showLogoutAlertDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Yakin ingin keluar dari aplikasi?")
            .setCancelable(false)
            .setPositiveButton("Iya"){_, _ ->
                firebaseAuth.signOut()
                val action = SettingFragmentDirections.actionNavSettingToLoginActivity()
                findNavController().navigate(action)
            }
            .setNegativeButton("Tidak"){dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        Log.d("EDSThesis_Setting", "Currently on Setting Fragment...")
        super.onAttach(context)
        (activity as ActionBarTitleSetter).setTitle(getString(R.string.menu_pengaturan))
    }


}