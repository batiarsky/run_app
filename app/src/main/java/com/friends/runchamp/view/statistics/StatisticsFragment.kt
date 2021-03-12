package com.friends.runchamp.view.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.friends.runchamp.R
import org.koin.android.ext.android.inject

class StatisticsFragment : Fragment() {

    private val mViewModel by inject<StatisticsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.statistics_screen_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(mViewModel)

        mViewModel.getRunningData()
    }
}




