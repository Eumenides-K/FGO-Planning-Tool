package com.ssttkkl.fgoplanningtool.ui.planlist.confirmchangeplan

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.FragmentConfirmChangePlanBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity

class ConfirmChangePlanFragment : Fragment() {
    private lateinit var binding: FragmentConfirmChangePlanBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentConfirmChangePlanBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[ConfirmChangePlanFragmentViewModel::class.java].apply {
            plans.value = arguments!!.getParcelableArray("plans")!!.map { it as ConfirmablePlan }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.drawerState = false
        setHasOptionsMenu(true)

        binding.recView.apply {
            adapter = DeductItemListRecViewAdapter(context!!, this@ConfirmChangePlanFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }

        binding.viewModel?.apply {
            title.observe(this@ConfirmChangePlanFragment, Observer {
                onTitleChanged(it ?: "")
            })
            finishEvent.observe(this@ConfirmChangePlanFragment, Observer {
                navigateUp()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.confirm_change_plan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.selectAll -> binding.viewModel?.onClickSelectAll()
            R.id.yes -> binding.viewModel?.onClickYes()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun navigateUp() {
        findNavController().navigate(R.id.action_global_planListFragment)
    }

    private fun onTitleChanged(title: String) {
        (activity as? MainActivity)?.title = title
    }

    companion object {
        fun buildArgumentsWithChange(old: Plan, new: Plan) = Bundle().apply {
            putParcelableArray("plans", arrayOf(ConfirmablePlan(old, new, Mode.Change)))
        }

        fun buildArgumentsWithRemove(plans: Collection<Plan>) = Bundle().apply {
            putParcelableArray("plans", plans.map { ConfirmablePlan(it, null, Mode.Remove) }.toTypedArray())
        }

        fun buildArgumentsWithRemove(plan: Plan) = buildArgumentsWithRemove(listOf(plan))
    }
}