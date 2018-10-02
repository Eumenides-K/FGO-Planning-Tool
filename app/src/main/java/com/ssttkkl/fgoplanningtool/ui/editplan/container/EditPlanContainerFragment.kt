package com.ssttkkl.fgoplanningtool.ui.editplan.container


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.costitemlist.CostItemListFragment
import com.ssttkkl.fgoplanningtool.ui.editplan.EditPlanActivity
import com.ssttkkl.fgoplanningtool.ui.editplan.EditPlanViewModel
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException
import kotlinx.android.synthetic.main.fragment_editplan_container.*

class EditPlanContainerFragment : Fragment() {
    interface Callback {
        fun onSaveAction()
        fun onRemoveAction()
    }

    private var callback: Callback? = null

    private lateinit var viewModel: EditPlanViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = when {
            parentFragment is Callback -> parentFragment as Callback
            activity is Callback -> activity as Callback
            else -> throw NoInterfaceImplException(Callback::class)
        }
        viewModel = ViewModelProviders.of(activity!!).get(EditPlanViewModel::class.java)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_editplan_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.apply {
            // setup the Toolbar
            setHasOptionsMenu(true)
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = when (viewModel.mode) {
                    EditPlanActivity.Mode.New -> getString(R.string.title_new_editplan)
                    EditPlanActivity.Mode.Edit -> getString(R.string.title_edit_editplan)
                }
            }
        }

        // setup the viewpager
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            private val titles = listOf(resources.getString(R.string.tab_edit_editplan), resources.getString(R.string.tab_items_editplan))
            override fun getPageTitle(pos: Int) = titles[pos]
            override fun getItem(pos: Int): Fragment = if (pos == 0) EditPlanFragment() else CostItemListFragment()
            override fun getCount() = titles.size
            override fun instantiateItem(container: ViewGroup, position: Int) =
                    super.instantiateItem(container, position).also {
                        if (it is CostItemListFragment)
                            it.plans = listOf(viewModel.plan)
                    }
        }
        tabLayout.setupWithViewPager(viewPager)

        // observe the plan changing
        viewModel.apply {
            listOf(servantId, nowExp, planExp,
                    nowSkillI, nowSkillII, nowSkillIII,
                    planSkillI, planSkillII, planSkillIII).forEach {
                it.observe(this@EditPlanContainerFragment, Observer { onPlanChanged() })
            }
            listOf(ascendedOnNowStage, ascendedOnPlanStage).forEach {
                it.observe(this@EditPlanContainerFragment, Observer { onPlanChanged() })
            }
            dress.observe(this@EditPlanContainerFragment, Observer { onPlanChanged() })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.editplan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.finish()
            R.id.save_action -> callback?.onSaveAction()
            R.id.remove_action -> callback?.onRemoveAction()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onPlanChanged() {
        (childFragmentManager.fragments.firstOrNull { it is CostItemListFragment } as? CostItemListFragment)
                ?.plans = listOf(viewModel.plan)
    }
}
