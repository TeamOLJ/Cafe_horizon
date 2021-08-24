package com.teamolj.cafehorizon.stamp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentStampBinding

class StampFragment : Fragment() {
    private lateinit var binding: FragmentStampBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentStampBinding.inflate(inflater, container, false)

        val stampCount = (activity as StampActivity).getStampCount() % 10

        binding.txtStampCount.text = stampCount.toString()
        when (stampCount) {
            1 -> binding.imgEachStamp.setImageResource(R.drawable.stamp1)
            2 -> binding.imgEachStamp.setImageResource(R.drawable.stamp2)
            3 -> binding.imgEachStamp.setImageResource(R.drawable.stamp3)
            4 -> binding.imgEachStamp.setImageResource(R.drawable.stamp4)
            5 -> binding.imgEachStamp.setImageResource(R.drawable.stamp5)
            6 -> binding.imgEachStamp.setImageResource(R.drawable.stamp6)
            7 -> binding.imgEachStamp.setImageResource(R.drawable.stamp7)
            8 -> binding.imgEachStamp.setImageResource(R.drawable.stamp8)
            9 -> binding.imgEachStamp.setImageResource(R.drawable.stamp9)
            10 -> binding.imgEachStamp.setImageResource(R.drawable.stamp10)
            else -> binding.imgEachStamp.visibility = View.GONE
        }

        binding.txtStampUsage.text = (activity as StampActivity).getStampInformation()

        return binding.root
    }

}