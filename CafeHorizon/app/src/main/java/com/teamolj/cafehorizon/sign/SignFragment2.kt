package com.teamolj.cafehorizon.sign

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentSign2Binding

class SignFragment2 : Fragment() {
    private lateinit var binding: FragmentSign2Binding

    val isIdChecked = false
    val isPwdChecked = false

    val isIdValid = false
    val isNickValid = false
    val isPwdValid = false
    val isDBirthValid = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSign2Binding.inflate(inflater, container, false)



        //아이디 중복확인
        binding.btnDuplCk.setOnClickListener {

        }


        //회원가입 버튼
        binding.btnSign.setOnClickListener {

        }

        return inflater.inflate(R.layout.fragment_sign2, container, false)
    }

}